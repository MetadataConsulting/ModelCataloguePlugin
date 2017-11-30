package org.modelcatalogue.core.logging

import grails.converters.JSON
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.SecurityService

class LoggingController {

    SecurityService modelCatalogueSecurityService
    LoggingService loggingService

    def logsToAssets() {
        render(Asset.getWithRetries(loggingService.saveLogsToAsset()) as JSON)
    }

}
