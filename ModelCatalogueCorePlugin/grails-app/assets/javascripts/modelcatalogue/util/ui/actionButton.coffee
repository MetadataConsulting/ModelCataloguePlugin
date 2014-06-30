angular.module('mc.util.ui.actionButton', ['mc.util.ui.actionButtonSingle', 'mc.util.ui.actionButtonDropdown']).directive 'actionButton',  ['$compile', ($compile)-> {
  restrict: 'E'

  scope:
    action:     '='
    size:       '=?'
    iconOnly:   '=?'

  link: (scope, element) ->
    tpl = if scope.action.children then '<action-button-dropdown action="action" size="size" icon-only="iconOnly"></action-button-dropdown>' else  '<action-button-single action="action" size="size" icon-only="iconOnly"></action-button-single>'
    element.replaceWith($compile(tpl)(scope))
}]