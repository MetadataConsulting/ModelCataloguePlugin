package org.modelcatalogue.core

import grails.transaction.Transactional
import org.modelcatalogue.core.util.ListAndCount

@Transactional
class ModelService {

    ListAndCount getTopLevelModels(Map params) {
        RelationshipType hierarchy      = RelationshipType.hierarchyType
        PublishedElementStatus status   = PublishedElementService.getStatusFromParams(params)


        List<Model> list = Model.executeQuery("""
            select distinct m
            from Model m
            where m.status = :status and m.id not in (select distinct r.source.id from Relationship r where r.relationshipType = :type)
        """, [type: hierarchy, status: status], params)

        Long count = Model.executeQuery("""
            select count(m.id)
            from Model m
            where m.status = :status and m.id not in (select distinct r.source.id from Relationship r where r.relationshipType = :type)
        """, [type: hierarchy, status: status])[0]

        new ListAndCount(count: count, list: list)
    }

}
