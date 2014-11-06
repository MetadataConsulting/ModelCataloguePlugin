package org.modelcatalogue.core;

public enum ElementStatus {
    DRAFT, UPDATED, PENDING, FINALIZED, REMOVED, DEPRECATED;

    public boolean isModificable() {
        return ordinal() <= FINALIZED.ordinal();
    }
}
