angular.module('mc.core.ui.bs.navigationRightActions', ['mc.util.ui.actions', 'mc.util.security']).config ['actionsProvider', 'names', (actionsProvider, names)->

  actionsProvider.registerActionInRole 'search-menu', actionsProvider.ROLE_NAVIGATION_RIGHT, ['security', (security) ->
    {
      position:   -10000
      icon:       'fa fa-search'
      label:      'Search'
      iconOnly:   true
      action: ->
        # TODO implement with the new admin state
    }
  ]

  actionsProvider.registerActionInRole 'user-menu', actionsProvider.ROLE_NAVIGATION_RIGHT, ['security', (security) ->
    {
      position:   10000
      icon:       'fa fa-user'
      abstract:   true
      label:      'User'
      iconOnly:   true
    }
  ]

  actionsProvider.registerChildAction 'user-menu', 'user-login-right', ['security', (security) ->
    return undefined if security.isUserLoggedIn()
    {
      position:   10000
      icon:       'fa fa-sign-in fa-fw'
      label:      'Log In'
      action: ->
        security.requireLogin()
    }
  ]

  actionsProvider.registerChildAction 'user-menu', 'user-login-right', ['security', (security) ->
    return undefined unless security.isUserLoggedIn()
    {
      position:   10000
      icon:       'fa fa-sign-out fa-fw'
      label:      'Log Out'
      action: ->
        security.logout()
    }
  ]



  actionsProvider.registerChildAction 'user-menu', 'user-favorites', ['security', (security) ->
    return undefined unless security.isUserLoggedIn()
    {
      position:   1000
      icon:       'fa fa-star fa-fw'
      label:      'Favorites'
      action: ->
        # TODO implement with the new admin state
    }
  ]



  actionsProvider.registerActionInRole 'admin-menu', actionsProvider.ROLE_NAVIGATION_RIGHT, ['security', (security) ->
    return undefined unless security.hasRole('ADMIN')
    {
      position:   5000
      icon:       'fa fa-cog'
      label:      'Admin'
      iconOnly:   true
    }
  ]

  actionsProvider.registerChildAction 'admin-menu', 'relationship-types', ['security', (security) ->
    {
      icon:       'fa fa-chain fa-fw'
      label:      'Relationship Types'
      action: ->
        # TODO implement with the new admin state
    }
  ]

  actionsProvider.registerChildAction 'admin-menu', 'action-batches', ['security', (security) ->
    {
      icon:       'fa fa-flash fa-fw'
      label:      'Actions'
      action: ->
       # TODO implement with the new admin state
    }
  ]


  actionsProvider.registerActionInRole 'curator-menu', actionsProvider.ROLE_NAVIGATION_RIGHT, ['security', (security) ->
    return undefined unless security.hasRole('CURATOR')
    {
      position:   1000
      icon:       'fa fa-object-group'
      label:      'Curator'
      iconOnly:   true
    }
  ]

  actionsProvider.registerChildAction 'curator-menu', 'csv-transformations', ['security', (security) ->
    {
      icon:       'fa fa-long-arrow-right fa-fw'
      label:      'CSV Transformations'
      action: ->
        # TODO implement with the new admin state
    }
  ]

  actionsProvider.registerChildAction 'curator-menu', 'annotate-letter', ['security', (security) ->
    {
      icon:       'fa fa-envelope-o fa-fw'
      label:      'Annotate Letter'
      action: ->
        # TODO implement with the new admin state
    }
  ]

]