angular.module('mc.util.ui.actions', []).provider 'actions', ->
  availableActionsById      = {}
  actionsChildrenByParentId = {}
  actionsProvider           = {}

  registerActionInternal = (parentId, id, actionFactory) ->
    throw {message: "Missing action id"} if not id
    throw {message: "id must be string", id: id} if not angular.isString(id)
    throw {message: "parent id must be string", parentId: parentId} if parentId and not angular.isString(parentId)
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
            generatorAction = childAction
            updateChildActions = (input)->
              ret = $filter('filter')(action.children, (cha) -> cha.generatedBy != generatorAction.id and cha.id != generatorAction.id)
              createdActions = createActionsFunction(input) ? []
              for createdAction, i in createdActions
                createdAction.generatedBy = generatorAction.id
                createdAction.id          = createdAction.id ? "#{generatorAction.id}:#{i}"
                createdAction.position    = generatorAction.position + (1 + i)
                createdAction.run         = ->
                  $rootScope.$broadcast "actionPerformed:#{generatorAction.id}", createdAction.action()
                ret.push createdAction

              if createdActions?.length > 0
                ret.push generatorAction

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


    ###
      Retrieves top-level action by it's id for the current scope. Mainly for the testing purposes.
    ###
    actions.getActionById = (id, $scope) ->
      createAction(undefined, id, availableActionsById[id], actions, $scope)

    actions
  ]

  actionsProvider