angular.module('mc.core.ui.bs.navigationActions', ['mc.util.ui.actions', 'mc.util.security']).config ['actionsProvider', 'names', (actionsProvider, names)->

  RESOURCES = [
    'classification'
    'model'
    'dataElement'
    'valueDomain'
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
      return undefined if (resource == 'batch' or resource == 'relationshipType' or resource == 'csvTransformation') and not security.hasRole('CURATOR')

      label = names.getNaturalName(resource) + 's'

      if resource == 'batch'
        label = 'Actions'
      else if resource == 'csvTransformation'
        label = 'CSV Transformations'

      action = {
        icon:       catalogue.getIcon(resource)
        position:   index * 100
        label:      label
        currentStatus: undefined
        action: ->

          $state.go 'mc.resource.list', {resource: resource, status: @currentStatus}, {inherit: false}
      }

      $scope.$on '$stateChangeSuccess', (ignored, ignoredToState, toParams) ->
        action.active = toParams.resource == resource
        action.currentStatus = toParams.status if toParams.hasOwnProperty('status')

      action
    ]
    actionsProvider.registerChildAction 'navbar-catalogue-elements', 'navbar-' + resource , goToResource
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

  uninstantiatedElements = ['$scope', '$state', ($scope, $state) ->
    action = {
      position:    200
      label:      'Uninstantiated Data Elements'
      icon:       'fa fa-fw fa-cube'
      action: ->
        $state.go 'mc.resource.list', {resource: 'dataElement', status: 'uninstantiated'}
    }

    $scope.$on '$stateChangeSuccess', (ignored, state) ->
      action.active = state.name == 'mc.dataArchitect.uninstantiatedDataElements'

    action
  ]
  actionsProvider.registerChildAction 'navbar-data-architect', 'navbar-uninstantiated-elements', uninstantiatedElements
  actionsProvider.registerActionInRole 'global-uninstantiated-elements', actionsProvider.ROLE_GLOBAL_ACTION, uninstantiatedElements

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

  toggleClassification = (global) -> ['security', 'messages', '$scope', 'rest', 'enhance', 'modelCatalogueApiRoot', '$state', '$stateParams', (security, messages, $scope, rest, enhance, modelCatalogueApiRoot, $state, $stateParams) ->
    return undefined if not security.isUserLoggedIn()

    getLabel = (user) ->
      if not user or not user.classifications
        return 'All Classifications'
      if user.classifications.unclassifiedOnly
        return 'Unclassified Only'

      label = 'All Classifications'

      if user.classifications.includes?.length > 0
        label = (classification.name for classification in user.classifications.includes).join(', ')

      if user.classifications.excludes?.length > 0
        label += " except " + (classification.name for classification in user.classifications.excludes).join(', ')

      return label

    action = {
      position:   2100
      label:      if global then 'Filter by Classification' else  getLabel(security.getCurrentUser())
      icon:       'fa fa-tags'
      action: -> messages.prompt('Select Classifications', 'Select which classifications should be visible to you', type: 'classification-filter', filter: security.getCurrentUser().classifications).then (filter) ->
        security.requireUser().then ->
          enhance(rest(method: 'POST', url: "#{modelCatalogueApiRoot}/user/classifications", data: filter)).then (user)->
            action.label = getLabel(user)
            security.getCurrentUser().classifications = user.classifications
            $state.go '.', $stateParams, reload: true
    }

    action
  ]

  actionsProvider.registerActionInRole 'classifications', actionsProvider.ROLE_NAVIGATION_BOTTOM_LEFT, toggleClassification(false)
  actionsProvider.registerActionInRole 'global-classifications', actionsProvider.ROLE_GLOBAL_ACTION, toggleClassification(true)

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

  actionsProvider.registerActionInRole 'dashboard', actionsProvider.ROLE_GLOBAL_ACTION, ['$state', ($state) ->
    {
      label:      'Dashboard'
      icon:       'fa fa-tachometer'
      action: -> $state.go 'dashboard'
    }
  ]



]