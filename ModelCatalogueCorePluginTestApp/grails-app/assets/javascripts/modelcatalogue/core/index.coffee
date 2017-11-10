angular.module 'mc.core', [
  # depends on util
  'mc.util'
  'ui.bootstrap'
  # list of modules

  'mc.core.catalogueElementResource'
  'mc.core.catalogue'
  'modelcatalogue.core.enhancersConf'
  'mc.core.modelCatalogueApiRoot'
  'mc.core.modelCatalogueSearch'
  'mc.core.modelCatalogueDataArchitect'
  'mc.core.serverPushUpdates'
]
angular.module('mc.core.modelCatalogueApiRoot', []).value 'modelCatalogueApiRoot', '/api/modelCatalogue/core'
angular.module('mc.core').config(
  ['$animateProvider', ($animateProvider) ->
    $animateProvider.classNameFilter(/^((?!(fa-spinner)).)*$/)
  ]
)
