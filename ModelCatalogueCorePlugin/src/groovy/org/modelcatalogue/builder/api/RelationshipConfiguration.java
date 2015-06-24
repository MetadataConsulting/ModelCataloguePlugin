package org.modelcatalogue.builder.api;

import java.util.Map;

public interface RelationshipConfiguration extends ExtensionAwareBuilder {
    
    void archived(boolean archived);
    boolean getArchived();
    Map<String, String> getExtensions();
}
