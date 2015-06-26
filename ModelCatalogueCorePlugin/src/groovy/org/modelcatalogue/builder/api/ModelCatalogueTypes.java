package org.modelcatalogue.builder.api;

import java.util.HashMap;
import java.util.Map;

public enum ModelCatalogueTypes implements BuilderKeyword {

    CLASSIFICATION,
    MODEL,
    DATA_ELEMENT,
    VALUE_DOMAIN,
    MEASUREMENT_UNIT,
    DATA_TYPE,
    ENUMERATED_TYPE;

    private static Map<Class, ModelCatalogueTypes> CLASSES_TO_TYPES = new HashMap<Class, ModelCatalogueTypes>();
    private Class implementation;

    public static ModelCatalogueTypes getType(Class clazz) {
        return CLASSES_TO_TYPES.get(clazz);
    }

    public Class getImplementation() {
        return implementation;
    }

    public void setImplementation(Class implementation) {
        CLASSES_TO_TYPES.put(implementation, this);
        this.implementation = implementation;
    }
}
