#= require_self
#= require modelcatalogue/util/index
#= require catalogueElementEnhancer
#= require catalogueElementResource
#= require listEnhancer
#= require listReferenceEnhancer
#= require modelCatalogueApiRoot
#= require removableItemEnhancer
#= require resolvableItemEnhancer
#= require modelCatalogueSearch
#= require modelCatalogueDataArchitect


angular.module 'mc.core', [
  # depends on util
  'mc.util'
  # list of modules
  'mc.core.catalogueElementEnhancer'
  'mc.core.catalogueElementResource'
  'mc.core.listEnhancer'
  'mc.core.listReferenceEnhancer'
  'mc.core.modelCatalogueApiRoot'
  'mc.core.modelCatalogueSearch'
  'mc.core.modelCatalogueDataArchitect'
  'mc.core.removableItemEnhancer'
  'mc.core.resolvableItemEnhancer'
]
