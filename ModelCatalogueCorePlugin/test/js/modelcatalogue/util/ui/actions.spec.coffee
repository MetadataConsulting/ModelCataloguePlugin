describe "mc.util.ui.actions", ->

  beforeEach module 'mc.util.ui.actions'
  beforeEach module 'mc.util.security'

  beforeEach module (actionsProvider) ->
    actionsProvider.registerAction {
      id:         'edit-catalogue-element'
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
          actionContext.element.edit()
      ]
    }

    actionsProvider.registerAction {
      parent:     'edit-catalogue-element'
      id:         'edit-catalogue-element-nested'
      label:      'Edit as Admin'
      icon:       'edit'
      action: [ 'actionContext', (actionContext) ->
        actionContext.element.editAsAdmin()
      ]
    }

    return

  it "registers and retrieves action", inject (actions) ->
    editCalled =  false
    element =
      edit: -> editCalled = true
      name: "Foo"
    currentActions = actions.getActions({element: element})

    expect(editCalled).toBeFalsy()
    expect(currentActions.length).toBe(1)

    editAction = currentActions[0]

    expect(editAction).toBeDefined()
    expect(editAction.position).toBe(100)
    expect(editAction.label).toBe('Edit')
    expect(editAction.icon).toBe('edit')
    expect(editAction.type).toBe('primary')

    expect(editAction.children).toBeArray()
    expect(editAction.children.length).toBe(1)

    editAction()

    expect(editCalled).toBeTruthy()