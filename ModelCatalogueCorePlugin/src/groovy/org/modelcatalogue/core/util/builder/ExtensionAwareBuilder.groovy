package org.modelcatalogue.core.util.builder

import org.modelcatalogue.core.CatalogueElement

/**
 * Created by ladin on 22.01.15.
 */
interface ExtensionAwareBuilder {
    void ext(String key, String value)
    void ext(Map<String, String> values)
}