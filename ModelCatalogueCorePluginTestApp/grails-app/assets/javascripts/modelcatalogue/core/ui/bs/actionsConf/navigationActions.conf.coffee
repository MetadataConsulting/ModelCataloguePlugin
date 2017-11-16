angular.module('modelcatalogue.core.ui.bs.actionsConf.navigationActions', ['mc.util.ui.actions', 'mc.util.security'])
.config (actionsProvider, names, actionRoleRegister, actionClass)->
  'ngInject'
  Action = actionClass
  ##############
  # Data Model #
  ##############

  # Import
  actionsProvider.registerChildAction 'catalogue-element', 'add-import', ($scope, messages, names, security, catalogue) ->
    'ngInject'
    return undefined unless $scope.element?.isInstanceOf?('dataModel')
    return undefined unless security.hasRole('CURATOR')
    Action.createStandardAction(
      position:   -1000
      label:      'Add Data Model Import'
      icon:       'fa fa-fw fa-puzzle-piece'
      type:       'success'
      action:     ->
        messages.prompt('Add Data Model Import', 'If you want to reuse data classes, data types or measurement units ' +
            'form different data models you need to import the containing data model first.',
          {type: 'catalogue-elements', resource: 'dataModel' }).then (elements) ->
            angular.forEach elements, (element) ->
              unless angular.isString(element)
                $scope.element.imports.add element
    )

  # Create
  angular.forEach [
    'dataClass'
    'dataElement'
    'dataType'
    'measurementUnit'
    'asset'
    'validationRule'
  ], (resource, index) ->
    actionsProvider.registerChildAction 'catalogue-element', 'catalogue-element-create-' + resource,
      ($scope, names, security, messages, $state, $log, catalogue, dataModelService) ->
        'ngInject'
        dataModel = dataModelService.anyParentDataModel($scope)
        return undefined unless security.hasRole('CURATOR')
        return undefined unless messages.hasPromptFactory('create-' + resource) or
          messages.hasPromptFactory('edit-' + resource)
        return undefined unless $scope.element?.isInstanceOf?('dataModel') or resource is 'dataClass' and $scope.element?.isInstanceOf?('dataClass')

        Action.createStandardAction(
          position: 5000 + index
          label: "New #{names.getNaturalName(resource)}"
          icon: catalogue.getIcon(resource)
          type: 'success'
          action:     ->
            args =
              create: resource
              currentDataModel: dataModel

            args.parent = $scope.element if resource is 'dataClass' and $scope.element.isInstanceOf('dataClass')
            args.type = if messages.hasPromptFactory('create-' + resource) then "create-#{resource}" else "edit-#{resource}"

            security.requireRole('CURATOR')
            .then ->
              messages.prompt('Create ' + names.getNaturalName(resource), '', args).then ->
                $state.go 'dataModel.resource.list', {status: 'draft'}, {reload: true}
            , (errors)->
              $log.error errors
              messages.error('You don\'t have rights to create new elements')
        )
          .disabledIf $scope.element.status != 'DRAFT'




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
