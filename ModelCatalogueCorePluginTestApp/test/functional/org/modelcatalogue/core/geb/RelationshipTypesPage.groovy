package org.modelcatalogue.core.geb

import geb.Page

class RelationshipTypesPage extends Page {

    static url = '/#/catalogue/relationshipType/all'

    static at = { title == 'Relationship Types' }

    static content = {
        nav { $('#topmenu', 0).module(NavModule) }
    }
}
