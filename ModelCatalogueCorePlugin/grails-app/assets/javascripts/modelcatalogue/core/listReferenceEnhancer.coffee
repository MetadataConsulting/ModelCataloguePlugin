angular.module('mc.core.listReferenceEnhancer', ['mc.util.rest', 'mc.util.enhance', 'mc.core.modelCatalogueApiRoot']).config ['enhanceProvider', (enhanceProvider)->
  condition = (list) -> list.hasOwnProperty('count') and list.hasOwnProperty('link')
  factory   = ['modelCatalogueApiRoot', 'rest', '$rootScope', 'enhance', (modelCatalogueApiRoot, rest, $rootScope, enhance) ->
    (listReference) ->
      link = "#{modelCatalogueApiRoot}#{listReference.link}"
      query = (tail = null, params = {}) ->
        enhance rest method: 'GET', url: "#{link}#{if tail? then '/' + tail else ''}", params: params
      query.total = listReference.count
      query.link = link.toString()
      query.base = listReference.link
      query.itemType = listReference.itemType
      query.add = (tail, payload) ->
        if not payload?
          payload = tail
          tail = null
        if not payload.elementType?
          payload.elementType = listReference.itemType

        url = "#{link}#{if tail? then '/' + tail else ''}"
        enhance(rest(method: 'POST', url: url, data: payload)).then (result)->
          $rootScope.$broadcast 'catalogueElementCreated', result, url, payload
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
      query
  ]

  enhanceProvider.registerEnhancerFactory('listReference', condition, factory)
]
