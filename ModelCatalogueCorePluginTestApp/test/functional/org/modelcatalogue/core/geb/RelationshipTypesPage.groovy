package org.modelcatalogue.core.geb

import geb.Page

class RelationshipTypesPage extends Page {

    static url = '/#/catalogue/relationshipType/all'

    static at = { title == 'Relationship Types' }

    static content = {
        nav { $('div.navbar-collapse', 0).module(NavModuleAdmin) }
    }
}
