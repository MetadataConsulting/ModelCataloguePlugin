describe "mc.util.ui.actionButtonDropdown", ->
  beforeEach module 'mc.util.ui.bs.actionButtonDropdown'
  beforeEach module 'mc.util.ui.actions'

  beforeEach module (actionsProvider) ->
    actionsProvider.registerAction  'edit-catalogue-element-2', [ '$scope', ($scope) ->
      {
        position:   100
        label:      'Edit'
        icon:       'glyphicon glyphicon-edit'
        type:       'primary'
        action: ->
          $scope.element.name = 'The Earth Element'
      }
    ]

    actionsProvider.registerChildAction 'edit-catalogue-element-2', 'edit-catalogue-element-nested-2', [ '$scope', ($scope) ->

      {
        label:      'Edit as Admin'
        icon:       'glyphicon glyphicon-edit'
        action: ->
          $scope.element.name = 'The Element Edited'
      }
    ]

    return

  it "render dropdown for button for action with some children", inject ($compile, $rootScope, actions) ->

    contextEl         = {name: 'The Element'}
    $rootScope.action = actions.getActionById('edit-catalogue-element-2', {element: contextEl })

    expect($rootScope.action).toBeDefined()
    expect($rootScope.action.id).toBe('edit-catalogue-element-2')
    expect($rootScope.action.label).toBe('Edit')
    expect($rootScope.action.parent).toBeUndefined()

    $rootScope.action.abstract = true

    element = $compile('''
        <action-button-dropdown action="action" />
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
