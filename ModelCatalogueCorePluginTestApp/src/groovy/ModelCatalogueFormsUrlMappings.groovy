import org.springframework.http.HttpMethod

/** What is this? */
class ModelCatalogueFormsUrlMappings {

    static mappings = {
        "/api/modelCatalogue/core/forms/generate/$id" (controller: 'formGenerator', action: 'generateForm', method: HttpMethod.GET)
        "/api/modelCatalogue/core/forms/preview/$id" (controller: 'formGenerator', action: 'previewForm', method: HttpMethod.GET)
    }

}
