#= require menuItemDropdown.tpl.coffee
angular.module('mc.util.ui.contextualMenu.itemDropdown').directive 'menuItemDropdown',  [-> {
  restrict: 'E'
  replace: true
  scope:
    action:     '='

  templateUrl: 'modelcatalogue/util/ui/menuItemDropdown.html'
}]
