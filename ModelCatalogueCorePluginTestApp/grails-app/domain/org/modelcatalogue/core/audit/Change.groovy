package org.modelcatalogue.core.audit

import grails.util.Environment
import org.modelcatalogue.core.util.FriendlyErrors
import org.springframework.transaction.TransactionStatus

class Change {

    def auditService

    Long latestVersionId
    Long changedId
    Long authorId
    Long parentId

    ChangeType type

    String property

    String newValue
    String oldValue

    Date dateCreated

    /**
     * If the action was already undone
     */
    Boolean undone = Boolean.FALSE

    /**
     * Other side changes don't appear in user, classification or global feeds
     */
    Boolean otherSide = Boolean.FALSE

    /**
     * System changes don't appear in user, classification or global feeds
     */
    Boolean system = Boolean.FALSE

    static constraints = {
        authorId nullable: true
        parentId nullable: true
        undone nullable: true
        system nullable: true
        property maxSize: 255, nullable: true
        newValue maxSize: 60000, nullable: true
        oldValue maxSize: 60000, nullable: true
    }

    static mapping = {
        table  (Environment.current == Environment.TEST ? '`CHANGE`' : '`change`')
        version false
        newValue type: 'text'
        oldValue type: 'text'
        latestVersionId index: 'change_idx_4'
    }

    @Override
    String toString() {
        "Change[id: $id, changed element: $changedId ($latestVersionId), parent change: $parentId, author: $authorId, type: $type, property: $property, newValue: $newValue, oldValue: $oldValue, undone: $undone, otherSide: $otherSide]"
    }

    boolean undo() {
        if (!type) {
            return false
        }

        if (undone) {
            return true
        }

        Change.withTransaction { TransactionStatus status ->
            auditService.mute {
                if (type.undo(this)) {
                    undone = true
                    FriendlyErrors.failFriendlySave(this)
                    return true
                } else {
                    status.setRollbackOnly()
                    return false
                }
            }
        }
    }
}
