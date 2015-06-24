package org.modelcatalogue.core.api.builder;

import org.modelcatalogue.core.api.builder.ExtensionAwareBuilder;

import java.util.Map;

public interface RelationshipConfiguration extends ExtensionAwareBuilder {
    
    void archived(boolean archived);
    boolean getArchived();
    Map<String, String> getExtensions();
}
