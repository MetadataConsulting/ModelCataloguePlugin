angular.module('mc.util.ui.contextualActions', ['mc.util.ui.bs.actionButtonSingle','mc.util.ui.bs.actionButtonDropdown']).directive 'contextualActions',  ['$compile', '$templateCache', 'actions', ($compile, $templateCache, actions)-> {
  restrict: 'E'
  replace:  true
 scope:
    group:      '@'
    size:       '@'
    iconOnly:   '@'


  templateUrl: 'modelcatalogue/util/ui/contextualActions.html'

  link: ($scope, $element) ->
    getTemplate = (action) ->
      $templateCache.get(if action.children then 'modelcatalogue/util/ui/actionButtonDropdown.html' else 'modelcatalogue/util/ui/actionButtonSingle.html')


    updateActions = ->
      $element.empty()
      for action in actions.getActions($scope.$parent)

        newScope = $scope.$new()
        newScope.action = action
        $element.append($compile(getTemplate(action))(newScope))

    updateActions()


    $scope.$on 'userLoggedIn', updateActions
    $scope.$on 'userLoggedOut', updateActions

    $scope.$on 'redrawContextualActions', updateActions
}]