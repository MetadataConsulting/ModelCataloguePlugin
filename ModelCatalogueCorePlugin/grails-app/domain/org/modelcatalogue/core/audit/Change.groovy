package org.modelcatalogue.core.audit

class Change {

    Long latestVersionId
    Long changedId
    Long authorId

    ChangeType type

    String property

    String newValue
    String oldValue

    Date dateCreated

    /**
     * Other side changes don't appear in user, classification or global feeds
     */
    Boolean otherSide = Boolean.FALSE

    static constraints = {
        authorId nullable: true
        property maxSize: 255, nullable: true
        newValue maxSize: 2000, nullable: true
        oldValue maxSize: 2000, nullable: true
    }

    static mapping = {
        table '`change`'
        version false
    }

    @Override
    String toString() {
        "Change[change: $changedId, latest: $latestVersionId, author: $authorId, type: $type, property: $property, newValue: $newValue, oldValue: $oldValue]"
    }
}
