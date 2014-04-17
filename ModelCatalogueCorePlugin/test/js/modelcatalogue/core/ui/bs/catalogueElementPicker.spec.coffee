describe "mc.core.ui.catalogueElementPicker", ->
  beforeEach module 'mc.core.ui.catalogueElementPicker'

  it "element uses global search by default",  inject ($compile, $rootScope, enhance, $httpBackend, modelCatalogueApiRoot) ->
    catEl = enhance angular.copy(fixtures.valueDomain.showOne)

    $rootScope.element = catEl

    element = $compile('''
        <input ng-model="element" catalogue-element-picker>
      ''')($rootScope)

    $rootScope.$digest()

    expect(element.prop('tagName')).toBe('INPUT')
    expect(element.val()).toBe('value domain Celsius (Value Domain: ' + fixtures.valueDomain.showOne.id + ')')

    $httpBackend.expect('GET', "#{modelCatalogueApiRoot}/search?search=test").respond(fixtures.valueDomain.searchElement15)

    element.val('test')
    element.change()

    $httpBackend.flush()


  it "element type can be hidden",  inject ($compile, $rootScope, enhance) ->
    catEl = enhance angular.copy(fixtures.valueDomain.showOne)

    $rootScope.element = catEl

    element = $compile('''
        <input ng-model="element" catalogue-element-picker hide-element-type="true">
      ''')($rootScope)

    $rootScope.$digest()

    expect(element.prop('tagName')).toBe('INPUT')
    expect(element.val()).toBe('value domain Celsius')


  it "the resource can be specified as string",  inject ($compile, $rootScope, enhance, $httpBackend, modelCatalogueApiRoot) ->
    catEl = enhance angular.copy(fixtures.valueDomain.showOne)

    $rootScope.element = catEl

    element = $compile('''
          <input ng-model="element" catalogue-element-picker="valueDomain">
        ''')($rootScope)

    $rootScope.$digest()

    expect(element.prop('tagName')).toBe('INPUT')
    expect(element.val()).toBe('value domain Celsius (Value Domain: ' + fixtures.valueDomain.showOne.id + ')')

    $httpBackend.expect('GET', "#{modelCatalogueApiRoot}/valueDomain/search?search=test").respond(fixtures.valueDomain.searchElement15)

    element.val('test')
    element.change()

    $httpBackend.flush()

  it "the resource can be specified as reference",  inject ($compile, $rootScope, enhance, $httpBackend, modelCatalogueApiRoot) ->
    catEl = enhance angular.copy(fixtures.valueDomain.showOne)

    $rootScope.element = catEl
    $rootScope.resource = 'valueDomain'

    element = $compile('''
          <input ng-model="element" catalogue-element-picker resource="resource">
        ''')($rootScope)

    $rootScope.$digest()

    expect(element.prop('tagName')).toBe('INPUT')
    expect(element.val()).toBe('value domain Celsius (Value Domain: ' + fixtures.valueDomain.showOne.id + ')')

    $httpBackend.expect('GET', "#{modelCatalogueApiRoot}/valueDomain/search?search=test").respond(fixtures.valueDomain.searchElement15)

    element.val('test')
    element.change()

    $httpBackend.flush()

    $rootScope.resource = 'model'
    $rootScope.$digest()

    $httpBackend.expect('GET', "#{modelCatalogueApiRoot}/model/search?search=other+test").respond(fixtures.model.searchElement13)

    element.val('other test')
    element.change()

    $httpBackend.flush()