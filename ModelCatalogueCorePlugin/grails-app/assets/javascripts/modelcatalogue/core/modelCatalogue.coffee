angular.module('mc.modelCatalogue', ['mc.util.createConstantPromise', 'mc.util.rest']).provider 'modelCatalogue', [ ->

  # Private variables
  apiRoot = '/api/modelCatalogue/core'

  # Public API for configuration
  @getApiRoot = () ->
    apiRoot

  @setApiRoot = (newApiRoot) ->
    apiRoot = newApiRoot

  # Method for instantiating
  @$get = ['createConstantPromise', 'rest', (createConstantPromise, rest) ->

    # replaces previous/next links with functions fetching given results
    # size and url properties can be called directly on enhnaced next and previous functions
    createListEnhancer = (elementResource, params = {}) ->
      enhancer = (list) ->
        return if list.$enhanced
        if list.next? and list.next != ""
          nextUrl = list.next
          list.next = () ->
            rest({method: 'GET', url: "#{apiRoot}#{nextUrl}", params: params, enhancer: enhancer})
          list.next.size   = Math.min(list.page, list.total - (list.offset + list.page))
          list.next.url    = nextUrl
          list.next.total  = list.total
        else
          list.next = createConstantPromise({
            total:      list.total
            list:       []
            size:       0
            page:       list.page
            success:    false
          # promising this will return same empty list
            next:       createConstantPromise(this)
          # promising list will get back to regular lists
            previous:   createConstantPromise(list),
            offset:     list.offset + list.page
          })
          list.next.size   = 0
          list.next.total  = list.total
        if list.previous? and list.previous != ""
          prevUrl = list.previous
          list.previous = () ->
            rest({method: 'GET', url: "#{apiRoot}#{prevUrl}", params: params, enhancer: enhancer})
          list.previous.size   = Math.min(list.page, list.offset)
          list.previous.total  = list.total
          list.previous.url    = prevUrl
        else
          list.previous = createConstantPromise({
            total:      list.total
            list:       []
            size:       0
            page:       list.page
            success:    false
          # promising list will get back to regular lists
            next:       createConstantPromise(list)
          # promising this will return same empty list
            previous:   createConstantPromise(this)
            offset:     0
          })
          list.previous.size   = 0
          list.previous.total  = list.total
        list.$enhanced = true

        enhance = createElementEnhancer(elementResource)
        for element, i in list.list
          list.list[i] = enhance element

        list

    createElementEnhancer = (elementResource) ->
      (element) ->
        # all catalogue elements have element type, we can constraint this somehow later
        if element.hasOwnProperty('elementType')
          return new CatalogueElement(element, elementResource)
        element

    # Class defintion
    class CatalogueElement
      constructor: (element, elementResource) ->
        @defaultExcludes = ['elementTypeName', 'elementType', 'incomingRelationships', 'outgoingRelationships']
        @resource = elementResource
        @updatableProperties = []

        for name, ignored of element
          unless name in @defaultExcludes
            @updatableProperties.push(name)

        angular.extend(@, element)
        if element.hasOwnProperty('incomingRelationships')
          incoming = element.incomingRelationships
          @incomingRelationships       = () -> rest({method: 'GET', url: "#{apiRoot}#{incoming.link}", enhancer: createListEnhancer(@resource)})
          @incomingRelationships.url   = incoming.link
          @incomingRelationships.total = incoming.count
        if element.hasOwnProperty('outgoingRelationships')
          outgoing = element.outgoingRelationships
          @outgoingRelationships = () -> rest({method: 'GET', url: "#{apiRoot}#{outgoing.link}", enhancer: createListEnhancer(@resource)})
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
      constructor: (pathName) ->
        @pathName = pathName

      getIndexPath: () ->
        "#{apiRoot}/#{@pathName}"

      get: (id) ->
        rest({method: 'GET', url: "#{@getIndexPath()}/#{id}", enhancer: createElementEnhancer(@)})

      delete: (id) ->
        rest({method: 'DELETE', url: "#{@getIndexPath()}/#{id}"})

      save: (data) ->
        rest({method: 'POST', url: "#{@getIndexPath()}", data: data, enhancer: createElementEnhancer(@)})

      update: (data) ->
        if !data.id?
          throw "Missing ID, use save instead"
        props = angular.copy(data)
        delete props.id
        rest({method: 'PUT', url: "#{@getIndexPath()}/#{data.id}", data: props, enhancer: createElementEnhancer(@)})

      list: (params = {}) ->
        rest({method: 'GET', url: @getIndexPath(), params: params, enhancer: createListEnhancer(@, params)})

    class ModelCatalogue
      getApiRoot: ->
        apiRoot

      elements: (pathName) ->
        new CatalogueElementResource(pathName)

    new ModelCatalogue()
  ]
  # Always return this from CoffeeScript AngularJS factory functions!
  @
]
