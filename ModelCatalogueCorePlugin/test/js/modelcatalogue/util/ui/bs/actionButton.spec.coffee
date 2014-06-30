describe "mc.util.ui.bs.actionButton", ->
  beforeEach module 'mc.util.ui.bs.actionButton'
  beforeEach module 'mc.util.ui.actions'
  beforeEach module 'mc.util.security'

  beforeEach module (actionsProvider) ->
    actionsProvider.registerAction {
      id:         'edit-catalogue-element-2'
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
      parent:     'edit-catalogue-element-2'
      id:         'edit-catalogue-element-nested-2'
      label:      'Edit as Admin'
      icon:       'edit'
      action: [ 'actionContext', (actionContext) ->
        actionContext.element.name = 'The Element Edited'
      ]
    }

    return

  it "render single button for action with no children", inject ($compile, $rootScope, actions) ->

    contextEl = {name: 'The Element'}
    $rootScope.action  = actions.getActionById('edit-catalogue-element-nested-2', {element: contextEl })

    element = $compile('''
        <action-button action="action" />
      ''')($rootScope)

    $rootScope.$digest()

    expect(element.find('button').hasClass('btn')).toBeTruthy()
    expect(element.find('button').hasClass('btn-default')).toBeTruthy()
    expect(element.find('button').text().trim()).toBe('Edit as Admin')
    expect(element.find('button').find('span.glyphicon.glyphicon-edit').length).toBe(1)

    expect(contextEl.name).toBe('The Element')

    element.find('button').click()

    expect(contextEl.name).toBe('The Element Edited')



  it "render dropdown for button for action with some children", inject ($compile, $rootScope, actions) ->

    contextEl         = {name: 'The Element'}
    $rootScope.action = actions.getActionById('edit-catalogue-element-2', {element: contextEl })
    $rootScope.action.abstract = true

    element = $compile('''
        <action-button action="action" />
      ''')($rootScope)

    $rootScope.$digest()

    expect(element.find('button:first-child').hasClass('btn')).toBeTruthy()
    expect(element.find('button:first-child').hasClass('dropdown-toggle')).toBeTruthy()
    expect(element.find('button:first-child').hasClass('btn-primary')).toBeTruthy()
    expect(element.find('button:first-child').text().trim()).toBe('Edit')
    expect(element.find('button:first-child').find('span.glyphicon.glyphicon-edit').length).toBe(1)

    expect(contextEl.name).toBe('The Element')

    element.find('button:first-child').click()

    expect(contextEl.name).toBe('The Element')

    childActionLink = element.find('ul.dropdown-menu li a')

    expect(childActionLink.length).toBe(1)

    expect(childActionLink.text().trim()).toBe('Edit as Admin')
    expect(childActionLink.find('span.glyphicon.glyphicon-edit').length).toBe(1)

    childActionLink.click()

    expect(contextEl.name).toBe('The Element Edited')
