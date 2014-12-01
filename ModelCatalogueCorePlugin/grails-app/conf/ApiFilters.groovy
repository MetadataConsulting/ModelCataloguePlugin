import org.codehaus.groovy.grails.commons.GrailsClass
import org.modelcatalogue.core.AbstractRestfulController

class ApiFilters {

    def filters = {
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
