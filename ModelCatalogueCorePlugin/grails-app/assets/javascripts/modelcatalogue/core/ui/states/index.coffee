#= require_self
#= require angular-ui-router/release/angular-ui-router
#= require defaultStates


angular.module('mc.core.ui.states', [
  # depends on
  'ui.router'
  'mc.core.ui'
  # list of modules
  'mc.core.ui.states.defaultStates'
])