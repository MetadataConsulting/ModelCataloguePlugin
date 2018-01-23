import org.codehaus.groovy.grails.commons.GrailsClass
import org.modelcatalogue.core.AbstractRestfulController
import org.modelcatalogue.core.util.Legacy

class ApiFilters {


    def filters = {
        legacy(controller:'*', action:'*', controllerExclude: 'dataModel', actionExclude: 'inventoryDoc|excelExporterSpreadsheet|gridSpreadsheet|inventorySpreadsheet') {
            before = {
                if (Legacy.hasLegacyName(controllerName) && !request.forwardURI.contains(controllerName)) {
                    redirect url: Legacy.getRedirectUrl(controllerName, request), permanent: true
                }
            }
        }
        expires(controller:'*', action:'*') {
            before = {
                if (!controllerName) {
                    return
                }

                GrailsClass ctrlClass = grailsApplication.getArtefactByLogicalPropertyName('Controller', controllerName)

                if (!ctrlClass) {
                    return
                }

                if (!AbstractRestfulController.isAssignableFrom(ctrlClass.clazz)) {
                    return
                }

                if(request.getHeader('Accept')?.contains('application/json')) {
                    response.setHeader('Expires', '-1')
                }
            }
            after = { Map model ->

            }
            afterView = { Exception e ->

            }
        }
    }
}
