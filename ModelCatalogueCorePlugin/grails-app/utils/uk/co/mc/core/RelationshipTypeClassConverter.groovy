package uk.co.mc.core

import java.beans.PropertyEditorSupport

/**
 * Created by adammilward on 05/03/2014.
 */
class RelationshipTypeClassConverter extends PropertyEditorSupport {

    def grailsApplication

    @Override
    def String getAsText() {
        return value.toString()
    }

    @Override
    void setAsText(String text) {
        //grailsApplication.getArtefact("Domain",text)?.getClazz()?.get(1)
        //def cl = this.class.classLoader.loadClass(text)
        this.value = text
    }

}