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

      actionsScope = $scope.scope ? $scope.$parent

      for action in actions.getActions(actionsScope, $scope.role)
        watches = []
        hasActions = hasActions || true
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
            start = new Date().getTime()
            if angular.equals(newValue, oldValue)
              console.log('equals', (new Date().getTime() - start), newValue, oldValue, actionsScope.$id, actionsScope.$$watchers.length, (w.exp for w in actionsScope.$$watchers))
              return
            console.log('not equals - updating actions', (new Date().getTime() - start), newValue, oldValue, actionsScope.$id, actionsScope.$$watchers.length, (w.exp for w in actionsScope.$$watchers))
            updateActions()
          ))
          newScope.$on '$destroy', ->
            removeWatchers($scope)


      if not hasActions and $scope.noActions
        $element.append("""<em>No Actions</em>""")

    updateActions()


    $scope.$on 'userLoggedIn', updateActions
    $scope.$on 'userLoggedOut', updateActions
    $scope.$on 'redrawContextualActions', updateActions
}]