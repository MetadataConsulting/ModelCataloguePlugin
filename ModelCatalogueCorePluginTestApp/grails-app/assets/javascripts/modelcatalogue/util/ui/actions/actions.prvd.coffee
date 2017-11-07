angular.module('mc.util.ui.actions').provider 'actions', ->

  # actionsChildrenByParentId[parentId][childId] = actionFactory
  # parent-child relation only goes one level deep.
  # according to this structure, a parent action is actually exactly like a role, just one level below.
  parentChildActionMap = {}
  # roleIdActionMap[role][actionId] = actionFactory
  # actions stored here are definitely not children
  roleIdActionMap      = {}

  actionsProvider           = {}


  registerActionInternal = (parentId, id, actionFactory, roles) ->
    throw {message: "Missing action id"} if not id
    throw {message: "id must be string", id: id} if not angular.isString(id)
    throw {message: "parent id must be string", parentId: parentId} if parentId and not angular.isString(parentId)

    rolesChildErrorMessage = "Action registered with roles if, AND ONLY IF, it is NOT a child action"

    if parentId
      if roles
        throw {message: rolesChildErrorMessage}
      else # no roles
        parentChildActionMap[parentId] ?= {}
        parentChildActionMap[parentId][id] = actionFactory
    else # no parent
      if roles
        angular.forEach roles, (role)->
          roleIdActionMap[role] ?= {}
          roleIdActionMap[role][id] = actionFactory
      else # no roles
        throw {message: rolesChildErrorMessage}


  actionsProvider.registerActionInRole  = (id, role, actionFactory) ->
    registerActionInternal(undefined, id, actionFactory, [role])

  actionsProvider.registerActionInRoles = (id, roles, actionFactory) ->
    registerActionInternal(undefined, id, actionFactory, roles)

  actionsProvider.registerChildAction = (parentId, id, actionFactory) ->
    registerActionInternal(parentId, id, actionFactory, undefined)

  actionsProvider.$get = [ '$injector', '$filter', '$rootScope', '$q', '$log', '$timeout', ($injector, $filter, $rootScope, $q, $log, $timeout) ->
    printTwoLevelMap = (map, levelOneLabel, levelTwoLabel) ->
      for id1 of map
        console.log("#{levelOneLabel}: #{id1}")
        for id2 of map[id1]
          console.log("  #{levelTwoLabel}: #{id2}")

    createAction = (parentId, id, actionFactory, actionsService, $scope) ->
# use DI to invoke the factory
      try
        action = $injector.invoke(actionFactory, undefined, {$scope: $scope, actions: actionsService})
      catch e
        $log.error e

      return if not action

      action.abstract = true unless action.action
      action.type     = 'default' unless action.type
      action.parent   = parentId
      action.id       = id

      if action.action
        action.run = ->
          unless action.disabled
            $q.when(action.action()).then (result) ->
              $rootScope.$broadcast "actionPerformed", action.id, $q.when(result)
      else
        action.run = action.run ? ->

      actionChildren = parentChildActionMap[action.id]

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
                createdAction.id          = createdAction.id ? "#{generatorAction.id}_#{i}"
                createdAction.position    = generatorAction.position + (1 + i)
                createdAction.run         = ->
                  $rootScope.$broadcast "createdAction:#{@id}", @action()
                ret.push createdAction

              if createdActions?.length > 0
                ret.push generatorAction

              action.children = ret

              action.sortChildren()
              action.watches = watchExpression

            updateChildActions($scope.$eval(watchExpression))

          childAction.heading = true

          childAction.generator(action, childAction)


        action.sortChildren = ->
          action.children = $filter('orderBy')(action.children, 'position')

        action.sortChildren()

      return action

    actions = {}
    actions.printRoleIdActionMap = () ->
      printTwoLevelMap(roleIdActionMap, "Role", "Action ID")
    actions.printParentChildActionMap = () ->
      printTwoLevelMap(parentChildActionMap, "Parent", "Child")


    actions.getActions = ($scope, role) ->
      throw {message: "role undefined in #{$scope}"} if not role
      currentActions = []
      for id, actionConfig of roleIdActionMap[role]
        action = createAction(undefined, id, actionConfig, actions, $scope)
        currentActions.push action if action

      $filter('orderBy')(currentActions, if role is actionsProvider.ROLE_GLOBAL_ACTION then 'label' else 'position')


    ###
      Retrieves top-level action by it's id for the current scope. Mainly for the testing purposes.
    ###
    actions.getActionById = (id, $scope, role) ->
      throw {message: "Role needed for getActionById"} if not role
      createAction(undefined, id, roleIdActionMap[role][id], actions, $scope)

    actions
  ]

  actionsProvider
