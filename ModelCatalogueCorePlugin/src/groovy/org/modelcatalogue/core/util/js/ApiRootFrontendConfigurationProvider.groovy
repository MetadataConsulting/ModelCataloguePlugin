package org.modelcatalogue.core.util.js

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.beans.factory.annotation.Autowired

class ApiRootFrontendConfigurationProvider extends  FrontendConfigurationProvider {

    @Autowired GrailsApplication grailsApplication

    @Override
    String getJavascriptConfiguration() {
        //language=JavaScript
        """
        angular.module('mc.core.currentApiRoot', ['mc.core.modelCatalogueApiRoot']).value('modelCatalogueApiRoot', '${grailsApplication.config.grails.serverURL}/api/modelCatalogue/core');
        modelcatalogue.registerModule('mc.core.currentApiRoot');
        """
    }
}
