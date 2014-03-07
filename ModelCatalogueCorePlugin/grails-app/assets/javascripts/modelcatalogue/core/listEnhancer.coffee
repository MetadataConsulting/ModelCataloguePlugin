angular.module('mc.core.listEnhancer', ['mc.util.rest', 'mc.util.enhance', 'mc.core.modelCatalogueApiRoot']).config ['enhanceProvider', (enhanceProvider)->
  condition = (list) -> list.hasOwnProperty('next') or list.hasOwnProperty('previous')
  factory   = ['$q', 'modelCatalogueApiRoot', 'rest', ($q, modelCatalogueApiRoot, rest) ->
    (list, enhance = @enhance) ->
      class ListDecorator
        constructor: (list) ->
          angular.extend(@, list)

          if @next
            nextUrl = @next
            @next = () -> enhance rest method: 'GET', url: "#{modelCatalogueApiRoot}#{nextUrl}"
            @next.size   = Math.min(@page, @total - (@offset + @page))
            @next.url    = nextUrl
            @next.total  = @total
          else
            @next = () -> $q.when({
              total:      @total
              list:       []
              size:       0
              page:       @page
              success:    false
            # promising this will return same empty list
              next:       () -> $q.when(this)
            # promising list will get back to regular lists
              previous:   () -> $q.when(list),
              offset:     @offset + @page
            })
            @next.size   = 0
            @next.total  = @total
          if @previous
            prevUrl = @previous
            @previous = () -> enhance rest method: 'GET', url: "#{modelCatalogueApiRoot}#{prevUrl}"
            @previous.size   = Math.min(@page, @offset)
            @previous.total  = @total
            @previous.url    = prevUrl
          else
            @previous = () -> $q.when({
              total:      @total
              list:       []
              size:       0
              page:       @page
              success:    false
            # promising list will get back to regular lists
              next:       () -> $q.when(list)
            # promising this will return same empty list
              previous:   () -> $q.when(this)
              offset:     0
            })
            @previous.size   = 0
            @previous.total  = @total

      # return new list decorator
      new ListDecorator(list)
  ]

  enhanceProvider.registerEnhancerFactory('list', condition, factory)
]
