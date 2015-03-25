#= require_self
#= require modelcatalogue/core/index
#= require infiniteList
#= require infiniteTable
#= require infiniteListCtrl
#= require columnsSupportCtrl
#= require catalogueElementView
#= require csvTransformationView
#= require batchView
#= require catalogueElementTreeviewItem
#= require catalogueElementTreeview
#= require catalogueElementPicker
#= require catalogueElementIcon
#= require catalogueElementProperties
#= require propertiesPane
#= require messagesPanel
#= require columns
#= require expectCatalogueElement
#= require columnsConfiguration
#= require simpleObjectEditor
#= require elementsAsTags
#= require diffTable

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
  'mc.core.ui.csvTransformationView'
  'mc.core.ui.batchView'
  'mc.core.ui.catalogueElementTreeviewItem'
  'mc.core.ui.catalogueElementTreeview'
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
])