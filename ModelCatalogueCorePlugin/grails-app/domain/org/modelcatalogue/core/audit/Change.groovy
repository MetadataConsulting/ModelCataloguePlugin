package org.modelcatalogue.core.audit

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.security.User

class Change {

    Long latestVersionId
    Long changedId
    Long authorId

    ChangeType type

    String property

    String newValue
    String oldValue

    Date dateCreated

    static constraints = {
        authorId nullable: true
        property maxSize: 250, nullable: true
        newValue maxSize: 2000, nullable: true
        oldValue maxSize: 2000, nullable: true
    }

    static mapping = {
        version false
    }

    static transients = ['changed', 'latestVersion']

    CatalogueElement getChanged() { CatalogueElement.get(changedId) }
    CatalogueElement getLatestVersion() { CatalogueElement.get(latestVersionId) }
    User getAuthor() { authorId ? User.get(authorId) : null}

}
