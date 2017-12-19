angular.module('mc.util.ui.actions', []).provider 'actions', ->

  actionsRolesById          = {}
  actionsChildrenByParentId = {}
  availableActionsById      = {}
  actionsProvider           =
    ROLE_NAVIGATION:              'navigation'
    ROLE_NAVIGATION_RIGHT:        'navigation-right'
    ROLE_LIST_ACTION:             'list'
    ROLE_ITEM_ACTION:             'item'
    ROLE_ITEM_DETAIL_ACTION:      'item-detail'
    ROLE_ITEM_INFINITE_LIST:     'item-infinite-list'
    ROLE_MODAL_ACTION:            'modal'
    ROLE_LIST_HEADER_ACTION:      'header'
    ROLE_LIST_FOOTER_ACTION:      'footer'
    ROLE_GLOBAL_ACTION:           'global-action'

  getRoleAwareId = (role, id) -> "role_#{role}_#{id}"


  registerActionInternal = (parentId, id, actionFactory, roles) ->
    throw {message: "Missing action id"} if not id
    throw {message: "id must be string", id: id} if not angular.isString(id)
    throw {message: "parent id must be string", parentId: parentId} if parentId and not angular.isString(parentId)

    actionsRolesById[id] ?= roles

    if parentId
      roles = roles ? actionsRolesById[parentId]
      if not roles
        actionsChildrenByParentId[parentId] ?= {}
        actionsChildrenByParentId[parentId][id] = actionFactory
      else
        angular.forEach roles, (role)->
          actionsChildrenByParentId[getRoleAwareId(role, parentId)] ?= {}
          actionsChildrenByParentId[getRoleAwareId(role, parentId)][id] = actionFactory
    else
      if not roles
        availableActionsById[id] = actionFactory
      else
        angular.forEach roles, (role)->
          availableActionsById[getRoleAwareId(role, id)] = actionFactory

  actionsProvider.registerActionInRole  = (id, role, actionFactory) ->
    registerActionInternal(undefined, id, actionFactory, [role])

  actionsProvider.registerActionInRoles = (id, roles, actionFactory) ->
    registerActionInternal(undefined, id, actionFactory, roles)

  actionsProvider.registerAction = (id, actionFactory, roles = undefined) ->
    registerActionInternal(undefined, id, actionFactory, roles)

  actionsProvider.registerChildActionInRole = (parentId, id, role, actionFactory) ->
    registerActionInternal(parentId, id, actionFactory, [role])

  actionsProvider.registerChildActionInRoles = (parentId, id, roles, actionFactory) ->
    registerActionInternal(parentId, id, actionFactory, roles)

  actionsProvider.registerChildAction = (parentId, id, actionFactory, roles = undefined) ->
    registerActionInternal(parentId, id, actionFactory, roles)

  actionsProvider.$get = [ '$injector', '$filter', '$rootScope', '$q', '$log', '$timeout', ($injector, $filter, $rootScope, $q, $log, $timeout) ->

    createAction = (parentId, id, actionFactory, actionsService, $scope) ->
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

          # getExpression to get the reports JSON, which should return the reports and also assign them to the object specified by the watchExpression
          action.createActionsFrom = (getExpression, watchExpression, createActionsFunction) ->
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

            # resolve: if it's a promise, keep it a promise, otherwise wrap data into a promise.
            $q.resolve($scope.$eval(getExpression)).then (reportData) ->
              updateChildActions(reportData)


          childAction.heading = true

          childAction.generator(action, childAction)


        action.sortChildren = ->
          action.children = $filter('orderBy')(action.children, 'position')

        action.sortChildren()

      return action


    actions =
      ROLE_NAVIGATION:              actionsProvider.ROLE_NAVIGATION
      ROLE_NAVIGATION_RIGHT:        actionsProvider.ROLE_NAVIGATION_RIGHT
      ROLE_LIST_ACTION:             actionsProvider.ROLE_LIST_ACTION
      ROLE_ITEM_ACTION:             actionsProvider.ROLE_ITEM_ACTION
      ROLE_ITEM_DETAIL_ACTION:      actionsProvider.ROLE_ITEM_DETAIL_ACTION
      ROLE_MODAL_ACTION:            actionsProvider.ROLE_MODAL_ACTION
      ROLE_LIST_HEADER_ACTION:      actionsProvider.ROLE_LIST_HEADER_ACTION
      ROLE_ITEM_INFINITE_LIST:     actionsProvider.ROLE_ITEM_INFINITE_LIST
      ROLE_LIST_FOOTER_ACTION:      actionsProvider.ROLE_LIST_FOOTER_ACTION
      ROLE_GLOBAL_ACTION:           actionsProvider.ROLE_GLOBAL_ACTION

    actions.getActions = ($scope, role = undefined) ->
      currentActions = []
      for id, actionConfig of availableActionsById
        match = id.match(/^role_(.*)_(.*)$/)
        if not role and not match or role and match and match[1] == role
          action = createAction(undefined, id, actionConfig, actions, $scope)
          currentActions.push action if action

      $filter('orderBy')(currentActions, if role is actionsProvider.ROLE_GLOBAL_ACTION then 'label' else 'position')


    ###
      Retrieves top-level action by it's id for the current scope. Mainly for the testing purposes.
    ###
    actions.getActionById = (id, $scope, role = undefined) ->
      if role
        createAction(undefined, id, availableActionsById[getRoleAwareId(role, id)], actions, $scope)
      else
        createAction(undefined, id, availableActionsById[id], actions, $scope)

    actions
  ]

  actionsProvider
