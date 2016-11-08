package org.modelcatalogue.core.util.js

import org.springframework.beans.factory.annotation.Autowired

import javax.annotation.PostConstruct

abstract class FrontendConfigurationProvider {

    @Autowired FrontendConfigurationProviderRegistry frontendConfigurationProviderRegistry

    /**
     * @return arbitrary JavaScript to be run after all external dependencies are added to the header but before the application
     * module is initialized with "angular.module('metadataCurator', window.modelcatalogue.getModules())"
     */
    abstract String getJavascriptConfiguration()

    @PostConstruct void autoRegister() {
        frontendConfigurationProviderRegistry.providers.add this
    }

}
