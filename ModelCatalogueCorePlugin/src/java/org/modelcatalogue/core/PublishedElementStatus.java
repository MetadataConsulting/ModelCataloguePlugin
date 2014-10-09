package org.modelcatalogue.core;

public enum PublishedElementStatus {
    DRAFT, UPDATED, PENDING, FINALIZED, REMOVED, DEPRECATED;

    public boolean isModificable() {
        return ordinal() <= FINALIZED.ordinal();
    }
}
