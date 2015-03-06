angular.module('mc.core.catalogueElementResource', ['mc.core.modelCatalogueApiRoot', 'mc.util.rest', 'mc.util.enhance', 'mc.util.names']).provider 'catalogueElementResource', [ ->
  # Method for instantiating
  @$get = ['$rootScope', 'modelCatalogueApiRoot', 'rest', 'enhance', 'names', ($rootScope, modelCatalogueApiRoot, rest, enhance, names) ->
    class CatalogueElementResource
      constructor: (pathName) ->
        throw "Resource pathname must be defined" if not pathName?
        @pathName = names.getPropertyNameFromType(pathName)

      getIndexPath: () ->
        "#{modelCatalogueApiRoot}/#{@pathName}"

      getByUUID: (uuid) ->
        enhance rest method: 'GET', url: "#{@getIndexPath()}/uuid/#{uuid}"

      get: (id) ->
        enhance rest method: 'GET', url: "#{@getIndexPath()}/#{id}"

      delete: (id) ->
        thePathName = @pathName
        enhance(rest(method: 'DELETE', url: "#{@getIndexPath()}/#{id}")).then (result)->
          $rootScope.$broadcast 'catalogueElementDeleted', {link: "/#{thePathName}/#{id}"}
          result

      save: (data) ->
        url = "#{@getIndexPath()}"
        thePathName = @pathName

        creationChecker = (status) ->
          if status == 200
            $rootScope.$broadcast 'displayGlobalMessage', "#{names.getNaturalName(names.getPropertyNameFromType(thePathName))} not created", "Reused existing #{names.getNaturalName(names.getPropertyNameFromType(thePathName))} '#{data.name}' instead of creating new one.", 'warning', true

        enhance(rest(method: 'POST', url: url, data: data, statusListener: creationChecker)).then (result)->
          $rootScope.$broadcast 'catalogueElementCreated', result, url, data
          result

      update: (data, params) ->
        if !data.id?
          throw "Missing ID, use save instead"
        props = angular.copy(data)
        delete props.id
        enhance(rest(method: 'PUT', url: "#{@getIndexPath()}/#{data.id}", data: props, params: angular.extend({format: 'json'}, params))).then (result)->
          $rootScope.$broadcast 'catalogueElementUpdated', result
          result

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
