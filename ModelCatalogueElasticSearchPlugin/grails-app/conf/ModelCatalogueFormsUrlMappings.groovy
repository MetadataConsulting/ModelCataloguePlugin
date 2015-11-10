import org.springframework.http.HttpMethod

class ModelCatalogueFormsUrlMappings {

    static mappings = {
        "/api/modelCatalogue/core/forms/generate/$id" (controller: 'formGenerator', action: 'generateForm', method: HttpMethod.GET)
    }

}