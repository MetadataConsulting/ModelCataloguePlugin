package org.modelcatalogue.core.geb

import geb.Page

class DataTypePage extends Page implements InputUtils {

    static url = '/#'

    static at = { title.startsWith('History of') }

    static content = {
        enumeratedTypeDropdown {
            $('a#role_item_catalogue-element-menu-item-link')
        }
        validateValueLink { $('a#validate-value-menu-item-link') }
    }

    void enumeratedType() {
        enumeratedTypeDropdown.click()
    }

    void validateValue() {
        validateValueLink.click()
    }

    boolean isDataTypePageFor(String value) {
        $('h3.ce-name span', text: value).displayed
    }

}
