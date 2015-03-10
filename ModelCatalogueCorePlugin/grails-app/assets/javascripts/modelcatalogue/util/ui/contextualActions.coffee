angular.module('mc.util.ui.contextualActions', ['mc.util.ui.bs.actionButtonSingle','mc.util.ui.bs.actionButtonDropdown']).directive 'contextualActions',  ['$compile', '$templateCache', 'actions', ($compile, $templateCache, actions)-> {
  restrict: 'E'
  replace:  true
 scope:
    scope:      '=?'
    group:      '@'
    size:       '@'
    iconOnly:   '@'
    noColors:   '@'
    role:       '@?'
    noActions:  '@?'


  templateUrl: 'modelcatalogue/util/ui/contextualActions.html'

  link: ($scope, $element) ->
    getTemplate = (action) ->
      $templateCache.get(if action.children?.length or action.abstract then 'modelcatalogue/util/ui/actionButtonDropdown.html' else 'modelcatalogue/util/ui/actionButtonSingle.html')


    updateActions = ->
      hasActions = false
      $element.empty()
      for action in actions.getActions($scope.scope ? $scope.$parent, $scope.role)
        hasActions = hasActions || true
        newScope = $scope.$new()
        newScope.action = action
        $element.append($compile(getTemplate(action))(newScope))

      if not hasActions and $scope.noActions
        $element.append("""<em>No Actions</em>""")

    updateActions()


    $scope.$on 'userLoggedIn', updateActions
    $scope.$on 'userLoggedOut', updateActions

    $scope.$on 'redrawContextualActions', updateActions
}]