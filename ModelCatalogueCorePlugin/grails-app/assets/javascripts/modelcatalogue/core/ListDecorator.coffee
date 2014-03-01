angular.module('mc.core.listDecorator', ['mc.util.rest', 'mc.core.modelCatalogueApiRoot', 'mc.util.createConstantPromise']).provider 'listDecorator', ['restProvider', (restProvider)->

  @$get = ['modelCatalogueApiRoot', 'createConstantPromise', (modelCatalogueApiRoot, createConstantPromise) ->
    (list, response, rest) ->
      class ListDecorator
        constructor: (list) ->
          angular.extend(@, list)

          if @next
            nextUrl = @next
            @next = () -> rest({method: 'GET', url: "#{modelCatalogueApiRoot}#{nextUrl}"})
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
            @previous = () -> rest({method: 'GET', url: "#{modelCatalogueApiRoot}#{prevUrl}"})
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

      # return new list decorator
      new ListDecorator(list)
  ]

  condition = (list) -> list.hasOwnProperty('next') or list.hasOwnProperty('previous')

  restProvider.registerEnhancerFactory('listDecorator', condition, @$get)

  @
]
