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
        if !response.data and response.status >=200 and response.status < 300
          deferred.resolve response.status
        else if !response.data.errors?
          deferred.resolve enhancer(response.data)
        else
          deferred.reject response
    , (response) ->
      deferred.reject response
    )

    deferred.promise


  # replaces previous/next links with functions fetching given results
  # size and url properties can be called directly on enhnaced next and previous functions
  createListEnhancer = ($http, $q, elementResource, params = {}) ->
     enhancer = (list) ->
       return if list.$enhanced
       if list.next? and list.next != ""
         nextUrl = list.next
         list.next = () ->
           httpUnwrappedRequest($http, $q, {method: 'GET', url: "#{apiRoot}#{nextUrl}", params: params}, enhancer)
         list.next.size   = Math.min(list.page, list.total - (list.offset + list.page))
         list.next.url    = nextUrl
         list.next.total  = list.total
       else
         list.next = promiseValue($q, {
           total:      list.total
           list:       []
           size:       0
           page:       list.page
           success:    false
           # promising this will return same empty list
           next:       promiseValue($q, this)
           # promising list will get back to regular lists
           previous:   promiseValue($q, list),
           offset:     list.offset + list.page
         })
         list.next.size   = 0
         list.next.total  = list.total
       if list.previous? and list.previous != ""
         prevUrl = list.previous
         list.previous = () ->
           httpUnwrappedRequest($http, $q, {method: 'GET', url: "#{apiRoot}#{prevUrl}", params: params}, enhancer)
         list.previous.size   = Math.min(list.page, list.offset)
         list.previous.total  = list.total
         list.previous.url    = prevUrl
       else
         list.previous = promiseValue($q, {
           total:      list.total
           list:       []
           size:       0
           page:       list.page
           success:    false
           # promising list will get back to regular lists
           next:       promiseValue($q, list)
           # promising this will return same empty list
           previous:   promiseValue($q, this)
           offset:     0
         })
         list.previous.size   = 0
         list.previous.total  = list.total
       list.$enhanced = true

       enhance = createElementEnhancer($http, $q, elementResource)
       for element, i in list.list
         list.list[i] = enhance element

       list

  createElementEnhancer = ($http, $q, elementResource) ->
    (element) ->
      # all catalogue elements have element type, we can constraint this somehow later
      if element.hasOwnProperty('elementType')
        return new CatalogueElement($http, $q, element, elementResource)
      element

  # Class defintion
  class CatalogueElement
    constructor: ($http, $q, element, elementResource) ->
      @defaultExcludes = ['elementTypeName', 'elementType', 'incomingRelationships', 'outgoingRelationships']

      @$http    = $http
      @$q       = $q
      @resource = elementResource

      @updatableProperties = []

      for name, ignored of element
       unless name in @defaultExcludes
          @updatableProperties.push(name)

      angular.extend(@, element)
      if element.hasOwnProperty('incomingRelationships')
        incoming = element.incomingRelationships
        @incomingRelationships       = () -> httpUnwrappedRequest(@$http, @$q, {method: 'GET', url: "#{apiRoot}#{incoming.link}"}, createListEnhancer(@$http, @$q, @resource))
        @incomingRelationships.url   = incoming.link
        @incomingRelationships.total = incoming.count
      if element.hasOwnProperty('outgoingRelationships')
        outgoing = element.outgoingRelationships
        @outgoingRelationships = () -> httpUnwrappedRequest(@$http, @$q, {method: 'GET', url: "#{apiRoot}#{outgoing.link}"}, createListEnhancer(@$http, @$q, @resource))
        @outgoingRelationships.url   = outgoing.link
        @outgoingRelationships.total = outgoing.count

    delete:                 () -> @resource.delete(@id)

    update:                 () ->
      payload = {}
      for name in @updatableProperties
        payload[name] = this[name]
      @resource.update(payload)

    getUpdatableProperties: () -> angular.copy(@updatableProperties)


  class CatalogueElementResource
    constructor: ($http, $q, pathName) ->
      @$http    = $http
      @$q       = $q
      @pathName = pathName

    getIndexPath: () ->
      "#{apiRoot}/#{@pathName}"

    get: (id) ->
      httpUnwrappedRequest(@$http, @$q, {method: 'GET', url: "#{@getIndexPath()}/#{id}"}, createElementEnhancer(@$http, @$q, @))

    delete: (id) ->
      httpUnwrappedRequest(@$http, @$q, {method: 'DELETE', url: "#{@getIndexPath()}/#{id}"})

    save: (data) ->
      httpUnwrappedRequest(@$http, @$q, {method: 'POST', url: "#{@getIndexPath()}", data: data}, createElementEnhancer(@$http, @$q, @))

    update: (data) ->
      if !data.id?
        throw "Missing ID, use save instead"
      props = angular.copy(data)
      delete props.id
      httpUnwrappedRequest(@$http, @$q, {method: 'PUT', url: "#{@getIndexPath()}/#{data.id}", data: props}, createElementEnhancer(@$http, @$q, @))

    list: (params = {}) ->
      httpUnwrappedRequest(@$http, @$q, {method: 'GET', url: @getIndexPath(), params: params}, createListEnhancer(@$http, @$q, @, params))

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
