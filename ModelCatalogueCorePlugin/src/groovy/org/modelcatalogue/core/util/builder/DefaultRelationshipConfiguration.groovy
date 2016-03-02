package org.modelcatalogue.core.util.builder

import groovy.transform.CompileStatic
import org.modelcatalogue.builder.api.RelationshipConfiguration

@CompileStatic
class DefaultRelationshipConfiguration implements RelationshipConfiguration {

    Map<String, String> extensions = [:]
    boolean archived = false
    boolean inherited = true

    void archived(boolean archived) {
        this.archived = archived
    }

    void inherited(boolean inherited) {
        this.inherited = inherited
    }

    void ext(String key, String value) {
        extensions[key] = value
    }

    void ext(Map<String, String> values) {
        extensions.putAll(values)
    }
}
