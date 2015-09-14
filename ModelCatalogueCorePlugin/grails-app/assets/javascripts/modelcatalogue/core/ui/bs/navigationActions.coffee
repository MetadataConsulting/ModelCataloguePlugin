angular.module('mc.core.ui.bs.navigationActions', ['mc.util.ui.actions', 'mc.util.security']).config ['actionsProvider', 'names', (actionsProvider, names)->

  RESOURCES = [
    'dataModel'
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
    goToResource = ['$scope', '$state', '$stateParams', 'names', 'security', 'messages', 'catalogue', ($scope, $state, $stateParams, names, security, messages, catalogue) ->
      return undefined if not security.hasRole('VIEWER')
      return undefined if (resource == 'batch' or resource == 'relationshipType' or resource == 'csvTransformation') and not security.hasRole('CURATOR')

      label = names.getNaturalName(resource) + 's'

      if resource == 'batch'
        label = 'Actions'
      else if resource == 'csvTransformation'
        label = 'CSV Transformations'
      else if resource == 'dataClass'
        label = 'Data Classes'

      action = {
        active:     $stateParams.resource == resource
        icon:       catalogue.getIcon(resource)
        position:   index * 100
        label:      label
        currentStatus: undefined
        action: ->

          $state.go 'mc.resource.list', {resource: resource, status: @currentStatus, dataModelId: $stateParams.dataModelId}, {inherit: false}
      }

      $scope.$on '$stateChangeSuccess', (ignored, ignoredToState, toParams) ->
        action.active = toParams.resource == resource
        action.currentStatus = toParams.status if toParams.hasOwnProperty('status')

      action
    ]
    actionsProvider.registerActionInRole  'sidenav-' + resource, actionsProvider.ROLE_SIDENAV , goToResource
    actionsProvider.registerActionInRole 'global-list-' + resource, actionsProvider.ROLE_GLOBAL_ACTION, goToResource
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


  actionsProvider.registerActionInRole 'navbar-data-architect', actionsProvider.ROLE_NAVIGATION, ['security', (security) ->
    return undefined if not security.hasRole('CURATOR')

    {
      navigation: true
      abstract:   true
      position:   1000
      label:      'Data Architect'
    }
  ]

  actionsProvider.registerChildAction 'navbar-data-architect', 'navbar-relations-by-metadata-key', ['$scope', '$state', ($scope, $state) ->
    action = {
      position:    300
      label:      'Create COSD Synonym Data Element Relationships'
      icon:       'fa fa-fw fa-exchange'
      action: ->
        $state.go 'mc.dataArchitect.findRelationsByMetadataKeys'
    }

    $scope.$on '$stateChangeSuccess', (ignored, state) ->
      action.active = state.name == 'mc.dataArchitect.findRelationsByMetadataKeys'

    action
  ]

  actionsProvider.registerChildAction 'navbar-data-architect', 'navbar-element-without-key', ['$scope', '$state', ($scope, $state) ->
    action = {
      position:    400
      label:      'Data Elements without Metadata Key'
      icon:       'fa fa-fw fa-key'
      action: ->
        $state.go 'mc.dataArchitect.metadataKey'
    }

    $scope.$on '$stateChangeSuccess', (ignored, state) ->
      action.active = state.name == 'mc.dataArchitect.metadataKey'

    action
  ]


  actionsProvider.registerActionInRoles 'cart', [actionsProvider.ROLE_NAVIGATION, actionsProvider.ROLE_GLOBAL_ACTION], ['security', '$state', (security, $state) ->
    return undefined if not security.isUserLoggedIn()

    action = {
      position:   2000
      label:      'Favorites'
      icon:       'fa fa-star'
      action: ->
        $state.go 'mc.favorites'

    }
#    $scope.$on '$stateChangeSuccess', (ignored, state) ->
#      action.active = state.name == 'mc.favorites'

    action
  ]


  actionsProvider.registerActionInRole 'currentDataModel', actionsProvider.ROLE_NAVIGATION, ['security', '$scope', '$state', '$rootScope', (security, $scope, $state, $rootScope) ->
    return undefined if not security.isUserLoggedIn()

    getLabel = ->
      return 'All Data Models' unless $rootScope.currentDataModel
      return "#{ $rootScope.currentDataModel.name} (draft)" if $rootScope.currentDataModel.status == 'DRAFT'
      return $rootScope.currentDataModel.name

    action = {
      position:   - 100000
      label: getLabel()
      icon: 'fa fa-book'
      watches: ['currentDataModel']
    }

    action
  ]

  actionsProvider.registerChildAction 'currentDataModel', 'show-data-model', ['catalogue', '$scope', '$state', '$rootScope', (catalogue, $scope, $state, $rootScope) ->
    return undefined if not catalogue.isFilteredByDataModel()

    action = {

      position:   2000
      label: 'Show Detail'
      icon: 'fa fa-book fa-fw'
      active: $state.name == 'mc.resource.show' and $state.params.id == ('' + catalogue.getCurrentDataModel().id)
      action: ->
        $state.go 'mc.resource.show', {resource: 'dataModel', id: catalogue.getCurrentDataModel().id, dataModelId: catalogue.getCurrentDataModel().id}
    }

    $rootScope.$on '$stateChangeSuccess', (ignored, state, params) ->
      action.active = state.name == 'mc.resource.show' and params.id == ('' + catalogue.getCurrentDataModel().id) if angular.isFunction(catalogue.getCurrentDataModel) and catalogue.getCurrentDataModel()

    action
  ]

  actionsProvider.registerChildAction 'currentDataModel', 'all-data-models', ['security', '$scope', '$state', 'catalogue', (security, $scope, $state, catalogue) ->
    return undefined if not security.isUserLoggedIn()

    {
      position:   3000
      label: 'Show All Data Models'
      icon:  'fa fa-book fa-fw'
      action: ->
        $state.go 'mc.resource.list', {resource: 'dataModel', dataModelId: 'catalogue', status: undefined }
    }
  ]

  actionsProvider.registerChildAction 'currentDataModel', 'add-import', ['$scope', 'messages', 'names', 'security', 'catalogue', ($scope, messages, names, security, catalogue) ->
    return undefined if not security.isUserLoggedIn()
    return undefined if not catalogue.isFilteredByDataModel()

    {
      position:   2000
      label:      'Add Data Model Import'
      icon:       'fa fa-fw fa-puzzle-piece'
      type:       'success'
      action:     ->
        messages.prompt('Add Data Model Import', 'If you want to reuse data classes, data types or measurement units form different data models you need to import the containing data model first.', {type: 'catalogue-elements', resource: 'dataModel', status: 'finalized' }).then (elements) ->
          angular.forEach elements, (element) ->
            unless angular.isString(element)
              catalogue.getCurrentDataModel().imports.add element
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