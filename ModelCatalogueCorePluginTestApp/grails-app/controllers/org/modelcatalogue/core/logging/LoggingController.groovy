package org.modelcatalogue.core.logging

import grails.converters.JSON
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.SecurityService
import org.springframework.context.MessageSource

import static org.springframework.http.HttpStatus.OK

class LoggingController {

    SecurityService modelCatalogueSecurityService
    LoggingService loggingService
    MessageSource messageSource

    def logsToAssets() {
        Asset asset = Asset.getWithRetries(loggingService.saveLogsToAsset())
        if(request.getHeader('Accept')?.contains('application/json')) {
            render(asset as JSON)
            return
        }
        if ( !asset ) {
            flash.error = messageSource.getMessage('logs.create.failed', [] as Object[], 'Unable to generate Logs archive', request.locale)
            redirect controller: 'logs'
            return
        }
        redirectToAsset(asset.id)
    }

    protected redirectToAsset(Long id){
        response.setHeader("X-Asset-ID",  id.toString())
        redirect url: grailsApplication.config.grails.serverURL +  "/api/modelCatalogue/core/asset/" + id
    }

}
