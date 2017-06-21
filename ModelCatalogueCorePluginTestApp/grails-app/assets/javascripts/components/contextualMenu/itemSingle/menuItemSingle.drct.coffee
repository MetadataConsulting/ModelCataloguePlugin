#= require menuItemSingle.tpl.coffee
angular.module('mc.util.ui.contextualMenu.itemSingle').directive 'menuItemSingle',  [-> {
  restrict: 'E'
  replace: true
  scope:
    action:     '='

  templateUrl: 'modelcatalogue/util/ui/menuItemSingle.html'
}]
