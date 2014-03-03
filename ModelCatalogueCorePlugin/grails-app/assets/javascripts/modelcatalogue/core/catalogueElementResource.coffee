angular.module('mc.core.catalogueElementResource', ['mc.core.modelCatalogueApiRoot', 'mc.util.rest']).provider 'catalogueElementResource', [ ->
  # Method for instantiating
  @$get = ['modelCatalogueApiRoot', 'rest', (modelCatalogueApiRoot, rest) ->
    class CatalogueElementResource
      constructor: (pathName) ->
        @pathName = pathName

      getIndexPath: () ->
        "#{modelCatalogueApiRoot}/#{@pathName}"

      get: (id) ->
        rest({method: 'GET', url: "#{@getIndexPath()}/#{id}"})

      delete: (id) ->
        rest({method: 'DELETE', url: "#{@getIndexPath()}/#{id}"})

      save: (data) ->
        rest({method: 'POST', url: "#{@getIndexPath()}", data: data})

      update: (data) ->
        if !data.id?
          throw "Missing ID, use save instead"
        props = angular.copy(data)
        delete props.id
        rest({method: 'PUT', url: "#{@getIndexPath()}/#{data.id}", data: props})

      list: (params = {}) ->
        rest({method: 'GET', url: @getIndexPath(), params: params})

    (pathName) -> new CatalogueElementResource(pathName)
  ]
  # Always return this from CoffeeScript AngularJS factory functions!
  @
]
