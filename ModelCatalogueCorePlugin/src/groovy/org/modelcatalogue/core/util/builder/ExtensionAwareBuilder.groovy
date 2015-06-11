package org.modelcatalogue.core.util.builder

interface ExtensionAwareBuilder {
    void ext(String key, String value)
    void ext(Map<String, String> values)
}