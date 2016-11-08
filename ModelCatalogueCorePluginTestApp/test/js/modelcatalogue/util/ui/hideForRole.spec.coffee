describe "mc.util.ui.hideForRole", ->
  loggedIn = true

  beforeEach module 'mc.util.ui.hideForRole'
  beforeEach module (securityProvider) ->
    securityProvider.setup ['$log', ($log) ->
      security =
        isUserLoggedIn: -> loggedIn
        getCurrentUser: -> if loggedIn then { displayName: 'Horrible Monster', username: 'hmonster', hasRole: (role) -> role == 'VIEWER' } else undefined
        hasRole: (role) -> loggedIn and role == 'VIEWER'
        login: (username, password, rememberMe = false) -> loggedIn = true ; security.getCurrentUser()
        logout: -> loggedIn = false ; $log.info "Logout requested on custom security service"
    ]

  it "the element has security-hide class if the user has the role", inject ($compile, $rootScope, security) ->
    expect(security.hasRole('VIEWER')).toBeTruthy()

    element = $compile('''
        <h1 hide-for-role="VIEWER">Foo</h1>
      ''')($rootScope)

    $rootScope.$digest()

    expect(element.hasClass('security-hide')).toBeTruthy()

  it "the element has not security-hide class if the user has the role", inject ($compile, $rootScope, security) ->
    expect(security.hasRole('CURATOR')).toBeFalsy()

    element = $compile('''
        <h1 hide-for-role="CURATOR">Foo</h1>
      ''')($rootScope)

    $rootScope.$digest()


    expect(element.hasClass('security-hide')).toBeFalsy()

  it "the element reacts on logged in and logged out events", inject ($compile, $rootScope, security) ->

    element = $compile('''
        <h1 hide-for-role="VIEWER">Foo</h1>
      ''')($rootScope)

    $rootScope.$digest()

    expect(security.hasRole('VIEWER')).toBeTruthy()
    expect(element.hasClass('security-hide')).toBeTruthy()

    security.logout()

    $rootScope.$digest()

    expect(security.hasRole('VIEWER')).toBeFalsy()
    expect(element.hasClass('security-hide')).toBeFalsy()

    security.login()

    $rootScope.$digest()

    expect(security.hasRole('VIEWER')).toBeTruthy()
    expect(element.hasClass('security-hide')).toBeTruthy()

