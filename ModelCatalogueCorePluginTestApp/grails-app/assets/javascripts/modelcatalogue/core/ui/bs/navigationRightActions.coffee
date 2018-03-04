angular.module('mc.core.ui.bs.navigationRightActions', ['mc.util.ui.actions', 'mc.util.security']).config ['actionsProvider', 'names', (actionsProvider, names)->

  actionsProvider.registerActionInRole 'search-menu', actionsProvider.ROLE_NAVIGATION_RIGHT, [
    '$scope', 'security', 'messages',
    ($scope ,  security ,  messages) ->
      return undefined
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

  actionsProvider.registerActionInRole 'create-data-model', actionsProvider.ROLE_NAVIGATION_RIGHT, ['$window', 'security', ($window, security) ->
    return undefined unless security.hasRole('CURATOR')
    {
      position:   -5000
      icon:       'fa fa-plus fa-fw'
      label:      'Create Data Model'
      iconOnly:   true
      action: ->
        $window.open("#{security.contextPath}/dashboard/index")
    }
  ]

  actionsProvider.registerActionInRole 'fast-action', actionsProvider.ROLE_NAVIGATION_RIGHT, ['security', 'messages', (security, messages) ->
    return undefined
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
      icon:       'fa fa-user'
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
    return undefined unless security.isUserLoggedIn()
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
    return undefined unless security.isUserLoggedIn()
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


  actionsProvider.registerActionInRole 'admin-menu', actionsProvider.ROLE_NAVIGATION_RIGHT, ['security', (security) ->
    return undefined unless security.hasRole('CURATOR')
    {
      position:   5000
      icon:       'fa fa-cog fa-fw'
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

  actionsProvider.registerChildAction 'admin-menu', 'code-version', ($window, security) ->
    "ngInject"
    return undefined unless security.isUserLoggedIn()
    {
      position:   1000
      icon:       'fa fa-fw fa-question'
      label:      'Code Version'
      action: ->
        $window.open("#{security.contextPath}/modelCatalogueVersion/index")
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

  actionsProvider.registerChildAction 'admin-menu', 'relationship-types', ['$state', 'security', ($state, security) ->
    return undefined unless security.isUserLoggedIn()
    {
      position:   2000
      icon:       'fa fa-chain fa-fw'
      label:      'Relationship Types'
      action: ->
        $state.go 'simple.resource.list', resource: 'relationshipType'
    }
  ]

  actionsProvider.registerChildAction 'admin-menu', 'data-model-policies', ['$state', 'security', ($state, security) ->
    return undefined unless security.isUserLoggedIn()
    {
      position:   2100
      icon:       'fa fa-check-square-o fa-fw'
      label:      'Data Model Policies'
      action: ->
        $state.go 'simple.resource.list', resource: 'dataModelPolicy'
    }
  ]

  actionsProvider.registerChildAction 'admin-menu', 'action-batches', ($window, security) ->
    "ngInject"
    return undefined unless security.hasRole('CURATOR')
    {
      position:   1000
      icon:       'fa fa-flash fa-fw'
      label:      'Mapping Utility'
      action: ->
        $window.open("#{security.contextPath}/batch/all")
    }

  actionsProvider.registerChildAction 'admin-menu', 'user-last-seen', ($window, security) ->
    "ngInject"
    return undefined unless security.hasRole('SUPERVISOR')
    {
      position:   1000
      icon:       'fa fa-fw fa-eye'
      label:      'Activity'
      action: ->
        $window.open("#{security.contextPath}/lastSeen/index")
    }

  actionsProvider.registerChildAction 'admin-menu', 'reindex-catalogue', ($window, security) ->
    "ngInject"
    return undefined unless security.hasRole('SUPERVISOR')
    {
      position:   1000
      icon:       'fa fa-fw fa-search'
      label:      'Reindex Catalogue'
      action: ->
        $window.open("#{security.contextPath}/reindexCatalogue/index")
    }

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

  actionsProvider.registerChildAction 'admin-menu', 'logs-archive', ($window, security) ->
    "ngInject"
    return undefined unless security.hasRole('SUPERVISOR')
    {
      position:   10400
      icon:       'fa fa-fw fa-archive'
      label:      'Logs'
      action: ->
        $window.open("#{security.contextPath}/logs/index")
    }

  actionsProvider.registerChildAction 'admin-menu', 'feedbacks', ($window, security) ->
    "ngInject"
    return undefined unless security.isUserLoggedIn()
    {
      position:   10400
      icon:       'fa fa-tasks fa-fw'
      label:      'Feedbacks'
      action: ->
        $window.open("#{security.contextPath}/#/catalogue/feedback/all")
    }

  actionsProvider.registerActionInRole 'curator-menu', actionsProvider.ROLE_NAVIGATION_RIGHT, ['security', (security) ->
    return undefined unless security.hasRole('CURATOR')
    {
      position:   1000
      icon:       'fa fa-upload'
      label:      'Curator'
      iconOnly:   true
    }
  ]

  actionsProvider.registerChildAction 'curator-menu', 'import-excel', ($window, security) ->
    "ngInject"
    return undefined unless security.hasRole('CURATOR')
    {
      position:   10400
      icon:       'fa fa-fw fa-file'
      label:      'Import Excel'
      action: ->
        $window.open("#{security.contextPath}/dataImport/excel")
    }

  actionsProvider.registerChildAction 'curator-menu', 'import-obo', ($window, security) ->
    "ngInject"
    return undefined unless security.hasRole('CURATOR')
    {
      position:   10400
      icon:       'fa fa-fw fa-file'
      label:      'Import OBO'
      action: ->
        $window.open("#{security.contextPath}/dataImport/obo")
    }

  actionsProvider.registerChildAction 'curator-menu', 'import-dsl', ($window, security) ->
    "ngInject"
    return undefined unless security.hasRole('CURATOR')
    {
      position:   10400
      icon:       'fa fa-fw fa-file'
      label:      'Import Model Catalogue DSL File'
      action: ->
        $window.open("#{security.contextPath}/dataImport/dsl")
    }

  actionsProvider.registerChildAction 'curator-menu', 'import-xml', ($window, security) ->
    "ngInject"
    return undefined unless security.hasRole('CURATOR')
    {
      position:   10400
      icon:       'fa fa-fw fa-file'
      label:      'Import Catalogue XML'
      action: ->
        $window.open("#{security.contextPath}/dataImport/xml")
    }

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
    actionsProvider.registerActionInRole 'global-import-loinc', actionsProvider.ROLE_GLOBAL_ACTION, loincImport

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
    actionsProvider.registerActionInRole 'global-import-uml', actionsProvider.ROLE_GLOBAL_ACTION, umlImport

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
    actionsProvider.registerActionInRole 'global-import-csv', actionsProvider.ROLE_GLOBAL_ACTION, rareDiseaseCsvImport

  actionsProvider.registerActionInRole 'connected', actionsProvider.ROLE_NAVIGATION_RIGHT, ($rootScope, messages, $window) ->
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
