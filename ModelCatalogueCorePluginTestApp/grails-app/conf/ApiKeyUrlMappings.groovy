import org.springframework.http.HttpMethod

class ApiKeyUrlMappings {
    static mappings = {
        "/apiKey/index"(controller: 'apiKey', action: 'index', method: HttpMethod.GET)
        "/apiKey/reset"(controller: 'apiKey', action: 'reset', method: HttpMethod.POST)
    }
}