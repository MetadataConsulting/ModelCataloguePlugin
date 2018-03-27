import org.springframework.http.HttpMethod

class LogsUrlMappings {

    static mappings = {
        "/logs/index"(controller: 'logs', action: 'index', method: HttpMethod.GET)
    }
}