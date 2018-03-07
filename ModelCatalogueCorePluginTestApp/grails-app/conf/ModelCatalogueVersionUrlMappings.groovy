import org.springframework.http.HttpMethod

class ModelCatalogueVersionUrlMappings {
    static mappings = {
        "/modelCatalogueVersion/index"(controller: 'modelCatalogueVersion', action: 'index', method: HttpMethod.GET)
    }
}