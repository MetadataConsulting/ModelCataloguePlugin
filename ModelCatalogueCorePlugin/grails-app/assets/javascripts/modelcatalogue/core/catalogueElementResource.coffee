angular.module('mc.core.catalogueElementResource', ['mc.core.modelCatalogueApiRoot', 'mc.util.rest', 'mc.util.enhance']).provider 'catalogueElementResource', [ ->
  # Method for instantiating
  @$get = ['modelCatalogueApiRoot', 'rest', 'enhance', (modelCatalogueApiRoot, rest, enhance) ->
    class CatalogueElementResource
      constructor: (pathName) ->
        @pathName = pathName

      getIndexPath: () ->
        "#{modelCatalogueApiRoot}/#{@pathName}"

      get: (id) ->
        enhance rest method: 'GET', url: "#{@getIndexPath()}/#{id}"

      delete: (id) ->
        enhance rest method: 'DELETE', url: "#{@getIndexPath()}/#{id}"

      save: (data) ->
        enhance rest method: 'POST', url: "#{@getIndexPath()}", data: data

      update: (data) ->
        if !data.id?
          throw "Missing ID, use save instead"
        props = angular.copy(data)
        delete props.id
        enhance rest method: 'PUT', url: "#{@getIndexPath()}/#{data.id}", data: props

      validate: (data) ->
        enhance rest method: 'POST', url: "#{@getIndexPath()}/validate", data: data

      list: (params = {}) ->
        enhance rest method: 'GET', url: @getIndexPath(), params: params

    (pathName) -> new CatalogueElementResource(pathName)
  ]
  # Always return this from CoffeeScript AngularJS factory functions!
  @
]
