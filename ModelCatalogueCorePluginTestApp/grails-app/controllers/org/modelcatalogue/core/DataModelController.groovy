package org.modelcatalogue.core

import com.google.common.collect.ImmutableSet
import grails.transaction.Transactional
import grails.util.GrailsNameUtils
import groovy.transform.CompileStatic
import org.hibernate.SessionFactory
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.dataimport.excel.ExcelExporter
import org.modelcatalogue.core.export.inventory.CatalogueElementToXlsxExporter
import org.modelcatalogue.core.export.inventory.DataModelToDocxExporter
import org.modelcatalogue.core.policy.VerificationPhase
import org.modelcatalogue.core.publishing.DraftContext
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.builder.BuildProgressMonitor
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.ListWrapper
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.core.util.lists.Relationships
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshaller
import grails.plugin.springsecurity.acl.AclUtilService
import org.modelcatalogue.core.dataexport.excel.gmcgridreport.GMCGridReportXlsxExporter
import org.springframework.http.HttpStatus
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Acl
import org.springframework.security.acls.model.ObjectIdentity
import org.springframework.security.acls.model.ObjectIdentityRetrievalStrategy
import grails.plugin.springsecurity.acl.AclService
import org.springframework.validation.Errors
import org.springframework.security.access.annotation.Secured
import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.OK
import grails.plugin.springsecurity.SpringSecurityService

class DataModelController<T extends CatalogueElement> extends AbstractCatalogueElementController<DataModel> {

    SessionFactory sessionFactory

    SpringSecurityService springSecurityService

    DataModelGormService dataModelGormService

    AclUtilService aclUtilService

    ObjectIdentityRetrievalStrategy objectIdentityRetrievalStrategy

    FavouriteService favouriteService

    AclService aclService

	DataModelController() {
		super(DataModel, false)
	}

    @CompileStatic
    @Override
    protected DataModel queryForResource(Serializable id) {
        dataModelGormService.findById(id as Long)
    }

    //can be used in the future
    static final String DOC_IMAGE_PATH = ""

    //custom template is better than the default inventory report - so using it
    static Closure customTemplate = {
        'document' font: [family: 'Calibri', size: 11], margin: [left: 20, right: 10]
        'paragraph.title' font: [color: '#1F497D', size: 32.pt, bold: true], margin: [top: 150.pt, bottom: 10.pt]
        'paragraph.subtitle' font: [color: '#1F497D', size: 36.pt], margin: [top: 0.pt]
        'paragraph.description' font: [color: '#13D4CA', size: 16.pt, italic: true], margin: [left: 30, right: 30]
        'heading1' font: [size: 18, bold: true]
        'heading2' font: [size: 18, bold: true]
        'heading3' font: [size: 16, bold: true]
        'heading4' font: [size: 16, bold: true]
        'heading5' font: [size: 15]
        'heading6' font: [size: 14]
        'table.row.cell.headerCell' font: [color: '#FFFFFF', size: 12.pt, bold: true], background: '#1F497D'
        'table.row.cell' font: [size: 10.pt]
        'paragraph.headerImage' height: 1.366.inches, width: 2.646.inches
    }



