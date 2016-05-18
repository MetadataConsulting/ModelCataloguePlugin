angular.module('mc.core.listReferenceEnhancer', ['mc.util.rest', 'mc.util.enhance', 'mc.core.modelCatalogueApiRoot', 'ui.router']).config (enhanceProvider)->
  condition = (list) -> list.hasOwnProperty('count') and list.hasOwnProperty('link')
  factory   = (modelCatalogueApiRoot, rest, $rootScope, enhance, $q, $state) ->
    "ngInject"
    (listReference) ->
      link = "#{modelCatalogueApiRoot}#{listReference.link}"
      query = (tail = null, params = {}) ->
        if not params.dataModel and $state.params.dataModelId and not URI(link).hasQuery('dataModel')
          params = angular.extend({dataModel: $state.params.dataModelId}, params)

        enhance rest method: 'GET', url: "#{link}#{if tail? then '/' + tail else ''}", params: params
      query.total = listReference.count
      query.link = link.toString()
      query.base = listReference.link
      query.itemType = listReference.itemType
      query.add = (tail, payload, update = false) ->
        if not payload?
          payload = tail
          tail = null
        if not payload.elementType?
          payload.elementType = listReference.itemType

        url = "#{link}#{if tail? then '/' + tail else ''}"
        enhance(rest(method: 'POST', url: url, data: payload)).then (result)->
          $rootScope.$broadcast 'catalogueElementCreated', result, url, payload unless update
          result
      query.remove = (tail, payload) ->
        if not payload?
          payload = tail
          tail = null
        if not payload.elementType?
          payload.elementType = listReference.itemType

        url = "#{link}#{if tail? then '/' + tail else ''}"
        enhance(rest(method: 'DELETE', url: url, data: payload)).then (result)->
          $rootScope.$broadcast 'catalogueElementDeleted', payload, result, url
          result

      query.reorder = (moved, current) ->
        enhance(rest(method: 'PUT', url: link, data: {moved: moved, current: current})).then (result)->
          if result.error
            return $q.reject(result.error)
          $rootScope.$broadcast 'listReferenceReordered', query, moved, current
          result

      query.toString = ->
        """listReference {\n  link: #{@link},\n  total: #{@total},\n  base: #{@base},\n  itemType: #{@itemType} \n}"""

      query
  enhanceProvider.registerEnhancerFactory('listReference', condition, factory)
