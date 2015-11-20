angular.module('mc.core.ui.bs.navigationActions', ['mc.util.ui.actions', 'mc.util.security']).config ['actionsProvider', 'names', (actionsProvider, names)->

  RESOURCES = [
    'dataClass'
    'dataElement'
    'dataType'
    'measurementUnit'
    'asset'
    'relationshipType'
    'csvTransformation'
    'batch'
  ]

  actionsProvider.registerActionInRole 'navbar-catalogue-elements', actionsProvider.ROLE_NAVIGATION, ['security', (security) ->
    return undefined if not security.isUserLoggedIn()
    {
      position:   100
      abstract:   true
      label:      'Catalogue'
    }
  ]


  angular.forEach RESOURCES, (resource, index) ->

    unless resource == 'batch'
      actionsProvider.registerActionInRole 'global-create-' + resource, actionsProvider.ROLE_GLOBAL_ACTION, ['$scope', 'names', 'security', 'messages', '$state', '$log', ($scope, names, security, messages, $state, $log) ->
        return undefined if not security.hasRole('CURATOR')
        return undefined if not messages.hasPromptFactory('create-' + resource) and not messages.hasPromptFactory('edit-' + resource)

        {
        label:      "New #{names.getNaturalName(resource)}"
        icon:       'fa fa-plus'
        type:       'success'
        action:     ->
          args      = {create: (resource)}
          args.type = if messages.hasPromptFactory('create-' + resource) then "create-#{resource}" else "edit-#{resource}"

          security.requireRole('CURATOR')
          .then ->
            messages.prompt('Create ' + names.getNaturalName(resource), '', args).then ->
              $state.go 'mc.resource.list', {status: 'draft'}, {reload: true}
          , (errors)->
            $log.error errors
            messages.error('You don\'t have rights to create new elements')
        }
      ]

  actionsProvider.registerActionInRole 'all-data-models', actionsProvider.ROLE_GLOBAL_ACTION ,['security', '$scope', '$state', 'catalogue', (security, $scope, $state, catalogue) ->
    return undefined if not security.isUserLoggedIn()

    {
      position:   3000
      label: 'Show All Data Models'
      icon:  'fa fa-book fa-fw'
      action: ->
        $state.go 'dataModels'
    }
  ]

  actionsProvider.registerChildAction 'catalogue-element', 'add-import', ['$scope', 'messages', 'names', 'security', 'catalogue', ($scope, messages, names, security, catalogue) ->
    return undefined if not security.isUserLoggedIn()
    return undefined if not $scope.element
    return undefined if not angular.isFunction($scope.element.isInstanceOf)
    return undefined if not $scope.element.isInstanceOf('dataModel')

    {
      position:   2000
      label:      'Add Data Model Import'
      icon:       'fa fa-fw fa-puzzle-piece'
      type:       'success'
      action:     ->
        messages.prompt('Add Data Model Import', 'If you want to reuse data classes, data types or measurement units form different data models you need to import the containing data model first.', {type: 'catalogue-elements', resource: 'dataModel', status: 'finalized' }).then (elements) ->
          angular.forEach elements, (element) ->
            unless angular.isString(element)
              $scope.element.imports.add element
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

]