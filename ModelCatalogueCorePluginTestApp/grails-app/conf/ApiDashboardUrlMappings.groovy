import org.springframework.http.HttpMethod

class ApiDashboardUrlMappings {
    static mappings = {
        "/api/dashboard/dataModels"(controller: 'apiDashboard', action: 'dataModels', method: HttpMethod.GET)
        "/api/dashboard/$dataModelId/catalogueElements"(controller: 'apiDashboard', action: 'catalogueElements', method: HttpMethod.GET)
    }
}