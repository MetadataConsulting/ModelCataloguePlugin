angular.module('mc.core.listReferenceDecorator', ['mc.util.rest', 'mc.util.enhance', 'mc.core.modelCatalogueApiRoot', 'mc.util.createConstantPromise']).config ['enhanceProvider', (enhanceProvider)->
  condition = (list) -> list.hasOwnProperty('count') and list.hasOwnProperty('link')
  factory   = ['modelCatalogueApiRoot', 'rest', (modelCatalogueApiRoot, rest) ->
    (listReference, enhance = @enhance) ->
      link = "#{modelCatalogueApiRoot}#{listReference.link}"
      query = (tail = null) -> enhance rest method: 'GET', url: "#{link}#{if tail? then '/' + tail else ''}"
      query.total = listReference.count
      query.link  = link.toString()
      query
  ]

  enhanceProvider.registerEnhancerFactory('listReferenceDecorator', condition, factory)
]
