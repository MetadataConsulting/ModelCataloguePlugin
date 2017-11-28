###
  Here be all front-end actions related to backend Actions/Batches
###
angular.module('modelcatalogue.core.ui.states.catalogue.actions.actionsConf',['mc.util.ui.actions', 'modelcatalogue.core.ui.states.catalogue.actions.modalPromptGenerateSuggestions']).config (actionsProvider, names, actionRoleRegister, actionClass) ->
  'ngInject'
  Action = actionClass

###
  Individual Action actions
###

  actionsProvider.registerActionInRole 'run-action', actionRoleRegister.ROLE_ACTION_ACTION, ['$scope', ($scope) ->
    return undefined unless $scope.action and $scope.action.state == 'PENDING'

    Action.createStandardAction(
      position: 200
      label: 'Run'
      icon: 'glyphicon glyphicon-play'
      type: 'success'
      action: ->
        $scope.action.run().then ->
          $scope.reload() if angular.isFunction($scope.reload)
          $scope.batch.$$reload()  if angular.isFunction($scope.batch?.$$reload)
    )
  ]


  actionsProvider.registerActionInRole 'dismiss-action', actionRoleRegister.ROLE_ACTION_ACTION, ['$scope', ($scope) ->
    return undefined unless $scope.action and $scope.action.state == 'PENDING'

    Action.createStandardAction(
      position: 500
      label: 'Dismiss'
      icon: 'glyphicon glyphicon-remove'
      type: 'danger'
      action: ->
        $scope.action.dismiss().then ->
          $scope.reload() if angular.isFunction($scope.reload)
          $scope.batch.$$reload() if angular.isFunction($scope.batch?.$$reload)
    )
  ]


  actionsProvider.registerActionInRole 'reactivate-action', actionRoleRegister.ROLE_ACTION_ACTION, ['$scope', ($scope) ->
    return undefined unless $scope.action and $scope.action.state == 'DISMISSED'

    Action.createStandardAction(
      position: 200
      label: 'Reactivate'
      icon: 'glyphicon glyphicon-repeat'
      type: 'success'
      action: ->
        $scope.action.reactivate().then ->
          $scope.reload() if angular.isFunction($scope.reload)
          $scope.batch.$$reload()  if angular.isFunction($scope.batch?.$$reload)
    )
  ]

  actionsProvider.registerActionInRole 'repeat-action', actionRoleRegister.ROLE_ACTION_ACTION, ['$scope', ($scope) ->
    return undefined unless $scope.action and $scope.action.state == 'FAILED'

    Action.createStandardAction(
      position: 900
      label: 'Retry'
      icon: 'glyphicon glyphicon-repeat'
      type: 'success'
      action: ->
        $scope.action.reactivate().then ->
          $scope.reload() if angular.isFunction($scope.reload)
          $scope.batch.$$reload()  if angular.isFunction($scope.batch?.$$reload)
    )
  ]

  ###
  Both for individual action and for batch.
  ###
  actionsProvider.registerActionInRoles 'reload-actions', [actionRoleRegister.ROLE_ACTION_ACTION, actionRoleRegister.ROLE_ITEM_ACTION,
    actionRoleRegister.ROLE_NAVIGATION_ACTION], ['$scope', ($scope) ->
    return undefined unless angular.isFunction($scope.batch?.$$reload) and ($scope.action and $scope.action.state == 'PERFORMING') or ($scope.batch and not $scope.action)

    Action.createStandardAction(
      position: 900
      label: 'Reload'
      icon: 'glyphicon glyphicon-refresh'
      type: 'success'
      action: ->
        if $scope.batch?.$$reload
          $scope.batch?.$$reload()
          return
          $scope.reload() if angular.isFunction($scope.reload)
          $scope.batch.$$reload()  if angular.isFunction($scope.batch?.$$reload)
    )
  ]

  actionsProvider.registerActionInRole 'link-actions', actionRoleRegister.ROLE_ACTION_ACTION, ['$scope', '$rootScope', 'messages',
    ($scope, $rootScope, messages) ->
      return undefined unless $scope.action and
        $scope.action.state != 'PERFORMING' and
        $scope.action.state != 'PERFORMED'

      action = Action.createStandardAction(
        position: 950
        label: 'Add or Remove Dependency'
        icon: 'glyphicon glyphicon-open'
        type: 'primary'
        action: ->
          if $rootScope.selectedAction == $scope.action
            $rootScope.selectedAction = undefined
          else
            if @mode == 'select'
              $rootScope.selectedAction = $scope.action
            else
              selected = $rootScope.selectedAction
              if @mode == 'add'
                messages.prompt('Add Dependency', 'Please, provide the name of the role for the new dependency').then (role) ->
                  selected.addDependency($scope.action.id, role).then ->
                    $scope.reload() if angular.isFunction($scope.reload)
                    $scope.batch.$$reload()  if angular.isFunction($scope.batch?.$$reload)
              else if @mode == 'remove'
                messages.confirm('Remove Dependency', 'Do you really want to remove dependency between these two actions? This may cause problems executing given action!').then ->
                  selected.removeDependency(selected.dependsOn['' + $scope.action.id]).then ->
                    $scope.reload() if angular.isFunction($scope.reload)
                    $scope.batch.$$reload()  if angular.isFunction($scope.batch?.$$reload)
              $rootScope.selectedAction = undefined


      ).watching (-> $rootScope.selectedAction)

      if $rootScope.selectedAction
        if $rootScope.selectedAction == $scope.action
          action.active = true
          action.icon = 'glyphicon glyphicon-open'
          action.label = 'Add or Remove Dependency'
          action.mode = 'select'
        else
          action.active = false
          if $rootScope.selectedAction.dependsOn.hasOwnProperty('' + $scope.action.id)
            action.icon = 'glyphicon glyphicon-remove-circle'
            action.label = 'Remove Dependency'
            action.mode = 'remove'
          else
            action.icon = 'glyphicon glyphicon-save'
            action.label = 'Select as Dependency'
            action.mode = 'add'

      else
        action.icon = 'glyphicon glyphicon-open'
        action.active = false
        action.label = 'Add or Remove Dependency'
        action.mode = 'select'

      action
  ]



  actionsProvider.registerActionInRole 'update-action-parameters', actionRoleRegister.ROLE_ACTION_ACTION, ['$scope', 'messages', 'names',
    'security', ($scope, messages, names, security) ->
      return undefined unless $scope.action
      return undefined unless $scope.action.state not in ['PERFORMING', 'PERFORMED']
      return undefined unless security.hasRole('CURATOR')

      Action.createStandardAction(
        position: 100
        label: 'Update Action Parameters'
        icon: 'glyphicon glyphicon-edit'
        type: 'primary'

        action: ->
          messages.prompt('Update Action Parameters', '', {type: 'update-action-parameters', action: $scope.action}).then (updated)->
            $scope.action = updated
      ).watching 'action.state'
        .disabledIf $scope.action.state in ['PERFORMING', 'PERFORMED']

  ]


