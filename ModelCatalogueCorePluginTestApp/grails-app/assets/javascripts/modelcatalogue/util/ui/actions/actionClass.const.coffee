###
Front-end Actions used in Contextual Menu/Contextual Actions
###
class Action

  ###
  The standard fields.

  @param [Number] position –Position according to which actions are sorted in whatever menu they appear in.
  @param [String] label -Human-readable label of action which appears in menu
  @param [String] icon –String which is used as the set of Bootstrap classes for icons
  @param [String] type –String which is used to make Bootstrap class for buttons which may be of different colours. e.g. 'success', 'danger', 'primary', 'warning', etc.
  @param [Function] action –action which is performed when the button is clicked.
  ###
  constructor: (@position, @label, @icon, @type, @action) ->
    @type ?= 'default'
    @abstract = true unless @action

  ###

  @example
    Action.createStandardAction(
      position: 200
      label: 'Create Relationship'
      icon: 'fa fa-fw fa-chain'
      type: 'success'
      action: ->
        messages.prompt('Create Relationship', '', {type: 'create-new-relationship', element: $scope.element, currentDataModel: $scope.currentDataModel}).catch showErrorsUsingMessages(messages)
    )
      .watching ['element.status', 'element.archived']
      .disabledIf $scope.element.archived
  Takes map of named parameters for the five standard fields of an action
  ###
  @createStandardAction: ({position, label, icon, type, action}) ->
    return new Action(position, label, icon, type, action)

  ###
  An abstract action has no action. All abstract actions are parents and vice-versa.
  ###
  @createAbstractAction: ({position, label, icon, type}) ->
    return new Action(position, label, icon, type, null)


  ###the following are three special setters for commonly but not always used fields. The @param syntax automatically sets the parameter as the field with the same name.
  ###

  # @param [Array<String>] watches –events watched for updating actions
  watching: (@watches) -> return @
  # @param [Boolean] active –when the button has active in its HTML class
  activeIf: (@active) -> return @
  # @param [Boolean] disabled –when the button has its HTML disabled attribute set
  disabledIf: (@disabled) -> return @

  # any other fields used will be rare. e.g. iconOnly, run, submit, mode, controller, generator.

  ###
  initialize the run method with angularJS $q and $rootScope for promise and broadcast. Should have id set first.
  For some reason using q as a variable makes it highlighted as if it were a method, so I used promiseQ. Also $broadcast is highlighted when accessed with dot.
  ###
  initializeRun: (promiseQ, rootScope) ->
  if @action
    @run = ->
      unless @disabled
        promiseQ.when(@action()).then (result) ->
          rootScope['$broadcast'] "actionPerformed", @id, promiseQ.when(result)
  else
    @run ?= ->

  ###
  Initialize the action, after it is fresh out of the factory, with more details. Should be used in createAction
  ###
  initialize: (parentId, id, q, rootScope) ->
    @parent = parentId
    @id = id
    initializeRun(q, rootScope)

actionClass = Action
angular.module('mc.util.ui.actions').constant('actionClass', actionClass)
