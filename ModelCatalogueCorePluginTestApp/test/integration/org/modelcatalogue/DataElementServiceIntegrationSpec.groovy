package org.modelcatalogue

import org.hibernate.SQLQuery
import org.hibernate.Session
import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import spock.lang.Ignore

/**
 * Testing data element queries with LOINC
 * Created by james on 22/12/2017.
 */
class DataElementServiceIntegrationSpec extends AbstractIntegrationSpec {

    Long loincModelId = 76530
    Closure setModelIdClosure(Long modelId) {
        return {
            setLong('modelId', modelId)
        } as Closure
    }
    @Ignore
    def "Test SQLQuery"() {
        Session session = sessionFactory.currentSession
        //SELECT COUNT(DISTINCT ce.id, ce.name, de.data_type_id) FROM catalogue_element ce
        // SELECT DISTINCT ce.*, de.data_type_id FROM catalogue_element ce
        SQLQuery sqlQuery = session.createSQLQuery(
                "SELECT DISTINCT ce.*, de.data_type_id" +
                // "SELECT COUNT(DISTINCT ce.id, ce.name, de.data_type_id) " +
                """
                    FROM catalogue_element ce
                    JOIN data_element de on ce.id = de.id
                    WHERE
                    ce.data_model_id = :modelId  
                    ORDER BY ce.name
                """)//.addEntity(DataElement)
        // get ALL data elements in LOINC works WITHOUT addEntity(DataElement). Fails with addEntity.
        // maybe it's just too much data. When you create the entity. Whereas returning raw values is fine.

        sqlQuery.with(setModelIdClosure(modelId))

        // if doing count
        Long total = sqlQuery.list()[0]
        println sqlQuery.list()[0]

        // if returning elements
//        sqlQuery.setMaxResults(20)
//        List results = sqlQuery.list()
//        Long total = results.size()
//        println (results[0])
        expect:
        assert total == 0


        where:
        modelId << [loincModelId]
    }

    @Ignore
    def "Test HQL"() {
        List results = DataElement.executeQuery("""
            select count(*) from DataElement de
            where de.dataModel=:dataModel
            order by de.name           
            """,
        [dataModel: DataModel.findById(dataModelId),
        hierarchytypeId: 4,
        containmentTypeId: 1])

                """
        or find_in_set(de.id, GetAllDestinations(GetTopLevelDataClasses(:modelId, :hierarchytypeId), :hierarchytypeId, :containmentTypeId))""" // Stored functions won't work.

        Long total = results[0] //results.size()
        println total
        expect:
        assert total == 0


        where:
        dataModelId << [loincModelId]
    }

}
