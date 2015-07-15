describe "mc.core.ui.catalogueElementPicker", ->
  beforeEach module 'mc.core.ui.catalogueElementPicker'
  beforeEach module 'mc.core.catalogueElementEnhancer'

  it "element uses global search by default",  inject ($compile, $rootScope, enhance, $httpBackend, modelCatalogueApiRoot) ->
    catEl = enhance angular.copy(fixtures.valueDomain.showOne)

    $rootScope.element = catEl

    element = $compile('''
        <input ng-model="element" catalogue-element-picker typeahead-wait-ms="0">
      ''')($rootScope)

    $rootScope.$digest()

    expect(element.prop('tagName')).toBe('INPUT')
    expect(element.val()).toBe('school subject')

    $httpBackend.expect('GET', "#{modelCatalogueApiRoot}/search?search=test").respond(fixtures.valueDomain.searchElement15)

    element.val('test')
    element.change()

    $httpBackend.flush()


  it "label can be customized",  inject ($compile, $rootScope, enhance) ->
    catEl = enhance angular.copy(fixtures.valueDomain.showOne)

    $rootScope.element = catEl

    element = $compile('''
        <input ng-model="element" catalogue-element-picker label="el.name" typeahead-wait-ms="0">
      ''')($rootScope)

    $rootScope.$digest()

    expect(element.prop('tagName')).toBe('INPUT')
    expect(element.val()).toBe('school subject')


  it "the resource can be specified as string",  inject ($compile, $rootScope, enhance, $httpBackend, modelCatalogueApiRoot) ->
    catEl = enhance angular.copy(fixtures.valueDomain.showOne)

    $rootScope.element = catEl

    element = $compile('''
          <input ng-model="element" catalogue-element-picker="valueDomain" typeahead-wait-ms="0">
        ''')($rootScope)

    $rootScope.$digest()

    expect(element.prop('tagName')).toBe('INPUT')
    expect(element.val()).toBe('school subject')

    $httpBackend.expect('GET', "#{modelCatalogueApiRoot}/valueDomain/search?search=test").respond(fixtures.valueDomain.searchElement15)

    element.val('test')
    element.change()

    $httpBackend.flush()

  it "the resource can be specified as reference",  inject ($compile, $rootScope, enhance, $httpBackend, modelCatalogueApiRoot) ->
    catEl = enhance angular.copy(fixtures.valueDomain.showOne)

    $scope = $rootScope.$new(true)

    $scope.element = catEl
    $scope.resource = 'valueDomain'

    element = $compile('''
          <input ng-model="element" catalogue-element-picker resource="resource" typeahead-wait-ms="0">
        ''')($scope)

    $scope.$digest()

    expect(element.prop('tagName')).toBe('INPUT')
    expect(element.val()).toBe('school subject')

    $httpBackend.expect('GET', "#{modelCatalogueApiRoot}/valueDomain/search?search=test").respond(fixtures.valueDomain.searchElement15)

    element.val('test')
    element.change()

    $httpBackend.flush()

    $scope.resource = 'model'
    $scope.$digest()

    $httpBackend.expect('GET', "#{modelCatalogueApiRoot}/model/search?search=other+test").respond(fixtures.dataClass.searchElement13)

    element.val('other test')
    element.change()

    $httpBackend.flush()