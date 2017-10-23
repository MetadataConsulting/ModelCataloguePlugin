package org.modelcatalogue.core.logging

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.SecurityService
import org.modelcatalogue.core.util.builder.BuildProgressMonitor
import org.springframework.http.HttpStatus

class LoggingController {

    SecurityService modelCatalogueSecurityService
    LoggingService loggingService

    @Secured(['ROLE_SUPERVISOR'])
    def logsToAssets() {
        render(Asset.getWithRetries(loggingService.saveLogsToAsset()) as JSON)
    }

}
