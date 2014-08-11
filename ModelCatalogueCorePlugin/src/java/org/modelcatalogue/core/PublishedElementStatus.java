package org.modelcatalogue.core;

public enum PublishedElementStatus {
    DRAFT, UPDATED, PENDING, FINALIZED, REMOVED, ARCHIVED;

    public boolean isModificable() {
        return ordinal() <= FINALIZED.ordinal();
    }
}
