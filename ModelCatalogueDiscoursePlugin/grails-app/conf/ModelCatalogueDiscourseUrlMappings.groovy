import org.springframework.http.HttpMethod

class ModelCatalogueDiscourseUrlMappings {


    static mappings = {

        def catalogueElements = ['asset', 'dataElement', 'model', 'catalogueElement', 'dataType', 'enumeratedType', 'measurementUnit', 'valueDomain', 'user', 'classification']

        for (String controllerName in catalogueElements) {
            "/api/modelCatalogue/core/$controllerName/$id/comments"(controller: 'comments', action: 'comments', method: HttpMethod.GET)
        }

    }
}