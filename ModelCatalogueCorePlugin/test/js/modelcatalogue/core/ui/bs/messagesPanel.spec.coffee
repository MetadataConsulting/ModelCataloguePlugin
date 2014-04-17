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


  it "element uses global search by default",  inject ($compile, $rootScope, enhance, $httpBackend, modelCatalogueApiRoot) ->
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