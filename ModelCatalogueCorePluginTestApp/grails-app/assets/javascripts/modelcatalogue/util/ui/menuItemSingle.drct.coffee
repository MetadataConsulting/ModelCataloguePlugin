angular.module('mc.util.ui.menuItemSingle', []).directive 'menuItemSingle',  [-> {
  restrict: 'E'
  replace: true
  scope:
    action:     '='

  templateUrl: 'modelcatalogue/util/ui/menuItemSingle.html'
}]