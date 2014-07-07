angular.module('mc.util.ui.actions', []).provider 'actions', ->
  availableActionsById      = {}
  actionsChildrenByParentId = {}
  actionsProvider           = {}

  registerActionInternal = (parentId, id, actionFactory) ->
    throw "Missing action id" if not id
    if parentId
      actionsChildrenByParentId[parentId] ?= {}
      actionsChildrenByParentId[parentId][id] = actionFactory
    else
      availableActionsById[id] = actionFactory

  actionsProvider.registerAction = (id, actionFactory) ->
    registerActionInternal(undefined, id, actionFactory)

  actionsProvider.registerChildAction = (parentId, id, actionFactory) ->
    registerActionInternal(parentId, id, actionFactory)

  actionsProvider.$get = [ '$injector', '$filter', '$rootScope', ($injector, $filter, $rootScope) ->

    createAction = (parentId, id, actionFactory, actionsService, $scope) ->
      action = $injector.invoke(actionFactory, undefined, {$scope: $scope, actions: actionsService})

      return if not action

      action.abstract = true unless action.action
      action.type     = 'default' unless action.type
      action.parent   = parentId
      action.id       = id

      if action.action
        unless action.disabled
          action.run = ->
            $rootScope.$broadcast "actionPerformed:#{action.id}", action.action()
        else
          action.run = ->
      else
        action.run = action.run ? ->

      actionChildren = actionsChildrenByParentId[action.id]

      if actionChildren
        action.children = []

        # disable sorting during generation
        action.sortChildren = ->

        for childId, childFactory of (actionChildren ? {})
          childAction = createAction(id, childId, childFactory, actionsService, $scope)

          continue if not childAction

          unless childAction.generator
            action.children.push childAction
            continue

          action.createActionsFrom = (watchExpression, createActionsFunction) ->
            updateChildActions = (input)->
              ret = $filter('filter')(action.children, (cha) -> cha.generatedBy != childAction.id and cha.id != childAction.id)
              createdActions = createActionsFunction(input) ? []
              for createdAction, i in createdActions
                createdAction.generatedBy = childAction.id
                createdAction.id          = createdAction.id ? "#{childAction.id}:#{i}"
                createdAction.position    = childAction.position + (1 + i)
                createdAction.run         = ->
                  $rootScope.$broadcast "actionPerformed:#{childAction.id}", createdAction.action()
                ret.push createdAction

              if createdActions?.length > 0
                ret.push childAction

              action.children = ret

              action.sortChildren()

            $scope.$watch watchExpression, updateChildActions

            updateChildActions($scope.$eval(watchExpression))

          childAction.heading = true

          childAction.generator(action, childAction)


        action.sortChildren = ->
          action.children = $filter('orderBy')(action.children, 'position')

        action.sortChildren()

      return action


    actions = {}
    actions.getActions = ($scope) ->
      currentActions = []
      for id, actionConfig of availableActionsById
        action = createAction(undefined, id, actionConfig, actions, $scope)
        currentActions.push action if action

      $filter('orderBy')(currentActions, 'position')

    actions
  ]

  actionsProvider