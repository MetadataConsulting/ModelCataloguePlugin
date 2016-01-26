angular.module('mc.core.ui.bs.navigationRightActions', ['mc.util.ui.actions', 'mc.util.security']).config ['actionsProvider', 'names', (actionsProvider, names)->

  actionsProvider.registerActionInRole 'search-menu', actionsProvider.ROLE_NAVIGATION_RIGHT, [
    '$scope', 'security', 'messages',
    ($scope ,  security ,  messages) ->

      return undefined unless security.isUserLoggedIn()
      {
        position:   -10000
        icon:       'fa fa-search fa-fw fa-2x-if-wide'
        label:      'Search'
        iconOnly:   true
        action: ->
          # TODO: act differently when not in mc.* state
          messages.prompt(null, null, type: 'search-catalogue-element', empty: true, currentDataModel: $scope.currentDataModel, global: 'allow').then (element) ->
            element.show()
      }
  ]

  actionsProvider.registerActionInRole 'fast-action', actionsProvider.ROLE_NAVIGATION_RIGHT, ['security', 'messages', (security, messages) ->
    return undefined unless security.isUserLoggedIn()
    {
      position:   -5000
      icon:       'fa fa-flash fa-fw fa-2x-if-wide'
      label:      'Fast Actions'
      iconOnly:   true
      action: ->
        messages.prompt null, null, type: 'search-action'
    }
  ]

  actionsProvider.registerActionInRole 'user-menu', actionsProvider.ROLE_NAVIGATION_RIGHT, ['security', (security) ->
    return undefined unless security.isUserLoggedIn()
    {
      position:   10000
      icon:       'fa fa-user fa-fw fa-2x-if-wide'
      abstract:   true
      label:      'User'
      iconOnly:   true
    }
  ]

  actionsProvider.registerChildAction 'user-menu', 'user-login-right', ['security', (security) ->
    {
      position:   100000
      icon:       'fa fa-sign-in fa-fw'
      label:      'Log In'
      action: ->
        security.requireLogin()
    }
  ]

  actionsProvider.registerChildAction 'user-menu', 'user-login-right', ['security', (security) ->
    return undefined unless security.isUserLoggedIn()
    {
      position:   100000
      icon:       'fa fa-sign-out fa-fw'
      label:      'Log Out'
      action: ->
        security.logout()
    }
  ]

  actionsProvider.registerChildAction 'user-menu', 'user-favorites', ['security', '$state', '$rootScope',  (security, $state, $rootScope) ->
    return undefined if not security.isUserLoggedIn()
    action =
      position:   1000
      label:      'Favorites'
      icon:       'fa fa-star fa-fw'
      active:     $state.current.name == 'simple.favorites'
      action: ->
        $state.go 'simple.favorites'

    $rootScope.$on '$stateChangeSuccess', (ignored, state) ->
        action.active = state.name == 'simple.favorites'

    action
  ]


  actionsProvider.registerActionInRole 'admin-menu', actionsProvider.ROLE_NAVIGATION_RIGHT, ['security', (security) ->
    return undefined unless security.hasRole('ADMIN')
    {
      position:   5000
      icon:       'fa fa-cog fa-fw fa-2x-if-wide'
      label:      'Admin'
      iconOnly:   true
    }
  ]

  actionsProvider.registerChildAction 'admin-menu', 'relationship-types', ['$state', ($state) ->
    {
      position:   2000
      icon:       'fa fa-chain fa-fw'
      label:      'Relationship Types'
      action: ->
        $state.go 'simple.resource.list', resource: 'relationshipType'
    }
  ]

  actionsProvider.registerChildAction 'admin-menu', 'action-batches', ['$state', ($state) ->
    {
      position:   1000
      icon:       'fa fa-flash fa-fw'
      label:      'Actions'
      action: ->
        $state.go 'simple.resource.list', resource: 'batch'
    }
  ]

  userLastSeen = [
    '$scope', 'names','security', '$state', 'messages',
    ($scope ,  names , security ,  $state ,  messages) ->
      return undefined if not security.hasRole('ADMIN')

      {
      position: 10100
      label: "Activity"
      icon: 'fa fa-users fa-fw'
      type: 'success'
      action: ->
        messages.prompt('Recent Activity', '', type: 'current-activity')
      }
  ]

  actionsProvider.registerActionInRoles 'user-last-seen', [actionsProvider.ROLE_GLOBAL_ACTION], userLastSeen
  actionsProvider.registerChildAction 'admin-menu', 'user-last-seen-child', userLastSeen

  reindexCatalogue = [
    '$scope', 'names','security', '$state', 'messages', 'rest', 'modelCatalogueApiRoot'
    ($scope ,  names , security ,  $state ,  messages ,  rest ,  modelCatalogueApiRoot ) ->
      return undefined if not security.hasRole('ADMIN')

      {
      position: 10200
      label: "Reindex Catalogue"
      icon: 'fa fa-search fa-fw'
      type: 'success'
      action: ->
        messages.confirm("Do you want to reindex catalogue?", "Whole catalogue will be reindexed. This may take a long time and it can have negative impact on the performance.").then ->
          rest(url: "#{modelCatalogueApiRoot}/search/reindex", method: 'POST').then ->
            messages.success('Reindex Catalogue', 'Reindexing the catalogue scheduled.')
      }
  ]

  actionsProvider.registerActionInRoles 'reindex-catalogue', [actionsProvider.ROLE_GLOBAL_ACTION], reindexCatalogue
  actionsProvider.registerChildAction 'admin-menu', 'reindex-catalogue-child', reindexCatalogue


  actionsProvider.registerActionInRole 'curator-menu', actionsProvider.ROLE_NAVIGATION_RIGHT, ['security', (security) ->
    return undefined unless security.hasRole('CURATOR')
    {
      position:   1000
      icon:       'fa fa-object-group fa-2x-if-wide'
      label:      'Curator'
      iconOnly:   true
    }
  ]

  actionsProvider.registerChildAction 'curator-menu', 'csv-transformations', ['$state', ($state) ->
    {
      position:   10000
      icon:       'fa fa-long-arrow-right fa-fw'
      label:      'CSV Transformations'
      action: ->
        $state.go 'simple.resource.list', resource: 'csvTransformation'
    }
  ]

  actionsProvider.registerActionInRole 'new-import', actionsProvider.ROLE_LIST_ACTION, [
    '$scope', 'names','security', '$state',
    ($scope ,  names , security ,  $state ) ->
      return undefined if not security.hasRole('CURATOR')
      return undefined if $state.current.name != 'mc.resource.list'
      return undefined if $scope.resource != 'asset'

      {
        position: 10000
        label: "Import"
        icon: 'fa fa-upload'
        type: 'success'
      }
  ]


  loincImport = ['$scope', 'messages', ($scope, messages) -> {
    position: 13001
    label: "Import Loinc"
    icon:  'fa fa-upload fa-fw'
    action: ->
      messages.prompt('Import Loinc File', '', type: 'new-loinc-import')
  }]
  actionsProvider.registerChildAction 'new-import', 'import-loinc', loincImport
  actionsProvider.registerChildAction 'import-data-models-screen', 'import-loinc', loincImport
  actionsProvider.registerChildAction 'curator-menu', 'import-loinc', loincImport
  actionsProvider.registerActionInRole 'global-import-loinc', actionsProvider.ROLE_GLOBAL_ACTION, loincImport

  excelImport = ['$scope', 'messages', ($scope, messages) -> {
    position: 13002
    label:  "Import Excel"
    icon:  'fa fa-upload fa-fw'
    action: ->
      messages.prompt('Import Excel File', '', type: 'new-excel-import')
  }]
  actionsProvider.registerChildAction 'new-import', 'import-excel', excelImport
  actionsProvider.registerChildAction 'import-data-models-screen', 'import-excel', excelImport
  actionsProvider.registerChildAction 'curator-menu', 'import-excel', excelImport
  actionsProvider.registerActionInRole 'global-import-excel', actionsProvider.ROLE_GLOBAL_ACTION, excelImport

  oboImport = ['$scope', 'messages', ($scope, messages) -> {
    position: 13003
    label: "Import OBO"
    icon:  'fa fa-upload fa-fw'
    action: ->
      messages.prompt('Import OBO File', '', type: 'new-obo-import')
  }]
  actionsProvider.registerChildAction 'new-import', 'import-obo', oboImport
  actionsProvider.registerChildAction 'import-data-models-screen', 'import-obo', oboImport
  actionsProvider.registerChildAction 'curator-menu', 'import-obo', oboImport
  actionsProvider.registerActionInRole 'global-import-obo', actionsProvider.ROLE_GLOBAL_ACTION, oboImport

  umlImport = ['$scope', 'messages', ($scope, messages) -> {
    position: 13004
    label: "Import Star Uml"
    icon:  'fa fa-upload fa-fw'
    action: ->
      messages.prompt('Import Star Uml File', '', type: 'new-umlj-import')
  }]
  actionsProvider.registerChildAction 'new-import', 'import-umlj', umlImport
  actionsProvider.registerChildAction 'import-data-models-screen', 'import-umlj', umlImport
  actionsProvider.registerChildAction 'curator-menu', 'import-umlj', umlImport
  actionsProvider.registerActionInRole 'global-import-uml', actionsProvider.ROLE_GLOBAL_ACTION, umlImport

  mcImport = ['$scope', 'messages', ($scope, messages) -> {
    position: 13005
    label: "Import MC"
    icon:  'fa fa-upload fa-fw'
    action: ->
      messages.prompt('Import Model Catalogue DSL File', '', type: 'new-mc-import')
  }]
  actionsProvider.registerChildAction 'new-import', 'import-mc', mcImport
  actionsProvider.registerChildAction 'import-data-models-screen', 'import-mc', mcImport
  actionsProvider.registerChildAction 'curator-menu', 'import-mc', mcImport
  actionsProvider.registerActionInRole 'global-import-mc', actionsProvider.ROLE_GLOBAL_ACTION, mcImport


  xmlImport = ['$scope', 'messages', ($scope, messages) -> {
    position: 13006
    label: "Import Catalogue XML"
    icon:  'fa fa-upload fa-fw'
    action: ->
      messages.prompt('Import Model Catalogue XML File', '', type: 'new-catalogue-xml-import')
  }]
  actionsProvider.registerChildAction 'new-import', 'import-catalogue-xml', xmlImport
  actionsProvider.registerChildAction 'import-data-models-screen', 'import-catalogue-xml', xmlImport
  actionsProvider.registerChildAction 'curator-menu', 'import-catalogue-xml', xmlImport
  actionsProvider.registerActionInRole 'global-import-xml', actionsProvider.ROLE_GLOBAL_ACTION, xmlImport

]