package org.modelcatalogue.core.util.builder;

import java.util.Map;

public interface RelationshipConfiguration extends ExtensionAwareBuilder {
    
    void archived(boolean archived);
    boolean getArchived();
    Map<String, String> getExtensions();
}
