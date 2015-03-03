package org.modelcatalogue.core.audit

import org.modelcatalogue.core.util.FriendlyErrors
import org.springframework.transaction.TransactionStatus

class Change {

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

    static constraints = {
        authorId nullable: true
        parentId nullable: true
        property maxSize: 255, nullable: true
        newValue maxSize: 15000, nullable: true
        oldValue maxSize: 15000, nullable: true
    }

    static mapping = {
        table '`change`'
        version false
    }

    @Override
    String toString() {
        "Change[id: $id, change: $changedId, parent: $parentId, latest: $latestVersionId, author: $authorId, type: $type, property: $property, newValue: $newValue, oldValue: $oldValue, undone: $undone, otherSide: $otherSide]"
    }

    boolean undo() {
        if (!type) {
            return false
        }

        if (undone) {
            return true
        }

        Change.withTransaction { TransactionStatus status ->
            AuditService.noAudit {
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
