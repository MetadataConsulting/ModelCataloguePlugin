package org.modelcatalogue.core;

public enum PublishedElementStatus {
    DRAFT, PENDING, FINALIZED, REMOVED, ARCHIVED;

    public boolean isModificable() {
        return ordinal() <= FINALIZED.ordinal();
    }
}
