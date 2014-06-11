#= require_self
#= require angular-ui-router/release/angular-ui-router
#= require ng-file-upload/angular-file-upload.js
#= require modelcatalogue/core/index
#= require decoratedList
#= require catalogueElementView
#= require importDataView
#= require catalogueElementTreeviewItem
#= require catalogueElementTreeview
#= require catalogueElementPicker
#= require propertiesPane
#= require messagesPanel
#= require columns
#= require columnsConfiguration
#= require simpleObjectEditor

angular.module('mc.core.ui', [
  # depends on
  'ui.router',
  'mc.core'
  # list of modules
  'mc.core.ui.decoratedList'
  'mc.core.ui.catalogueElementView'
  'mc.core.ui.importDataView'
  'mc.core.ui.catalogueElementTreeviewItem'
  'mc.core.ui.catalogueElementTreeview'
  'mc.core.ui.catalogueElementPicker'
  'mc.core.ui.propertiesPane'
  'mc.core.ui.messagesPanel'
  'mc.core.ui.columns'
  'mc.core.ui.columnsConfiguration'
  'mc.core.ui.simpleObjectEditor'
])