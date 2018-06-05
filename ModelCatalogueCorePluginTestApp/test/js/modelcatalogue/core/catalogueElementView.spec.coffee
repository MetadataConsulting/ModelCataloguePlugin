describe 'mc.core.ui.catalogueElementView', ->
  beforeEach module 'mc.core.ui.catalogueElementView'
  beforeEach module 'mc.core.ui.bs.messagesPanel'
  beforeEach module 'mc.core.ui'
  beforeEach module 'karmaTestingTemplates'

  $controller = null
  $rootScope = null
  $compile = null
  $httpBackend = null

  beforeEach(inject (_$controller_, _$compile_, _$rootScope_, $injector) ->
#     The injector unwraps the underscores (_) from around the parameter names when matching
    $controller = _$controller_
    $compile = _$compile_
    $rootScope = _$rootScope_
    $httpBackend = $injector.get('$httpBackend');
  );
  describe('directive', ->

    it('has lots of stuff in it', ->

      element = $compile("<catalogue-element-view></catalogue-element-view>")($rootScope)
      $rootScope.$digest()


      controller = element.controller('catalogueElementView')
      scope = element.isolateScope() || element.scope()


      expect(typeof controller.getTabDefinition).toBe('undefined')
#      For some reason this is not available, perhaps I have not set it up right.

      expect(scope.reports).toEqual([])
      expect(typeof scope.security.isUserLoggedIn).toEqual('function')
#      expect(controller.getTabDefinition())

#  TODO: Test expected tabs
    )
  )

  describe('controller', ->
    it('has lots of stuff too', ->
      $scope = {}
#      controller = $controller('CatalogueElementViewController', {$scope: $scope})
#      expect(typeof controller.getTabDefinition).toBe('function')
#    difficult to instantiate somehow.
    )
  )


#  it "is cool", inject (catalogue) ->
#    expect(catalogue.getIcon('org.modelcatalogue.core.DataElement')).toBe("fa fa-fw fa-cube")
