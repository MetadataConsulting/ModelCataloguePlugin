#= require_self
#= require contextualActions
#= require actionButtonSingle
#= require actionButtonDropdown

angular.module 'mc.util.ui.bs', [
  # dependencies
  'mc.util.ui'

  # list of modules
  'mc.util.ui.bs.contextualActions'
  'mc.util.ui.bs.actionButtonSingle'
  'mc.util.ui.bs.actionButtonDropdown'
]