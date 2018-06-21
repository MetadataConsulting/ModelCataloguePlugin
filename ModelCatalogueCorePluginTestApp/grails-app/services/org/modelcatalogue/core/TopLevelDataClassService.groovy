package org.modelcatalogue.core

import grails.transaction.Transactional
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.lists.ListWithTotalAndType


@Transactional
/**
 * Service dealing with marking Data Classes as TopLevel, so that we don't have to do the negative query "Find all Data Classes in this Data Model that DON'T have parent Data Classes".
 */
class TopLevelDataClassService {

    public static final String TOP_LEVEL_DATA_CLASS_EXTENSION_KEY = 'http://www.modelcatalogue.org/system/#top-level-data-class'
    public static final String TRUE = 'true'

    /**
     * Marks a DataClass as Top-Level
     * @param dataClass
     * @return
     */
    def markTopLevel(DataClass dataClass) {
        dataClass.ext.put(TOP_LEVEL_DATA_CLASS_EXTENSION_KEY, TRUE)
    }

    /**
     * Unmarks a DataClass as Top-Level
     * @param dataClass
     * @return
     */
    def unmarkTopLevel(DataClass dataClass) {
        dataClass.ext.remove(TOP_LEVEL_DATA_CLASS_EXTENSION_KEY)
    }

    /**
     * Retrieve all Top-Level DataClasses of a DataModel by their extension value
     * @param dataModel
     * @return
     */
    ListWithTotalAndType<DataClass> getTopLevelDataClasses(DataModelFilter dataModelFilter,
                                                           Map params = [:],
                                                           Boolean canViewDraftsParam = null) {
        // TODO: Implement similarly to DataClassService's method, only using extension value to search.
        // TODO: Use this method when D3 Basic Data Model View requests data model children
        // TODO: Use this method when Angular treeview requests top level data classes
        throw new Exception("Method not implemented!")
    }

    /**
     * For any DataModel in the catalogue that doesn't have data classes marked top-level, find top-level data classes with the old method, and then mark them.
     */
    def calculateAndMarkTopLevelDataClasses() {

        // TODO: Implement
        // TODO: Put this in Bootstrap
        throw new Exception("Method not implemented!")
    }


}
