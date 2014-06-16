angular.module('mc.util.security', []).provider 'security', [ ->
  noSecurityFactory = ['$log', ($log) ->
    security =
      isUserLoggedIn: -> true
      getCurrentUser: -> { displayName: 'Anonymous Curator' }
      hasRole: (role) -> true
      login: (username, password, rememberMe = false) -> security.getCurrentUser()
      logout: -> $log.info "Logout requested on default security service"
      mock: true

  ]

  readOnlySecurityFactory = ['$log', ($log) ->
    security =
      isUserLoggedIn: -> true
      getCurrentUser: -> { displayName: 'Anonymous Viewer' }
      hasRole: (role) -> role == 'VIEWER'
      login: (username, password, rememberMe = false) -> security.getCurrentUser()
      logout: -> $log.info "Logout requested on read only security service"
      mock: true
  ]

  securityFactory = noSecurityFactory

  securityProvider = {}
  securityProvider.readOnly = ->
    securityFactory = readOnlySecurityFactory

  securityProvider.noSecurity = ->
    securityFactory = noSecurityFactory

  securityProvider.setup = (factory) ->
    securityFactory = factory

  # factory method
  securityProvider.$get = [ '$injector', '$rootScope', '$q', '$log', ($injector, $rootScope, $q, $log) ->

    if securityFactory == noSecurityFactory
      $log.warn "You are using default security service. You should consider setting up more advanced one."

    security = $injector.invoke securityFactory

    throw "security service must provide isUserLoggedIn() method" if not angular.isFunction(security.isUserLoggedIn)
    throw "security service must provide getCurrentUser() method" if not angular.isFunction(security.getCurrentUser)
    throw "security service must provide hasRole(role) method" if not angular.isFunction(security.hasRole)
    throw "security service must provide login(username, password, rememberMe) method" if not angular.isFunction(security.login)
    throw "security service must provide logout() method" if not angular.isFunction(security.logout)

    # add event broadcast on login and logout
    loginFn         = security.login
    security.login  = (username, password, rememberMe) ->
      $q.when(loginFn(username, password, rememberMe)).then (user) ->
        if not user.errors
          $rootScope.$broadcast 'userLoggedIn', user
        user

    logoutFn        = security.logout
    security.logout = ->
      oldUser = security.getCurrentUser()
      logoutFn()
      $rootScope.$broadcast 'userLoggedOut', oldUser

    security
  ]

  securityProvider
]