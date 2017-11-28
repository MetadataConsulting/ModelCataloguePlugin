import org.modelcatalogue.core.CatalogueElement
import org.springframework.http.HttpStatus

class ModelCatalogueIDFilters {

    def filters = {
        all(controller: '*', action:'show', controllerExclude: 'dataModelPermission') {
            before = {
                if (!request.getHeader('Accept')?.contains('json')) {
                    CatalogueElement element = CatalogueElement.get(params.id)

                    if (!element) {
                        render status: HttpStatus.NOT_FOUND
                        return
                    }

                    String dataModelId = element.dataModel ? element.dataModel.getId() : 'catalogue'

                    redirect(uri: "/#/${dataModelId}/${controllerName}/${params.id}")
                    return
                }
            }
        }
    }
}
