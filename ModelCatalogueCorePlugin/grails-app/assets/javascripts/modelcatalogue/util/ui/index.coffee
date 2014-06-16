#= require_self
#= require hideForRole
#= require showForRole
#= require hideIfLoggedIn
#= require showIfLoggedIn

angular.module 'mc.util.ui', [
  # dependencies
  'mc.util'

  # list of modules
  'mc.util.ui.hideForRole'
  'mc.util.ui.showForRole'
  'mc.util.ui.hideIfLoggedIn'
  'mc.util.ui.showIfLoggedIn'
]