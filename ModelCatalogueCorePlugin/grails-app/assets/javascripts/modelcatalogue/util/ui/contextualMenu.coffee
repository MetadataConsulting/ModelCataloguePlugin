angular.module('mc.util.ui.contextualMenu', ['mc.util.ui.bs.menuItemDropdown','mc.util.ui.bs.menuItemSingle']).directive 'contextualMenu',  ['$compile', '$templateCache', 'actions', ($compile, $templateCache, actions)-> {
  restrict: 'E'
  replace:  true
 scope:
    scope:      '=?'
    role:       '@?'
    right:      '@?'

  templateUrl: 'modelcatalogue/util/ui/contextualMenu.html'

  link: ($scope, $element) ->
    getTemplate = (action) ->
      $templateCache.get(if action.children?.length or action.abstract then 'modelcatalogue/util/ui/menuItemDropdown.html' else 'modelcatalogue/util/ui/menuItemSingle.html')


    updateActions = ->
      $element.empty()
      for action in actions.getActions($scope.scope ? $scope.$parent, $scope.role ? actions.ROLE_NAVIGATION)
        if action.active and action.disabled
          action.$$class = 'active disabled'
        else if action.active
          action.$$class = 'active'
        else if action.disabled
          action.$$class = 'disabled'

        newScope = $scope.$new()
        newScope.action = action
        $element.append($compile(getTemplate(action))(newScope))

    updateActions()


    $scope.$on 'userLoggedIn', updateActions
    $scope.$on 'userLoggedOut', updateActions
    $scope.$on 'redrawContextualActions', updateActions
}]