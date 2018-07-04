package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.util.Holders
import groovy.transform.CompileDynamic
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.grails.datastore.mapping.query.Query
import org.hibernate.SQLQuery
import org.modelcatalogue.core.actions.Batch
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.dataarchitect.CsvTransformation
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.security.MetadataRoles
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.persistence.UserGormService
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.HibernateHelper
import org.modelcatalogue.core.util.lists.DetachedListWithTotalAndType
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.ListWithTotalAndTypeImpl
import org.modelcatalogue.core.util.lists.ListWithTotalAndTypeWrapper
import org.modelcatalogue.core.util.lists.ListWrapper
import org.modelcatalogue.core.util.lists.Lists

import javax.annotation.PostConstruct

class DataModelService {

    static transactional = false

    def grailsApplication
    def sessionFactory
    UserGormService userGormService
    SpringSecurityService springSecurityService
    def modelCatalogueSecurityService
    DataModelGormService dataModelGormService

    private boolean legacyDataModels

    @PostConstruct
    void init() {
        this.legacyDataModels = grailsApplication.config.mc.legacy.dataModels
    }

    public Map<String, Integer> getStatisticsSql(DataModel dataModel) {
        long dataModelId = dataModel.id

        String query = '''
SELECT
	mu.cnt AS totalMeasurementUnitCount,
	r.cnt AS totalValidationRuleCount,
	a.cnt AS totalAssetCount,
	t.cnt AS totalTagCount,
	ced.cnt AS deprecatedCatalogueElementCount
FROM
	(SELECT COUNT(a.id) AS cnt FROM asset AS a JOIN catalogue_element AS ce ON a.id = ce.id AND ce.data_model_id = :data_model_id) AS a,
	(SELECT COUNT(m.id) AS cnt FROM measurement_unit AS m JOIN catalogue_element AS ce ON m.id = ce.id AND ce.data_model_id = :data_model_id) AS mu,
	(SELECT COUNT(t.id) AS cnt FROM tag AS t JOIN catalogue_element AS ce ON t.id = ce.id AND ce.data_model_id = :data_model_id) AS t,
	(SELECT COUNT(r.id) AS cnt FROM validation_rule AS r JOIN catalogue_element AS ce ON r.id = ce.id AND ce.data_model_id = :data_model_id) AS r,
	(SELECT COUNT(dep.id) AS cnt FROM catalogue_element AS dep JOIN catalogue_element AS ce ON dep.id = ce.id AND ce.data_model_id = :data_model_id WHERE dep.`status` = 'DEPRECATED') AS ced
'''
        final session = sessionFactory.currentSession
        final SQLQuery sqlQuery = session.createSQLQuery(query)
        sqlQuery.with {
            setLong('data_model_id', dataModelId)
        }
        Object[] row = sqlQuery.list().get(0)
        return [
            'totalMeasurementUnitCount':row[0] as Integer,
            'totalValidationRuleCount': row[1] as Integer,
            'totalAssetCount': row[2] as Integer,
            'totalTagCount': row[3] as Integer,
            'deprecatedCatalogueElementCount': row[4] as Integer
        ]
    }

    public Map<String, Integer> getStatistics(DataModelFilter filter) {
        def model = [:]

        List<Class> displayed = [CatalogueElement, DataModel, DataClass, DataElement, DataType, MeasurementUnit, Asset, ValidationRule, Tag]

        for (Class type in displayed) {
            DetachedCriteria criteria = classified(type, filter)
            criteria.projections {
                property 'status'
                property 'id'
            }
            criteria.inList('status', [ElementStatus.DRAFT, ElementStatus.FINALIZED, ElementStatus.DEPRECATED, ElementStatus.PENDING])


            int draft = 0
            int finalized = 0
            int deprecated = 0
            int pending = 0

            for (Object[] row in criteria.list()) {
                ElementStatus status = row[0] as ElementStatus
                if (status == ElementStatus.DRAFT) draft++
                else if (status == ElementStatus.FINALIZED) finalized++
                else if (status == ElementStatus.DEPRECATED) deprecated++
                else if (status == ElementStatus.PENDING) pending++
            }

            model["draft${type.simpleName}Count"]       = draft
            model["finalized${type.simpleName}Count"]   = finalized
            model["pending${type.simpleName}Count"]     = pending
            model["deprecated${type.simpleName}Count"]  = deprecated
            model["total${type.simpleName}Count"]       = draft + finalized + pending
        }

        model.putAll([
                activeBatchCount:Batch.countByArchived(false),
                archivedBatchCount:Batch.countByArchived(true),
                relationshipTypeCount:RelationshipType.count(),
                transformationsCount:CsvTransformation.count()
        ])
        model
    }

