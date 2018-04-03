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

        newMetadata.each { key, value ->
            // fill value first as key might disable both input
            parent.find('.soe-table-property-row:last-child .soe-table-property-value input').value(value?.toString() ?: '')
            parent.find('.soe-table-property-row:last-child .soe-table-property-key input').value(key?.toString() ?: '')
            parent.find('.soe-table-property-row:last-child .soe-table-property-actions .soe-add-row').first().click()
        }
    }
}
