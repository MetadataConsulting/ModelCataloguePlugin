angular.module('modelcatalogue.core.enhancersConf.removableItemEnhancer', ['mc.util.rest', 'mc.util.enhance', 'mc.core.modelCatalogueApiRoot']).config ['enhanceProvider', (enhanceProvider)->
  condition = (item) -> item.hasOwnProperty('removeLink')
  factory   = ['modelCatalogueApiRoot', 'rest', '$rootScope', 'enhance', (modelCatalogueApiRoot, rest, $rootScope, enhance) ->
    (element) ->
      link = "#{modelCatalogueApiRoot}#{element.removeLink}"
      element.remove = () ->
        enhance(rest(method: 'DELETE', url: link, data: element)).then (result)->
          $rootScope.$broadcast 'catalogueElementDeleted', element
          result

      element

  ]

  enhanceProvider.registerEnhancerFactory('removableItem', condition, factory)
]
