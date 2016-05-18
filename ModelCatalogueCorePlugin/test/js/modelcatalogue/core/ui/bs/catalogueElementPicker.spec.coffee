describe "mc.core.ui.catalogueElementPicker", ->
  beforeEach module 'mc.core.ui.catalogueElementPicker'
  beforeEach module 'mc.core.catalogueElementEnhancer'

  return unless window.fixtures

  it "element uses global search by default",  inject ($compile, $rootScope, enhance, $httpBackend, modelCatalogueApiRoot) ->
    catEl = enhance angular.copy(fixtures.dataType.showOne)

    $rootScope.element = catEl

    element = $compile('''
        <input ng-model="element" catalogue-element-picker typeahead-wait-ms="0">
      ''')($rootScope)

    $rootScope.$digest()

    expect(element.prop('tagName')).toBe('INPUT')
    expect(element.val()).toBe('boolean')

    $httpBackend.expect('GET', "#{modelCatalogueApiRoot}/search?search=test").respond(fixtures.dataType.searchElement1)

    element.val('test')
    element.change()

    $httpBackend.flush()


  it "label can be customized",  inject ($compile, $rootScope, enhance) ->
    catEl = enhance angular.copy(fixtures.dataType.showOne)

    $rootScope.element = catEl

    element = $compile('''
        <input ng-model="element" catalogue-element-picker label="el.name" typeahead-wait-ms="0">
      ''')($rootScope)

    $rootScope.$digest()

    expect(element.prop('tagName')).toBe('INPUT')
    expect(element.val()).toBe('boolean')


  it "the resource can be specified as string",  inject ($compile, $rootScope, enhance, $httpBackend, modelCatalogueApiRoot) ->
    catEl = enhance angular.copy(fixtures.dataType.showOne)

    $rootScope.element = catEl

    element = $compile('''
          <input ng-model="element" catalogue-element-picker="dataType" typeahead-wait-ms="0">
        ''')($rootScope)

    $rootScope.$digest()

    expect(element.prop('tagName')).toBe('INPUT')
    expect(element.val()).toBe('boolean')

    $httpBackend.expect('GET', "#{modelCatalogueApiRoot}/dataType/search?search=test").respond(fixtures.dataType.searchElement1)

    element.val('test')
    element.change()

    $httpBackend.flush()

  it "the resource can be specified as reference",  inject ($compile, $rootScope, enhance, $httpBackend, modelCatalogueApiRoot) ->
    catEl = enhance angular.copy(fixtures.dataType.showOne)

    $scope = $rootScope.$new(true)

    $scope.element = catEl
    $scope.resource = 'dataType'

    element = $compile('''
          <input ng-model="element" catalogue-element-picker resource="resource" typeahead-wait-ms="0">
        ''')($scope)

    $scope.$digest()

    expect(element.prop('tagName')).toBe('INPUT')
    expect(element.val()).toBe('boolean')

    $httpBackend.expect('GET', "#{modelCatalogueApiRoot}/dataType/search?search=test").respond(fixtures.dataType.searchElement1)

    element.val('test')
    element.change()

    $httpBackend.flush()

    $scope.resource = 'model'
    $scope.$digest()

    $httpBackend.expect('GET', "#{modelCatalogueApiRoot}/model/search?search=other+test").respond(fixtures.dataClass.searchElement13)

    element.val('other test')
    element.change()

    $httpBackend.flush()
