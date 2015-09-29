package org.modelcatalogue.core.audit

import grails.util.Holders
import org.apache.log4j.Logger
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.FriendlyErrors

/**
 * Created by ladin on 17.02.15.
 */
enum ChangeType {

    NEW_ELEMENT_CREATED  {
        @Override
        boolean isUndoSupported() {
            true
        }

        @Override
        boolean doUndo(Change change, CatalogueElement target) {
            if (!change) {
                return false
            }

            CatalogueElement created = CatalogueElement.get(change.changedId)

            if (!created) {
                return true
            }

            created.clearAssociationsBeforeDelete()
            created.delete(flush: true)

            return true
        }
    },

    NEW_VERSION_CREATED {
        @Override
        boolean isUndoSupported() {
            true
        }

        @Override
        boolean doUndo(Change change, CatalogueElement target) {
            undoChildChanges(change)
        }
    },

    PROPERTY_CHANGED {
        @Override
        boolean isUndoSupported() {
            true
        }

        @Override
        boolean doUndo(Change change, CatalogueElement target) {
            target.setProperty(change.property, DefaultAuditor.readValue(change.oldValue))
            FriendlyErrors.failFriendlySave(target)
            return true
        }
    },

    ELEMENT_DELETED,

    ELEMENT_DEPRECATED {
        @Override
        boolean isUndoSupported() {
            true
        }

        @Override
        boolean doUndo(Change change, CatalogueElement target) {
            undoChildChanges(change)
        }
    },

    EXTERNAL_UPDATE {
        @Override
        boolean isUndoSupported() {
            true
        }

        @Override
        boolean doUndo(Change change, CatalogueElement target) {
            undoChildChanges(change)
        }
    },

    ELEMENT_FINALIZED {
        @Override
        boolean isUndoSupported() {
            true
        }

        @Override
        boolean doUndo(Change change, CatalogueElement target) {
            undoChildChanges(change)
        }
    },

    METADATA_CREATED {
        @Override
        boolean isUndoSupported() {
            true
        }

        @Override
        boolean doUndo(Change change, CatalogueElement target) {
            target.ext.remove(change.property)
            return true
        }
    },

    METADATA_UPDATED {
        @Override
        boolean isUndoSupported() {
            true
        }

        @Override
        boolean doUndo(Change change, CatalogueElement target) {
            target.ext.put(change.property, DefaultAuditor.readValue(change.oldValue) as String)
            return true
        }
    },

    METADATA_DELETED {
        @Override
        boolean isUndoSupported() {
            true
        }

        @Override
        boolean doUndo(Change change, CatalogueElement target) {
            target.ext.put(change.property, DefaultAuditor.readValue(change.oldValue) as String)
            return true
        }
    },

    MAPPING_CREATED {
        @Override
        boolean isUndoSupported() {
            true
        }

        @Override
        boolean doUndo(Change change, CatalogueElement target) {
            Map<String, Object> mapping = DefaultAuditor.readValue(change.newValue) as Map<String, Object>
            if (!mapping) {
                return false
            }
            Mapping updated = Holders.applicationContext.getBean(MappingService).unmap(CatalogueElement.get(mapping.source.id), CatalogueElement.get(mapping.destination.id))
            return !updated.hasErrors()
        }
    },

    MAPPING_UPDATED {
        @Override
        boolean isUndoSupported() {
            true
        }

        @Override
        boolean doUndo(Change change, CatalogueElement target) {
            Map<String, Object> mapping = DefaultAuditor.readValue(change.newValue) as Map<String, Object>
            if (!mapping) {
                return false
            }
            Mapping updated = Holders.applicationContext.getBean(MappingService).map(CatalogueElement.get(mapping.source.id), CatalogueElement.get(mapping.destination.id), DefaultAuditor.readValue(change.oldValue))
            return !updated.hasErrors()
        }
    },

    MAPPING_DELETED {
        @Override
        boolean isUndoSupported() {
            true
        }

        @Override
        boolean doUndo(Change change, CatalogueElement target) {
            Map<String, Object> mapping = DefaultAuditor.readValue(change.oldValue) as Map<String, Object>
            if (!mapping) {
                return false
            }
            Mapping updated = Holders.applicationContext.getBean(MappingService).map(CatalogueElement.get(mapping.source.id), CatalogueElement.get(mapping.destination.id), mapping.mapping)
            return !updated.hasErrors()
        }
    },

