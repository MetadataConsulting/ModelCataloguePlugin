describe "mc.util.ui.bs.contextualActions", ->
  beforeEach module 'mc.util.ui.bs.contextualActions'
  beforeEach module 'mc.util.ui.actions'
  beforeEach module 'mc.util.security'

  beforeEach module (actionsProvider) ->
    actionsProvider.registerAction 'edit-catalogue-element-3', ['$scope', ($scope)->
      {
        position:   100
        label:      'Edit'
        icon:       'edit'
        type:       'primary'
        action:     ->
          $scope.element.name = 'The Earth Element'
      }
    ]

    actionsProvider.registerChildAction 'edit-catalogue-element-3', 'edit-catalogue-element-nested-3', ['$scope', ($scope)->
      {
         label:      'Edit as Admin'
         icon:       'edit'
         action:  ->
           $scope.element.name = 'The Element Edited'
      }
    ]

    return

  it "render contextual action", inject ($compile, $rootScope) ->

    $rootScope.element = {name: 'The Element'}

    element = $compile('''
        <contextual-actions />
      ''')($rootScope)

    $rootScope.$digest()

    expect(element.find('button:first-child').hasClass('btn')).toBeTruthy()
    expect(element.find('button:first-child').hasClass('btn-primary')).toBeTruthy()
    expect(element.find('button:first-child').text().trim()).toBe('Edit')
    expect(element.find('button:first-child').find('span.glyphicon.glyphicon-edit').length).toBe(1)

    expect($rootScope.element.name).toBe('The Element')

    element.find('button:first-child').click()

    expect($rootScope.element.name).toBe('The Earth Element')


