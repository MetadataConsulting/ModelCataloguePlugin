angular.module('mc.core.actionEnhancer', ['mc.util.rest', 'mc.util.enhance', 'mc.core.modelCatalogueApiRoot']).config ['enhanceProvider', (enhanceProvider)->
  condition = (action) -> action.hasOwnProperty('link') && action.elementType == 'org.modelcatalogue.core.actions.Action'
  factory   = ['$q', 'modelCatalogueApiRoot', 'rest', '$rootScope', 'enhance', ($q, modelCatalogueApiRoot, rest, $rootScope, enhance) ->
    actionEnhancer = (action) ->
      action.run = ->
        enhance rest method: 'POST', url: "#{modelCatalogueApiRoot}#{action.link}/run"
      action.dismiss = ->
        enhance rest method: 'POST', url: "#{modelCatalogueApiRoot}#{action.link}/dismiss"
      action.reactivate = ->
        enhance rest method: 'POST', url: "#{modelCatalogueApiRoot}#{action.link}/reactivate"
      action
    actionEnhancer
  ]

  enhanceProvider.registerEnhancerFactory('action', condition, factory)
]