    RELATIONSHIP_CREATED {
        @Override
        boolean isUndoSupported() {
            true
        }

        @Override
        boolean doUndo(Change change, CatalogueElement target) {
            def rel = DefaultAuditor.readValue(change.newValue)
            CatalogueElement source = CatalogueElement.get(rel.source.id)
            CatalogueElement destination = CatalogueElement.get(rel.destination.id)
            RelationshipType type = RelationshipType.readByName(rel.type.name)

            if (!source || !destination || !type) {
                return true
            }

            Relationship old = source.removeLinkTo(destination, type)

            if (!old) {
                return true
            }
            return !old.hasErrors()
        }
    },
    RELATIONSHIP_DELETED {
        @Override
        boolean isUndoSupported() {
            true
        }

        @Override
        boolean doUndo(Change change, CatalogueElement target) {
            def rel = DefaultAuditor.readValue(change.oldValue)
            CatalogueElement source = CatalogueElement.get(rel.source.id)
            CatalogueElement destination = CatalogueElement.get(rel.destination.id)
            RelationshipType type = RelationshipType.readByName(rel.type.name)
            Classification classification = rel.classification ? Classification.get(rel.classification.id) : null
            Relationship newOne = source.relationshipService.link(source, destination, type, classification)
            if (!newOne) {
                return false
            }
            if (newOne.hasErrors()) {
                return false
            }
            newOne.ext = rel.ext
            return true
        }
    },

    RELATIONSHIP_ARCHIVED {
        @Override
        boolean isUndoSupported() {
            true
        }

        @Override
        boolean doUndo(Change change, CatalogueElement target) {
            def rel = DefaultAuditor.readValue(change.oldValue)
            if (!rel) {
                return false
            }
            rel.archived = true
            rel.save(flush: true)
            return !rel.hasErrors()
        }
    },


    RELATIONSHIP_METADATA_CREATED {
        @Override
        boolean isUndoSupported() {
            true
        }

        @Override
        boolean doUndo(Change change, CatalogueElement target) {
            def ext = DefaultAuditor.readValue(change.newValue)

            Relationship relationship = Relationship.get(ext.relationship.id)

            if (!relationship) {
                return false
            }

            relationship.ext.remove(ext.name)
            return true
        }
    },
    RELATIONSHIP_METADATA_UPDATED {
        @Override
        boolean isUndoSupported() {
            true
        }

        @Override
        boolean doUndo(Change change, CatalogueElement target) {
            def ext = DefaultAuditor.readValue(change.newValue)

            Relationship relationship = Relationship.get(ext.relationship.id)

            if (!relationship) {
                return false
            }

            relationship.ext.put(ext.name, DefaultAuditor.readValue(change.oldValue))
            return true
        }
    },
    RELATIONSHIP_METADATA_DELETED {
        @Override
        boolean isUndoSupported() {
            true
        }

        @Override
        boolean doUndo(Change change, CatalogueElement target) {
            def ext = DefaultAuditor.readValue(change.oldValue)

            Relationship relationship = Relationship.get(ext.relationship.id)

            if (!relationship) {
                return false
            }

            relationship.ext.put(ext.name, ext.extensionValue)
            return true
        }
    }

    boolean isUndoSupported() {
        return false
    }

    final boolean undo(Change change) {
        if (!undoSupported) {
            return false
        }
        if (change.type != this) {
            return false
        }

        CatalogueElement target = CatalogueElement.get(change.changedId)

        if (change.latestVersionId != (target.latestVersionId ?: target.id)) {
            return false
        }

        return doUndo(change, target)
    }

    protected boolean doUndo(Change change, CatalogueElement target) {
        false
    }

    protected static boolean undoChildChanges(Change change) {
        Logger log = Logger.getLogger(ChangeType)
        if (!change) {
            return false
        }

        List<Change> changes = Change.findAllByParentIdAndOtherSideNotEqual(change.id, Boolean.TRUE, [sort: 'dateCreated', order: 'desc'])


        boolean undone = true
        for (Change ch in changes) {
            if (!ch.type.undoSupported) {
                log.warn "Change not undoable $ch"
                continue
            }
            if (ch.undone) {
                log.info "Change already undone $ch"
                continue
            }
            if (!ch.undo()) {
                log.warn "Undo unsuccessful $ch"
                undone = false
            }
        }

        return undone
    }


}
