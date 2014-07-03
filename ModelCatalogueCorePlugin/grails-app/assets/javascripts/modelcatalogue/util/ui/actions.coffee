angular.module('mc.util.ui.actions', []).provider 'actions', ->
  createAction = (actionConfig, actionsService, $injector, $filter, $scope) ->
    condition = actionConfig.condition ? true
    if angular.isFunction(condition) or angular.isArray(condition)
      condition = $injector.invoke(condition, actionConfig, {$scope: $scope, actions: actionsService})

    return if not condition

    action =
      abstract: true
      type:     'default'
      disabled: condition == 'disabled'
      id:       actionConfig.id

    if actionConfig.action
      action.abstract = false
      unless action.disabled
        action.run = ->
          $injector.invoke(actionConfig.action, actionConfig, {$scope: $scope, action: action, actions: actionsService})
      else
        action.run = ->

    for property in ['label', 'position', 'icon', 'type']
      value = actionConfig[property]
      if angular.isFunction(value) or angular.isArray(value)
        action[property] = $injector.invoke(value, actionConfig, {$scope: $scope, action: action, actions: actionsService})
      else
        action[property] = value

    action.type ?= 'default'

    if actionConfig.children
      action.children = []

      # disable sorting for initial calls
      action.sortChildren = ->

      for childConfig in (actionConfig.children ? [])
        childAction = createAction(childConfig, actionsService, $injector, $filter, $scope)

        if childConfig.generator
          childAction.heading = true if childAction
          $injector.invoke(childConfig.generator, childConfig, {$scope: $scope, actions: actionsService, action: action, headingAction: childAction})

        action.children.push childAction if childAction


      action.sortChildren = ->
        action.children = $filter('orderBy')(action.children, 'position')

      action.sortChildren()

    return action

  availableActions          = []
  availableActionsById      = {}
  pendingChildrenByParentId = {}
  actionsProvider           = {}
  actionsProvider.registerAction = (actionConfig) ->
    if actionConfig.parent
      parent = availableActionsById[actionConfig.parent]
      if parent
        parent.children ?= []
        parent.children.push actionConfig
      else
        pendingChildrenByParentId[actionConfig.parent] ?= []
        pendingChildrenByParentId[actionConfig.parent].push actionConfig
    else
      availableActions.push actionConfig

    if actionConfig.id?
      availableActionsById[actionConfig.id] = actionConfig
      pendingChildren = pendingChildrenByParentId[actionConfig.id]
      actionConfig.children = (actionConfig.children ? []).contact(pendingChildren) if pendingChildren

  actionsProvider.$get = [ '$injector', '$filter', ($injector, $filter) ->
    actions = {}
    actions.getActions = ($scope) ->
      currentActions = []
      for actionConfig in availableActions
        action = createAction(actionConfig, actions, $injector, $filter, $scope)
        currentActions.push action if action

      $filter('orderBy')(currentActions, 'position')


    actions.getActionById = (id, $scope) ->
      createAction(availableActionsById[id], actions, $injector, $filter, $scope)

    actions
  ]

  actionsProvider