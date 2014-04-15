angular.module('mc.core.listEnhancer', ['mc.util.rest', 'mc.util.enhance', 'mc.core.modelCatalogueApiRoot']).config ['enhanceProvider', (enhanceProvider)->
  condition = (list) -> list.hasOwnProperty('next') or list.hasOwnProperty('previous')
  factory   = ['$q', 'modelCatalogueApiRoot', 'rest', '$rootScope', ($q, modelCatalogueApiRoot, rest, $rootScope) ->
    listEnhancer = (list, enhance = @enhance) ->
      class ListDecorator
        constructor: (list) ->
          angular.extend(@, list)

          self = @

          $rootScope.$on 'catalogueElementDeleted', (event, element) ->
            indexesToRemove = []
            for item, i in self.list when item.id == element.id and item.elementType == element.elementType
              indexesToRemove.push i

            for index, i in indexesToRemove
              self.list.splice index - i, 1
              self.total--
              self.size--



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
              currentPage:1
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
              currentPage:1
            })
            @previous.size   = 0
            @previous.total  = @total

          @currentPage = Math.floor(@offset / @page) + 1

          @goto = (page) ->
            return $q.when(@) if @total == 0 or @total <= @page
            theOffset = (page - 1) * @page
            theLink   = "#{modelCatalogueApiRoot}#{@previous.url ? @next.url}"

            if theLink.indexOf('offset=') >= 0
              theLink = theLink.replace /offset=(\d+)/, () => "offset=#{theOffset}"
            else if theLink.indexOf('?') >= 0
              theLink = "#{theLink}&offset=#{theOffset}"
            else
              theLink = "#{theLink}?offset=#{theOffset}"

            enhance rest method: 'GET', url: theLink


            # return new list decorator
      new ListDecorator(list)

    listEnhancer.createEmptyList = (itemType = null) -> {
      list: []
      next: {size: 0}
      previous: {size: 0}
      total: 0
      empty: true
      itemType: itemType
    }

    listEnhancer

  ]

  enhanceProvider.registerEnhancerFactory('list', condition, factory)
]
