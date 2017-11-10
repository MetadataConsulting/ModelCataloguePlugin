angular.module('modelcatalogue.util.ui.actions.components.contextualActions')
.directive('contextualActions', ($compile, $templateCache, actions, actionRoleAccess) -> {
  'ngInject'
  restrict: 'E'
  replace: true
  scope:
    scope: '=?'
    group: '@'
    size: '@'
    iconOnly: '@'
    noColors: '@'
    role: '@?'
    noActions: '@?'

  templateUrl: '/modelcatalogue/util/ui/actions/components/contextualActions/contextualActions.html'

  link: ($scope, $element) ->
    getTemplate = (action) ->
      templateName = '/modelcatalogue/util/ui/actions/components/contextualActions/actionButtonSingle.html'
      if action.submit
        templateName = '/modelcatalogue/util/ui/actions/components/contextualActions/actionButtonSingleSubmit.html'
      if action.children?.length or action.abstract
        templateName = '/modelcatalogue/util/ui/actions/components/contextualActions/actionButtonDropdown.html'
      $templateCache.get(templateName)

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
      content = []

      actionsScope = $scope.scope ? $scope.$parent

      for role in ($scope.role ? actionRoleAccess.ROLE_ITEM_ACTION).split(',')
        for action in actions.getActions(actionsScope, role)
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

          content.push($compile(getTemplate(action))(newScope))

          if watches.length > 0
            $scope.$$actionWatcherToBeRemoved.push(actionsScope.$watchGroup(watches, (newValue, oldValue) ->
              if angular.equals(newValue, oldValue)
                return
              updateActions()
            ))
            newScope.$on '$destroy', ->
              removeWatchers($scope)

      if not hasActions and $scope.noActions
        content.push("""<em>No Actions</em>""")

      $element.empty().append(content)

    updateActions()

    $scope.$on 'userLoggedIn', updateActions
    $scope.$on 'userLoggedOut', updateActions
    $scope.$on 'redrawContextualActions', updateActions
})
