angular.module('mc.core.listReferenceDecorator', ['mc.util.rest', 'mc.util.enhance', 'mc.core.modelCatalogueApiRoot', 'mc.util.createConstantPromise']).config ['enhanceProvider', (enhanceProvider)->
  condition = (list) -> list.hasOwnProperty('count') and list.hasOwnProperty('link')
  factory   = ['modelCatalogueApiRoot', 'rest', (modelCatalogueApiRoot, rest) ->
    (listReference, enhance = @enhance) ->
      query = () ->
        result = rest method: 'GET', url: "#{modelCatalogueApiRoot}#{listReference.link}"
        console.log(result)
        result.then (result) -> console.log result
        enhance result
      query.total = listReference.count
      query.link  = "#{modelCatalogueApiRoot}#{listReference.link}".toString()
      query
  ]

  enhanceProvider.registerEnhancerFactory('listReferenceDecorator', condition, factory)
]
