### NavigationRight is essentially the only place where modalSearchForActions is triggered, which is the only place where ROLE_GLOBAL_ACTION is used.
###
angular.module('modelcatalogue.core.ui.navigationRight.globalActions', ['mc.util.ui.actions', 'mc.util.security']).config (actionsProvider, names, actionRoleRegister, actionClass)->
  'ngInject'
  Action = actionClass

  actionsProvider.registerActionInRole 'all-data-models', actionRoleRegister.ROLE_GLOBAL_ACTION ,['security', '$scope', '$state', (security, $scope, $state) ->
    return undefined unless security.isUserLoggedIn()

    Action.createStandardAction(
      position:   3000
      label: 'Show All Data Models'
      icon:  'fa fa-book fa-fw'
      type: null
      action: ->
        $state.go 'dataModels'
    )
  ]
  # do global-draft, global-pending, global-finalized really need to define the field 'run' instead of 'action' unlike every other action? Not really.

  actionsProvider.registerActionInRole 'global-draft', actionRoleRegister.ROLE_GLOBAL_ACTION, ['$state', '$stateParams', 'catalogue', ($state, $stateParams, catalogue) ->
    return undefined unless $state.current.name == 'dataModel.resource.list'
    return undefined unless catalogue.isInstanceOf($stateParams.resource, 'catalogueElement')

    Action.createStandardAction(
      position: null
      label: 'Switch to Drafts'
      icon: 'fa fa-pencil'
      type: null
      action: -> $state.go '.', {status: 'draft'}, {reload: true}
    )
  ]

  actionsProvider.registerActionInRole 'global-pending', actionRoleRegister.ROLE_GLOBAL_ACTION, ['$state', '$stateParams', 'catalogue', ($state, $stateParams, catalogue) ->
    return undefined unless $state.current.name == 'dataModel.resource.list'
    return undefined unless catalogue.isInstanceOf($stateParams.resource, 'catalogueElement')

    Action.createStandardAction(
      position: null
      label: 'Switch to Pending'
      icon: 'fa fa-clock-o'
      type: null
      action: -> $state.go '.', {status: 'pending'}, {reload: true}
    )
  ]

  actionsProvider.registerActionInRole 'global-finalized', actionRoleRegister.ROLE_GLOBAL_ACTION, ['$state', '$stateParams', 'catalogue', ($state, $stateParams, catalogue) ->
    return undefined unless $state.current.name == 'dataModel.resource.list'
    return undefined unless catalogue.isInstanceOf($stateParams.resource, 'catalogueElement')
    Action.createStandardAction(
      position: null
      label: 'Switch to Finalized'
      icon: 'fa fa-check'
      type: null
      action: -> $state.go '.', {status: undefined}, {reload: true}
    )
  ]

  actionsProvider.registerActionInRole 'about-dialog', actionRoleRegister.ROLE_GLOBAL_ACTION, ['messages', (messages) ->
    Action.createStandardAction(
      position: null
      label: 'Model Catalogue Version'
      icon: 'fa fa-question'
      type: null
      action: ->  messages.prompt('','', type: 'about-dialog')
    )
  ]
