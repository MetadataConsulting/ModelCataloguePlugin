package org.modelcatalogue.core.util

import org.modelcatalogue.builder.api.ModelCatalogueTypes
import org.modelcatalogue.core.CatalogueElement

class CatalogueElementFinder {

    static Set<String> catalogueElementClasses = null
    static Map<Class, List<String>> allTypesCache = [:]

    private static initCatalogueElementClasses() {
        catalogueElementClasses = ModelCatalogueTypes.values()*.implementation.grep().unique()*.name
    }

    static Set<String> getCatalogueElementClasses() {
        if (catalogueElementClasses == null) {
            initCatalogueElementClasses()
        }
        catalogueElementClasses
    }

    static List<String> getAllTypesNames(Class cls) {
        List<String> ret = allTypesCache[cls]
        if (ret != null) {
            return ret
        }
        if (!cls || !CatalogueElement.isAssignableFrom(cls)) {
            allTypesCache[cls] = []
            return []
        }
        ret = [cls.name, *getAllTypesNames(cls.superclass)]
        allTypesCache[cls] = ret
        ret
    }


}
