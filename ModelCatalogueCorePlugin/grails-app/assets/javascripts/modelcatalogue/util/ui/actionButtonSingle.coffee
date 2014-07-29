angular.module('mc.util.ui.actionButtonSingle', []).directive 'actionButtonSingle',  [-> {
  restrict: 'E'
  replace: true
  scope:
    action:     '='
    size:       '=?'
    iconOnly:   '=?'
    noColors:   '=?'

  templateUrl: 'modelcatalogue/util/ui/actionButtonSingle.html'
}]