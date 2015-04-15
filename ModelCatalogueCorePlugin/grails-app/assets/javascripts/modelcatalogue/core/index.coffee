angular.module 'mc.core', [
  # depends on util
  'mc.util'
  # list of modules
  'mc.core.catalogueElementEnhancer'
  'mc.core.catalogueElementResource'
  'mc.core.catalogue'
  'mc.core.dateEnhancer'
  'mc.core.elementEnhancer'
  'mc.core.listEnhancer'
  'mc.core.actionEnhancer'
  'mc.core.listReferenceEnhancer'
  'mc.core.modelCatalogueApiRoot'
  'mc.core.modelCatalogueSearch'
  'mc.core.modelCatalogueDataArchitect'
  'mc.core.removableItemEnhancer'
  'mc.core.actionableItemEnhancer'
  'mc.core.orderedMapEnhancer'
]
