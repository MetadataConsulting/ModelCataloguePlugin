angular.module('mc.core.listReferenceEnhancer', ['mc.util.rest', 'mc.util.enhance', 'mc.core.modelCatalogueApiRoot', 'mc.util.createConstantPromise']).config ['enhanceProvider', (enhanceProvider)->
  condition = (list) -> list.hasOwnProperty('count') and list.hasOwnProperty('link')
  factory   = ['modelCatalogueApiRoot', 'rest', (modelCatalogueApiRoot, rest) ->
    (listReference, enhance = @enhance) ->
      link = "#{modelCatalogueApiRoot}#{listReference.link}"
      query = (tail = null) -> enhance rest method: 'GET', url: "#{link}#{if tail? then '/' + tail else ''}"
      query.total = listReference.count
      query.link  = link.toString()
      query.add   = (tail, payload) ->
        if not payload?
          payload = tail
          tail    = null
        enhance rest method: 'POST', url: "#{link}#{if tail? then '/' + tail else ''}", data: payload
      query.remove = (tail, payload) ->
        if not payload?
          payload = tail
          tail    = null
        enhance rest method: 'DELETE', url: "#{link}#{if tail? then '/' + tail else ''}", data: payload
      query
  ]

  enhanceProvider.registerEnhancerFactory('listReference', condition, factory)
]
