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
        nodeText {namePrefix -> $("tspan", text: contains(namePrefix))}
    }

    void goToAdvancedDataModelView() {advancedDataModelViewLink.click()}

    void clickElementWithName(String name) {

        int n = 5 // get this many characters prefix of name

        String namePrefix = (name.length() > (n)) ? name[0..n-1] : name // get a short prefix of the name since the node label tspans only contain part of the name
        nodeText(namePrefix).click()
    }
}
