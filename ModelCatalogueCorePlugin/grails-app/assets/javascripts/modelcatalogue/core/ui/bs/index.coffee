#= require_self
#= require angular-bootstrap/ui-bootstrap-tpls
#= require modelcatalogue/core/index
#= require decoratedList
#= require decoratedListTable

angular.module('mc.core.ui.bs', [
  # depends on
  'mc.core.ui'
  'ui.bootstrap'
  # list of modules
  'mc.core.ui.bs.decoratedListTable'
])