package org.modelcatalogue.core;

public enum PublishedElementStatus {
    DRAFT, PENDING, UPDATED, FINALIZED, REMOVED, ARCHIVED;

    public boolean isModificable() {
        return ordinal() <= FINALIZED.ordinal();
    }
}
