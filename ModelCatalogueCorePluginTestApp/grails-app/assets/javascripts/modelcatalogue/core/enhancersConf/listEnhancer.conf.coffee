angular.module('modelcatalogue.core.enhancersConf.listEnhancer', ['mc.util.rest', 'mc.util.enhance', 'mc.core.modelCatalogueApiRoot']).config ['enhanceProvider', (enhanceProvider)->
  condition = (list) -> angular.isObject(list) and list.hasOwnProperty('base') and list.hasOwnProperty('list')
  factory   = ['$q', 'modelCatalogueApiRoot', 'rest', '$rootScope', 'enhance', ($q, modelCatalogueApiRoot, rest, $rootScope, enhance) ->
    listEnhancer = (list) ->
      class ListDecorator
        constructor: (list) ->
          angular.extend(@, list)

          self = @

          $rootScope.$on 'catalogueElementDeleted', (event, element) ->
            return unless element
            return unless angular.isArray(self.list)

            indexesToRemove = []

            for item, i in self.list when item and item.id == element.id and item.elementType == element.elementType
              indexesToRemove.push i

            for index, i in indexesToRemove
              self.list.splice index - i, 1
              self.total--
              self.size--

            self.empty = self.size == 0



          if @next
            nextUrl = @next
            @next = (extraParameters = {}) ->
              enhance rest method: 'GET', url: "#{modelCatalogueApiRoot}#{arguments.callee.url}", params: extraParameters, join: true
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
            @previous = (extraParameters = {}) ->
              enhance rest method: 'GET', url: "#{modelCatalogueApiRoot}#{arguments.callee.url}", params: extraParameters, join: true
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
            return $q.when(@) if @total == 0 or @total <= @page or not (@previous.url or @next.url)
            theOffset = (page - 1) * @page
            theLink   = "#{modelCatalogueApiRoot}#{@previous.url ? @next.url}"

            if theLink.indexOf('offset=') >= 0
              theLink = theLink.replace /offset=(\d+)/, () => "offset=#{theOffset}"
            else if theLink.indexOf('?') >= 0
              theLink = "#{theLink}&offset=#{theOffset}"
            else
              theLink = "#{theLink}?offset=#{theOffset}"

            enhance rest method: 'GET', url: theLink, join: true

          @reload ?= (config = {}) ->
            params = {
              offset: @offset
              max:    @page
              sort:   @sort
              order:  @order
            }

            angular.extend(params, config)

            theLink = "#{modelCatalogueApiRoot}#{@base}"

            enhance rest method: 'GET', url: theLink, params: params , join: true


      # return new list decorator
      new ListDecorator(list)

    listEnhancer.createEmptyList = (extra = {}) ->
      enhanced = listEnhancer angular.extend({
        list: []
        size: 0
        next: ''
        previous: ''
        total: 0
        empty: true
        sort: 'name'
        order: 'asc'
      }, extra)
      enhanced.reload = -> $q.when(enhanced)
      enhanced

    listEnhancer.createArrayList = (array, extra = {}) ->
      enhanced = listEnhancer angular.extend({
        list: array
        size: array.length
        next: ''
        previous: ''
        total: array.length
        sort: 'name'
        order: 'asc'
      }, extra)
      enhanced.reload = -> $q.when(enhanced)
      enhanced

    listEnhancer.createSingletonList = (item, extra = {base: item.link}) ->
      enhanced = listEnhancer.createArrayList([item], extra)
      enhanced.itemType = item.elementType if not enhanced.itemType and item.elementType
      enhanced

    listEnhancer

  ]

  enhanceProvider.registerEnhancerFactory('list', condition, factory)
]
