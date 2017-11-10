angular.module('modelcatalogue.core.enhancersConf.actionEnhancer', ['mc.util.rest', 'mc.util.enhance', 'mc.core.modelCatalogueApiRoot']).config ['enhanceProvider', (enhanceProvider)->
  condition = (action) -> angular.isObject(action) and action.hasOwnProperty('link') && action.elementType == 'org.modelcatalogue.core.actions.Action'
  factory   = ['$q', 'modelCatalogueApiRoot', 'rest', '$rootScope', 'enhance', ($q, modelCatalogueApiRoot, rest, $rootScope, enhance) ->
    actionEnhancer = (action) ->
      action.run = ->
        enhance rest method: 'POST', url: "#{modelCatalogueApiRoot}#{action.link}/run"
      action.dismiss = ->
        enhance rest method: 'POST', url: "#{modelCatalogueApiRoot}#{action.link}/dismiss"
      action.reactivate = ->
        enhance rest method: 'POST', url: "#{modelCatalogueApiRoot}#{action.link}/reactivate"
      action.addDependency = (providerId, role)->
        enhance rest method: 'POST', url: "#{modelCatalogueApiRoot}#{action.link}/dependsOn", params: { providerId: providerId, role: role }
      action.removeDependency = (role)->
        enhance rest method: 'DELETE', url: "#{modelCatalogueApiRoot}#{action.link}/dependsOn", params: { role: role }
      action.updateParameters = ->
        enhance rest method: 'PUT', url: "#{modelCatalogueApiRoot}#{action.link}/parameters", data: @parameters
      action
    actionEnhancer
  ]

  enhanceProvider.registerEnhancerFactory('action', condition, factory)
]