    /**
     * Saves a resource
     * Overrides the base method - there is a slightly different security model
     */
    @Secured(['ROLE_METADATA_CURATOR','ROLE_ADMIN','ROLE_SUPERVISOR'])
    @Transactional
    def save() {
        if (handleReadOnly()) {
            return
        }
        DataModel instance = createResource()

        instance.validate()

        if (!params.skipPolicies) {
            validatePolicies(VerificationPhase.PROPERTY_CHECK, instance, objectToBind)
        }

        if (instance.hasErrors()) {
            if (!hasUniqueName() || getObjectToBind().size() > 1 || !getObjectToBind().containsKey('name')) {
                respond instance.errors
                return
            }

            Errors errors = instance.errors

            if (errors.getFieldError('name').any { it.code == 'unique' }) {
                T found = resource.findByName(getObjectToBind().name, [sort: 'versionNumber', order: 'desc'])
                if (found) {
                    if (!found.instanceOf(CatalogueElement)) {
                        respond found
                        return
                    }
                    if (found.status != ElementStatus.DRAFT) {
                        found = elementService.createDraftVersion(found, DraftContext.userFriendly())
                    }
                    respond found
                    return
                }
            }

            respond errors
            return
        }

        cleanRelations(instance)

        instance.save flush: true

        bindRelations(instance, false)

        instance.save flush: true

        if (!params.skipPolicies) {
            validatePolicies(VerificationPhase.EXTENSIONS_CHECK, instance, objectToBind)
        }

        if (instance.hasErrors()) {
            respond instance.errors
            return
        }

        if (favoriteAfterUpdate && modelCatalogueSecurityService.userLoggedIn && instance) {
            favouriteService.favouriteModel(instance)
        }

        ObjectIdentity objectIdentity = objectIdentityRetrievalStrategy.getObjectIdentity(instance)
        aclService.createAcl(objectIdentity)

        String username = springSecurityService.principal.username
        aclUtilService.addPermission instance, username, BasePermission.ADMINISTRATION

        respond instance, [status: CREATED]
    }


    /**
     * Finalize a model
     * @param id cloned element id
     * @param destinationDataModelId destination data model id
     */
    @Transactional
    def finalizeElement() {

        if (!modelCatalogueSecurityService.hasRole('CURATOR', getDataModel()) ) {
            unauthorized()
            return
        }

        if (handleReadOnly()) {
            return
        }

        T instance = queryForResource(params.id)
        if (instance == null) {
            notFound()
            return
        }

        if (!instance.instanceOf(DataModel) && !instance.dataModel) {
            instance.errors.reject 'catalogue.element.at.least.one.data.model', "'$instance.name' has to be declared wihtin a data model"
            respond instance.errors
            return
        }


        String semanticVersion = params.semanticVersion
        String revisionNotes = params.revisionNotes

        if (instance.instanceOf(DataModel)) {
            instance.checkFinalizeEligibility(semanticVersion, revisionNotes)
        }

        if (instance.hasErrors()) {
            respond instance.errors, view: 'edit' // STATUS CODE 422
            return
        }

        Long id = instance.getId()

        executorService.submit {
            try {
                CatalogueElement element = resource.get(id)
                if (element.instanceOf(DataModel)) {
                    elementService.finalizeDataModel(element as DataModel, semanticVersion, revisionNotes, BuildProgressMonitor.create("Finalizing $element", id))
                } else {
                    elementService.finalizeElement(element, BuildProgressMonitor.create("Finalizing $element", id))
                }
            } catch (e) {
                log.error "Exception finalizing element on the background", e
                CatalogueElement element = resource.get(id)
                element.status = ElementStatus.DRAFT
                element.save(flush: true)
            }

        }

        respond instance, [status: OK]
    }

    /**
     * Create a new version of a data model
     * @param id
     */
    @Secured(['ROLE_METADATA_CURATOR','ROLE_ADMIN','ROLE_SUPERVISOR'])
    @Transactional
    def newVersion() {
        if(handleReadOnly()) {
            return
        }

        T instance = queryForResource(params.id)
        if (instance == null) {
            notFound()
            return
        }

        String semanticVersion = params.semanticVersion ?: objectToBind.semanticVersion
        String preferDrafts = params.preferDrafts ?: objectToBind.preferDrafts

        Long id = instance.getId()

        executorService.submit {
            try {
                CatalogueElement element = resource.get(id)
                DraftContext context = DraftContext.userFriendly().withMonitor(BuildProgressMonitor.create("Create new version of $element", id))

                if (preferDrafts) {
                    preferDrafts.split(/\s*,\s*/).each {
                        context.preferDraftFor(Long.parseLong(it, 10))
                    }
                }

                if (element.instanceOf(DataModel)) {
                    elementService.createDraftVersion((DataModel) element, semanticVersion, context) as T
                } else {
                    elementService.createDraftVersion(element, context) as T
                }
            } catch (e) {
                log.error "Exception creating draft in the background", e
            }
        }

        respond instance, [status: OK]
    }

