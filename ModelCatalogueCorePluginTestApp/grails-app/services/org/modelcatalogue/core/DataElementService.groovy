package org.modelcatalogue.core

import org.hibernate.SQLQuery
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists

class DataElementService {

    static transactional = false

    def sessionFactory

    ListWithTotalAndType<DataElement> findAllDataElementsInModel(Map params, DataModel model){

        long modelId = model.id
        long hierarchyType = RelationshipType.hierarchyType.id
        long containmentType = RelationshipType.containmentType.id
        long tagTypeId = RelationshipType.tagType.id

        def tagId = params.tag

        // if tagId is null, run the same query as it is at the moment
        // if tagId in ['none', 'null', 'undefined'] then search for every data element not destination of tag relationship
        // if tagId search for every data element which has relationship with given tag (given tag is a source, data element is destination)

        if (!tagId) {
            return  buildDataElementsList(params,  """
                    SELECT DISTINCT ce.*, de.data_type_id FROM catalogue_element ce
                    JOIN data_element de on ce.id = de.id
                    LEFT JOIN catalogue_element dm on ce.data_model_id = dm.id
                    WHERE
                    ce.data_model_id = :modelId
                """) {
                        setLong('modelId', modelId)
                    }
        }

        if (tagId in ['none', 'null', 'undefined']) {
            return buildDataElementsList(params,  """
                SELECT DISTINCT ce.*, de.data_type_id FROM catalogue_element ce
                  JOIN data_element de on ce.id = de.id
                  LEFT JOIN catalogue_element dm on ce.data_model_id = dm.id
                  LEFT JOIN relationship rel on rel.destination_id = de.id and rel.relationship_type_id = :tagTypeId
                WHERE
                  (ce.data_model_id = :modelId
                  or find_in_set(de.id, GetAllDestinations(GetTopLevelDataClasses(:modelId, :hierarchytypeId), :hierarchytypeId, :containmentTypeId)))
                  AND rel.id is null
                ORDER BY ce.name
            """) {
                    setLong('modelId', modelId)
                    setLong('hierarchytypeId', hierarchyType)
                    setLong('containmentTypeId', containmentType)
                    setLong('tagTypeId', tagTypeId)
                }
        }


        buildDataElementsList(params,  """
            SELECT DISTINCT ce.*, de.data_type_id FROM catalogue_element ce
              JOIN data_element de on ce.id = de.id
              LEFT JOIN catalogue_element dm on ce.data_model_id = dm.id
            WHERE
              (ce.data_model_id = :modelId
              or find_in_set(de.id, GetAllDestinations(GetTopLevelDataClasses(:modelId, :hierarchytypeId), :hierarchytypeId, :containmentTypeId)))
              AND de.id in (select destination_id from relationship where relationship_type_id = :tagTypeId and source_id = :tagId)
            ORDER BY ce.name
        """) {
            setLong('modelId', modelId)
            setLong('hierarchytypeId', hierarchyType)
            setLong('containmentTypeId', containmentType)
            setLong('tagTypeId', tagTypeId)
            setLong('tagId', tagId as Long)
        }
    }

    private ListWithTotalAndType<DataElement>  buildDataElementsList(Map params, String query, @DelegatesTo(SQLQuery) Closure closure) {
        Closure queryClosure = {
            def max = it?.max
            def offset = it?.offset

            final session = sessionFactory.currentSession

            // Create native SQL query.
            final sqlQuery = session.createSQLQuery(query)

            // Use Groovy with() method to invoke multiple methods
            // on the sqlQuery object.
            sqlQuery.addEntity(DataElement)

            if (max) {
                sqlQuery.setMaxResults(max as Integer)
            }

            if (offset) {
                sqlQuery.setFirstResult(offset as Integer)
            }

            sqlQuery.with closure

            // Get all results.
            if (it instanceof Map) {
                return sqlQuery.list()
            }
            return sqlQuery.list().size()
        }

        Lists.lazy(params, DataElement, queryClosure, queryClosure)
    }


}
