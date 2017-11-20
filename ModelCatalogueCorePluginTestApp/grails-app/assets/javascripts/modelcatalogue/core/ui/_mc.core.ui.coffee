angular.module('mc.core.ui', [
  # depends on
  'ui.router',
  'mc.core'
  # list of modules
  'mc.core.ui.infiniteList'
  'mc.core.ui.infiniteTable'
  'mc.core.ui.infiniteListCtrl'
  'mc.core.ui.columnsSupportCtrl'
  'mc.core.ui.catalogueElementView'
  'mc.core.ui.batchView'
  'modelcatalogue.core.components.catalogueElementTreeview'
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
  'mc.core.ui.diffTable'
  'modelcatalogue.core.sections.metadataEditors'
  'modelcatalogue.core.sections.detailSections'
  'modelcatalogue.core.sections.detailSections.metadata'
  'modelcatalogue.core.components.metadataEditor'
  'mc.core.ui.simpleObjectEditor'
])
