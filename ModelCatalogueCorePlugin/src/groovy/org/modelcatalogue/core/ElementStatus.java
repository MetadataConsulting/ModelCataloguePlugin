package org.modelcatalogue.core;

import org.modelcatalogue.core.util.builder.PublishingStatus;

public enum ElementStatus implements PublishingStatus {
    /**
     * DRAFT status indicates newly created element which will probably change in future.
     */
    DRAFT,

    /**
     * UPDATED status indicates element being currently updated.
     *
     * This status is used while processing elements to prevent infinite loops. Set status to UPDATED when you start
     * task which may cause infinite loop.
     */
    UPDATED,

    /**
     * PENDING status usually indicates element being imported from external form before all the elements from the
     * batch are imported.
     */
    PENDING,

    /**
     * FINALIZED status indicates element which is safe to use and won't change in current version. To change element
     * in FINALIZED status, create new draft first using element service.
     */
    FINALIZED,

    /**
     * REMOVED status is not currently used. It's supposed to soft delete items but this behaviour was superseded by
     * DEPRECATED status.
     */
    @Deprecated
    REMOVED,

    /**
     * DEPRECATED status indicates elements which should no longer be used, usually previous version of FINALIZED
     * elements. DEPRECATED elements are rarely shown in the application apart from History tabs and as relations
     * with the relationship type having versionSpecific set to true (e.g. containment, hierarchy)
     */
    DEPRECATED;

    public boolean isModificable() {
        return ordinal() <= FINALIZED.ordinal();
    }
}
