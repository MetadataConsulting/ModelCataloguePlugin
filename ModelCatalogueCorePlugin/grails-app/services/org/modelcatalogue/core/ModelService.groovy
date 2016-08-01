package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.ClassificationFilter
import org.modelcatalogue.core.util.ListCountAndType
import org.modelcatalogue.core.util.ListWithTotalAndType
import org.modelcatalogue.core.util.Lists

@Transactional
class ModelService {

    SecurityService modelCatalogueSecurityService
    ClassificationService classificationService

    ListWithTotalAndType<Model> getTopLevelModels(Map params) {
        getTopLevelModels(classificationService.classificationsInUse, params)
    }

    ListWithTotalAndType<Model> getTopLevelModels(ClassificationFilter classifications, Map params) {
        RelationshipType hierarchy      = RelationshipType.hierarchyType
        ElementStatus status            = ElementService.getStatusFromParams(params)
        RelationshipType classification = RelationshipType.classificationType

        DetachedCriteria<Model> criteria = new DetachedCriteria<Model>(Model)




        if (classifications.unclassifiedOnly) {
            // language=HQL
            return Lists.fromQuery(params, Model, """
                select distinct m
                from Model as m left join m.incomingRelationships as rel
                where m.status = :status
                    and (
                        (
                            m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :type)
                            and rel.relationshipType != :classificationType
                        )
                        or m.incomingRelationships is empty
                     )
                group by m.name, m.id
                order by m.name
            ""","""
                select count(m.id)
                from Model as m left join m.incomingRelationships as rel
                where m.status = :status
                    and (
                        (
                            m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :type)
                            and rel.relationshipType != :classificationType
                        )
                        or m.incomingRelationships is empty
                     )
            """, [type: hierarchy, status: status, classificationType: classification ])
        }
        if (classifications.excludes && !classifications.includes) {
            // language=HQL
            return Lists.fromQuery(params, Model, """
                select distinct m
                from Model as m
                where m.status = :status
                    and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :type)
                    and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :classificationType and r.source.id in (:classifications))
                group by m.name, m.id
                order by m.name
            ""","""
                select count(m.id)
                from Model as m
                where m.status = :status
                    and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :type)
                    and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :classificationType and r.source.id in (:classifications))
            """, [type: hierarchy, status: status, classifications: classifications.excludes, classificationType: classification ])
        }
        if (classifications.excludes && classifications.includes) {
            // language=HQL
            return Lists.fromQuery(params, Model, """
                select distinct m
                from Model as m
                where m.status = :status
                    and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :type)
                    and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :classificationType and r.source.id in (:excludes))
                    and m.id in (select distinct r.destination.id from Relationship r where r.relationshipType = :classificationType and r.source.id in (:includes))
                group by m.name, m.id
                order by m.name
            ""","""
                select count(m.id)
                from Model as m
                where m.status = :status
                    and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :type)
                    and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :classificationType and r.source.id in (:excludes))
                    and m.id in (select distinct r.destination.id from Relationship r where r.relationshipType = :classificationType and r.source.id in (:includes))
            """, [type: hierarchy, status: status, includes: classifications.includes, excludes: classifications.excludes, classificationType: classification ])
        }
        if (classifications.includes && !classifications.excludes) {
            // language=HQL
            return Lists.fromQuery(params, Model, """
                select distinct m
                from Model as m join m.incomingRelationships as rel
                where m.status = :status
                    and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :type)
                    and rel.source.id in (:classifications)
                    and rel.relationshipType = :classificationType
                group by m.name, m.id
                order by m.name
            ""","""
                select count(m.id)
                from Model as m join m.incomingRelationships as rel
                where m.status = :status
                    and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :type)
                    and rel.source.id in (:classifications)
                    and rel.relationshipType = :classificationType
            """, [type: hierarchy, status: status, classifications: classifications.includes, classificationType: classification ])
        }
        // language=HQL
        Lists.fromQuery params, Model, """
            select distinct m
            from Model m
            where m.status = :status and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :type)
            group by m.name, m.id
            order by m.name
        ""","""
            select count(m.id)
            from Model m
            where m.status = :status and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :type)
        """, [type: hierarchy, status: status]
    }

    ListWithTotalAndType<Model> getSubModels(Model model) {
        Map<String, Model> models = collectChildren(5, model, new TreeMap<String, Model>())
        new ListCountAndType<Model>(count: models.size(), list: models.values().toList(), itemType: Model)

    }

    ListWithTotalAndType<DataElement> getDataElementsFromModels(List<Model> models){
        def results = []
        models.each{ model ->
            results.addAll(model.contains)
        }
        new ListCountAndType<DataElement>(count: results.size(), list: results, itemType: DataElement)
    }


    protected Map<String, Model> collectChildren(int maxLevel, Model model, Map<String, Model> results) {
        log.info "Collecting children for $model.name ($model.combinedVersion)"
        String key = "$model.name $model.combinedVersion".toString()
        if (model && !results.containsKey(key)) {
            results[key] = model
            if (maxLevel > 0) {
                model.parentOf?.each { Model child ->
                    collectChildren(maxLevel - 1, child, results)
                }
            } else {
                int count = model.countParentOf()
                if (count > 0) {
                    log.info "Reached max level for travelsal at $model.name ($model.combinedVersion) with $count child models left"
                }
            }

        }
        results
    }


}
