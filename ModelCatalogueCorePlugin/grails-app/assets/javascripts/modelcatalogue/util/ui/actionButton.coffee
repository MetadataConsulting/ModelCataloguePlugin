angular.module('mc.util.ui.actionButton', []).directive 'actionButton',  [-> {
  restrict: 'E'
  replace: true
  scope:
    action: '=?'

  templateUrl: 'modelcatalogue/util/ui/actionButton.html'
}]