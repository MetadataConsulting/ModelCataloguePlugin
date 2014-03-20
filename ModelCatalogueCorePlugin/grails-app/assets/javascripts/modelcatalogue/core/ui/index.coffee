#= require_self
#= require modelcatalogue/core/index
#= require decoratedList
#= require catalogueElementView
#= require propertiesPane

angular.module('mc.core.ui', [
  # depends on
  'mc.core'
  # list of modules
  'mc.core.ui.decoratedList'
  'mc.core.ui.catalogueElementView'
  'mc.core.ui.propertiesPane'
])