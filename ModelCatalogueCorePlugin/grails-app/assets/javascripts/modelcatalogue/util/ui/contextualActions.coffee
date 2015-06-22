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

    scopes = []

    updateActions = ->
      hasActions = false
      scope.$destroy() for scope in scopes
      scopes = []

      $element.empty()

      actionsScope = $scope.scope ? $scope.$parent

      for action in actions.getActions(actionsScope, $scope.role)
        watches = []
        hasActions = hasActions || true
        newScope = $scope.$new()
        newScope.action = action

        scopes.push newScope

        if angular.isArray(action.watches)
          watches =  action.watches
        else if action.watches
          watches.push action.watches

        $element.append($compile(getTemplate(action))(newScope))

        if watches.length > 0
          removeWatchers = actionsScope.$watchGroup watches, (newValue, oldValue) ->
            if angular.equals(newValue, oldValue)
              return
            updateActions()
          newScope.$on '$destroy', ->
            removeWatchers()

      if not hasActions and $scope.noActions
        $element.append("""<em>No Actions</em>""")

    updateActions()


    $scope.$on 'userLoggedIn', updateActions
    $scope.$on 'userLoggedOut', updateActions
    $scope.$on 'redrawContextualActions', updateActions
}]