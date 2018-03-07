import org.springframework.http.HttpMethod

class DashboardUrlMappings {

    static mappings = {
        // Dashboard
        "/dashboard/index"(controller: 'dashboard', action: 'index', method: HttpMethod.GET)
    }
}

