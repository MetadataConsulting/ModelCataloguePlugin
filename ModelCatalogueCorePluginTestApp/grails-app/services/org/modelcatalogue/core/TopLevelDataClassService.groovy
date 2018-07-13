package org.modelcatalogue.core

import com.google.common.collect.ImmutableSet
import grails.transaction.Transactional
import groovy.util.logging.Slf4j
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.persistence.DataClassGormService
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists

/**
 * Service dealing with marking Data Classes as TopLevel, so that we don't have to do the negative query "Find all Data Classes in this Data Model that DON'T have parent Data Classes".
 */
@Slf4j
class TopLevelDataClassService {

    public static final String TOP_LEVEL_DATA_CLASS_EXTENSION_KEY = 'http://www.modelcatalogue.org/system/#top-level-data-class'
    public static final String TRUE = 'true'

    DataModelGormService dataModelGormService
    DataClassGormService dataClassGormService
    DataClassService dataClassService

    /**
     * Marks a DataClass as Top-Level
     * @param dataClass
     * @return
     */
    @Transactional
    def markTopLevel(Long dataClassId) {
        if (!dataClassId) {
            log.warn("No data class id in markTopLevel")
            return
        }
        DataClass dataClass = dataClassGormService.findById(dataClassId)
        if (!dataClass) {
            log.warn('Data Class not found with id {}', dataClassId)
            return
        }
        dataClass.ext.put(TOP_LEVEL_DATA_CLASS_EXTENSION_KEY, TRUE)
    }

    /**
     * Unmarks a DataClass as Top-Level
     * @param dataClass
     * @return
     */
    @Transactional
    def unmarkTopLevel(Long dataClassId) {
        if (!dataClassId) {
            log.warn("No data class id in markTopLevel")
            return
        }
        DataClass dataClass = dataClassGormService.findById(dataClassId)
        if (!dataClass) {
            log.warn('Data Class not found with id {}', dataClassId)
            return
        }
        ExtensionValue.where {element == dataClass && name == TOP_LEVEL_DATA_CLASS_EXTENSION_KEY}.deleteAll() // somehow this worked when the others didn't in the afterInsert() clause...
//      ExtensionValue.findAllByElementAndName(dataClass, TOP_LEVEL_DATA_CLASS_EXTENSION_KEY).each {it.delete()}
//        dataClass.ext.remove(TOP_LEVEL_DATA_CLASS_EXTENSION_KEY)
    }


    /**
     * Retrieve all Top-Level DataClasses of a DataModel by their extension value, recalculating with the old query if there are none.
     * @param dataModel
     * @return
     */
    @Transactional(readOnly = true)
    ListWithTotalAndType<DataClass> getTopLevelDataClassesRecalculateIfNecessary(DataModel dataModel,
                                                           Map params = [:]) {
        DataModelFilter dataModelFilter = DataModelFilter.create(ImmutableSet.<DataModel> of(dataModel), ImmutableSet.<DataModel> of())

        ListWithTotalAndType<DataClass> dataClasses = getTopLevelDataClasses(dataModelFilter, params)

        if (dataClasses.total == 0) { // none marked as top-level, so recalculate
            calculateAndMarkTopLevelDataClassesForDataModel(dataModel)
            dataClasses = getTopLevelDataClasses(dataModelFilter, params)
            return dataClasses
        }
        else {
            return dataClasses
        }
    }

