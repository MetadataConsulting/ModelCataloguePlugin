describe "mc.util.security", ->

  beforeEach module "mc.util.security"

  describe "default security doesn't provide any security constraints", ->

    it "returns default security service", inject (security, $rootScope) ->
      expect(security.mock).toBeTruthy()
      expect(security.isUserLoggedIn()).toBeTruthy()

      expect(security.login).toBeFunction()
      expect(security.logout).toBeFunction()

      expect(security.hasRole('CURATOR')).toBeTruthy()
      expect(security.hasRole('VIEWER')).toBeTruthy()

      user = security.getCurrentUser()

      expect(user).toBeDefined()
      expect(user.displayName).toBe('Anonymous Curator')


      newUser = null

      security.login().then (_newUser_) ->
        newUser = _newUser_

      expect(newUser).toBeNull()

      $rootScope.$digest()

      expect(angular.equals(newUser, user)).toBeTruthy()

  describe "read only security provides user with the role VIEWER", ->
    beforeEach module (securityProvider) ->
      securityProvider.readOnly()
      return

    it "returns read only security service", inject (security, $rootScope) ->

      expect(security.mock).toBeTruthy()
      expect(security.isUserLoggedIn()).toBeTruthy()

      expect(security.login).toBeFunction()
      expect(security.logout).toBeFunction()


      expect(security.hasRole('CURATOR')).toBeFalsy()
      expect(security.hasRole('VIEWER')).toBeTruthy()

      user = security.getCurrentUser()

      expect(user).toBeDefined()
      expect(user.displayName).toBe('Anonymous Viewer')

      newUser = null

      security.login().then (_newUser_) ->
        newUser = _newUser_

      expect(newUser).toBeNull()

      $rootScope.$digest()

      expect(angular.equals(newUser, user)).toBeTruthy()


  describe "can setup own provider", ->
    beforeEach module (securityProvider) ->
      securityProvider.setup ['$log', ($log) ->
        security =
          isUserLoggedIn: -> true
          getCurrentUser: -> { displayName: 'Horrible Monster', username: 'hmonster', hasRole: (role) -> role == 'BOO' }
          hasRole: (role) -> role == 'BOO'
          login: (username, password, rememberMe = false) -> security.getCurrentUser()
          logout: -> $log.info "Logout requested on custom security service"
      ]
      return

    it "returns read only security service", inject (security, $rootScope) ->

      expect(security.mock).toBeUndefined()
      expect(security.isUserLoggedIn()).toBeTruthy()

      expect(security.login).toBeFunction()
      expect(security.logout).toBeFunction()

      user = security.getCurrentUser()

      expect(user).toBeDefined()
      expect(user.displayName).toBe('Horrible Monster')

      newUser = null

      security.login().then (_newUser_) ->
        newUser = _newUser_

      expect(newUser).toBeNull()

      $rootScope.$digest()

      expect(angular.equals(newUser, user)).toBeTruthy()