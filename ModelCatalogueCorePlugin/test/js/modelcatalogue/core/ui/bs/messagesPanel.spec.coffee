describe "mc.core.ui.bs.messagesPanel", ->

  beforeEach module 'mc.core.ui.bs.messagesPanel'

  it "element is compiled", inject (messages, $rootScope, $compile) ->
    element = $compile('''
    <messages-panel max="2"></messages-panel>
    ''')($rootScope)

    $rootScope.$digest()

    expect(element.find('.alert').length).toBe(0)

    messages.info('Test 1', 'Test 1 Body')

    $rootScope.$digest()

    expect(element.find('.alert').length).toBe(1)
    expect(element.find('.alert.alert-info').length).toBe(1)

    messages.error('This should not happen!')

    $rootScope.$digest()

    expect(element.find('.alert').length).toBe(2)
    expect(element.find('.alert.alert-info').length).toBe(1)
    expect(element.find('.alert.alert-danger').length).toBe(1)

    messages.warning('Warn you!')

    $rootScope.$digest()

    expect(element.find('.alert').length).toBe(2)
    expect(element.find('.alert.alert-info').length).toBe(0)
    expect(element.find('.alert.alert-danger').length).toBe(1)
    expect(element.find('.alert.alert-warning').length).toBe(1)

    element.find('.close').click()

    $rootScope.$digest()

    expect(element.find('.alert').length).toBe(1)
    expect(messages.getMessages().length).toBe(1)

    element.find('.close').click()

    $rootScope.$digest()

    expect(element.find('.alert').length).toBe(0)
    expect(messages.getMessages().length).toBe(0)

  it "element can use custom messages", inject (messages, $rootScope, $compile) ->
    $rootScope.localMessages = messages.createNewMessages()

    element = $compile('''
    <messages-panel max="2" messages="localMessages"></messages-panel>
    ''')($rootScope)

    $rootScope.$digest()

    expect(element.find('.alert').length).toBe(0)

    messages.info('Test 1', 'Test 1 Body')

    $rootScope.$digest()

    expect(element.find('.alert').length).toBe(0)

    $rootScope.localMessages.info('Test local', 'Body local')

    $rootScope.$digest()

    expect(element.find('.alert').length).toBe(1)


