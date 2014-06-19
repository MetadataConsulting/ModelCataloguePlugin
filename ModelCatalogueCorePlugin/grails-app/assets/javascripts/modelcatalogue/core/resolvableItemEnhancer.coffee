angular.module('mc.core.resolvableItemEnhancer', ['mc.util.rest', 'mc.util.enhance', 'mc.core.modelCatalogueApiRoot']).config ['enhanceProvider', (enhanceProvider)->
  condition = (item) -> item.hasOwnProperty('resolveAllLink')
  factory   = ['modelCatalogueApiRoot', 'rest', '$rootScope', 'enhance', (modelCatalogueApiRoot, rest, $rootScope, enhance) ->
    (element) ->
      link = "#{modelCatalogueApiRoot}#{element.resolveLink}"
      element.resolveAll = () ->
        debugger
        enhance(rest(method: 'POST', url: link, data: element)).then (result)->
          $rootScope.$broadcast 'actionsResolved', element
          result

      element

  ]

  enhanceProvider.registerEnhancerFactory('resolvableItem', condition, factory)
]
