describe "mc.core.ui.catalogueElementTreeviewItem", ->

  beforeEach module 'mc.core.ui.states'
  beforeEach module 'mc.core.ui.bs.catalogueElementTreeviewItem'

  it "element get compiled",  inject ($compile, $rootScope, enhance, modelCatalogueApiRoot, $httpBackend) ->

    catEl = enhance angular.copy(fixtures.dataType.showOne)
    catEl.description = "Hello World!"
    catEl.valueDomains.total = 5

    $rootScope.element = catEl
    $rootScope.descend = ['valueDomains']

    element = $compile('''
      <catalogue-element-treeview-item element="element" descend="descend" root-id="'treewidget'"></catalogue-element-treeview-item>
    ''')($rootScope)

    $rootScope.$digest()

    expect(element.prop('tagName').toLowerCase()).toBe('li')
    expect(element.find('span.catalogue-element-treeview-name').text()).toBe(catEl.name)
    expect(element.find('span.badge').text()).toBe("#{catEl.valueDomains.total}")


    valueDomains = angular.copy(fixtures.dataType.valueDomains1)

    $httpBackend.expect('GET', catEl.valueDomains.link).respond(valueDomains)

    element.find('a').click()

    $httpBackend.flush()

