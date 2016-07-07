package org.modelcatalogue.core.logging

import grails.converters.JSON
import org.modelcatalogue.core.SecurityService
import org.modelcatalogue.core.util.builder.BuildProgressMonitor
import org.springframework.http.HttpStatus

class LoggingController {

    SecurityService modelCatalogueSecurityService
    LoggingService loggingService

    def logsToAssets() {
        if (!modelCatalogueSecurityService.hasRole('ADMIN')) {
            render status: HttpStatus.UNAUTHORIZED
            return
        }

        BuildProgressMonitor monitor = loggingService.saveLogsToAsset()

        render(monitor as JSON)
    }

}