    public <T> ListWrapper<T> classified(ListWrapper<T> list, DataModelFilter modelFilter = dataModelFilter) {
        if (!(list instanceof ListWithTotalAndTypeWrapper)) {
            throw new IllegalArgumentException("Cannot classify list $list. Only ListWithTotalAndTypeWrapper is currently supported")
        }

        if (list.list instanceof DetachedListWithTotalAndType) {
            classified(list.list as DetachedListWithTotalAndType<T>, modelFilter)
        } else {
            throw new IllegalArgumentException("Cannot classify list $list. Only wrappers of DetachedListWithTotalAndType are supported")
        }

        return list
    }

    public <T> ListWithTotalAndType<T> classified(ListWithTotalAndType<T> list, DataModelFilter modelFilter = dataModelFilter) {
        if (!(list instanceof DetachedListWithTotalAndType)) {
            throw new IllegalArgumentException("Cannot classify list $list. Only DetachedListWithTotalAndType is currently supported")
        }

        classified(list.criteria, modelFilter)

        return list
    }

    public <T> DetachedCriteria<T> classified(DetachedCriteria<T> criteria) {
        classified(criteria, dataModelFilter)
    }

    public static <T> DetachedCriteria<T> classified(DetachedCriteria<T> criteria, DataModelFilter modelFilter) {
        if (criteria.persistentEntity.javaClass == DataModel) {
            return criteria
        }

        if (!modelFilter) {
            return criteria
        }

        if (modelFilter.unclassifiedOnly) {
            criteria.isNull('dataModel')
            return criteria
        }

        if (CatalogueElement.isAssignableFrom(criteria.persistentEntity.javaClass)) {
            if (modelFilter.includes) {
                criteria.'in' 'dataModel.id', modelFilter.includes
            } else if (modelFilter.excludes) {
                criteria.not {
                    'in' 'dataModel.id', modelFilter.excludes
                }
            }
        } else if (Relationship.isAssignableFrom(criteria.persistentEntity.javaClass)) {
            criteria.or {
                and {
                    if (modelFilter.includes) {
                        criteria.'in' 'dataModel.id', modelFilter.includes
                    } else if (modelFilter.excludes) {
                        criteria.not {
                            'in' 'dataModel.id', modelFilter.excludes
                        }
                    }
                }
                isNull('dataModel')
            }
        }
        criteria
    }

    public <T> DetachedCriteria<T> classified(Class<T> resource, DataModelFilter modelFilter = dataModelFilter) {
        classified(new DetachedCriteria<T>(resource), modelFilter)
    }

    public DataModelFilter getDataModelFilter() {
        if (!springSecurityService.isLoggedIn()) {
            return DataModelFilter.NO_FILTER
        }

        Long userId = loggedUserId()
        if (userId == null) {
            return DataModelFilter.NO_FILTER
        }
        User user = userGormService.findById(userId)
        if (user == null) {
            return DataModelFilter.NO_FILTER
        }


        DataModelFilter.from(user)
    }

    @CompileDynamic
    Long loggedUserId() {
        if ( springSecurityService.principal instanceof String ) {
            return null
        }
        springSecurityService.principal.id as Long
    }

    public boolean isLegacyDataModels() {
        return this.legacyDataModels
    }

    Set<DataModel> findDependents(DataModel dataModel) {
        Set<DataModel> dependents = new TreeSet<DataModel>({DataModel a, DataModel b -> a.name <=> b.name} as Comparator<DataModel>)

        dataModel.declares.each {
            Class type = HibernateHelper.getEntityClass(it)
            GrailsDomainClass domainClass = Holders.applicationContext.getBean(GrailsApplication).getDomainClass(type.name) as GrailsDomainClass

            for (prop in domainClass.persistentProperties) {
                if (prop.association && (prop.manyToOne || prop.oneToOne) && prop.name != 'dataModel') {
                    def value = it.getProperty(prop.name)
                    if (value instanceof CatalogueElement && value.dataModel && value.dataModel != dataModel) {
                        dependents << value.dataModel
                    }
                }
            }
        }

        dependents
    }

    static List<Tag> allTags(DataModel dataModel) {
        Relationship.where { relationshipType == RelationshipType.tagType && destination.dataModel == dataModel }.distinct('source').list().sort { a, b -> a.name <=> b.name }
    }

    List<Tag> allTagsMySQL(DataModel model){

        long modelId = model.id
        long hierarchyType = RelationshipType.hierarchyType.id
        long containmentType = RelationshipType.containmentType.id
        long tagTypeId = RelationshipType.tagType.id

        String query = """SELECT DISTINCT  ce.* from catalogue_element ce
            join tag t on t.id = ce.id
            join relationship rel on t.id = rel.source_id and rel.relationship_type_id = :tagTypeId
            join catalogue_element de on rel.destination_id = de.id
            WHERE
              de.data_model_id = :modelId
              or find_in_set(de.id, GetAllDestinations(GetTopLevelDataClasses(:modelId, :hierarchytypeId), :hierarchytypeId, :containmentTypeId))
            ORDER BY ce.name;"""

        final session = sessionFactory.currentSession

        // Create native SQL query.
        final sqlQuery = session.createSQLQuery(query)

        // Use Groovy with() method to invoke multiple methods
        // on the sqlQuery object.
        sqlQuery.with {
            // Set domain class as entity.
            // Properties in domain class id, name, level will
            // be automatically filled.
            addEntity(Tag)

            // Set value for parameter startId.
            setLong('modelId', modelId)
            setLong('hierarchytypeId', hierarchyType)
            setLong('containmentTypeId', containmentType)
            setLong('tagTypeId', tagTypeId)

            // Get all results.
            list()
        }
    }



