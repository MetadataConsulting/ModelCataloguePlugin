package org.modelcatalogue.core.geb

import geb.Page

/**
 * Describes the new "BasicDataModelView"
 */
class BasicDataModelViewPage extends Page {

    static at = {
        title.startsWith('Basic Data Model View')
    }

    static url = '/#'

    static content = {
        advancedDataModelViewLink {$('#advanced-view-link')}
        nodeText {String name ->

            int n = 5 // get this many characters prefix of name

            String namePrefix = (name.length() > (n)) ? name[0..n-1] : name // get a short prefix of the name since the node label tspans only contain part of the name
            $("tspan", text: contains(namePrefix))
        }
        contentPanelItemName {
            String name ->
                $('#d3-info-name-description a', text: contains(name))
        }
    }

    void goToAdvancedDataModelView() {advancedDataModelViewLink.click()}

    boolean hasItemNameInContentPanel(String name) {
        return contentPanelItemName(name) as boolean
    }

    boolean hasNodeWithName(String name) {
        return nodeText(name) as boolean
    }

    void clickElementWithName(String name) {
        nodeText(name).click()
    }
}
