#= require menuItemSingle.tpl.coffee
angular.module('modelcatalogue.util.ui.actions.components.contextualMenu.itemSingle').directive 'menuItemSingle',  [-> {
  restrict: 'E'
  replace: true
  scope:
    action:     '='

  templateUrl: 'modelcatalogue/util/ui/menuItemSingle.html'
}]
