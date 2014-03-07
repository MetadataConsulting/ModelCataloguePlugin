#= require_self
#= require modelcatalogue/util/index
#= require catalogueElementEnhancer
#= require catalogueElementResource
#= require listEnhancer
#= require listReferenceEnhancer
#= require modelCatalogueApiRoot
#= require removableItemEnhancer
#= require modelcatalogueSearch


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
  'mc.core.removableItemEnhancer'
]