   /**
    * Check if a data model contains or imports another catalogue element
    * @param id of data model, other id of item to be checked
    */
    def containsOrImports() {

        DataModel dataModel = dataModelGormService.findById(params.long('id'))
        if (!dataModel) {
            notFound()
            return
        }

        CatalogueElement other = CatalogueElement.get(params.other)
        if (!other) {
            notFound()
            return
        }

        if (!other.dataModel) {
            respond(success: false, contains: false, imports: false)
            return
        }

        if (dataModel == other.dataModel) {
            respond(success: true, contains: true, imports: false)
            return
        }

        DataModelFilter filter = DataModelFilter.includes(dataModel).withImports(dataModelGormService.findAll())


        if (filter.isIncluding(other.dataModel)) {
            respond(success: true, contains: false, imports: true)
            return
        }
        respond(success: false, contains: false, imports: false)
    }

   /**
    * Redindex a data model using the elasticsearch service.
    * @param id of data model
    */
    def reindex() {
        if (handleReadOnly()) {
            return
        }

        DataModel dataModel = dataModelGormService.findById(params.long('id'))
        if (!dataModel) {
            notFound()
            return
        }

        Long id = dataModel.id

        executorService.submit {
            DataModel model = dataModelGormService.findById(id)
            modelCatalogueSearchService.index(model.declares).subscribe {
                log.info "${model} reindexed"
            }
        }

        respond(success: true)
    }


    /**
     * Get the content of a data model
     * returns all the things that should be displayed in the data model ui
     * @param id of data model
     */

    //TODO: needs work, counts etc are confusing and have been removed from the ui but they should at some point be update properly

    def content() {
        DataModel dataModel = dataModelGormService.findById(params.long('id'))
        if (!dataModel) {
            notFound()
            return
        }

        DataModelFilter filter = DataModelFilter.create(ImmutableSet.<DataModel>of(dataModel), ImmutableSet.<DataModel>of())
        Map<String, Integer> stats = dataModelService.getStatistics(filter)

        ListWithTotalAndType<DataClass> dataClasses = dataClassService.getTopLevelDataClasses(filter, [toplevel: true, status: dataModel.status != ElementStatus.DEPRECATED ? 'active' : '' ])

        ListWithTotalAndType<Map> list = Lists.lazy(params, Map) {
            List<Map> contentDescriptors = []

            contentDescriptors << createContentDescriptor(dataModel, 'Data Classes', DataClass, dataClasses.total)

            contentDescriptors << createDataElementsByTagDescriptor(dataModel)
            contentDescriptors << createContentDescriptor(dataModel, 'Data Types', DataType, Integer.MAX_VALUE)
            contentDescriptors << createContentDescriptor(dataModel, 'Measurement Units', MeasurementUnit, stats["totalMeasurementUnitCount"])
            contentDescriptors << createContentDescriptor(dataModel, 'Business Rules', ValidationRule, stats["totalValidationRuleCount"])
            contentDescriptors << createContentDescriptor(dataModel, 'Assets', Asset, stats["totalAssetCount"])
            contentDescriptors << createContentDescriptor(dataModel, 'Tags', Tag, stats["totalTagCount"])

            if (dataModel.status != ElementStatus.DEPRECATED) {
                Map deprecatedItems = createContentDescriptor(dataModel, 'Deprecated Items', CatalogueElement, stats["deprecatedCatalogueElementCount"])
                deprecatedItems.link = deprecatedItems.link.replace('status=active', 'status=deprecated')
                deprecatedItems.content.link = deprecatedItems.link
                deprecatedItems.status = 'DEPRECATED'
                contentDescriptors << deprecatedItems
            }

            contentDescriptors << createContentDescriptorForRelationship('Imported Data Models', 'imports',  dataModel, RelationshipType.importType, RelationshipDirection.OUTGOING)

            if (params.boolean('root')) {
                contentDescriptors << createVersionsDescriptor(dataModel)
            }

            contentDescriptors
        }

        respond Lists.wrap(params, "/${resourceName}/${params.id}/content", list)
    }


