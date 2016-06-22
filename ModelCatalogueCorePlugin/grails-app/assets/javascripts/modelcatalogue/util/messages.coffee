angular.module('mc.util.messages', []).provider 'messages', [ ->
  confirmFactory = null
  defaultPromptFactory = null

  promptFactories = {}

  nextId = 1

  hideAfter = 3000
  errorHideAfter = 15000

  messagesProvider = @

  @setConfirmFactory = (customConfirm) ->
    confirmFactory = customConfirm

  @setDefaultPromptFactory = (customPrompt) ->
    defaultPromptFactory = customPrompt

  @setPromptFactory = (type, customPromptFactory) ->
    promptFactories[type] = customPromptFactory

  @hasPromptFactory = (type) ->
    promptFactories[type]?

  @setHideAfter = (newHideAfter) ->
    hideAfter = newHideAfter

  # factory method
  @$get = [ '$injector', '$q', '$log', '$window', '$timeout', '$rootScope', ($injector, $q, $log, $window, $timeout, $rootScope) ->
    createNewMessages = (timeout) ->
      messagesStack = []

      messages = {}

      confirm = null
      prompt = null
      promptByTypes = {}

      defaultConfirm = (title, body) ->
        deferred = $q.defer()
        if $window.confirm("#{title}\n#{body}")
          deferred.resolve()
        else
          deferred.reject()
        deferred.promise

      defaultPrompt = (title, body) ->
        deferred = $q.defer()
        result = $window.prompt("#{title}\n#{body}")
        if result
          deferred.resolve result
        else
          deferred.reject result
        deferred.promise

      if confirmFactory?
        confirm = $injector.invoke(confirmFactory, undefined, {messages: messages})
      else
        confirm = defaultConfirm

      if defaultPromptFactory?
        prompt = $injector.invoke(defaultPromptFactory, undefined, {messages: messages})
      else
        prompt = defaultPrompt

      for type, factory of promptFactories
        promptByTypes[type] = $injector.invoke(factory, undefined, {messages: messages})

      addMessage = (title, body, type) ->
        afterTimeout = null
        # if you pass only first argument it will became the body
        if not body?
          body = title
          title = null
        msg =
          title: title
          body: body
          type: type
          messageId: nextId++

        msg.remove = ->
          messages.removeMessage(msg)


        msg.timeout = (timeout) ->
          # first cancel the old timeout
          $timeout.cancel(afterTimeout) if afterTimeout
          # set new timeout
          afterTimeout = $timeout((-> msg.remove()), timeout)
          msg


        for existing in messagesStack
          return existing if existing.type == msg.type and existing.title == msg.title and existing.body == msg.body

        messagesStack.push msg

        if timeout
          afterTimeout = $timeout((-> msg.remove()), hideAfter)
          msg.noTimeout = -> $timeout.cancel(afterTimeout)
        else
          msg.noTimeout = ->

        msg

      $rootScope.$on 'displayGlobalMessage', (ignored, title, body, type, noTimeout) ->
        msg = addMessage(title, body, type)
        msg.noTimeout() if noTimeout

      ###
        Shows the info message to the user. Returns the message instance.
      ###
      messages.info = (title, body) ->
        addMessage(title, body, 'info')

      ###
        Shows the success message to the user. Returns the message instance.
      ###
      messages.success = (title, body) ->
        addMessage(title, body, 'success')

      ###
        Shows the warning message to the user. Returns the message instance.
      ###
      messages.warning = (title, body) ->
        msg = addMessage(title, body, 'warning')
        msg.timeout(errorHideAfter) if timeout
        msg

      ###
        Shows the error message to the user. Returns the message instance.
      ###
      messages.error = (title, body) ->
        msg = addMessage(title, body, 'danger')
        msg.timeout(errorHideAfter) if timeout
        msg

      ###
        Shows the confirm dialog and returns a promise which is always resolved to boolean value which
        will be true if user confirms the dialog or false if the user rejectes.
      ###
      messages.confirm = (title, body) ->
        confirm(title, body)

      ###
        Prompts users for input. The method returns promise which is resolved if the user submits the value
        and rejected if the user cancels the input. The type is optional type of input which doesn't have to be
        supported by all the implementations.
      ###

      messages.prompt = (title, body, args) ->
        return prompt(title, body, args) if not args?.type?
        customPrompt = promptByTypes[args.type]

        if not customPrompt?
          $log.warn("Prompt for type #{args.type} is not registered, using default instead")
          return prompt(title, body, args)

        customPrompt title, body, args


      ###
        Array of currently displayed messages if stacking of messages is supported.
        The messages are stored as objects with type, title and body messages.
      ###
      messages.getMessages = () ->
        messagesStack


      messages.clearAllMessages = () ->
        messagesStack = []

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

      # by default custom messages does not timeout
      messages.createNewMessages = (timeout) ->
        createNewMessages(timeout)

      messages.hasPromptFactory = (type) ->
        messagesProvider.hasPromptFactory(type)

      messages.showErrorsFromResponse = (response) ->
        if response?.data and response.data.errors
          if angular.isString response.data.errors
            messages.error response.data.errors
          else
            for err in response.data.errors
              messages.error err.message
        messages

      messages

    # messages timeouts by default
    createNewMessages(true)
  ]

  # Always return this from CoffeeScript AngularJS factory functions!
  @
]
