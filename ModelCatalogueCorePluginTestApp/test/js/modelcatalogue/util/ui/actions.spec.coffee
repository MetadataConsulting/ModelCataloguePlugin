describe "mc.util.ui.actions", ->

  beforeEach module 'mc.util.ui.actions'
  beforeEach module 'mc.util.security'

  beforeEach module (actionsProvider) ->

    actionsProvider.registerAction 'edit-catalogue-element', ['security', '$scope', (security, $scope)->
      {
        position:   100
        label:      'Edit'
        icon:       'edit'
        type:       'primary'
        action: ->
          $scope.element.edit()
      }
    ]

    actionsProvider.registerChildAction 'edit-catalogue-element', 'edit-catalogue-element-nested', ['$scope', ($scope)->
      {
        label:      'Edit as Admin'
        icon:       'edit'
        action: ->
          $scope.element.editAsAdmin()
      }
    ]

    actionsProvider.registerAction 'update-catalogue-element', ['security', '$scope', (security, $scope)->
      {
        position:   100
        label:      'Edit'
        icon:       'edit'
        type:       'primary'
        action: ->
          $scope.element.update()
      }
    ], ['navigation']

    return

  it "registers and retrieves action", inject (actions) ->
    editCalled    =  false
    updateCalled  = false
    element =
      edit:   -> editCalled   = true
      update: -> updateCalled = true
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

    editAction.run()

    expect(editCalled).toBeTruthy()

    byId = actions.getActionById('edit-catalogue-element', {element: element})

    expect(byId).toBeDefined()
    expect(byId.position).toBe(100)
    expect(byId.label).toBe('Edit')
    expect(byId.icon).toBe('edit')
    expect(byId.type).toBe('primary')
    expect(byId.children).toBeArray()
    expect(byId.children.length).toBe(1)