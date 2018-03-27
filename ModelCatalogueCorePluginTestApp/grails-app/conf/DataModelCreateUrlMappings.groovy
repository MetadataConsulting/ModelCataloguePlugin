import org.springframework.http.HttpMethod

class DataModelCreateUrlMappings {
    static mappings = {
        "/dataModel/create"(controller: 'dataModelCreate', action: 'create', method: HttpMethod.GET)
        "/dataModel/save"(controller: 'dataModelCreate', action: 'save', method: HttpMethod.POST)
    }
}