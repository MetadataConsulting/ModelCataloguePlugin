angular.module('mc.core.listReferenceEnhancer', ['mc.util.rest', 'mc.util.enhance', 'mc.core.modelCatalogueApiRoot']).config ['enhanceProvider', (enhanceProvider)->
  condition = (list) -> list.hasOwnProperty('count') and list.hasOwnProperty('link')
  factory   = ['modelCatalogueApiRoot', 'rest', '$rootScope', (modelCatalogueApiRoot, rest, $rootScope) ->
    (listReference, enhance = @enhance) ->
      link = "#{modelCatalogueApiRoot}#{listReference.link}"
      query = (tail = null) ->
        enhance rest method: 'GET', url: "#{link}#{if tail? then '/' + tail else ''}"
      query.total = listReference.count
      query.link = link.toString()
      query.itemType = listReference.itemType
      query.add = (tail, payload) ->
        if not payload?
          payload = tail
          tail = null
        enhance rest method: 'POST', url: "#{link}#{if tail? then '/' + tail else ''}", data: payload
      query.remove = (tail, payload) ->
        if not payload?
          payload = tail
          tail = null
        enhance(rest(method: 'DELETE', url: "#{link}#{if tail? then '/' + tail else ''}", data: payload)).then (result)->
          $rootScope.$broadcast 'catalogueElementDeleted', payload
          result
      query
  ]

  enhanceProvider.registerEnhancerFactory('listReference', condition, factory)
]
