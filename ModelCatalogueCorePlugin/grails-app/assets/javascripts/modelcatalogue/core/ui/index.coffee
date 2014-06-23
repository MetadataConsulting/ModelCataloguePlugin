#= require_self
#= require angular-ui-router/release/angular-ui-router
#= require modelcatalogue/core/index
#= require decoratedList
#= require catalogueElementView
#= require catalogueElementTreeviewItem
#= require catalogueElementTreeview
#= require catalogueElementPicker
#= require catalogueElementProperties
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
  'mc.core.ui.catalogueElementTreeviewItem'
  'mc.core.ui.catalogueElementTreeview'
  'mc.core.ui.catalogueElementPicker'
  'mc.core.ui.catalogueElementProperties'
  'mc.core.ui.propertiesPane'
  'mc.core.ui.messagesPanel'
  'mc.core.ui.columns'
  'mc.core.ui.columnsConfiguration'
  'mc.core.ui.simpleObjectEditor'
])