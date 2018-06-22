package org.modelcatalogue.core.geb

import geb.navigator.Navigator

trait MetadataUtils {
    /**
     * Fills the metadata with the new values
     * @param newMetadata
     */
    void fillMetadata(Map newMetadata, Navigator parent = null) {
        if (!parent) {
            parent = $('table.soe-table')
        }

        while (parent.find('.soe-table-property-row').size() > 1) {
            parent.find('.soe-table-property-row:first-child .soe-table-property-actions .soe-remove-row').first().click()
        }
        // last value might not be deleted
        parent.find('.soe-table-property-row:first-child .soe-table-property-actions .soe-remove-row').first().click()

        Set keys = newMetadata.keySet()
        for ( int i = 0; i < keys.size(); i++ ) {
            def key = keys[i]
            def value = newMetadata.get(key)
            // fill value first as key might disable both input
            parent.find('.soe-table-property-row:last-child .soe-table-property-value input').value(value?.toString() ?: '')
            parent.find('.soe-table-property-row:last-child .soe-table-property-key input').value(key?.toString() ?: '')
            if ( i != (keys.size() - 1) ) { // don't click for last item being filled
                parent.find('.soe-table-property-row:last-child .soe-table-property-actions .soe-add-row').first().click()
            }
        }
    }
}
