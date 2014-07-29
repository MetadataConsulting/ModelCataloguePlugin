package org.modelcatalogue.core

import grails.transaction.Transactional
import org.modelcatalogue.core.util.ListAndCount
import org.modelcatalogue.core.util.ListCountAndType
import org.modelcatalogue.core.util.ListWithTotal
import org.modelcatalogue.core.util.ListWithTotalAndType
import org.modelcatalogue.core.util.Lists
import org.modelcatalogue.core.util.QueryListWithTotalAndType

@Transactional
class ModelService {

    ListWithTotalAndType<Model> getTopLevelModels(Map params) {
        RelationshipType hierarchy      = RelationshipType.hierarchyType
        PublishedElementStatus status   = PublishedElementService.getStatusFromParams(params)

        Lists.fromQuery params, Model, """
            select distinct m
            from Model m
            where m.status = :status and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :type)
            group by m.name
            order by m.name
        ""","""
            select count(m.id)
            from Model m
            where m.status = :status and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :type)
        """, [type: hierarchy, status: status]
    }

    ListWithTotalAndType<Model> getSubModels(Model model) {
        List<Model> models = listChildren(model)
        new ListCountAndType<Model>(count: models.size(), list: models, itemType: Model)

    }

    ListWithTotalAndType<DataElement> getDataElementsFromModels(List<Model> models){
        def results = []
        models.each{ model ->
            results.addAll(model.contains)
        }
        new ListCountAndType<DataElement>(count: results.size(), list: results, itemType: DataElement)
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
