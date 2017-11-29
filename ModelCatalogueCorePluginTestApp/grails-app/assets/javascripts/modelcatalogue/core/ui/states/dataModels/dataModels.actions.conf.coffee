angular.module('modelcatalogue.core.ui.states.dataModels.actionConf', ['mc.util.ui.actions',
'modelcatalogue.core.domain.catalogueElement.create.dataModelWizard']).config (actionsProvider, names, actionRoleRegister, actionClass) ->
  'ngInject'
  Action = actionClass

  actionsProvider.registerActionInRole 'import-data-models-screen', actionRoleRegister.ROLE_DATA_MODELS_ACTION, [
    'security',
    (security) ->
      return undefined unless security.hasRole('CURATOR')
      action = Action.createAbstractAction(
        position: 10000
        label: 'Import'
        icon: 'fa fa-fw fa-upload'
        type: 'primary'
      )
      action.expandToLeft= true
      return action
  ]


  actionsProvider.registerActionInRole 'create-data-model', actionRoleRegister.ROLE_DATA_MODELS_ACTION ,['$scope', 'names', 'security', 'messages', '$state', '$log', ($scope, names, security, messages, $state, $log) ->
    return undefined unless security.hasRole('CURATOR')

    Action.createStandardAction(
      position:   0
      label:      "Create"
      icon:       'fa fa-fw fa-plus-circle'
      type:       'success'
      action:     ->
        args      = {create: 'dataModel', type: 'create-dataModel'}

        security.requireRole('CURATOR')
        .then ->
          messages.prompt('Create New Data Model', '', args).then (dataModel) ->
            dataModel.show()
        , (errors)->
          $log.error errors
          messages.error('You don\'t have rights to create new elements')
    )

  ]
