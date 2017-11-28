package org.modelcatalogue.core.ddl

import grails.util.Holders
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.publishing.DraftContext

class CreateDraftDefinition {

    final DataDefinitionLanguage ddl

    CreateDraftDefinition(DataDefinitionLanguage ddl) {
        this.ddl = ddl
    }

    void of(String name) {
        Holders.applicationContext.getBean(ElementService).createDraftVersion(ddl.find(CatalogueElement, name), DraftContext.userFriendly())
    }
}
