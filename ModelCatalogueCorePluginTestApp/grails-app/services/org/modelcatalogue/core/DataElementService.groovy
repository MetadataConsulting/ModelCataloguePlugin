package org.modelcatalogue.core

import org.hibernate.SQLQuery
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.core.util.lists.MethodNotClosureLazyListWithTotalAndType

class DataElementService {

    static transactional = false

    SessionFactory sessionFactory

    ListWithTotalAndType<DataElement> findAllDataElementsInModel(Map params, DataModel model){

        long modelId = model.id
        long hierarchyType = RelationshipType.hierarchyType.id
        long containmentType = RelationshipType.containmentType.id
        long tagTypeId = RelationshipType.tagType.id

        def tagId = params.tag

        // if tagId is null, run the same query as it is at the moment
        // if tagId in ['none', 'null', 'undefined'] then search for every data element not destination of tag relationship
        // if tagId search for every data element which has relationship with given tag (given tag is a source, data element is destination)

        // variables ce, de
        String selectDEs = "SELECT DISTINCT ce.*, de.data_type_id"
        String selectCount = "SELECT COUNT(DISTINCT ce.id)" // COUNT(DISTINCT ce.id) returns same number as SELECT DISTINCT ce.*, de.data_type_id

        String fromBasic = """
                FROM catalogue_element ce
                  JOIN data_element de on ce.id = de.id
            """

        boolean findInGetAllDestinations = false
        String findInGetAllDestinationsCondition = findInGetAllDestinations ? "OR find_in_set(de.id, GetAllDestinations(GetTopLevelDataClasses(:modelId, :hierarchytypeId), :hierarchytypeId, :containmentTypeId))" : ""

        String whereBasic = "WHERE (ce.data_model_id = :modelId $findInGetAllDestinationsCondition)"

        String order = "ORDER BY ce.name"

        if (!tagId) {
            return  buildDataElementsList(params,
                    selectDEs, selectCount,
                            "${fromBasic} ${whereBasic} ${order}".toString()
                    ) {
                        setLong('modelId', modelId)
                        if (findInGetAllDestinations) {
                            setLong('hierarchytypeId', hierarchyType)
                            setLong('containmentTypeId', containmentType)
                        }
                    }
        } else if (tagId in ['none', 'null', 'undefined']) {

            // introduces variable rel
            String joinTagRel = "LEFT JOIN relationship rel on rel.destination_id = de.id and rel.relationship_type_id = :tagTypeId"
            String tagRelTagIdNullCondition = "AND rel.id is null"

            return buildDataElementsList(params,
                    selectDEs, selectCount, "${fromBasic} ${joinTagRel} ${whereBasic} ${tagRelTagIdNullCondition} ${order}".toString()
                    ) {
                    setLong('modelId', modelId)
                    if (findInGetAllDestinations) {
                        setLong('hierarchytypeId', hierarchyType)
                        setLong('containmentTypeId', containmentType)
                    }
                    setLong('tagTypeId', tagTypeId)
                }
        } else {
            String tagReltagIdNotNullCondition = "AND de.id in (select destination_id from relationship where relationship_type_id = :tagTypeId and source_id = :tagId)"
            buildDataElementsList(params,
                    selectDEs, selectCount,
                    "${fromBasic} ${whereBasic} ${tagReltagIdNotNullCondition} ${order}".toString()
            ) {
                setLong('modelId', modelId)
                if (findInGetAllDestinations) {
                    setLong('hierarchytypeId', hierarchyType)
                    setLong('containmentTypeId', containmentType)
                }
                setLong('tagTypeId', tagTypeId)
                setLong('tagId', tagId as Long)
            }
        }
    }

    /**
     *
     This is basically similar to what QueryListWithTotalAndType does.
      Except QueryListWithTotalAndType is generic and uses HQL.
      We're using SQL directly only for the stored function calls– which themselves we're not sure we need.
     */
    class QuerySetupListMethods implements MethodNotClosureLazyListWithTotalAndType.ListMethods<DataElement> {

        private String selectElements
        private String selectCount
        private String fromQuery
        private Closure querySetupClosure

        QuerySetupListMethods(String selectElements, String selectCount, String fromQuery, @DelegatesTo(SQLQuery) Closure querySetupClosure) {
            this.selectElements = selectElements
            this.selectCount = selectCount
            this.fromQuery = fromQuery
            this.querySetupClosure = querySetupClosure // For now this just passes in query parameters.
        }

        SQLQuery initSQLQuery(String query) {
            final Session session = sessionFactory.currentSession

            // Create native SQL query.
            final SQLQuery sqlQuery = session.createSQLQuery(query)

            // Use Groovy with() method to invoke multiple methods
            // on the sqlQuery object.
            sqlQuery.with querySetupClosure

            return sqlQuery
        }

        @Override
        List<DataElement> getItems(Map params) {

            SQLQuery sqlQuery = initSQLQuery(selectElements + fromQuery)

            // set max and offset
            def max = params.max
            def offset = params.offset
            if (max) {
                sqlQuery.setMaxResults(max as Integer)
            }
            if (offset) {
                sqlQuery.setFirstResult(offset as Integer)
            }

            // set to create Hibernate Entity from results
            sqlQuery.addEntity(DataElement)

            return sqlQuery.list()
        }

        @Override
        Long getTotal() {
            SQLQuery sqlQuery = initSQLQuery(selectCount + fromQuery)
            return sqlQuery.list()[0]
        }
    }

    private ListWithTotalAndType<DataElement>  buildDataElementsList(Map params, String selectElements, String selectCount, String fromQuery, @DelegatesTo(SQLQuery) Closure closure) {
        QuerySetupListMethods querySetupListMethods = new QuerySetupListMethods(selectElements, selectCount, fromQuery, closure)
        return Lists.methodNotClosureLazy(params, DataElement, querySetupListMethods)

//        Closure queryClosure = {
//            def max = it?.max
//            def offset = it?.offset
//
//            final session = sessionFactory.currentSession
//
//            // Create native SQL query.
//            final sqlQuery = session.createSQLQuery(query)
//
//            // Use Groovy with() method to invoke multiple methods
//            // on the sqlQuery object.
//            sqlQuery.addEntity(DataElement)
//
//            if (max) {
//                sqlQuery.setMaxResults(max as Integer)
//            }
//
//            if (offset) {
//                sqlQuery.setFirstResult(offset as Integer)
//            }
//
//            sqlQuery.with closure
//
//            // Get all results.
//            if (it instanceof Map) {
//                return sqlQuery.list()
//            }
//            return sqlQuery.list().size()
//        }
//
//        Lists.lazy(params, DataElement, queryClosure, queryClosure)



    }


}
