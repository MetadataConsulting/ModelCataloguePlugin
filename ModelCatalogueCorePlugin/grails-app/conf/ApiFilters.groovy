import org.codehaus.groovy.grails.commons.GrailsClass
import org.modelcatalogue.core.AbstractRestfulController
import org.springframework.http.HttpStatus

class ApiFilters {

    static final Map<String, String> LEGACY_ENTITY_NAMES = [dataClass: 'model', dataModel: 'classification']

    def filters = {
        legacy(controller:'*', action:'*') {
            before = {
                if (LEGACY_ENTITY_NAMES.containsKey(controllerName) && !request.forwardURI.contains(controllerName)) {
                    String url = grailsApplication.config.grails.serverURL + (request.forwardURI.replaceAll("/${LEGACY_ENTITY_NAMES[controllerName]}(?!Catalogue)", "/$controllerName"))
                    if (request.contextPath) {
                        url = (grailsApplication.config.grails.serverURL - request.contextPath) + (request.forwardURI.replaceAll("/${LEGACY_ENTITY_NAMES[controllerName]}(?!Catalogue)", "/$controllerName"))
                    }
                    redirect url: url, permanent: true
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
