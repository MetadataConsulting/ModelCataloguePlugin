describe "mc.util.ui.showIfLoggedIn", ->
  loggedIn = true

  beforeEach module 'mc.util.ui.showIfLoggedIn'
  beforeEach module (securityProvider) ->
    securityProvider.setup ['$log', ($log) ->
      security =
        isUserLoggedIn: -> loggedIn
        getCurrentUser: -> if loggedIn then { displayName: 'Horrible Monster', username: 'hmonster', hasRole: (role) -> role == 'VIEWER' } else undefined
        hasRole: (role) -> loggedIn and role == 'VIEWER'
        login: (username, password, rememberMe = false) -> loggedIn = true ; security.getCurrentUser()
        logout: -> loggedIn = false ; $log.info "Logout requested on custom security service"
    ]

  it "the element has not security-hide class if the user is logged in", inject ($compile, $rootScope, security) ->
    security.login()
    $rootScope.$digest()

    expect(security.isUserLoggedIn()).toBeTruthy()

    element = $compile('''
        <h1 show-if-logged-in>Foo</h1>
      ''')($rootScope)

    $rootScope.$digest()

    expect(element.hasClass('security-hide')).toBeFalsy()

  it "the element has security-hide class if the user is logged out", inject ($compile, $rootScope, security) ->
    security.logout()
    expect(security.isUserLoggedIn()).toBeFalsy()

    element = $compile('''
        <h1 show-if-logged-in>Foo</h1>
      ''')($rootScope)

    $rootScope.$digest()


    expect(element.hasClass('security-hide')).toBeTruthy()

  it "the element reacts on logged in and logged out events", inject ($compile, $rootScope, security) ->
    security.login()

    element = $compile('''
        <h1 show-if-logged-in>Foo</h1>
      ''')($rootScope)

    $rootScope.$digest()

    expect(security.isUserLoggedIn()).toBeTruthy()
    expect(element.hasClass('security-hide')).toBeFalsy()

    security.logout()

    $rootScope.$digest()

    expect(security.isUserLoggedIn()).toBeFalsy()
    expect(element.hasClass('security-hide')).toBeTruthy()

    security.login()

    $rootScope.$digest()

    expect(security.isUserLoggedIn()).toBeTruthy()
    expect(element.hasClass('security-hide')).toBeFalsy()

