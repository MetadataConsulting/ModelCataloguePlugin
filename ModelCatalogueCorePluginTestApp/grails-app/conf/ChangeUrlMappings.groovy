import org.springframework.http.HttpMethod

class ChangeUrlMappings {
    static mappings = {
        "/changes/index"(controller: 'changes', action: 'index', method: HttpMethod.GET)
    }
}