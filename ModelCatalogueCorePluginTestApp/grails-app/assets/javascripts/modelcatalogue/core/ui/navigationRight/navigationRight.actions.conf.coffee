angular.module('modelcatalogue.core.ui.navigationRight.actionsConf', ['mc.util.ui.actions', 'mc.util.security', 'modelcatalogue.core.ui.navigationRight.global.modalSearchForActions']).config ['actionsProvider', 'names', 'actionRoleRegister', 'actionClass', (actionsProvider, names, actionRoleRegister, actionClass)->
  Action = actionClass

  actionsProvider.registerActionInRole 'search-menu', actionRoleRegister.ROLE_NAVIGATION_RIGHT_ACTION, [
    '$scope', 'security', 'messages',
    ($scope ,  security ,  messages) ->
      return undefined unless security.isUserLoggedIn()

      Action.createDefaultTypeAction(
        position:   -10000
        label:      'Search'
        icon:       'fa fa-search fa-fw fa-2x-if-wide'
        action: ->
# TODO: act differently when not in mc.* state
          messages.prompt(null, null, type: 'search-catalogue-element', empty: true, currentDataModel: $scope.currentDataModel, global: 'allow').then (element) ->
            element.show()
      ).withIconOnly()
  ]

  actionsProvider.registerActionInRole 'fast-action', actionRoleRegister.ROLE_NAVIGATION_RIGHT_ACTION, ['security', 'messages', (security, messages) ->
    return undefined unless security.isUserLoggedIn()

    Action.createDefaultTypeAction(
      position:   -5000
      label:      'Fast Actions'
      icon:       'fa fa-flash fa-fw fa-2x-if-wide'
      action: ->
        messages.prompt null, null, type: 'search-action'
    ).withIconOnly()
  ]

  actionsProvider.registerActionInRole 'user-menu', actionRoleRegister.ROLE_NAVIGATION_RIGHT_ACTION, ['security', (security) ->
    return undefined unless security.isUserLoggedIn()

    Action.createAbstractAction(
      position:   10000
      label:      'User'
      icon:       'fa fa-user fa-fw fa-2x-if-wide'
      type:       null
    ).withIconOnly()

  ]

  actionsProvider.registerChildAction 'user-menu', 'user-info', ['security', (security) ->
    return undefined unless security.getCurrentUser()
    Action.createDefaultTypeAction(
      position:   -100000
      label:      security.getCurrentUser()?.username
      icon:       'fa fa-fw fa-user'
      action: -> return
    ).disabledIf true # Well this is odd. This isn't even a performable action. It's just a menu item showing the username.
  ]

  actionsProvider.registerChildAction 'user-menu', 'user-login-right', ['security', (security) ->
    Action.createDefaultTypeAction(
      position:   100000
      label:      'Log In'
      icon:       'fa fa-sign-in fa-fw'
      action: ->
        security.requireLogin()
    )
  ]

  actionsProvider.registerChildAction 'user-menu', 'user-login-right', ['security', (security) ->
    return undefined unless security.isUserLoggedIn()
    Action.createDefaultTypeAction(
      position:   100000
      label:      'Log Out'
      icon:       'fa fa-sign-out fa-fw'
      action: ->
        security.logout()
    )
  ]

  actionsProvider.registerChildAction 'user-menu', 'user-favourites', (security, $state, $rootScope) ->
    'ngInject'
    return undefined unless security.isUserLoggedIn()

    Action.createDefaultTypeAction(
      position:   1000
      label:      'Favourites'
      icon:       'fa fa-star fa-fw'
      action: ->
        $state.go 'catalogue.favourites'
    ).activeIf $state.current.name == 'catalogue.favourites'
      .watching '$stateChangeSuccess'

  actionsProvider.registerChildAction 'user-menu', 'user-api-key', (messages, security, rest, modelCatalogueApiRoot) ->
    'ngInject'
    return undefined unless security.isUserLoggedIn()
    Action.createStandardAction(
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
    )


  actionsProvider.registerActionInRole 'admin-menu', actionRoleRegister.ROLE_NAVIGATION_RIGHT_ACTION, ['security', (security) ->
    return undefined unless security.hasRole('ADMIN')

    Action.createAbstractAction(
      position:   5000
      label:      'Admin'
      icon:       'fa fa-cog fa-fw fa-2x-if-wide'
      type: null
    ).withIconOnly()
  ]

  actionsProvider.registerChildAction 'admin-menu', 'user-super-admin', ($window, security) ->
    "ngInject"
    return undefined unless security.hasRole('SUPERVISOR')

    Action.createDefaultTypeAction(
      position:   1000
      label:      'Users'
      icon:       'fa fa-fw fa-user-plus'
      action: ->
        $window.open("#{security.contextPath}/userAdmin")
    )

  actionsProvider.registerChildAction 'admin-menu', 'user-simple-admin', ($window, $state, security) ->
    "ngInject"
    return undefined unless not security.hasRole('SUPERVISOR')

    Action.createDefaultTypeAction(
      position:   1000
      label:      'Users'
      icon:       'fa fa-fw fa-user-plus'
      action: ->
        $state.go 'catalogue.resource.list', resource: 'user'
    )

  actionsProvider.registerChildAction 'admin-menu', 'relationship-types', ['$state', ($state) ->
    Action.createDefaultTypeAction(
      position:   2000
      label:      'Relationship Types'
      icon:       'fa fa-chain fa-fw'
      action: ->
        $state.go 'catalogue.resource.list', resource: 'relationshipType'
    )
  ]

  actionsProvider.registerChildAction 'admin-menu', 'data-model-policies', ['$state', ($state) ->
    Action.createDefaultTypeAction(
      position:   2100
      label:      'Data Model Policies'
      icon:       'fa fa-check-square-o fa-fw'
      action: ->
        $state.go 'catalogue.resource.list', resource: 'dataModelPolicy'
    )
  ]

  actionsProvider.registerChildAction 'admin-menu', 'action-batches', ['$state', ($state) ->
    Action.createDefaultTypeAction(
      position:   1000
      label:      'Action Batches'
      icon:       'fa fa-flash fa-fw'
      action: ->
        $state.go 'catalogue.resource.list', resource: 'batch'
    )
  ]

  userLastSeen = [
    '$scope', 'names','security', '$state', 'messages',
    ($scope ,  names , security ,  $state ,  messages) ->
      return undefined unless security.hasRole('ADMIN')

      Action.createStandardAction(
        position: 10100
        label: "Activity"
        icon: 'fa fa-users fa-fw'
        type: 'success'
        action: ->
          messages.prompt('Recent Activity', '', type: 'current-activity')
      )
  ]

  actionsProvider.registerActionInRole 'user-last-seen', actionRoleRegister.ROLE_GLOBAL_ACTION, userLastSeen
  actionsProvider.registerChildAction 'admin-menu', 'user-last-seen-child', userLastSeen

  reindexCatalogue = [
    '$scope', 'names','security', '$state', 'messages', 'rest', 'modelCatalogueApiRoot'
    ($scope ,  names , security ,  $state ,  messages ,  rest ,  modelCatalogueApiRoot ) ->
      return undefined unless security.hasRole('SUPERVISOR')

      Action.createStandardAction(
        position: 10200
        label: "Reindex Catalogue"
        icon: 'fa fa-search fa-fw'
        type: 'success'
        action: ->
          messages.confirm("Do you want to reindex catalogue?", "Whole catalogue will be reindexed. This may take a long time and it can have negative impact on the performance.").then ->
            rest(url: "#{modelCatalogueApiRoot}/search/reindex", method: 'POST', params: {soft: true}).then ->
              messages.success('Reindex Catalogue', 'Reindexing the catalogue scheduled.')
      )
  ]

  actionsProvider.registerActionInRole 'reindex-catalogue', actionRoleRegister.ROLE_GLOBAL_ACTION, reindexCatalogue
  actionsProvider.registerChildAction 'admin-menu', 'reindex-catalogue-child', reindexCatalogue

  actionsProvider.registerChildAction 'admin-menu', 'monitoring', ($window, security) ->
    "ngInject"
    return undefined unless security.hasRole('SUPERVISOR')
    Action.createDefaultTypeAction(
      position:   10300
      label:      'Monitoring'
      icon:       'fa fa-fw fa-cogs'
      action: ->
        $window.open("#{security.contextPath}/monitoring")
    )

  actionsProvider.registerActionInRole 'curator-menu', actionRoleRegister.ROLE_NAVIGATION_RIGHT_ACTION, ['security', (security) ->
    return undefined unless security.hasRole('CURATOR')
    Action.createAbstractAction(
      position:   1000
      label:      'Curator'
      icon:       'fa fa-object-group fa-2x-if-wide'
      type: null
    ).withIconOnly()
  ]

  actionsProvider.registerChildAction 'curator-menu', 'csv-transformations', ['$state', ($state) ->
    Action.createDefaultTypeAction(
      position:   10000
      label:      'CSV Transformations'
      icon:       'fa fa-long-arrow-right fa-fw'
      action: ->
        $state.go 'catalogue.resource.list', resource: 'csvTransformation'
    )
  ]

  actionsProvider.registerChildAction 'curator-menu', 'feedbacks', ($state) ->
    'ngInject'

    Action.createDefaultTypeAction(
      position:   200000
      label:      'Feedbacks'
      icon:       'fa fa-tasks fa-fw'
      action: ->
        $state.go 'catalogue.resource.list', resource: 'feedback'
    )

  actionsProvider.registerChildAction 'admin-menu', 'logs', (messages,  enhance, rest,  modelCatalogueApiRoot) ->
    "ngInject"
    Action.createDefaultTypeAction(
      position:   10300
      label:      'Logs'
      icon:       'fa fa-fw fa-archive'
      action: ->
        messages.confirm("Do you want to create logs archive?", "New asset containing the application logs will be created and accessible to all users.").then ->
          enhance(rest(url: "#{modelCatalogueApiRoot}/logs")).then (asset) ->
            asset.show()
    )

]
