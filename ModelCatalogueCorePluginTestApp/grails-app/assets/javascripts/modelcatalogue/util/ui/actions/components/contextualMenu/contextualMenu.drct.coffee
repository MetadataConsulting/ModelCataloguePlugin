#= require contextualMenu.tpl.coffee
angular.module('modelcatalogue.util.ui.actions.components.contextualMenu').directive 'contextualMenu',  ['$compile', '$templateCache', 'actions', 'actionRoleAccess', ($compile, $templateCache, actions, actionRoleAccess)-> {
  restrict: 'E'
  replace:  true
  scope:
    scope:      '=?' # bidirectional optional
    role:       '@?' # one-way optional
    right:      '@?'

  templateUrl: 'modelcatalogue/util/ui/contextualMenu.html'

  link: ($scope, $element) ->
    getTemplate = (action) ->
      $templateCache.get(if action.children?.length or action.abstract then 'modelcatalogue/util/ui/menuItemDropdown.html' else 'modelcatalogue/util/ui/menuItemSingle.html')

    scopes = []

    $scope.$$actionWatcherToBeRemoved = $scope.$$actionWatcherToBeRemoved ? []

    removeWatchers =  ($scope) ->
      fn() for fn in $scope.$$actionWatcherToBeRemoved
      $scope.$$actionWatcherToBeRemoved = []


    collectWatchers =  (action, watches = []) ->
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


    updateActions =  ->
      removeWatchers($scope)
      scope.$destroy() for scope in scopes
      scopes = []

      $element.empty()
      for role in ($scope.role ? actionRoleAccess.ROLE_NAVIGATION_ACTION).split(',')
        for action in actions.getActions($scope.scope ? $scope.$parent, role)
          if action.children
            for child in action.children
              childClasses = []
              childClasses.push 'text-muted navbar-dropdown-heading' if child.heading
              childClasses.push 'active' if child.active
              childClasses.push 'disabled' if child.disabled

              child.$$class = childClasses.join ' '

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
