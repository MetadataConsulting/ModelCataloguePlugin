#= require_self
#= require hideForRole
#= require showForRole
#= require hideIfLoggedIn
#= require showIfLoggedIn
#= require contextualActions
#= require actions
#= require actionButtonSingle
#= require actionButtonDropdown
#= require applicationTitle
#= require contextualActions
#= require contextualMenu
#= require focusMe
#= require menuItemDropdown
#= require menuItemSingle

angular.module 'mc.util.ui', [
  # dependencies
  'mc.util'

  # list of modules
  'mc.util.ui.hideForRole'
  'mc.util.ui.showForRole'
  'mc.util.ui.hideIfLoggedIn'
  'mc.util.ui.showIfLoggedIn'
  'mc.util.ui.contextualActions'
  'mc.util.ui.actions'
  'mc.util.ui.focusMe'
  'mc.util.ui.actionButtonSingle'
  'mc.util.ui.actionButtonDropdown'
  'mc.util.ui.applicationTitle'
  'mc.util.ui.contextualActions'
  'mc.util.ui.contextualMenu'
  'mc.util.ui.menuItemDropdown'
  'mc.util.ui.menuItemSingle'
]