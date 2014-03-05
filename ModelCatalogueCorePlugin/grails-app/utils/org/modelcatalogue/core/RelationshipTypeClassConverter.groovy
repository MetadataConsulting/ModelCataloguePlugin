package org.modelcatalogue.core

import java.beans.PropertyEditorSupport

/**
 * Created by adammilward on 05/03/2014.
 */
class RelationshipTypeClassConverter extends PropertyEditorSupport {

    @Override
    def String getAsText() {
        return value.getName()
    }

    @Override
    void setAsText(String text) {
        Class domain = Class.forName(text, true, Thread.currentThread().contextClassLoader)
        this.value = domain
    }

}