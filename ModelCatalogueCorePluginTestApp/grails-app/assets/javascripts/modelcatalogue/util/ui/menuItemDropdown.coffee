angular.module('mc.util.ui.menuItemDropdown', []).directive 'menuItemDropdown',  [-> {
  restrict: 'E'
  replace: true
  scope:
    action:     '='

  templateUrl: 'modelcatalogue/util/ui/menuItemDropdown.html'
}]