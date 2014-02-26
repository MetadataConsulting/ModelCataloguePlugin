angular.module('mc.modelCatalogue', []).provider 'modelCatalogue', [ ->

  # Private variables
  apiRoot = '/api/modelCatalogue/core'

  promiseValue = ($q, value) ->
    () ->
      deferred = $q.defer()
      deferred.resolve(value)
      deferred.promise

  httpUnwrappedRequest = ($http, $q, config, enhancer = (toBeEnhanced)-> toBeEnhanced) ->
    deferred = $q.defer()

    $http(config).then(
      (response) ->
        if response.data.success
          deferred.resolve enhancer(response.data)
        else
          deferred.reject response
    , (response) ->
      deferred.reject response
    )

    deferred.promise

  # Class defintion
  class CatalogueElementResource
    constructor: ($http, $q, pathName) ->
      @$http    = $http
      @$q       = $q
      @pathName = pathName

    getIndexPath: () ->
      "#{apiRoot}/#{@pathName}"

    list: (params = {}) ->
      [$http, $q] = [@$http, @$q]
      enhancer = (list) ->
        if list.next? and list.next != ""
          nextUrl = list.next
          list.next = () ->
            httpUnwrappedRequest($http, $q, {method: 'GET', url: "#{apiRoot}#{nextUrl}", params: params}, enhancer)
        else
          list.next = promiseValue($q, {
            total:      list.total
            list:       []
            size:       0
            page:       list.page
            success:    false
            next:       promiseValue($q, this)
            previous:   promiseValue($q, list),
            offset:     list.offset + list.page
          })

        if list.previous? and list.previous != ""
          prevUrl = list.previous
          list.previous = () ->
            httpUnwrappedRequest($http, $q, {method: 'GET', url: "#{apiRoot}#{prevUrl}", params: params}, enhancer)
        else
          list.previous = promiseValue($q, {
            total:      list.total
            list:       []
            size:       0
            page:       list.page
            success:    false
            next:       promiseValue($q, list)
            previous:   promiseValue($q, this)
            offset:     0
          })
        list
      httpUnwrappedRequest($http, $q, {method: 'GET', url: @getIndexPath(), params: params}, enhancer)

  class ModelCatalogue
    constructor: ($http, $q) ->
      @$http = $http
      @$q    = $q

    getApiRoot: ->
      apiRoot

    elements: (pathName) ->
      new CatalogueElementResource(@$http, @$q, pathName)

  # Public API for configuration

  @getApiRoot = () ->
    apiRoot

  @setApiRoot = (newApiRoot) ->
    apiRoot = newApiRoot

  # Method for instantiating
  @$get = [ '$http', '$q', ($http, $q) ->
    new ModelCatalogue($http, $q)
  ]
  # Always return this from CoffeeScript AngularJS factory functions!
  @
]
