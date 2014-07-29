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
            where m.status = :status and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :type)
            group by m.name
            order by m.name
        """, [type: hierarchy, status: status], params)

        Long count = Model.executeQuery("""
            select count(m.id)
            from Model m
            where m.status = :status and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :type)
        """, [type: hierarchy, status: status])[0]

        new ListAndCount(count: count, list: list)
    }

    ListAndCount getSubModels(Model model) {

        List<Model> models = listChildren(model)
        new ListAndCount(count: models.size(), list: models)

    }

    ListAndCount getDataElementsFromModels(List<Model> models){
        def results = []
        models.each{ model ->
            results.addAll(model.contains)
        }
        new ListAndCount(count: results.size(), list: results)
    }


    protected List<Model> listChildren(Model model, results = []){
            if (model && !results.contains(model)) {
                    results += model
                    model.parentOf?.each { child ->
                        results += listChildren(child, results)
                    }
            }
            results.unique()
    }


}
