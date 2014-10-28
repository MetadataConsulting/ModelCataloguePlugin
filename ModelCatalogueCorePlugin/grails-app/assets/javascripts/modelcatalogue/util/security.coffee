angular.module('mc.util.security', ['http-auth-interceptor', 'mc.util.messages']).provider('security', [ ->
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

  # you need login to return
  securityProvider.springSecurity = (config = {}) ->
    securityFactory = ['$http', '$rootScope', '$q',  ($http, $rootScope, $q) ->
      httpMethod    = config.httpMethod ? 'POST'
      loginUrl      = 'j_spring_security_check'
      logoutUrl     = 'logout'
      userUrl       = 'user/current'
      usernameParam = config.username ? 'j_username'
      passwordParam = config.password ? 'j_password'
      rememberParam = config.rememberMe ? '_spring_security_remember_me'


      if config.userUrl
        userUrl = config.userUrl
      else if config.contextPath
        userUrl = "#{config.contextPath}/#{userUrl}"

      if config.loginUrl
        loginUrl = config.loginUrl
      else if config.contextPath
        loginUrl = "#{config.contextPath}/#{loginUrl}"

      if config.logoutUrl
        logoutUrl = config.logoutUrl
      else if config.contextPath
        logoutUrl = "#{config.contextPath}/#{logoutUrl}"

      currentUser = config.currentUser

      handleUserResponse = (result) ->
        if result.data.success
          currentUser = result.data
          currentUser.displayName     ?= currentUser.username
          currentUser.roles           ?= []
          currentUser.classifications ?= []

          for roleName, roleSynonyms of (config.roles ? [])
            for role in roleSynonyms
              if role in currentUser.roles
                currentUser.roles.push roleName

        result

      security =
        isUserLoggedIn: ->
          currentUser?
        getCurrentUser: ->
          currentUser
        hasRole: (role) ->
          return false if not currentUser?.roles
          return role in currentUser.roles
        login: (username, password, rememberMe = false) ->
          params = {ajax: true}
          params[usernameParam] = username
          params[passwordParam] = password

          if rememberMe
            params[rememberParam] = 'on'

          $http(
            method: httpMethod,
            url: loginUrl
            params: params
          ).then handleUserResponse
        logout: ->
          $http(method: httpMethod, url: logoutUrl).then ->
            currentUser = null
        requireUser: ->
          $http(method: 'GET', url: userUrl).then(handleUserResponse).then (result)->
            if result.data?.success
              return security.getCurrentUser()
            $q.reject result
        requireRole: (role) ->
          security.requireUser().then ->
            if security.hasRole(role)
              return security.getCurrentUser()
            $q.reject security.getCurrentUser()

      if currentUser
        currentUser.success = true
        handleUserResponse data: currentUser
      else
        currentUserPromise = $http(method: 'GET', url: userUrl)

        currentUserPromise.then(handleUserResponse).then ->
          if currentUser
            $rootScope.$broadcast 'userLoggedIn', currentUser

      return security
    ]

  securityProvider.setup = (factory) ->
    securityFactory = factory

  # factory method
  securityProvider.$get = [ '$injector', '$rootScope', '$q', '$log', ($injector, $rootScope, $q, $log) ->

    if securityFactory == noSecurityFactory
      $log.warn "You are using default security service. You should consider setting up more advanced one."

    security = $injector.invoke securityFactory

    throw "security service must not provide requireLogin() method" if angular.isFunction(security.requireLogin)
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
      $q.when(logoutFn()).then ->
        $rootScope.$broadcast 'userLoggedOut', oldUser

    security.requireLogin = ->
      $rootScope.$broadcast 'event:auth-loginRequired'

    security
  ]

  securityProvider
]).run ['security', '$rootScope', 'messages', 'authService', (security, $rootScope, messages, authService) ->
  # installs the security listeners
  $rootScope.$on 'event:auth-loginRequired', ->
    if security.mock
      messages.error('You are trying to access protected resource',
        'The application will not work as expected. Please, set up the security properly.').noTimeout()
    else
      messages.prompt('Login', null, type: 'login').then (success)->
        authService.loginConfirmed(success)
        messages.clearAllMessages()
      , ->
        messages.warning('You are trying to access protected resource',
          if security.isUserLoggedIn() then 'Please, sign in as different user' else 'Please, sign in').noTimeout()
  $rootScope.$security = security

]