#= require_self
#= require modelcatalogue/core/index
#= require decoratedList
#= require catalogueElementView
#= require propertiesPane
#= require columns

angular.module('mc.core.ui', [
  # depends on
  'mc.core'
  # list of modules
  'mc.core.ui.decoratedList'
  'mc.core.ui.catalogueElementView'
  'mc.core.ui.propertiesPane'
  'mc.core.ui.columns'
])