    /**
     * Retrieve all Top-Level DataClasses of a DataModel by their extension value
     * @param dataModel
     * @return
     */
    @Transactional(readOnly = true)
    ListWithTotalAndType<DataClass> getTopLevelDataClasses(DataModelFilter dataModelFilter,
                                                           Map params = [:]) {
        // TODO: Use this method when D3 Basic Data Model View requests data model children
        // TODO: Use this method when Angular treeview requests top level data classes

        List<ElementStatus> status = ElementService.getStatusFromParams(params)


        if (dataModelFilter.unclassifiedOnly) {
            // language=HQL
            return Lists.fromQuery(params, DataClass, """
                select distinct m
                
                from DataClass as m JOIN m.extensions as e
                where m.status in :status
                    and e.name = :topLevelKey
                    and e.extensionValue = :topLevelValue
                    
                    and m.dataModel is null
                    
                group by m.name, m.id
                order by m.name
            ""","""
                select count(m.id)
                
                from DataClass as m JOIN m.extensions as e
                where m.status in :status
                    and e.name = :topLevelKey
                    and e.extensionValue = :topLevelValue
                    
                    and m.dataModel is null
            """, [status: status, topLevelKey: TOP_LEVEL_DATA_CLASS_EXTENSION_KEY, topLevelValue: TRUE])
        }

        if (dataModelFilter.excludes && !dataModelFilter.includes) {
            // language=HQL
            return Lists.fromQuery(params, DataClass, """
                select distinct m
                
                from DataClass as m JOIN m.extensions as e
                where m.status in :status
                    and e.name = :topLevelKey
                    and e.extensionValue = :topLevelValue
                    
                    and m.dataModel.id not in (:dataModels) or m.dataModel is null
                    
                group by m.name, m.id
                order by m.name
            ""","""
                select count(m.id)
                
                from DataClass as m JOIN m.extensions as e
                where m.status in :status
                    and e.name = :topLevelKey
                    and e.extensionValue = :topLevelValue
                    
                    and m.dataModel.id not in (:dataModels) or m.dataModel is null
            """, [status: status, dataModels: dataModelFilter.excludes, topLevelKey: TOP_LEVEL_DATA_CLASS_EXTENSION_KEY, topLevelValue: TRUE])
        }
        if (dataModelFilter.excludes && dataModelFilter.includes) {
            throw new IllegalStateException("Combining exclusion and inclusion is no longer supported. Exclusion would be ignored!")
        }
        if (dataModelFilter.includes && !dataModelFilter.excludes) {
            // language=HQL
            return Lists.fromQuery(params, DataClass, """
                select distinct m
                
                from DataClass as m JOIN m.extensions as e
                where m.status in :status
                    and e.name = :topLevelKey
                    and e.extensionValue = :topLevelValue
                    
                    and m.dataModel.id in (:dataModels)
                    
                group by m.name, m.id
                order by m.name
            ""","""
                select count(m.id)
                
                from DataClass as m JOIN m.extensions as e
                where m.status in :status
                    and e.name = :topLevelKey
                    and e.extensionValue = :topLevelValue
                    
                    and m.dataModel.id in (:dataModels)
            """, [status: status, dataModels: dataModelFilter.includes, topLevelKey: TOP_LEVEL_DATA_CLASS_EXTENSION_KEY, topLevelValue: TRUE])
        }

        // language=HQL
        Lists.fromQuery params, DataClass, """
            select distinct m
            
            from DataClass as m JOIN m.extensions as e
                where m.status in :status
                    and e.name = :topLevelKey
                    and e.extensionValue = :topLevelValue
                    
            group by m.name, m.id
            order by m.name
        ""","""
            select count(m.id)
            
            from DataClass as m JOIN m.extensions as e
                where m.status in :status
                    and e.name = :topLevelKey
                    and e.extensionValue = :topLevelValue
        """, [status: status, topLevelKey: TOP_LEVEL_DATA_CLASS_EXTENSION_KEY, topLevelValue: TRUE]
    }


    /**
     * For any DataModel in the catalogue (optionally: that doesn't have any DataClasses marked top-level), find top-level DataClasses with the old method, and then mark them.
     * @param calculateForAll whether to calculate for all data models (true) or just those that don't have any data classes marked top level (false)
     */
    @Transactional
    def calculateAndMarkTopLevelDataClasses(boolean calculateForAll) {

        List<DataModel> dataModels = dataModelGormService.findAll()
        for (DataModel dataModel: dataModels) {
            // not sure about using DataModelGormService? It filters its results depending on the user's permission. Not sure what will happen in Bootstrap when this is run.
            if (calculateForAll) {
                calculateAndMarkTopLevelDataClassesForDataModel(dataModel)
            }
            else {
                DataModelFilter dataModelFilter = DataModelFilter.create(ImmutableSet.<DataModel> of(dataModel), ImmutableSet.<DataModel> of())
                ListWithTotalAndType<DataClass> topLevelDataClasses = getTopLevelDataClasses(dataModelFilter, [:])
                if (topLevelDataClasses.total == 0) {
                    calculateAndMarkTopLevelDataClassesForDataModel(dataModel)
                }
            }
        }
    }

    /**
     * Calculate (with the old negative query), and mark top-level DataClasses for a particular DataModel
     * @param dataModel
     */
    @Transactional
    def calculateAndMarkTopLevelDataClassesForDataModel(DataModel dataModel) {
        DataModelFilter dataModelFilter = DataModelFilter.create(ImmutableSet.<DataModel> of(dataModel), ImmutableSet.<DataModel> of())
        ListWithTotalAndType<DataClass> topLevelDataClasses = dataClassService.getTopLevelDataClasses(dataModelFilter, [:])
        for (DataClass dataClass : topLevelDataClasses.items) {
            markTopLevel(dataClass.id)
        }
        log.info "Calculated and marked top-level DataClasses for ${dataModel.toString()}"
    }


}
