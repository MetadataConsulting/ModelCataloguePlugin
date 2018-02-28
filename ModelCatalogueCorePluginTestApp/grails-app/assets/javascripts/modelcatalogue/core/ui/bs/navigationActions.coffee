angular.module('mc.core.ui.bs.navigationActions', ['mc.util.ui.actions', 'mc.util.security'])
.config (actionsProvider, names)->
  'ngInject'

  ##############
  # Data Model #
  ##############

  # Import
  actionsProvider.registerChildAction 'catalogue-element', 'add-import', ($scope, messages, names, security, catalogue) ->
    'ngInject'
    return undefined if not $scope.element
    return undefined if not angular.isFunction($scope.element.isInstanceOf)
    return undefined if not $scope.element.isInstanceOf('dataModel')
    return undefined if not security.hasRole('CURATOR')

    {
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
    }

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
        return undefined unless messages.hasPromptFactory('create-' + resource) or messages.hasPromptFactory('edit-' + resource)
        return undefined unless angular.isFunction($scope.element.isInstanceOf)
        return undefined unless $scope.element.isInstanceOf('dataModel') or resource is 'dataClass' and $scope.element.isInstanceOf('dataClass')

        {
          label: "New #{names.getNaturalName(resource)}"
          icon: catalogue.getIcon(resource)
          type: 'success'
          position: 5000 + index
          disabled: $scope.element.status != 'DRAFT'
          action:     ->
            args =
              create: resource
              currentDataModel: dataModel

            args.parent = $scope.element if resource is 'dataClass' and $scope.element.isInstanceOf('dataClass')
            args.type = if messages.hasPromptFactory('create-' + resource) then "create-#{resource}" else "edit-#{resource}"

            security.requireRole('CURATOR')
            .then ->
              messages.prompt('Create ' + names.getNaturalName(resource), '', args).then ->
                $state.go 'mc.resource.list', {status: 'draft'}, {reload: true}
            , (errors)->
              $log.error errors
              messages.error('You don\'t have rights to create new elements')
        }




  actionsProvider.registerActionInRole 'create-data-model', 'data-models' ,['$scope', 'names', 'security', 'messages', '$state', '$log', ($scope, names, security, messages, $state, $log) ->
      return undefined
      # return undefined unless security.hasRole('CURATOR')
      {
        label:      "Create"
        icon:       'fa fa-fw fa-plus-circle'
        type:       'success'
        position:   0
        action:     ->
          args      = {create: 'dataModel', type: 'create-dataModel'}

          security.requireRole('CURATOR')
          .then ->
            messages.prompt('Create New Data Model', '', args).then (dataModel) ->
              dataModel.show()
          , (errors)->
            $log.error errors
            messages.error('You don\'t have rights to create new elements')
      }
  ]

  actionsProvider.registerActionInRole 'all-data-models', actionsProvider.ROLE_GLOBAL_ACTION ,['security', '$scope', '$state', (security, $scope, $state) ->
    return undefined if not security.isUserLoggedIn()

    {
      position:   3000
      label: 'Show All Data Models'
      icon:  'fa fa-book fa-fw'
      action: ->
        $state.go 'dataModels'
    }
  ]

  actionsProvider.registerActionInRole 'global-draft', actionsProvider.ROLE_GLOBAL_ACTION, ['$state', '$stateParams', 'catalogue', ($state, $stateParams, catalogue) ->
    return undefined unless $state.current.name == 'mc.resource.list'
    return undefined unless catalogue.isInstanceOf($stateParams.resource, 'catalogueElement')

    {
      icon: 'fa fa-pencil'
      label: 'Switch to Drafts'
      run: -> $state.go '.', {status: 'draft'}, {reload: true}
    }
  ]

  actionsProvider.registerActionInRole 'global-pending', actionsProvider.ROLE_GLOBAL_ACTION, ['$state', '$stateParams', 'catalogue', ($state, $stateParams, catalogue) ->
    return undefined unless $state.current.name == 'mc.resource.list'
    return undefined unless catalogue.isInstanceOf($stateParams.resource, 'catalogueElement')

    {
      icon: 'fa fa-clock-o'
      label: 'Switch to Pending'
      run: -> $state.go '.', {status: 'pending'}, {reload: true}
    }
  ]

  actionsProvider.registerActionInRole 'global-finalized', actionsProvider.ROLE_GLOBAL_ACTION, ['$state', '$stateParams', 'catalogue', ($state, $stateParams, catalogue) ->
    return undefined unless $state.current.name == 'mc.resource.list'
    return undefined unless catalogue.isInstanceOf($stateParams.resource, 'catalogueElement')

    {
      icon: 'fa fa-check'
      label: 'Switch to Finalized'
      run: -> $state.go '.', {status: undefined}, {reload: true}
    }
  ]

  actionsProvider.registerActionInRole 'about-dialog', actionsProvider.ROLE_GLOBAL_ACTION, ['messages', (messages) ->

    {
      icon: 'fa fa-question'
      label: 'Model Catalogue Version'
      action: ->  messages.prompt('','', type: 'about-dialog')
    }
  ]
