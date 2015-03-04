#= require_self
#= require defaultStates
#= require advancedSearch


angular.module('mc.core.ui.states', [
  # depends on
  'mc.util'
  'mc.core.ui'
  # list of modules
  'mc.core.ui.states.defaultStates'
  'mc.core.ui.states.advancedSearch'
])