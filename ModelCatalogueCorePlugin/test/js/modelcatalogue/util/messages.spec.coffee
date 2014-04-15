describe "mc.util.messages", ->
  beforeEach module "mc.util.messages"

  $window = null


  beforeEach module ($provide)->
    $window =
      confirm: () -> true
      prompt:  () -> "hello"

    $provide.value('$window', $window)
    return


  afterEach inject (messages) ->
    messages.clearAllMessages()

  testMessageAdded = (name, type = name) ->
    inject (messages) ->
      expect(messages.getMessages().length).toBe(0)
      first = messages[name]("#{name} title", "#{name} body")
      expect(messages.getMessages().length).toBe(1)
      expect(messages.getMessages()[0].title).toBe("#{name} title")
      expect(messages.getMessages()[0].body).toBe("#{name} body")
      expect(messages.getMessages()[0].type).toBe(type)
      expect(messages.getMessages()[0].messageId).toBeDefined()

      second = messages[name]("#{name} title 2", "#{name} body 2")
      expect(messages.getMessages().length).toBe(2)

      first.remove()
      expect(messages.getMessages().length).toBe(1)

      messages.removeMessage(second)
      expect(messages.getMessages().length).toBe(0)

  it "stacks info messages in getMessages() array", testMessageAdded('info')
  it "stacks success messages in getMessages() array", testMessageAdded('success')
  it "stacks warning messages in getMessages() array", testMessageAdded('warning')
  it "stacks error messages in getMessages() array", testMessageAdded('error', 'danger')

  it "returns true from confirm", inject (messages, $rootScope) ->
    value = null
    messages.confirm('the title', 'the body').then ()->
      value = true

    expect(value).toBeNull()

    $rootScope.$apply()

    expect(value).toBeTruthy()

  it "returns hello from prompt", inject (messages, $rootScope) ->
    value = null
    messages.prompt('the title', 'the body').then (val)->
      value  = val

    expect(value).toBeNull()

    $rootScope.$apply()

    expect(value).toBe("hello")