###
  Batches actions (actually seen from catalogue/resource=batch/list). There might be a better place to put this but for now it's with the Action actions.
###

  actionsProvider.registerActionInRole 'generate-suggestions', actionRoleRegister.ROLE_LIST_ACTION, ['$scope', 'security',
    'catalogue', 'modelCatalogueApiRoot', 'enhance', 'rest', 'messages', '$state',
    ($scope, security, catalogue, modelCatalogueApiRoot, enhance, rest, messages, $state)->
      return undefined unless security.isUserLoggedIn()
      return undefined unless catalogue.isInstanceOf($scope.list?.itemType, 'batch')
      Action.createStandardAction(
        position: 100
        label: 'Generate Suggestions'
        icon: 'fa fa-flash'
        type: 'primary'
        action: ->
          messages.prompt('Generate Suggestions', "Select a type of optimization. Suggestions of this type will be generated when you submit your selection. This may take a long time, depending on complexity of the catalogue. You can rerun the action later to clean all resolved batches generated by this action.", {type: 'generate-suggestions'}).then ->
            $state.go('.', {page: undefined}, {reload: true})
      )
  ]

  actionsProvider.registerActionInRole 'refresh-batches', actionRoleRegister.ROLE_LIST_ACTION, ['$state', '$scope',
    'security', 'catalogue', ($state, $scope, security, catalogue)->
      return undefined unless security.isUserLoggedIn()
      return undefined unless catalogue.isInstanceOf($scope.list?.itemType, 'batch')
      Action.createStandardAction(
        position: 0
        label: 'Refresh'
        icon: 'fa fa-refresh'
        type: 'primary'
        action: ->
          $state.go('.', {page: undefined}, {reload: true})
      )
  ]

  ###
  Only seen from dataModel.resource=batch.list
  ###
  actionsProvider.registerActionInRole 'switch-archived-batches', actionRoleRegister.ROLE_LIST_ACTION, ['$state', '$scope',
    '$stateParams', ($state, $scope, $stateParams) ->
      return undefined unless $state.current.name == 'dataModel.resource.list' and $scope.list and $stateParams.resource == 'batch'

      Action.createAbstractAction(
        position: 500
        label: if $stateParams.status == 'archived' then 'Archived' else 'Active'
        icon: null
        type: if $stateParams.status == 'archived' then 'info' else 'glyphicon glyphicon-ok'
      )
  ]

  actionsProvider.registerChildAction 'switch-archived-batches', 'switch-archived-batches-active', ['$state',
    '$stateParams', ($state, $stateParams) ->
      Action.createStandardAction(
        position: 300
        label: "Active"
        icon: 'glyphicon glyphicon-ok'
        type: 'primary'
        action: ->
          newParams = angular.copy($stateParams)
          newParams.status = undefined
          $state.go 'dataModel.resource.list', newParams
      ).activeIf !$stateParams.status
  ]

  actionsProvider.registerChildAction 'switch-archived-batches', 'switch-archived-batches-archived', ['$state',
    '$stateParams', ($state, $stateParams) ->
      Action.createStandardAction(
        position: 200
        label: "Archived"
        icon: 'glyphicon glyphicon-time'
        type: 'warning'
        action: ->
          newParams = angular.copy($stateParams)
          newParams.status = 'archived'
          $state.go 'dataModel.resource.list', newParams
      ).activeIf $stateParams.status == 'archived'
  ]

  ###
    Batch actions in ROLE_ITEM_ACTION
  ###

  actionsProvider.registerActionInRole 'archive-batch', actionRoleRegister.ROLE_ITEM_ACTION, ['$rootScope', '$scope',
    'messages', 'names', 'security', 'enhance', 'rest', 'modelCatalogueApiRoot',
    ($rootScope, $scope, messages, names, security, enhance, rest, modelCatalogueApiRoot) ->
      return undefined unless $scope.element?.isInstanceOf?('batch') or $scope.batch
      return undefined unless security.hasRole('CURATOR')

      Action.createStandardAction(
        position: 150
        label: 'Archive'
        icon: 'glyphicon glyphicon-compressed'
        type: 'danger'
        action: ->
          batch = $scope.batch ? $scope.element
          messages.confirm("Do you want to archive batch #{batch.name} ?", "The batch #{batch.name} will be archived").then ->
            enhance(rest(url: "#{modelCatalogueApiRoot}#{batch.link}/archive", method: 'POST')).then (archived) ->
              batch.updateFrom archived
            , showErrorsUsingMessages(messages)
      ).watching ['batch.archived', 'element.archived']
        .disabledIf ($scope.batch ? $scope.element).archived
  ]



  actionsProvider.registerActionInRole 'run-all-actions-in-batch', actionRoleRegister.ROLE_ITEM_ACTION, ['$scope',
    'messages', 'modelCatalogueApiRoot', 'enhance', 'rest', '$timeout', 'security',
    ($scope, messages, modelCatalogueApiRoot, enhance, rest, $timeout, security) ->
      return undefined unless security.hasRole('CURATOR')
      return undefined unless $scope.element?.isInstanceOf?('batch') or $scope.batch

      action = Action.createStandardAction(
        position: 200
        label: 'Run All Pending'
        icon: 'glyphicon glyphicon-flash'
        type: 'success'
        action: ->
          batch = $scope.batch ? $scope.element
          messages.confirm('Run All Actions', "Do you really wan to run all actions from '#{batch.name}' batch").then ->
            enhance(rest(method: 'POST', url: "#{modelCatalogueApiRoot}#{batch.link}/run")).then (updated) ->
              batch.updateFrom(updated)
            $timeout($scope.reload, 1000) if angular.isFunction($scope.reload)
      ).watching ['batch', 'element']

      updateDisabled = (batch) ->
        return unless batch
        action.disabled = not batch.pending.total

      updateDisabled($scope.batch ? $scope.element)

      action

  ]
