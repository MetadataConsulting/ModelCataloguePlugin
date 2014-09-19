#= require_self
#= require contextualActions
#= require contextualMenu
#= require actionButtonSingle
#= require actionButtonDropdown
#= require menuItemSingle
#= require menuItemDropdown

angular.module 'mc.util.ui.bs', [
  # dependencies
  'mc.util.ui'

  # list of modules
  'mc.util.ui.bs.contextualActions'
  'mc.util.ui.bs.contextualMenu'
  'mc.util.ui.bs.actionButtonSingle'
  'mc.util.ui.bs.actionButtonDropdown'
  'mc.util.ui.bs.menuItemSingle'
  'mc.util.ui.bs.menuItemDropdown'
]