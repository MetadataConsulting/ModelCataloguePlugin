describe "mc.util.ui.bs.contextualActions", ->
  beforeEach module 'mc.util.ui.bs.contextualActions'
  beforeEach module 'mc.util.ui.actions'
  beforeEach module 'mc.util.security'

  beforeEach module (actionsProvider) ->
    actionsProvider.registerAction {
      id:         'edit-catalogue-element-3'
      position:   100
      label:      'Edit'
      icon:       'edit'
      type:       'primary'
      condition:  ['security', 'actionContext', (security, actionContext)->
        # returning false will hide the element completely
        return false if not actionContext.element
        # returning 'disabled' will show the action but disable it (action.disabled == true)
        return 'disabled' if not security.hasRole('CURATOR')
        # always return true explicitly
        return true
      ]
      action: [ 'actionContext', (actionContext) ->
        actionContext.element.name = 'The Earth Element'
      ]
    }

    actionsProvider.registerAction {
      parent:     'edit-catalogue-element-3'
      id:         'edit-catalogue-element-nested-3'
      label:      'Edit as Admin'
      icon:       'edit'
      action: [ 'actionContext', (actionContext) ->
        actionContext.element.name = 'The Element Edited'
      ]
    }

    return

  it "render contextual action", inject ($compile, $rootScope) ->

    $rootScope.context = {element: {name: 'The Element'}}

    element = $compile('''
        <contextual-actions context="context" />
      ''')($rootScope)

    $rootScope.$digest()

    expect(element.find('button:first-child').hasClass('btn')).toBeTruthy()
    expect(element.find('button:first-child').hasClass('btn-primary')).toBeTruthy()
    expect(element.find('button:first-child').text().trim()).toBe('Edit')
    expect(element.find('button:first-child').find('span.glyphicon.glyphicon-edit').length).toBe(1)

    expect($rootScope.context.element.name).toBe('The Element')

    element.find('button:first-child').click()

    expect($rootScope.context.element.name).toBe('The Earth Element')


