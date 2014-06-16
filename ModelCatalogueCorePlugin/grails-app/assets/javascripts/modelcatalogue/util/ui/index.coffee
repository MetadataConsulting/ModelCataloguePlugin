#= require_self
#= require hideForRole
#= require showForRole
#= require hideIfNotLoggedIn
#= require showIfNotLoggedIn

angular.module 'mc.util.ui', [
  # dependencies
  'mc.util'

  # list of modules
  'mc.util.ui.hideForRole'
  'mc.util.ui.showForRole'
  'mc.util.ui.hideIfNotLoggedIn'
  'mc.util.ui.showIfNotLoggedIn'
]