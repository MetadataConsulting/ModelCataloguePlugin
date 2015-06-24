package org.modelcatalogue.core.api.builder;

import java.util.Map;

public interface ExtensionAwareBuilder {
    void ext(String key, String value);
    void ext(Map<String, String> values);
}
