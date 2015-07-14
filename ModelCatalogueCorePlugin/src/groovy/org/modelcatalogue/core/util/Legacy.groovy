package org.modelcatalogue.core.util

import grails.util.Holders
import org.codehaus.groovy.grails.commons.GrailsApplication

import javax.servlet.http.HttpServletRequest

class Legacy {

    private static final Map<String, String> LEGACY_ENTITY_NAMES = [dataClass: 'model', dataModel: 'classification']

    static boolean isLegacyResourceName(String resourceName) {
        return LEGACY_ENTITY_NAMES.containsValue(resourceName)
    }

    static boolean hasLegacyName(String resourceName) {
        return LEGACY_ENTITY_NAMES.containsKey(resourceName)
    }

    static String getLegacyResourceName(String newName) {
        LEGACY_ENTITY_NAMES[newName]
    }

    static String getNewResourceName(String oldName) {
        LEGACY_ENTITY_NAMES.find { it.value == oldName}?.key
    }

    static String getRedirectUrl(String newResourceName, HttpServletRequest request) {
        GrailsApplication grailsApplication = Holders.grailsApplication
        if (request.contextPath) {
            return (grailsApplication.config.grails.serverURL - request.contextPath) + (request.forwardURI.replaceAll("/${getLegacyResourceName(newResourceName)}(?!Catalogue)", "/$newResourceName"))
        }
        return grailsApplication.config.grails.serverURL + (request.forwardURI.replaceAll("/${getLegacyResourceName(newResourceName)}(?!Catalogue)", "/$newResourceName"))

    }



}
