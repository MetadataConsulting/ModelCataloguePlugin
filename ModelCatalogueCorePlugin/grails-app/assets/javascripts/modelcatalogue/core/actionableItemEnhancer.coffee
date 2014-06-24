angular.module('mc.core.actionableItemEnhancer', ['mc.util.rest', 'mc.util.enhance', 'mc.core.modelCatalogueApiRoot']).config ['enhanceProvider', (enhanceProvider)->
  condition = (item) -> item.hasOwnProperty('actionLinks')
  factory   = ['modelCatalogueApiRoot', 'rest', '$rootScope', 'enhance', (modelCatalogueApiRoot, rest, $rootScope, enhance) ->
    (element) ->
      link = "#{modelCatalogueApiRoot}#{element.actionLinks}"
      element.action = () ->
        enhance(rest(method: 'POST', url: link, data: element)).then (result)->
          $rootScope.$broadcast 'actionsResolved', element
          result
      element
  ]

  enhanceProvider.registerEnhancerFactory('action', condition, factory)
]
