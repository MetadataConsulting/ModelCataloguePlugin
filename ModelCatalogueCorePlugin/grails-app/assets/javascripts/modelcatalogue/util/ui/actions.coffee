angular.module('mc.util.ui.actions', []).provider 'actions', ->
  createAction = (actionConfig, actionsService, $injector, actionContext) ->
    condition = actionConfig.condition ? true
    if angular.isFunction(condition) or angular.isArray(condition)
      condition = $injector.invoke(condition, actionConfig, {actionContext: actionContext, actions: actionsService})

    return if not condition

    action = {abstract: true}

    if actionConfig.action
      action = ->
        self = this
        $injector.invoke(actionConfig.action, actionConfig, {actionContext: actionContext, action: self, actions: actionsService})

    action.disabled   = condition == 'disabled'
    action.id         = actionConfig.id

    for property in ['label', 'position', 'icon', 'type']
      value = actionConfig[property]
      if angular.isFunction(value) or angular.isArray(value)
        action[property] = $injector.invoke(value, actionConfig, {actionContext: actionContext, action: action, actions: actionsService})
      else
        action[property] = value

    if actionConfig.children
      action.children = []
      for childConfig in actionConfig.children
        childAction = createAction(childConfig, actionsService, $injector, actionContext)
        action.children.push childAction if childAction

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
    actions.getActions = (actionContext) ->
      currentActions = []
      for actionConfig in availableActions
        action = createAction(actionConfig, actions, $injector, actionContext)
        currentActions.push action if action

      $filter('orderBy')(currentActions, 'position')

    actions
  ]

  actionsProvider