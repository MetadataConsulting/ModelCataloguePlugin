package org.modelcatalogue.core.geb

import geb.Page

/**
 * "Advanced" Data Model View Page– The original AngularJS view with treeview on the left, individual element view on the right with editors.
 * TODO: Make appropriate Page classes subclass this class
 * TODO: Abstract more stuff out of the subclasses like treeview content
 *
 */
class AdvancedDataModelViewPage extends Page {

    static content = {
        basicDataModelViewLink { $('a.basic-data-model-view-link') }
    }

    void goToBasicDataModelView() {basicDataModelViewLink.click()}
}
