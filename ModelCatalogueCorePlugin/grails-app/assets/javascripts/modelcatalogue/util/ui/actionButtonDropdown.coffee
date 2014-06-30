angular.module('mc.util.ui.actionButtonDropdown', []).directive 'actionButtonDropdown',  [-> {
  restrict: 'E'
  replace: true
  scope:
    action:     '='
    size:       '=?'
    iconOnly:   '=?'

  templateUrl: 'modelcatalogue/util/ui/actionButtonDropdown.html'
}]