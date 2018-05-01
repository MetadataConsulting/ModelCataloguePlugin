package org.modelcatalogue.core.geb

import geb.Page

class CodeVersionPage extends Page {

    static url = '/modelCatalogueVersion/index'

    static at = { title == 'Model Catalogue Version'}

    static content = {
        links { $('.panel-body a') }
        nav { $('#topmenu', 0).module(NavModule) }
    }

    boolean isGithubLinkDisplayed() {
        for ( int i = 0; i < links.size(); i++ ) {
            if ( links[i].getAttribute('href').contains('github') ) {
                return true
            }
        }
        false
    }
}
