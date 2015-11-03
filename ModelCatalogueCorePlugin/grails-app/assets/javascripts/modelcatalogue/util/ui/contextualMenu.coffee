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

    scopes = []

    $scope.$$actionWatcherToBeRemoved = $scope.$$actionWatcherToBeRemoved ? []

    removeWatchers = ($scope) ->
      fn() for fn in $scope.$$actionWatcherToBeRemoved
      $scope.$$actionWatcherToBeRemoved = []


    collectWatchers = (action, watches = []) ->
      if angular.isArray(action.watches)
        for w in action.watches
          if w and watches.indexOf(w) == -1
            watches.push w
      else if action.watches
        if watches.indexOf(action.watches) == -1
          watches.push action.watches

      if angular.isArray(action.children) and action.children.length != 0
        for child in action.children
          collectWatchers(child, watches)

      watches

    updateActions = ->
      removeWatchers($scope)
      hasActions = false
      scope.$destroy() for scope in scopes
      scopes = []

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

        scopes.push newScope

        if angular.isArray(action.watches)
          watches = action.watches
        else if action.watches
          watches.push action.watches

        watches = collectWatchers(action)

        $element.append($compile(getTemplate(action))(newScope))

        if watches.length > 0
          $scope.$$actionWatcherToBeRemoved.push(actionsScope.$watchGroup(watches, (newValue, oldValue) ->
            if angular.equals(newValue, oldValue)
              return
            updateActions()
          ))
          newScope.$on '$destroy', ->
            removeWatchers($scope)

    updateActions()


    $scope.$on 'userLoggedIn', updateActions
    $scope.$on 'userLoggedOut', updateActions
    $scope.$on 'redrawContextualActions', updateActions
}]