    /**
     * Get all the data models the import this data model
     * @param id of data model
     */

    def dependents() {
        DataModel dataModel = dataModelGormService.findById(params.long('id'))
        if (!dataModel) {
            notFound()
            return
        }

        respond dataModelService.findDependents(dataModel)
    }


    /**
     * Get the history or changes for a data model
     * @param id of data model
     */

    //TODO: this needs some work

    def history(Integer max) {
        String name = getResourceName()
        Class type = resource

        params.max = Math.min(max ?: 10, 100)
        CatalogueElement element = queryForResource(params.id)
        if (!element) {
            notFound()
            return
        }

        Long id = element.id

        if (!element.latestVersionId) {
            respond Lists.wrap(params, "/${name}/${params.id}/history", Lists.lazy(params, type, {
                [type.get(id)]
            }, { 1 }))
            return
        }

        Long latestVersionId = element.latestVersionId

        def customParams = [:]
        customParams.putAll params

        customParams.sort = 'versionNumber'
        customParams.order = 'desc'

        respond Lists.fromCriteria(customParams, type, "/${name}/${params.id}/history") {
            eq 'latestVersionId', latestVersionId
        }
    }

    /**
     * Spreadsheet report
     * @param id of data model
     */
    def inventorySpreadsheet(String name, Integer depth) {

        if (handleReadOnly()) {
            return
        }

        DataModel dataModel = dataModelGormService.findById(params.long('id'))
        if (!dataModel) {
            respond status: HttpStatus.NOT_FOUND
            return
        }
        Long dataModelId = dataModel.id


        def assetId = assetService.storeReportAsAsset(
                dataModel,
                name: name ? name : "${dataModel.name} report as MS Excel Document",
                originalFileName: "${dataModel.name}-${dataModel.status}-${dataModel.version}.xlsx",
                contentType: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        ) { OutputStream outputStream ->
            // reload domain class as this is called in separate thread
            CatalogueElementToXlsxExporter.forDataModel(dataModelGormService.findById(dataModelId), dataClassService, grailsApplication, depth).export(outputStream)
        }

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
        return
    }

    /**
     * Grid Spreadsheet report
     * @param id of data model
     */
    def gridSpreadsheet(String name, Integer depth) {
        if (handleReadOnly()) {
            return
        }

        DataModel dataModel = dataModelGormService.findById(params.long('id'))

        Long dataModelId = dataModel.id

        if (!dataModel) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        def assetId = assetService.storeReportAsAsset(
                dataModel,
                name: name ? name : "${dataModel.name} report as MS Excel Document",
                originalFileName: "${dataModel.name}-${dataModel.status}-${dataModel.version}.xlsx",
                contentType: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        ) { OutputStream outputStream ->
            // reload domain class as this is called in separate thread
           // GridReportXlsxExporter.create(DataModel.get(dataModelId), dataClassService, grailsApplication, depth).export(outputStream)
            GMCGridReportXlsxExporter.create(dataModelGormService.findById(dataModelId), dataClassService, grailsApplication, depth).export(outputStream)

        }

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
        return
    }

    /**
     * Export using "ExcelExporter" to file with suffix .mc.xlsx
     * @param name
     * @param depth
     * @return
     */
    def excelExporterSpreadsheet(String name, Integer depth) {

        if (handleReadOnly()) {
            return
        }

        DataModel dataModel = dataModelGormService.findById(params.long('id'))

        Long dataModelId = dataModel.id

        if (!dataModel) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        def assetId = assetService.storeReportAsAsset(
                dataModel,
                name: name ? name : "${dataModel.name} report as MS Excel Document",
                originalFileName: "${dataModel.name}-${dataModel.status}-${dataModel.version}.mc.xlsx",
                contentType: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        ) { OutputStream outputStream ->
            // reload domain class as this is called in separate thread
            ExcelExporter.create(dataModelGormService.findById(dataModelId), dataClassService, grailsApplication, depth).export(outputStream)
        }

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
        return
    }


