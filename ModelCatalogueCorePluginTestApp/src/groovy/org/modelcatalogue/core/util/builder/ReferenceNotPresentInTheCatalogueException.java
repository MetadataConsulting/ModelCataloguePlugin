package org.modelcatalogue.core.util.builder;

import org.modelcatalogue.core.RelationshipType;

public class ReferenceNotPresentInTheCatalogueException extends RuntimeException {

    public ReferenceNotPresentInTheCatalogueException(String message) {
        super(message);
    }

    boolean isIgnorable(RelationshipType type) {
        return type.getSystem() || !type.getVersionSpecific();
    }

}
