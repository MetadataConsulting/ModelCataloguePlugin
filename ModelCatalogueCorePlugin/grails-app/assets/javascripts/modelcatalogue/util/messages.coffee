angular.module('mc.util.messages', []).provider 'messages', [ ->

  confirmFactory = null
  promptFactory  = null

  nextId        = 1
  messagesStack = []

  @setConfirmFactory = (customConfirm) ->
    confirmFactory = customConfirm

  @setPromptFactory = (customPrompt) ->
    promptFactory = customPrompt

  # factory method
  @$get = [ '$injector', '$q', '$window', ($injector, $q, $window) ->
    confirm        = null
    prompt         = null
    defaultConfirm = (title, body) -> $q.when($window.confirm("#{title}\n#{body}"))
    defaultPrompt  = (title, body) ->
      deferred = $q.defer()
      result = $window.prompt("#{title}\n#{body}")
      if result
        deferred.resolve result
      else
        deferred.reject result
      deferred.promise

    if confirmFactory?
      confirm = $injector.invoke(confirmFactory)
    else
      confirm = defaultConfirm

    if promptFactory?
      prompt = $injector.invoke(promptFactory)
    else
      prompt = defaultPrompt

    messages = {}

    addMessage = (title, body, type) ->
      msg =
        title:      title
        body:       body
        type:       type
        messageId:  nextId++

      msg.remove = () -> messages.removeMessage(msg)

      messagesStack.push msg
      msg

    ###
      Shows the info message to the user. Returns the message instance.
    ###
    messages.info     = (title, body) ->
      addMessage(title, body, 'info')

    ###
      Shows the success message to the user. Returns the message instance.
    ###
    messages.success  = (title, body) ->
      addMessage(title, body, 'success')

    ###
      Shows the warning message to the user. Returns the message instance.
    ###
    messages.warning  = (title, body) ->
      addMessage(title, body, 'warning')

    ###
      Shows the error message to the user. Returns the message instance.
    ###
    messages.error    = (title, body) ->
      addMessage(title, body, 'danger')

    ###
      Shows the confirm dialog and returns a promise which is always resolved to boolean value which
      will be true if user confirms the dialog or false if the user rejectes.
    ###
    messages.confirm  = (title, body) -> confirm(title, body)

    ###
      Prompts users for input. The method returns promise which is resolved if the user submits the value
      and rejected if the user cancels the input. The type is optional type of input which doesn't have to be
      supported by all the implementations.
    ###
    messages.prompt   = (title, body, type) -> prompt(title, body, type)

    ###
      Array of currently displayed messages if stacking of messages is supported.
      The messages are stored as objects with type, title and body messages.
    ###
    messages.getMessages = () -> messagesStack


    messages.clearAllMessages = () -> messagesStack = []

    messages.removeMessage = (messageToRemove) ->
      index = -1
      for msg, i in messagesStack
        if msg.messageId == messageToRemove.messageId
          index = i
          break

      removed = null

      if index >= 0
        removed = messagesStack[index]
        messagesStack.splice(index, 1)

      removed

    messages
  ]

  # Always return this from CoffeeScript AngularJS factory functions!
  @
]