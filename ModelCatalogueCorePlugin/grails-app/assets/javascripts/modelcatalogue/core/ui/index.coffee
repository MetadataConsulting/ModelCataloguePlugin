#= require_self
#= require modelcatalogue/core/index
#= require decoratedList
#= require catalogueElementView
#= require catalogueElementTreeviewItem
#= require catalogueElementTreeview
#= require catalogueElementPicker
#= require propertiesPane
#= require messagesPanel
#= require columns

angular.module('mc.core.ui', [
  # depends on
  'mc.core'
  # list of modules
  'mc.core.ui.decoratedList'
  'mc.core.ui.catalogueElementView'
  'mc.core.ui.catalogueElementTreeviewItem'
  'mc.core.ui.catalogueElementTreeview'
  'mc.core.ui.catalogueElementPicker'
  'mc.core.ui.propertiesPane'
  'mc.core.ui.messagesPanel'
  'mc.core.ui.columns'
])