    protected ListWrapper<DataModel> getAllEffectiveItems(Integer max, GrailsParameterMap params) {
        ListWrapper<DataModel> items = findUnfilteredEffectiveItems(max, params)
        filterUnauthorized(items)
    }

    protected ListWrapper<DataModel> findUnfilteredEffectiveItems(Integer max, GrailsParameterMap params) {
        Class resource = DataModel
        String resourceName ="dataModel"

        //if you only want the active data models (draft and finalised)
        if (params?.status?.toLowerCase() == 'active') {
            //if you have the role viewer you can see drafts
            if ( SpringSecurityUtils.ifAnyGranted(MetadataRoles.ROLE_USER) ) {
                return this.classified(withAdditionalIndexCriteria(Lists.fromCriteria(params, resource, "/${resourceName}/") {
                    'in' 'status', [ElementStatus.FINALIZED, ElementStatus.DRAFT, ElementStatus.PENDING]
                }), overridableDataModelFilter)
            }
            //if not you can only see finalised models
            return this.classified(withAdditionalIndexCriteria(Lists.fromCriteria(params, resource, "/${resourceName}/") {
                'eq' 'status', ElementStatus.FINALIZED
            }), overridableDataModelFilter)
        }

        //if you want models with a specific status
        //check that you can access drafts i.e. you have a viewer role
        //then return the models by the status - providing you have the correct role
        if (params.status) {
            return this.classified(withAdditionalIndexCriteria(Lists.fromCriteria(params, resource, "/${resourceName}/") {
                'in' 'status', ElementService.getStatusFromParams(params)
            }), overridableDataModelFilter)
        }

        return this.classified(withAdditionalIndexCriteria(Lists.all(params, resource, "/${resourceName}/")), overridableDataModelFilter)
    }


    protected ListWrapper<DataModel> filterUnauthorized(ListWrapper<DataModel> items) {
        if ( items instanceof ListWithTotalAndTypeWrapper ) {
            ListWithTotalAndTypeWrapper listWithTotalAndTypeWrapperInstance = (ListWithTotalAndTypeWrapper) items
            DetachedCriteria<DataModel> criteria = listWithTotalAndTypeWrapperInstance.list.criteria
            Map<String, Object> params = listWithTotalAndTypeWrapperInstance.list.params
            ListWithTotalAndType<DataModel> listWithTotalAndType = instantiateListWithTotalAndTypeWithCriteria(criteria, params)
            return ListWithTotalAndTypeWrapper.create(listWithTotalAndTypeWrapperInstance.params, listWithTotalAndTypeWrapperInstance.base, listWithTotalAndType)
        }
        items
    }

    protected ListWithTotalAndType<DataModel> instantiateListWithTotalAndTypeWithCriteria(DetachedCriteria<DataModel> criteria, Map<String, Object> params) {
        List<DataModel> dataModelList = dataModelGormService.findAllByCriteria(criteria)
        if ( !dataModelList ) {
            return new ListWithTotalAndTypeImpl<DataModel>(DataModel, [], 0L)
        }
        int total = dataModelList.size()
        dataModelList = MaxOffsetSublistUtils.subList(SortParamsUtils.sort(dataModelList, params), params)
        new ListWithTotalAndTypeImpl<DataModel>(DataModel, dataModelList, total as Long)
    }

    //TODO: not sure what this does
    protected <T> ListWrapper<T> withAdditionalIndexCriteria(ListWrapper<T> list) {
        if (!hasAdditionalIndexCriteria()) {
            return list
        }

        if (!(list instanceof ListWithTotalAndTypeWrapper)) {
            throw new IllegalArgumentException("Cannot add additional criteria list $list. Only ListWithTotalAndTypeWrapper is currently supported")
        }
        if (!(list.list instanceof DetachedListWithTotalAndType)) {
            throw new IllegalArgumentException("Cannot add additional criteria list $list. Only DetachedListWithTotalAndType is currently supported")
        }

        list.list.criteria.with buildAdditionalIndexCriteria()

        return list
    }

    //TODO: not sure what this does
    protected boolean hasAdditionalIndexCriteria() {
        return false
    }

    //TODO: not sure what this does
    protected Closure buildAdditionalIndexCriteria() {
        return {}
    }

    protected DataModelFilter getOverridableDataModelFilter() {
        this.dataModelFilter
    }


}
