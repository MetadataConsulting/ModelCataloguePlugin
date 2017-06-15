angular.module('mc.util.security', ['http-auth-interceptor', 'mc.util.messages', 'ui.router']).provider('security', [ ->
  noSecurityFactory = ['$log', ($log) ->
    defaultUser = -> { displayName: 'Anonymous Curator', username: 'curator' }
    security =
      isUserLoggedIn: -> true
      getCurrentUser: defaultUser
      hasRole: -> true
      hasRole: -> true
      login: (username, password, rememberMe = false) -> security.getCurrentUser()
      logout: -> $log.info "Logout requested on default security service"
      refreshUserData: defaultUser
      mock: true
  ]

  readOnlySecurityFactory = ['$log', ($log) ->
    defaultUser = -> { displayName: 'Anonymous Viewer', username: 'viewer' }
    security =
      isUserLoggedIn: -> true
      getCurrentUser: defaultUser
      hasRole: (role) -> role == 'VIEWER'
      login: (username, password, rememberMe = false) -> security.getCurrentUser()
      logout: -> $log.info "Logout requested on read only security service"
      refreshUserData: defaultUser
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
    securityFactory = ['$http', '$rootScope', '$q', '$state', '$httpParamSerializer',  ($http, $rootScope, $q, $state, $httpParamSerializer) ->
      httpMethod    = config.httpMethod ? 'POST'
      loginUrl      = 'j_spring_security_check'
      logoutUrl     = 'logout'
      userUrl       = 'user/current'
      usernameParam = config.username ? 'j_username'
      passwordParam = config.password ? 'j_password'
      rememberParam = config.rememberMe ? '_spring_security_remember_me'

      config.contextPath = '' if config.contextPath == '/'

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
        return result if not result.data.username
        if result.data.success
          currentUser = result.data
          currentUser.displayName     ?= currentUser.username
          currentUser.roles           ?= []
          currentUser.classifications ?= []

          for roleName, roleSynonyms of (config.roles ? [])
            for role in roleSynonyms
              if role in currentUser.roles
                currentUser.roles.push roleName
        else
          currentUser = null
        result

      security =
        oauthProviders: config.oauthProviders
        allowRegistration: config.allowRegistration
        canResetPassword: config.canResetPassword
        contextPath: config.contextPath
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

          requestConfig =
            method: httpMethod
            url: loginUrl
          if httpMethod == 'POST'
            requestConfig.data = $httpParamSerializer(params)
            requestConfig.headers =
              'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8;'
          else
            requestConfig.params = params

          $http(requestConfig).then handleUserResponse
        logout: ->
          $http(method: httpMethod, url: logoutUrl).then ->
            currentUser = null
            $state.go 'landing'

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

        refreshUserData: ->
          currentUserPromise = $http(method: 'GET', url: userUrl)

          currentUserPromise.then(handleUserResponse).then ->
            if currentUser
              $rootScope.$broadcast 'userLoggedIn', currentUser
              return currentUser
          return currentUserPromise

      if currentUser
        currentUser.success = true
        handleUserResponse data: currentUser
      else
        security.refreshUserData()

      return security
    ]

  securityProvider.setup = (factory) ->
    securityFactory = factory

  # factory method
  securityProvider.$get = [ '$injector', '$rootScope', '$q', '$log', ($injector, $rootScope, $q, $log) ->

    if securityFactory == noSecurityFactory
      $log.warn "You are using default security service. You should consider setting up more advanced one."

    security = $injector.invoke securityFactory


    throw new Error("Security service must not provide requireLogin() method. Factory: #{angular.toJson(securityFactory)}") if angular.isFunction(security.requireLogin)
    throw new Error("Security service must provide isUserLoggedIn() method. Factory: #{angular.toJson(securityFactory)}") if not angular.isFunction(security.isUserLoggedIn)
    throw new Error("Security service must provide getCurrentUser() method. Factory: #{angular.toJson(securityFactory)}") if not angular.isFunction(security.getCurrentUser)
    throw new Error("Security service must provide hasRole(role) method. Factory: #{angular.toJson(securityFactory)}") if not angular.isFunction(security.hasRole)
    throw new Error("Security service must provide login(username, password, rememberMe) method. Factory: #{angular.toJson(securityFactory)}") if not angular.isFunction(security.login)
    throw new Error("Security service must provide logout() method. Factory: #{angular.toJson(securityFactory)}") if not angular.isFunction(security.logout)

    # add event broadcast on login and logout
    loginFn         = security.login
    security.login  = (username, password, rememberMe) ->
      $q.when(loginFn(username, password, rememberMe)).then (user) ->
        unless user.errors or user.error or user.data?.errors or user.data?.error
          if user.username
            $rootScope.$broadcast 'userLoggedIn', user
            return user
          else if angular.isFunction(security.refreshUserData)
            return security.refreshUserData()
          else
            $log.warn "got wrong user object and don't know how to handle it", user
            return $q.reject user
        else
          $log.warn "login finished with errors", user
          return $q.reject user

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
]).run ['security', '$rootScope', 'messages', 'authService', '$state', (security, $rootScope, messages, authService, $state) ->
  # installs the security listeners
  loginShown = false
  $rootScope.$on 'event:auth-loginRequired', ->
    if security.mock
      messages.error('You are trying to access protected resource',
        'The application will not work as expected. Please, set up the security properly.').noTimeout()
    else
      return if loginShown
      loginShown = true
      messages.prompt('Login', null, type: 'login').then (success)->
        loginShown = false
        authService.loginConfirmed(success)
        messages.clearAllMessages()
      , ->
        loginShown = false
        messages.warning('You are trying to access protected resource', if security.isUserLoggedIn() then 'Please, sign in as different user' else 'Please, sign in').noTimeout()
        $state.go 'landing'


  $rootScope.$security = security

]