    /**
     * Word Document Report
     * @param id of data model
     */
    def inventoryDoc(String name, Integer depth) {
        if (handleReadOnly()) {
            return
        }

        DataModel dataModel = dataModelGormService.findById(params.long('id'))
        if (!dataModel) {
            respond status: HttpStatus.NOT_FOUND
            return
        }
        Long modelId = dataModel.id

        def assetId =  assetService.storeReportAsAsset(
                dataModel,
                name: name ? name : "${dataModel.name} report as MS Excel Document",
                originalFileName: "${dataModel.name}-${dataModel.status}-${dataModel.version}.docx",
                contentType: "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        ) { OutputStream outputStream ->
            new DataModelToDocxExporter(dataModelGormService.findById(modelId), dataClassService, elementService, customTemplate, DOC_IMAGE_PATH, depth).export(outputStream)
        }

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
        return
    }

//TODO: DOCUMENT
    /**
     * not sure what this does
     * @param id of data model
     */
    private static Map createDataElementsByTagDescriptor(DataModel dataModel) {
        String link = "/tag/forDataModel/${dataModel.getId()}?status=${dataModel.status != ElementStatus.DEPRECATED ? 'active' : ''}"
        Map ret = [:]
        ret.id = 'forDataModel'
        ret.dataModels = [CatalogueElementMarshaller.minimalCatalogueElementJSON(dataModel)]
        ret.elementType = DataElement.name
        ret.name = 'Data Elements'
        ret.content = [count: DataModelService.allTags(dataModel).size() + 2, itemType: Tag.name, link: link]
        ret.link = link
        ret.resource = GrailsNameUtils.getPropertyName(DataElement)
        ret.status = dataModel.status.toString()
        ret
    }


    //TODO: DOCUMENT
    /**
     * not sure what this does
     * @param id of data model
     */
    private static Map createContentDescriptor(DataModel dataModel, String name, Class clazz, long count) {
        String link = "/${GrailsNameUtils.getPropertyName(clazz)}?toplevel=true&dataModel=${dataModel.getId()}&status=${dataModel.status != ElementStatus.DEPRECATED ? 'active' : ''}"
        Map ret = [:]
        ret.id = 'all'
        ret.dataModels = [CatalogueElementMarshaller.minimalCatalogueElementJSON(dataModel)]
        ret.elementType = clazz.name
        ret.name = name
        ret.content = [count: count, itemType: clazz.name, link: link]
        ret.link = link
        ret.resource = GrailsNameUtils.getPropertyName(clazz)
		ret.status = dataModel.status.toString()
        ret
    }


    //TODO: DOCUMENT
    /**
     * not sure what this does
     * @param id of data model
     */
    private static Map createVersionsDescriptor(DataModel dataModel) {
        String link = "/dataModel/${dataModel.getId()}/history"
        Map ret = [:]
        ret.id = link
        ret.dataModels = [CatalogueElementMarshaller.minimalCatalogueElementJSON(dataModel)]
        ret.elementType = "${DataModel.name}.Versions"
        ret.name = 'Versions'
        ret.content = [count: dataModel.countVersions(), itemType: DataModel.name, link: link]
        ret.link = link
		ret.status = dataModel.status.toString()
        ret
    }

    //TODO: DOCUMENT
    /**
     * not sure what this does
     * @param id of data model
     */
	private static Map createContentDescriptorForRelationship(String name, String property, DataModel dataModel, RelationshipType relationshipType, RelationshipDirection direction) {
		String link = "/dataModel/${dataModel.getId()}/${direction.actionName}/${relationshipType.name}"
		Map ret = [:]
		ret.id = link
		ret.dataModels = [CatalogueElementMarshaller.minimalCatalogueElementJSON(dataModel)]
		ret.elementType = Relationships.name
		ret.name = name
		ret.content = [count: dataModel.countRelationshipsByDirectionAndType(direction, relationshipType), itemType: Relationship.name, link: link]
		ret.link = link
        ret.relationshipType = relationshipType
        ret.direction = direction.actionName
		ret.status = dataModel.status.toString()
        ret.element = dataModel
        ret.property = property

		ret
	}


