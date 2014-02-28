angular.module('mc.modelCatalogue', ['mc.util.createConstantPromise', 'mc.util.rest']).provider 'modelCatalogue', [ ->

  # Private variables
  modelCatalogueApiRoot = '/api/modelCatalogue/core'

  # Public API for configuration
  @getModelCatalogueApiRoot = () ->
    modelCatalogueApiRoot

  @setModelCatalogueApiRoot = (newApiRoot) ->
    modelCatalogueApiRoot = newApiRoot

  # Method for instantiating
  @$get = ['createConstantPromise', 'rest', (createConstantPromise, rest) ->
    # replaces previous/next links with functions fetching given results
    # size and url properties can be called directly on enhnaced next and previous functions
    class ListDecorator
      @createEnhancer: (resource) ->
        (list) ->
          if list.hasOwnProperty('next') or list.hasOwnProperty('previous')
            new ListDecorator(list, resource)
          else
            list

      constructor: (list, resource) ->
        @resource = resource
        angular.extend(@, list)

        if @next
          nextUrl = @next
          @next = () -> rest({method: 'GET', url: "#{modelCatalogueApiRoot}#{nextUrl}", enhancer: ListDecorator.createEnhancer(@resource)})
          @next.size   = Math.min(@page, @total - (@offset + @page))
          @next.url    = nextUrl
          @next.total  = @total
        else
          @next = createConstantPromise({
            total:      @total
            list:       []
            size:       0
            page:       @page
            success:    false
          # promising this will return same empty list
            next:       createConstantPromise(this)
          # promising list will get back to regular lists
            previous:   createConstantPromise(list),
            offset:     @offset + @page
          })
          @next.size   = 0
          @next.total  = @total
        if @previous
          prevUrl = @previous
          @previous = () -> rest({method: 'GET', url: "#{modelCatalogueApiRoot}#{prevUrl}", enhancer: ListDecorator.createEnhancer(@resource)})
          @previous.size   = Math.min(@page, @offset)
          @previous.total  = @total
          @previous.url    = prevUrl
        else
          @previous = createConstantPromise({
            total:      @total
            list:       []
            size:       0
            page:       @page
            success:    false
          # promising list will get back to regular lists
            next:       createConstantPromise(list)
          # promising this will return same empty list
            previous:   createConstantPromise(this)
            offset:     0
          })
          @previous.size   = 0
          @previous.total  = @total


        enhance = CatalogueElement.createEnhancer(@resource)
        for element, i in @list
          @list[i] = enhance element

        list

    # Class defintion
    class CatalogueElement
      @createEnhancer: (resource) ->
        (element) ->
          if element.hasOwnProperty('elementType')
            new CatalogueElement(element, resource)
          else
            element

      constructor: (element, @resource) ->
        @defaultExcludes = ['elementTypeName', 'elementType', 'incomingRelationships', 'outgoingRelationships']
        @updatableProperties = []

        for name, ignored of element
          unless name in @defaultExcludes
            @updatableProperties.push(name)

        angular.extend(@, element)
        if element.hasOwnProperty('incomingRelationships')
          incoming = element.incomingRelationships
          @incomingRelationships       = () -> rest({method: 'GET', url: "#{modelCatalogueApiRoot}#{incoming.link}", enhancer: ListDecorator.createEnhancer(@resource)})
          @incomingRelationships.url   = incoming.link
          @incomingRelationships.total = incoming.count
        if element.hasOwnProperty('outgoingRelationships')
          outgoing = element.outgoingRelationships
          @outgoingRelationships = () -> rest({method: 'GET', url: "#{modelCatalogueApiRoot}#{outgoing.link}", enhancer: ListDecorator.createEnhancer(@resource)})
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
        "#{modelCatalogueApiRoot}/#{@pathName}"

      get: (id) ->
        rest({method: 'GET', url: "#{@getIndexPath()}/#{id}", enhancer: CatalogueElement.createEnhancer(@)})

      delete: (id) ->
        rest({method: 'DELETE', url: "#{@getIndexPath()}/#{id}"})

      save: (data) ->
        rest({method: 'POST', url: "#{@getIndexPath()}", data: data, enhancer: CatalogueElement.createEnhancer(@)})

      update: (data) ->
        if !data.id?
          throw "Missing ID, use save instead"
        props = angular.copy(data)
        delete props.id
        rest({method: 'PUT', url: "#{@getIndexPath()}/#{data.id}", data: props, enhancer: CatalogueElement.createEnhancer(@)})

      list: (params = {}) ->
        rest({method: 'GET', url: @getIndexPath(), params: params, enhancer: ListDecorator.createEnhancer(@)})

    class ModelCatalogue
      getModelCatalogueApiRoot: ->
        modelCatalogueApiRoot

      elements: (pathName) ->
        new CatalogueElementResource(pathName)

    new ModelCatalogue()
  ]
  # Always return this from CoffeeScript AngularJS factory functions!
  @
]
