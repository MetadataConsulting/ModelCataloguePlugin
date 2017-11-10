#= require menuItemDropdown.tpl.coffee
angular.module('modelcatalogue.util.ui.actions.components.contextualMenu.itemDropdown').directive 'menuItemDropdown',  [-> {
  restrict: 'E'
  replace: true
  scope:
    action:     '='

  templateUrl: 'modelcatalogue/util/ui/menuItemDropdown.html'
}]
