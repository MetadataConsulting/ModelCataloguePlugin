import org.springframework.http.HttpMethod

class ReindexCatalogueUrlMappings {

    static mappings = {
        "/reindexCatalogue/index"(controller: 'reindexCatalogue', action: 'index', method: HttpMethod.GET)
    }
}