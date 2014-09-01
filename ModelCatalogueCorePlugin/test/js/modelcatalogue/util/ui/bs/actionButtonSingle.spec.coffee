describe "mc.util.ui.actionButtonSingle", ->

  beforeEach module 'mc.util.ui.bs.actionButtonSingle'
  beforeEach module 'mc.util.ui.actions'

  beforeEach module (actionsProvider) ->
    actionsProvider.registerAction 'edit-catalogue-element-5', ['$scope', ($scope)->
      {
        position:   100
        label:      'Edit'
        icon:       'glyphicon glyphicon-edit'
        type:       'primary'
        action:     ->
          $scope.element.name = 'The Earth Element'
      }
    ]

    return

  it "render single button for action with no children", inject ($compile, $rootScope, actions) ->

    contextEl = {name: 'The Element'}
    $scope = $rootScope.$new(true)
    $scope.element = contextEl
    $rootScope.action  = actions.getActionById('edit-catalogue-element-5', $scope)

    element = $compile('''
        <action-button-single action="action" />
      ''')($rootScope)

    $rootScope.$digest()

    expect(element.hasClass('btn')).toBeTruthy()
    expect(element.hasClass('btn-primary')).toBeTruthy()
    expect(element.text().trim()).toBe('Edit')
    expect(element.find('span.glyphicon.glyphicon-edit').length).toBe(1)

    expect(contextEl.name).toBe('The Element')

    element.click()

    expect(contextEl.name).toBe('The Earth Element')