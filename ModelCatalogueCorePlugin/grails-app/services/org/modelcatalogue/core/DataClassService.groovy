package org.modelcatalogue.core

import grails.transaction.Transactional
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.ListCountAndType
import org.modelcatalogue.core.util.ListWithTotalAndType
import org.modelcatalogue.core.util.Lists

@Transactional
class DataClassService {

    SecurityService modelCatalogueSecurityService
    DataModelService dataModelService

    ListWithTotalAndType<DataClass> getTopLevelDataClasses(Map params) {
        getTopLevelDataClasses(dataModelService.dataModelFilter, params)
    }

    ListWithTotalAndType<DataClass> getTopLevelDataClasses(DataModelFilter dataModelFilter, Map params) {
        if (dataModelFilter.unclassifiedOnly) {
            return getTopLevelDataClasses(DataModelFilter.excludes(DataModel.list()), params)
        }

        RelationshipType hierarchy      = RelationshipType.hierarchyType
        ElementStatus status            = ElementService.getStatusFromParams(params)
        RelationshipType declaration = RelationshipType.declarationType


        if (dataModelFilter.unclassifiedOnly) {
            // language=HQL
            return Lists.fromQuery(params, DataClass, """
                select distinct m
                from DataClass as m
                where m.status = :status
                    and m.id not in (
                        select distinct r.destination.id
                        from Relationship r
                        where r.relationshipType = :type
                        and r.source.id not in (
                            select distinct r.destination.id
                            from Relationship r
                            where r.relationshipType = :declarationType
                            and r.source.id in (select m.id from DataModel)
                        )
                    )
                    and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :declarationType and r.source.id in (select m.id from DataModel))
                group by m.name, m.id
                order by m.name
            ""","""
                select count(m.id)
                from DataClass as m
                where m.status = :status
                    and m.id not in (
                        select distinct r.destination.id
                        from Relationship r
                        where r.relationshipType = :type
                        and r.source.id not in (
                            select distinct r.destination.id
                            from Relationship r
                            where r.relationshipType = :declarationType
                            and r.source.id in (select m.id from DataModel)
                        )
                    )
                    and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :declarationType and r.source.id in (select m.id from DataModel))
            """, [type: hierarchy, status: status, declarationType: declaration ])
        }

        if (dataModelFilter.excludes && !dataModelFilter.includes) {
            // language=HQL
            return Lists.fromQuery(params, DataClass, """
                select distinct m
                from DataClass as m
                where m.status = :status
                    and m.id not in (
                        select distinct r.destination.id
                        from Relationship r
                        where r.relationshipType = :type
                        and r.source.id not in (
                            select distinct r.destination.id
                            from Relationship r
                            where r.relationshipType = :declarationType
                            and r.source.id in (:dataModels)
                        )
                    )
                    and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :declarationType and r.source.id in (:dataModels))
                group by m.name, m.id
                order by m.name
            ""","""
                select count(m.id)
                from DataClass as m
                where m.status = :status
                    and m.id not in (
                        select distinct r.destination.id
                        from Relationship r
                        where r.relationshipType = :type
                        and r.source.id not in (
                            select distinct r.destination.id
                            from Relationship r
                            where r.relationshipType = :declarationType
                            and r.source.id in (:dataModels)
                        )
                    )
                    and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :declarationType and r.source.id in (:dataModels))
            """, [type: hierarchy, status: status, dataModels: dataModelFilter.excludes, declarationType: declaration ])
        }
        if (dataModelFilter.excludes && dataModelFilter.includes) {
            // language=HQL
            return Lists.fromQuery(params, DataClass, """
                select distinct m
                from DataClass as m
                where m.status = :status
                    and m.id not in (
                        select distinct r.destination.id
                        from Relationship r
                        where r.relationshipType = :type
                        and r.source.id in (
                            select distinct d.destination.id
                            from Relationship d
                            where d.relationshipType = :declarationType
                            and d.source.id in (:includes)
                        )
                        and r.source.id not in (
                            select distinct r.destination.id
                            from Relationship r
                            where r.relationshipType = :declarationType
                            and r.source.id in (:excludes)
                        )
                    )
                    and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :declarationType and r.source.id in (:excludes))
                    and m.id in (select distinct r.destination.id from Relationship r where r.relationshipType = :declarationType and r.source.id in (:includes))
                group by m.name, m.id
                order by m.name
            ""","""
                select count(m.id)
                from DataClass as m
                where m.status = :status
                    and m.id not in (
                        select distinct r.destination.id
                        from Relationship r
                        where r.relationshipType = :type
                        and r.source.id in (
                            select distinct d.destination.id
                            from Relationship d
                            where d.relationshipType = :declarationType
                            and d.source.id in (:includes)
                        )
                        and r.source.id not in (
                            select distinct r.destination.id
                            from Relationship r
                            where r.relationshipType = :declarationType
                            and r.source.id in (:excludes)
                        )
                    )
                    and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :declarationType and r.source.id in (:excludes))
                    and m.id in (select distinct r.destination.id from Relationship r where r.relationshipType = :declarationType and r.source.id in (:includes))
            """, [type: hierarchy, status: status, includes: dataModelFilter.includes, excludes: dataModelFilter.excludes, declarationType: declaration ])
        }
        if (dataModelFilter.includes && !dataModelFilter.excludes) {
            // language=HQL
            return Lists.fromQuery(params, DataClass, """
                select distinct m
                from DataClass as m join m.incomingRelationships as rel
                where m.status = :status
                    and m.id not in (
                        select distinct r.destination.id
                        from Relationship r
                        where r.relationshipType = :type
                        and r.source.id in (
                            select distinct d.destination.id
                            from Relationship d
                            where d.relationshipType = :declarationType
                            and d.source.id in (:dataModels)
                        )
                    )
                    and rel.source.id in (:dataModels)
                    and rel.relationshipType = :declarationType
                group by m.name, m.id
                order by m.name
            ""","""
                select count(m.id)
                from DataClass as m join m.incomingRelationships as rel
                where m.status = :status
                    and m.id not in (
                        select distinct r.destination.id
                        from Relationship r
                        where r.relationshipType = :type
                        and r.source.id in (
                            select distinct d.destination.id
                            from Relationship d
                            where d.relationshipType = :declarationType
                            and d.source.id in (:dataModels)
                        )
                    )
                    and rel.source.id in (:dataModels)
                    and rel.relationshipType = :declarationType
            """, [type: hierarchy, status: status, dataModels: dataModelFilter.includes, declarationType: declaration ])
        }
        // language=HQL
        Lists.fromQuery params, DataClass, """
            select distinct m
            from DataClass m
            where m.status = :status and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :type)
            group by m.name, m.id
            order by m.name
        ""","""
            select count(m.id)
            from DataClass m
            where m.status = :status and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :type)
        """, [type: hierarchy, status: status]
    }

    ListWithTotalAndType<DataClass> getInnerClasses(DataClass model) {
        List<DataClass> models = listChildren(model)
        new ListCountAndType<DataClass>(count: models.size(), list: models, itemType: DataClass)

    }

    ListWithTotalAndType<DataElement> getDataElementsFromClasses(List<DataClass> models){
        def results = []
        models.each{ model ->
            results.addAll(model.contains)
        }
        new ListCountAndType<DataElement>(count: results.size(), list: results, itemType: DataElement)
    }


    protected List<DataClass> listChildren(DataClass model, results = []){
            if (model && !results.contains(model)) {
                    results += model
                    model.parentOf?.each { child ->
                        results += listChildren(child, results)
                    }
            }
            results.unique()
    }


}
