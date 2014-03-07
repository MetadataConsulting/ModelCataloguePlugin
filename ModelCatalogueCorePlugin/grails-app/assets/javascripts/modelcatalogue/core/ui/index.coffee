#= require_self
#= require modelcatalogue/core/index
#= require decoratedList
#= require decoratedListTable

angular.module('mc.core.ui', [
  # depends on
  'mc.core'
  # list of modules
  'mc.core.ui.decoratedList'
  'mc.core.ui.decoratedListTable'
])