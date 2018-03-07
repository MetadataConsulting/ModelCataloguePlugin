import org.springframework.http.HttpMethod

class LastSeenUrlMappings {
    static mappings = {
        "/lastSeen/index"(controller: 'lastSeen', action: 'index', method: HttpMethod.GET)
    }
}