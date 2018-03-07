angular.module 'mc.core', [
  # depends on util
  'mc.util'
  'ui.bootstrap'
  # list of modules
  'mc.core.catalogueElementEnhancer'
  'mc.core.catalogueElementResource'
  'mc.core.catalogue'
  'mc.core.dateEnhancer'
  'mc.core.elementEnhancer'
  'mc.core.listEnhancer'
  'mc.core.actionEnhancer'
  'mc.core.promiseEnhancer'
  'mc.core.listReferenceEnhancer'
  'mc.core.modelCatalogueApiRoot'
  'mc.core.modelCatalogueSearch'
  'mc.core.modelCatalogueDataArchitect'
  'mc.core.removableItemEnhancer'
  'mc.core.actionableItemEnhancer'
  'mc.core.orderedMapEnhancer'
  'mc.core.serverPushUpdates'
  'mc.core.publishedStatuses'
]

angular.module('mc.core').config(
  ['$animateProvider', ($animateProvider) ->
    $animateProvider.classNameFilter(/^((?!(fa-spinner)).)*$/)
  ]
)
