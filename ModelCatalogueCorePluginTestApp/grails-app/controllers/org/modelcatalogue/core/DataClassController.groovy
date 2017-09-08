package org.modelcatalogue.core

import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.export.inventory.DataClassToDocxExporter
import org.modelcatalogue.core.export.inventory.CatalogueElementToXlsxExporter
import org.modelcatalogue.core.export.inventory.DataModelToDocxExporter
import org.modelcatalogue.core.publishing.changelog.ChangeLogDocxGenerator
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.lists.ListWrapper
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.lists.Relationships

class DataClassController extends AbstractCatalogueElementController<DataClass> {

    PerformanceUtilService performanceUtilService

    DataClassController() {
        super(DataClass, false)
    }

    def referenceTypes(Integer max){
        handleParams(max)

        Boolean all = params.boolean('all')

        DataClass dataClass = queryForResource(params.id)
        if (!dataClass) {
            notFound()
            return
        }

        respond dataModelService.classified(Lists.fromCriteria(params, ReferenceType, "/${resourceName}/${params.id}/referenceType") {
            eq "dataClass", dataClass
            if (!all && !dataClass.attach().archived) {
                ne 'status', ElementStatus.DEPRECATED
                ne 'status', ElementStatus.UPDATED
                ne 'status', ElementStatus.REMOVED
            }
        })

    }

    def content(Integer max) {
        handleParams(max)

        params.sort = 'outgoingIndex'

        DataClass element = queryForResource(params.id)

        if (!element) {
            notFound()
            return
        }

        DataModelFilter filter = overridableDataModelFilter

        respond new Relationships(
                owner: element,
                direction: RelationshipDirection.OUTGOING,
                list: Lists.fromCriteria(params, Relationship, "/${resourceName}/${params.id}/content") {
                    join 'destination'
                    eq 'source', element
                    inList 'relationshipType', [RelationshipType.containmentType, RelationshipType.hierarchyType]

                    if (filter) {
                        or {
                            isNull 'dataModel'
                            and {
                                if (filter.excludes) {
                                    not {
                                        'in' 'dataModel.id', filter.excludes
                                    }
                                }
                                if (filter.includes) {
                                    'in'  'dataModel.id', filter.includes
                                }
                            }
                        }
                    }

                    sort('outgoingIndex')
                }
        )
    }

    def inventoryDoc(String name, Integer depth) {

        if (!allowSaveAndEdit()) {
            unauthorized()
            return
        }
        if (handleReadOnly()) {
            return
        }

        DataClass dataClass = DataClass.get(params.id)
        def assetId =  assetService.storeReportAsAsset(
                dataClass.dataModel,
                name: name ? name : "${dataClass.name} report as MS Excel Document",
                originalFileName: "${dataClass.name}-${dataClass.status}-${dataClass.version}.docx",
                contentType: "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        ) { OutputStream outputStream ->
            new DataClassToDocxExporter(dataClass, dataClassService, depth, elementService).export(outputStream)
        }

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    def inventorySpreadsheet(String name, Integer depth) {

        if (!allowSaveAndEdit()) {
            unauthorized()
            return
        }
        if (handleReadOnly()) {
            return
        }

        DataClass dataClass = DataClass.get(params.id)

        Long dataClassId = dataClass.id
        def assetId= assetService.storeReportAsAsset(
                dataClass.dataModel,
                name: name ? name : "${dataClass.name} report as MS Excel Document",
                originalFileName: "${dataClass.name}-${dataClass.status}-${dataClass.version}.xlsx",
                contentType: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )  { OutputStream out ->
            CatalogueElementToXlsxExporter.forDataClass(DataClass.get(dataClassId), dataClassService, grailsApplication, depth).export(out)
        }

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    def changelogDoc(String name, Integer depth, Boolean includeMetadata) {

        if (!allowSaveAndEdit()) {
            unauthorized()
            return
        }
        if (handleReadOnly()) {
            return
        }

        DataClass dataClass = DataClass.get(params.id)

        Long dataClassId = dataClass.id
        def assetId = assetService.storeReportAsAsset(
                dataClass.dataModel,
                name: name ? name : "${dataClass.name} changelog as MS Word Document",
                originalFileName: "${dataClass.name}-${dataClass.status}-${dataClass.version}-changelog.docx",
                contentType: "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        ) { OutputStream out ->
            new ChangeLogDocxGenerator(auditService, dataClassService, performanceUtilService , depth, includeMetadata)
                .generateChangelog(DataClass.get(dataClassId), out)
        }

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    @Override
    protected ListWrapper<DataClass> getAllEffectiveItems(Integer max) {
        if (!params.boolean("toplevel")) {
            return super.getAllEffectiveItems(max)
        }
        return Lists.wrap(params, "/${resourceName}/", dataClassService.getTopLevelDataClasses(overridableDataModelFilter, params))
    }
}
