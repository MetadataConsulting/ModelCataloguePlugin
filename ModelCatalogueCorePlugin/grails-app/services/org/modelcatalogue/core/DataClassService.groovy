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
        RelationshipType hierarchy = RelationshipType.hierarchyType
        List<ElementStatus> status = ElementService.getStatusFromParams(params)


        if (dataModelFilter.unclassifiedOnly) {
            // language=HQL
            return Lists.fromQuery(params, DataClass, """
                select distinct m
                from DataClass as m
                where m.status in :status
                    and m.id not in (
                        select distinct r.destination.id
                        from Relationship r
                        where r.relationshipType = :type
                        and r.source.dataModel is null
                    )
                    and m.dataModel is null
                group by m.name, m.id
                order by m.name
            ""","""
                select count(m.id)
                from DataClass as m
                where m.status in :status
                    and m.id not in (
                        select distinct r.destination.id
                        from Relationship r
                        where r.relationshipType = :type
                        and r.source.dataModel is null
                    )
                    and m.dataModel is null
            """, [type: hierarchy, status: status])
        }

        if (dataModelFilter.excludes && !dataModelFilter.includes) {
            // language=HQL
            return Lists.fromQuery(params, DataClass, """
                select distinct m
                from DataClass as m
                where m.status in :status
                    and m.id not in (
                        select distinct r.destination.id
                        from Relationship r
                        where r.relationshipType = :type
                        and (r.source.dataModel.id not in (:dataModels) or r.source.dataModel is null)
                    )
                    and m.dataModel.id not in (:dataModels) or m.dataModel is null
                group by m.name, m.id
                order by m.name
            ""","""
                select count(m.id)
                from DataClass as m
                where m.status in :status
                    and m.id not in (
                        select distinct r.destination.id
                        from Relationship r
                        where r.relationshipType = :type
                        and (r.source.dataModel.id not in (:dataModels) or r.source.dataModel is null)
                    )
                    and m.dataModel.id not in (:dataModels) or m.dataModel is null
            """, [type: hierarchy, status: status, dataModels: dataModelFilter.excludes])
        }
        if (dataModelFilter.excludes && dataModelFilter.includes) {
            throw new IllegalStateException("Combining exclusion and inclusion is no longer supported. Exclusion would be ignored!")
        }
        if (dataModelFilter.includes && !dataModelFilter.excludes) {
            // language=HQL
            return Lists.fromQuery(params, DataClass, """
                select distinct m
                from DataClass as m
                where m.status in :status
                    and m.id not in (
                        select distinct r.destination.id
                        from Relationship r
                        where r.relationshipType = :type
                        and r.source.dataModel.id in (:dataModels)
                    )
                    and m.dataModel.id in (:dataModels)
                group by m.name, m.id
                order by m.name
            ""","""
                select count(m.id)
                from DataClass as m
                where m.status in :status
                    and m.id not in (
                        select distinct r.destination.id
                        from Relationship r
                        where r.relationshipType = :type
                        and r.source.dataModel.id in (:dataModels)
                    )
                    and m.dataModel.id in (:dataModels)
            """, [type: hierarchy, status: status, dataModels: dataModelFilter.includes])
        }
        // language=HQL
        Lists.fromQuery params, DataClass, """
            select distinct m
            from DataClass m
            where m.status in :status and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :type)
            group by m.name, m.id
            order by m.name
        ""","""
            select count(m.id)
            from DataClass m
            where m.status in :status and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :type)
        """, [type: hierarchy, status: status]
    }

    ListWithTotalAndType<DataClass> getInnerClasses(DataClass dataClass) {
        Map<String, DataClass> dataClasses = collectChildren(3, dataClass, new TreeMap<String, DataClass>())
        new ListCountAndType<DataClass>(count: dataClasses.size(), list: dataClasses.values().toList(), itemType: DataClass)

    }

    ListWithTotalAndType<DataElement> getDataElementsFromClasses(List<DataClass> models){
        def results = []
        models.each{ model ->
            results.addAll(model.contains)
        }
        new ListCountAndType<DataElement>(count: results.size(), list: results, itemType: DataElement)
    }



    protected Map<String, DataClass> collectChildren(int maxLevel, DataClass dataClass, Map<String, DataClass> results) {
        log.info "Collecting inner classes for $dataClass.name ($dataClass.combinedVersion)"
        String key = "$dataClass.name $dataClass.combinedVersion".toString()
        if (dataClass && !results.containsKey(key)) {
            results[key] = dataClass
            if (maxLevel > 0) {
                dataClass.parentOf?.each { DataClass child ->
                    collectChildren(maxLevel - 1, child, results)
                }
            } else {
                int count = dataClass.countParentOf()
                if (count > 0) {
                    log.info "Reached max level for travelsal at $dataClass.name ($dataClass.combinedVersion) with $count inner data classes left"
                }
            }

        }
        results
    }

}
