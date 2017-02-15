package org.modelcatalogue.core

import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists

class DataElementService {

    static transactional = false

    def sessionFactory

    ListWithTotalAndType<DataElement> findAllDataElementsInModel(Map params, DataModel model){

        long modelId = model.id
        long hierarchyType = RelationshipType.hierarchyType.id
        long containmentType = RelationshipType.containmentType.id


        Lists.lazy(params, DataElement, {
            String query = """SELECT DISTINCT ce.*, de.data_type_id FROM catalogue_element ce
        JOIN data_element de on ce.id = de.id
        LEFT JOIN catalogue_element dm on ce.data_model_id = dm.id
        WHERE
        ce.data_model_id = :modelId
        or find_in_set(de.id, GetAllDestinations(GetTopLevelDataClasses(:modelId, :hierarchytypeId), :hierarchytypeId, :containmentTypeId))
        ORDER BY ce.name;"""

            final session = sessionFactory.currentSession

            // Create native SQL query.
            final sqlQuery = session.createSQLQuery(query)

            // Use Groovy with() method to invoke multiple methods
            // on the sqlQuery object.
            final results = sqlQuery.with {
                // Set domain class as entity.
                // Properties in domain class id, name, level will
                // be automatically filled.
                addEntity(DataElement)

                // Set value for parameter startId.
                setLong('modelId', modelId)
                setLong('hierarchytypeId', hierarchyType)
                setLong('containmentTypeId', containmentType)

                // Get all results.
                list()
            }

            results
        })





    }
}
