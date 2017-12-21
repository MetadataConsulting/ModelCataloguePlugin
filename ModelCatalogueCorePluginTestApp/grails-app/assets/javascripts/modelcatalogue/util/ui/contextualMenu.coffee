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

#    moved to actionsProvider code

#    makeCSSClassesForchild = (child) ->
#      childClasses = []
#      childClasses.push 'text-muted navbar-dropdown-heading' if child.heading
#      childClasses.push 'active' if child.active
#      childClasses.push 'disabled' if child.disabled
#
#      child.$$class = childClasses.join ' '
#
    updateActions = ->
      removeWatchers($scope)
      scope.$destroy() for scope in scopes
      scopes = []

      $element.empty()
      for role in ($scope.role ? actions.ROLE_NAVIGATION).split(',')
        for action in actions.getActions($scope.scope ? $scope.$parent, role)
#          if action.children
#            for child in action.children
#             makeCSSClassesForchild(child)

          classes = []
          classes.push('active') if action.active
          classes.push('disabled') if action.disabled
          classes.push('icon-only') if action.iconOnly

          action.$$class = classes.join(' ')

          newScope = $scope.$new()
          newScope.action = action

          scopes.push newScope

          watches = collectWatchers(action)

          $element.append($compile(getTemplate(action))(newScope))

          if watches.length > 0
            $scope.$$actionWatcherToBeRemoved.push($scope.$watchGroup(watches, (newValue, oldValue) ->
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
