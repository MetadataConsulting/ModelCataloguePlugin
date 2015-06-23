package org.modelcatalogue.core.util.builder

class DefaultRelationshipConfiguration implements RelationshipConfiguration {

    Map<String, String> extensions = [:]
    boolean archived = false

    void archived(boolean archived) {
        this.archived = archived
    }

    void ext(String key, String value) {
        extensions[key] = value
    }

    void ext(Map<String, String> values) {
        extensions.putAll(values)
    }
}
