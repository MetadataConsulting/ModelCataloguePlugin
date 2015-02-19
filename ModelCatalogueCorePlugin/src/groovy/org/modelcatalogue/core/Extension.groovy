package org.modelcatalogue.core

/**
 * Created by ladin on 21.05.14.
 */
public interface Extension {

    String getName()
    String getExtensionValue()
    void setExtensionValue(String value)
    public <T extends Extension> T save()
    void beforeRemove()

}