package org.modelcatalogue.core.logging

import grails.converters.JSON
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.SecurityService
import org.modelcatalogue.core.util.builder.BuildProgressMonitor
import org.springframework.http.HttpStatus

class LoggingController {

    SecurityService modelCatalogueSecurityService
    LoggingService loggingService

    def logsToAssets() {
        if (!modelCatalogueSecurityService.hasRole('SUPERVISOR')) {
            render status: HttpStatus.UNAUTHORIZED
            return
        }

        render(Asset.getWithRetries(loggingService.saveLogsToAsset()) as JSON)
    }

}
