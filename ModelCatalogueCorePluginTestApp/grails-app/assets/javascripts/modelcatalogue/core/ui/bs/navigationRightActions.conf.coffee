angular.module('mc.core.ui.bs.navigationRightActions', ['mc.util.ui.actions', 'mc.util.security']).config ['actionsProvider', 'names', 'actionRoleRegister', (actionsProvider, names, actionRoleRegister)->
  actionsProvider.registerActionInRole 'search-menu', actionRoleRegister.ROLE_NAVIGATION_RIGHT_ACTION, [
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

  actionsProvider.registerActionInRole 'fast-action', actionRoleRegister.ROLE_NAVIGATION_RIGHT_ACTION, ['security', 'messages', (security, messages) ->
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

  actionsProvider.registerActionInRole 'user-menu', actionRoleRegister.ROLE_NAVIGATION_RIGHT_ACTION, ['security', (security) ->
    return undefined unless security.isUserLoggedIn()
    {
      position:   10000
      icon:       'fa fa-user fa-fw fa-2x-if-wide'
      abstract:   true
      label:      'User'
      iconOnly:   true
    }
  ]

  actionsProvider.registerChildAction 'user-menu', 'user-info', ['security', (security) ->
    return undefined unless security.getCurrentUser()
    {
      position:   -100000
      icon:       'fa fa-fw fa-user'
      label:      security.getCurrentUser()?.username
      disabled:   true
      action: -> return
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

  actionsProvider.registerChildAction 'user-menu', 'user-favorites', (security, $state, $rootScope) ->
    'ngInject'
    return undefined if not security.isUserLoggedIn()
    action =
      position:   1000
      label:      'Favourites'
      icon:       'fa fa-star fa-fw'
      active:     $state.current.name == 'simple.favorites'
      action: ->
        $state.go 'simple.favorites'

    $rootScope.$on '$stateChangeSuccess', (ignored, state) ->
        action.active = state.name == 'simple.favorites'

    action

  actionsProvider.registerChildAction 'user-menu', 'user-api-key', (messages, security, rest, modelCatalogueApiRoot) ->
    'ngInject'

    return undefined if not security.isUserLoggedIn()

    {
      position:   10000
      label:      "API Key"
      icon:       "fa fa-key fa-fw"
      type:       'primary'
      action:     ->
        options = [
          {classes: 'btn btn-primary', icon: 'fa fa-key', label: 'Regenerate Key', value: 'regenerate'}
        ]

        console.log security.getCurrentUser()

        openApiKey = (response) ->
          messages.prompt("API Key", "<p>Use following key as password for your API calls</p><input type='text' class='form-control' readonly='readonly' value='#{response.apiKey}' select-on-click></input><p class='help-block small text-warning'>WARNING: Regenerating the key will prevent all application using the current key from accessing the catalogue on your behalf</p>", type: 'options', options: options).then (regenerate) ->
            if regenerate is 'regenerate'
              rest(url: "#{modelCatalogueApiRoot}/user/apikey?regenerate=true", method: 'POST').then(openApiKey)

        rest(url: "#{modelCatalogueApiRoot}/user/apikey", method: 'POST').then(openApiKey)
    }


  actionsProvider.registerActionInRole 'admin-menu', actionRoleRegister.ROLE_NAVIGATION_RIGHT_ACTION, ['security', (security) ->
    return undefined unless security.hasRole('ADMIN')
    {
      position:   5000
      icon:       'fa fa-cog fa-fw fa-2x-if-wide'
      label:      'Admin'
      iconOnly:   true
    }
  ]

  actionsProvider.registerChildAction 'admin-menu', 'user-super-admin', ($window, security) ->
    "ngInject"
    return undefined unless security.hasRole('SUPERVISOR')
    {
      position:   1000
      icon:       'fa fa-fw fa-user-plus'
      label:      'Users'
      action: ->
        $window.open("#{security.contextPath}/userAdmin")
    }

  actionsProvider.registerChildAction 'admin-menu', 'user-simple-admin', ($window, $state, security) ->
    "ngInject"
    return undefined if security.hasRole('SUPERVISOR')
    {
      position:   1000
      icon:       'fa fa-fw fa-user-plus'
      label:      'Users'
      action: ->
        $state.go 'simple.resource.list', resource: 'user'
    }

  actionsProvider.registerChildAction 'admin-menu', 'relationship-types', ['$state', ($state) ->
    {
      position:   2000
      icon:       'fa fa-chain fa-fw'
      label:      'Relationship Types'
      action: ->
        $state.go 'simple.resource.list', resource: 'relationshipType'
    }
  ]

  actionsProvider.registerChildAction 'admin-menu', 'data-model-policies', ['$state', ($state) ->
    {
      position:   2100
      icon:       'fa fa-check-square-o fa-fw'
      label:      'Data Model Policies'
      action: ->
        $state.go 'simple.resource.list', resource: 'dataModelPolicy'
    }
  ]

  actionsProvider.registerChildAction 'admin-menu', 'action-batches', ['$state', ($state) ->
    {
      position:   1000
      icon:       'fa fa-flash fa-fw'
      label:      'Action Batches'
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

  actionsProvider.registerActionInRole 'user-last-seen', actionRoleRegister.ROLE_GLOBAL_ACTION, userLastSeen
  actionsProvider.registerChildAction 'admin-menu', 'user-last-seen-child', userLastSeen

  reindexCatalogue = [
    '$scope', 'names','security', '$state', 'messages', 'rest', 'modelCatalogueApiRoot'
    ($scope ,  names , security ,  $state ,  messages ,  rest ,  modelCatalogueApiRoot ) ->
      return undefined unless security.hasRole('SUPERVISOR')

      {
      position: 10200
      label: "Reindex Catalogue"
      icon: 'fa fa-search fa-fw'
      type: 'success'
      action: ->
        messages.confirm("Do you want to reindex catalogue?", "Whole catalogue will be reindexed. This may take a long time and it can have negative impact on the performance.").then ->
          rest(url: "#{modelCatalogueApiRoot}/search/reindex", method: 'POST', params: {soft: true}).then ->
            messages.success('Reindex Catalogue', 'Reindexing the catalogue scheduled.')
      }
  ]

  actionsProvider.registerActionInRole 'reindex-catalogue', actionRoleRegister.ROLE_GLOBAL_ACTION, reindexCatalogue
  actionsProvider.registerChildAction 'admin-menu', 'reindex-catalogue-child', reindexCatalogue

  actionsProvider.registerChildAction 'admin-menu', 'monitoring', ($window, security) ->
    "ngInject"
    return undefined unless security.hasRole('SUPERVISOR')
    {
      position:   10300
      icon:       'fa fa-fw fa-cogs'
      label:      'Monitoring'
      action: ->
        $window.open("#{security.contextPath}/monitoring")
    }

  actionsProvider.registerActionInRole 'curator-menu', actionRoleRegister.ROLE_NAVIGATION_RIGHT_ACTION, ['security', (security) ->
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

  actionsProvider.registerChildAction 'curator-menu', 'feedbacks', ($state) ->
    'ngInject'

    {
      position:   200000
      icon:       'fa fa-tasks fa-fw'
      label:      'Feedbacks'
      action: ->
        $state.go 'simple.resource.list', resource: 'feedback'
    }

  actionsProvider.registerChildAction 'admin-menu', 'logs', (messages,  enhance, rest,  modelCatalogueApiRoot) ->
    "ngInject"
    {
      position:   10300
      icon:       'fa fa-fw fa-archive'
      label:      'Logs'
      action: ->
        messages.confirm("Do you want to create logs archive?", "New asset containing the application logs will be created and accessible to all users.").then ->
          enhance(rest(url: "#{modelCatalogueApiRoot}/logs")).then (asset) ->
            asset.show()
    }


  actionsProvider.registerActionInRole 'new-import', actionRoleRegister.ROLE_LIST_ACTION, [
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



  loincImport = ($scope, messages, security) ->
    'ngInject'
    return undefined if not security.hasRole('CURATOR')
    {
      position: 13001
      label: "Import Loinc"
      icon:  'fa fa-upload fa-fw'
      action: ->
        messages.prompt('Import Loinc File', '', type: 'new-loinc-import')
    }
  if false # No longer provide LOINC import because it is only half-implemented and we don't use it anyway
    actionsProvider.registerChildAction 'new-import', 'import-loinc', loincImport
    actionsProvider.registerChildAction 'import-data-models-screen', 'import-loinc', loincImport
    actionsProvider.registerChildAction 'curator-menu', 'import-loinc', loincImport
    actionsProvider.registerActionInRole 'global-import-loinc', actionRoleRegister.ROLE_GLOBAL_ACTION, loincImport

  excelImport = ($scope, messages, security) ->
    'ngInject'
    return undefined if not security.hasRole('CURATOR')
    {
      position: 13002
      label:  "Import Excel"
      icon:  'fa fa-upload fa-fw'
      action: ->
        messages.prompt('Import Excel File', '', type: 'new-excel-import')
    }

  actionsProvider.registerChildAction 'new-import', 'import-excel', excelImport
  actionsProvider.registerChildAction 'import-data-models-screen', 'import-excel', excelImport
  actionsProvider.registerChildAction 'curator-menu', 'import-excel', excelImport
  actionsProvider.registerActionInRole 'global-import-excel', actionRoleRegister.ROLE_GLOBAL_ACTION, excelImport

  oboImport = ($scope, messages, security) ->
    'ngInject'
    return undefined if not security.hasRole('CURATOR')
    {
      position: 13003
      label: "Import OBO"
      icon:  'fa fa-upload fa-fw'
      action: ->
        messages.prompt('Import OBO File', '', type: 'new-obo-import')
    }
  actionsProvider.registerChildAction 'new-import', 'import-obo', oboImport
  actionsProvider.registerChildAction 'import-data-models-screen', 'import-obo', oboImport
  actionsProvider.registerChildAction 'curator-menu', 'import-obo', oboImport
  actionsProvider.registerActionInRole 'global-import-obo', actionRoleRegister.ROLE_GLOBAL_ACTION, oboImport

  umlImport = ($scope, messages, security) ->
    'ngInject'
    return undefined if not security.hasRole('CURATOR')
    {
      position: 13004
      label: "Import Star Uml"
      icon:  'fa fa-upload fa-fw'
      action: ->
        messages.prompt('Import Star Uml File', '', type: 'new-umlj-import')
    }
  if false # No longer provide Star UML import as it is an old thing from days of collaboration with Oxford
    actionsProvider.registerChildAction 'new-import', 'import-umlj', umlImport
    actionsProvider.registerChildAction 'import-data-models-screen', 'import-umlj', umlImport
    actionsProvider.registerChildAction 'curator-menu', 'import-umlj', umlImport
    actionsProvider.registerActionInRole 'global-import-uml', actionRoleRegister.ROLE_GLOBAL_ACTION, umlImport

  mcImport = ($scope, messages, security) ->
    'ngInject'
    return undefined if not security.hasRole('CURATOR')
    {
      position: 13005
      label: "Import Model Catalogue DSL File"
      icon:  'fa fa-upload fa-fw'
      action: ->
        messages.prompt('Import Model Catalogue DSL File', '', type: 'new-mc-import')
    }
  actionsProvider.registerChildAction 'new-import', 'import-mc', mcImport
  actionsProvider.registerChildAction 'import-data-models-screen', 'import-mc', mcImport
  actionsProvider.registerChildAction 'curator-menu', 'import-mc', mcImport
  actionsProvider.registerActionInRole 'global-import-mc', actionRoleRegister.ROLE_GLOBAL_ACTION, mcImport

  xmlImport = ($scope, messages, security) ->
    'ngInject'
    return undefined if not security.hasRole('CURATOR')
    {
      position: 13006
      label: "Import Catalogue XML"
      icon:  'fa fa-upload fa-fw'
      action: ->
        messages.prompt('Import Model Catalogue XML File', '', type: 'new-catalogue-xml-import')
    }
  actionsProvider.registerChildAction 'new-import', 'import-catalogue-xml', xmlImport
  actionsProvider.registerChildAction 'import-data-models-screen', 'import-catalogue-xml', xmlImport
  actionsProvider.registerChildAction 'curator-menu', 'import-catalogue-xml', xmlImport
  actionsProvider.registerActionInRole 'global-import-xml', actionRoleRegister.ROLE_GLOBAL_ACTION, xmlImport

  rareDiseaseCsvImport = ($scope, messages, security) ->
    'ngInject'
    return undefined if not security.hasRole('CURATOR')
    {
      position: 13007
      label: "Import Rare Disease Csv"
      icon:  'fa fa-upload fa-fw'
      action: ->
        messages.prompt('Import Rare Disease Csv File', '', type: 'new-rare-disease-csv-import')
    }

  if false # No longer provide Rare Disease CSV Import function
    actionsProvider.registerChildAction 'new-import', 'rare-disease-csv', rareDiseaseCsvImport
    actionsProvider.registerChildAction 'import-data-models-screen', 'rare-disease-csv', rareDiseaseCsvImport
    actionsProvider.registerChildAction 'curator-menu', 'rare-disease-csv', rareDiseaseCsvImport
    actionsProvider.registerActionInRole 'global-import-csv', actionRoleRegister.ROLE_GLOBAL_ACTION, rareDiseaseCsvImport

  actionsProvider.registerActionInRole 'connected', actionRoleRegister.ROLE_NAVIGATION_RIGHT_ACTION, ($rootScope, messages, $window) ->
    'ngInject'
    if $rootScope.$$disconnected
      return {
        position:   -10000000
        icon:       'fa fa-exclamation-triangle text-danger fa-fw fa-2x-if-wide'
        label:      'Application is no longer receiving real-time updates from the server. Please, reload the page to reconnect.'
        iconOnly:   true
        action:     ->
          messages.confirm(
            'Application Disconnected',
            'Application is disconnected and no longer accepts real-time updates from the server. Do you want to reload current page? All unsaved progress will be lost.'
          ).then -> $window.location.reload()

      }

]
