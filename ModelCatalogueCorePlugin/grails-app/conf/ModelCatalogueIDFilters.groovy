class ModelCatalogueIDFilters {

    def filters = {
        all(controller: '*', action:'show') {
            before = {
                if (!request.getHeader('Accept')?.contains('json')) {
                    redirect(uri: "/#/catalogue/${controllerName}/${params.id}")
                }
            }
        }
    }
}