    //TODO: why is this here?
    /**
     * not sure what this does
     * @param id of data model
     */
	@Override
	protected boolean hasUniqueName() {
		true
	}

    //TODO: why is this here?
    /**
     * not sure what this does
     * @param id of data model
     */
    @Override
    protected boolean isFavoriteAfterUpdate() {
        return true
    }

    //TODO: why is this here?
    /**
     * not sure what this does
     * @param id of data model
     */

    protected bindRelations(DataModel instance, boolean newVersion, Object objectToBind) {

        if (!allowSaveAndEdit()) {
            unauthorized()
            return
        }
        if (handleReadOnly()) {
            return
        }

		if (objectToBind.declares != null) {
			for (domain in instance.declares.findAll { !(it.id in objectToBind.declares*.id) }) {
				domain.dataModel = null
                FriendlyErrors.failFriendlySave(domain)
			}
			for (domain in objectToBind.declares) {
				CatalogueElement catalogueElement = CatalogueElement.get(domain.id as Long)
                catalogueElement.dataModel = instance
                FriendlyErrors.failFriendlySave(catalogueElement)
			}
		}

        if (objectToBind.policies != null) {
            Set<DataModelPolicy> policies = objectToBind.policies.collect { DataModelPolicy.get(it.id) } as Set<DataModelPolicy>
            Set<DataModelPolicy> existing = instance.policies ?: Collections.emptySet()
            (policies - existing).each {
                instance.addToPolicies(it)
            }
            (existing - policies).each {
                instance.removeFromPolicies(it)
            }
            FriendlyErrors.failFriendlySave(instance)
        }

    }

    /**
     * not sure what this does - think it collects the fields used in the json?
     * @param id of data model
     */

	@Override
	protected getIncludeFields() {
		def fields = super.includeFields
		fields.removeAll(['declares'])
		fields
	}

    //TODO: why is this here?
    /**
     * not sure what this does
     * @param id of data model
     */

	@Override
	protected DataModel createResource() {
		DataModel instance = resource.newInstance()
		bindData instance, getObjectToBind(), [include: includeFields]
		instance
	}

    //TODO: why is this here?
    /**
     * not sure what this does
     * @param id of data model
     */
    protected String getHistoryOrderDirection() {
        'asc'
    }

    //TODO: why is this here?
    /**
     * not sure what this does
     * @param id of data model
     */
    protected String getHistorySortProperty() {
        'semanticVersion'
    }


    /**
     * override the abstract controller method so all effective items for a user is a list of data models based on the general role - rather than a specific data model role
     * i.e. we can view the basic info of data models that we aren't subscribed to
     * @param id of data model
     */

    @Override
    protected ListWrapper<T> getAllEffectiveItems(Integer max) {
        //if you only want the active data models (draft and finalised)
        if (params.status?.toLowerCase() == 'active') {
            //if you have the role viewer you can see drafts
            if (modelCatalogueSecurityService.hasRole('VIEWER')){
                return dataModelService.classified(withAdditionalIndexCriteria(Lists.fromCriteria(params, resource, "/${resourceName}/") {
                    'in' 'status', [ElementStatus.FINALIZED, ElementStatus.DRAFT, ElementStatus.PENDING]
                }), overridableDataModelFilter)
            }
            //if not you can only see finalised models
            return dataModelService.classified(withAdditionalIndexCriteria(Lists.fromCriteria(params, resource, "/${resourceName}/") {
                'eq' 'status', ElementStatus.FINALIZED
            }), overridableDataModelFilter)
        }

        //if you want models with a specific status
        //check that you can access drafts i.e. you have a viewer role
        //then return the models by the status - providing you have the correct role
        if (params.status) {
            return dataModelService.classified(withAdditionalIndexCriteria(Lists.fromCriteria(params, resource, "/${resourceName}/") {
                'in' 'status', ElementService.getStatusFromParams(params, modelCatalogueSecurityService.hasRole('VIEWER'))
            }), overridableDataModelFilter)
        }

        return dataModelService.classified(withAdditionalIndexCriteria(Lists.all(params, resource, "/${resourceName}/")), overridableDataModelFilter)
    }

}
