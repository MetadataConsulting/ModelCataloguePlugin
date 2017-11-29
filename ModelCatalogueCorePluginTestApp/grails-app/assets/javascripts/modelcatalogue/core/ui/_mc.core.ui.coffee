angular.module('mc.core.ui', [
  # depends on
  'ui.router',
  'mc.core.ui.bs'
  'mc.core.ui.modals'
  'mc.core.ui.utils'
  'mc.core.ui.general'

  'modelcatalogue.core.ui.imports'
  'modelcatalogue.core.ui.exports'
  'modelcatalogue.core.ui.navigationRight'
  'modelcatalogue.core.ui.states'
  # list of modules
  'mc.core.ui.columnsSupportCtrl'
  'mc.core.ui.catalogueElementIcon'
  'mc.core.ui.catalogueElementPicker'
  'mc.core.ui.expectCatalogueElement'
  'mc.core.ui.catalogueElementProperties'
  'mc.core.ui.propertiesPane'
  'mc.core.ui.messagesPanel'
  'mc.core.ui.columns'
  'mc.core.ui.columnsConfiguration'
  'mc.core.ui.simpleObjectEditor'
  'mc.core.ui.elementsAsTags'
  'modelcatalogue.core.sections.metadataEditors'
  'modelcatalogue.core.sections.detailSections'
  'modelcatalogue.core.sections.detailSections.metadata'
  'modelcatalogue.core.components.metadataEditor'
  'mc.core.ui.simpleObjectEditor'
])
