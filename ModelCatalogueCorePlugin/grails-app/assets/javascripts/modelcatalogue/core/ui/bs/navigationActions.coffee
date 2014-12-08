angular.module('mc.core.ui.bs.navigationActions', ['mc.util.ui.actions']).config ['actionsProvider', 'names', (actionsProvider, names)->

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

  actionsProvider.registerActionInRole 'navbar-catalogue-elements', actionsProvider.ROLE_NAVIGATION, -> {
    position:   100
    abstract:   true
    label:      'Catalogue'
  }


  angular.forEach RESOURCES, (resource, index) ->
    actionsProvider.registerChildAction 'navbar-catalogue-elements', 'navbar-' + resource , ['$scope', '$state', '$stateParams', 'names', 'security', 'messages', 'catalogue', ($scope, $state, $stateParams, names, security, messages, catalogue) ->
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
        action: ->
          $state.go 'mc.resource.list', {resource: resource}, {inherit: false}
      }

      $scope.$on '$stateChangeSuccess', (ignored, ignoredToState, toParams) ->
        action.active = toParams.resource == resource

      action


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

  actionsProvider.registerChildAction 'navbar-data-architect', 'navbar-uninstantiated-elements', ['$scope', '$state', ($scope, $state) ->
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


  actionsProvider.registerActionInRole 'cart', actionsProvider.ROLE_NAVIGATION, ['security', '$state', (security, $state) ->
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

  actionsProvider.registerActionInRole 'classifications', actionsProvider.ROLE_NAVIGATION_BOTTOM_LEFT, ['security', 'messages', '$scope', 'rest', 'enhance', 'modelCatalogueApiRoot', '$state', '$stateParams', '$rootScope', (security, messages, $scope, rest, enhance, modelCatalogueApiRoot, $state, $stateParams, $rootScope) ->
    return undefined if not security.isUserLoggedIn()

    getLabel = (user) ->
      if not user or not user.classifications
        return 'All Classifications'
      if user.classifications.length == 0
        return 'All Classifications'
      return (classification.name for classification in user.classifications).join(', ')

    action = {
      position:   2100
      label:      getLabel(security.getCurrentUser())
      icon:       'fa fa-tags'
      action: -> messages.prompt('Select Classifications', 'Select which classifications should be visible to you', type: 'catalogue-elements', resource: 'classification', elements: security.getCurrentUser().classifications).then (elements) ->
        security.requireUser().then ->
          enhance(rest(method: 'POST', url: "#{modelCatalogueApiRoot}/user/classifications/#{(el.id for el in elements).join(',')}")).then (user)->
            action.label = getLabel(user)
            security.getCurrentUser().classifications = user.classifications
            $rootScope.$$lastModels = undefined
            $state.go '.', $stateParams, reload: true
    }

    action


  ]

]