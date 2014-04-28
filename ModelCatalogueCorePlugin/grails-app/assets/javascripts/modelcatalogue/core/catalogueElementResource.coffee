angular.module('mc.core.catalogueElementResource', ['mc.core.modelCatalogueApiRoot', 'mc.util.rest', 'mc.util.enhance', 'mc.util.names']).provider 'catalogueElementResource', [ ->
  # Method for instantiating
  @$get = ['$rootScope', 'modelCatalogueApiRoot', 'rest', 'enhance', 'names', ($rootScope, modelCatalogueApiRoot, rest, enhance, names) ->
    class CatalogueElementResource
      constructor: (pathName) ->
        throw "Resource pathname must be defined" if not pathName?
        @pathName = names.getPropertyNameFromType(pathName)

      getIndexPath: () ->
        "#{modelCatalogueApiRoot}/#{@pathName}"

      get: (id) ->
        enhance rest method: 'GET', url: "#{@getIndexPath()}/#{id}"

      delete: (id) ->
        thePathName = @pathName
        enhance(rest(method: 'DELETE', url: "#{@getIndexPath()}/#{id}")).then (result)->
          $rootScope.$broadcast 'catalogueElementDeleted', {link: "/#{thePathName}/#{id}"}
          result

      save: (data) ->
        enhance(rest(method: 'POST', url: "#{@getIndexPath()}", data: data)).then (result)->
          $rootScope.$broadcast 'catalogueElementCreated', result
          result

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

      search: (query, additionalParams = {}) ->
        params = angular.extend({search: query}, additionalParams)
        enhance rest method: 'GET', url: "#{@getIndexPath()}/search", params: params

    (pathName) -> new CatalogueElementResource(pathName)
  ]
  # Always return this from CoffeeScript AngularJS factory functions!
  @
]
