#= require_self
#= require modelcatalogue/util/index
#= require catalogueElementEnhancer
#= require catalogueElementResource
#= require catalogue
#= require listEnhancer
#= require actionEnhancer
#= require dateEnhancer
#= require listReferenceEnhancer
#= require modelCatalogueApiRoot
#= require removableItemEnhancer
#= require actionableItemEnhancer
#= require modelCatalogueSearch
#= require modelCatalogueDataArchitect


angular.module 'mc.core', [
  # depends on util
  'mc.util'
  # list of modules
  'mc.core.catalogueElementEnhancer'
  'mc.core.catalogueElementResource'
  'mc.core.catalogue'
  'mc.core.dateEnhancer'
  'mc.core.listEnhancer'
  'mc.core.actionEnhancer'
  'mc.core.listReferenceEnhancer'
  'mc.core.modelCatalogueApiRoot'
  'mc.core.modelCatalogueSearch'
  'mc.core.modelCatalogueDataArchitect'
  'mc.core.removableItemEnhancer'
  'mc.core.actionableItemEnhancer'
]
