describe "mc.core.ui.catalogueElementTreeviewItem", ->

  beforeEach module 'mc.core.ui.states'
  beforeEach module 'mc.core.ui.bs.catalogueElementTreeviewItem'

  it "element get compiled",  inject ($compile, $rootScope, enhance, modelCatalogueApiRoot, $httpBackend) ->
    $httpBackend.when('GET', '/api/modelCatalogue/core/dashboard').respond(fixtures.dashboard.index)


    catEl = enhance angular.copy(fixtures.dataType.showOne)
    catEl.description = "Hello World!"
    catEl.outgoingRelationships.total = 5

    $rootScope.element = catEl
    $rootScope.descend = ['outgoingRelationships']
    $rootScope.treevew =
      select: (element) -> console.log element

    element = $compile('''
      <catalogue-element-treeview-item element="element" descend="descend" treeview="treeview"></catalogue-element-treeview-item>
    ''')($rootScope)

    $rootScope.$digest()

    expect(element.prop('tagName').toLowerCase()).toBe('li')
    expect(element.find('span.catalogue-element-treeview-name').text()).toBe("#{catEl.name} #{catEl.latestVersionId}.#{catEl.versionNumber}")
    expect(element.find('span.badge').text()).toBe("#{catEl.outgoingRelationships.total}")


    relationships = angular.copy(fixtures.dataType.outgoing1)

    $httpBackend.expect('GET', catEl.outgoingRelationships.link).respond(relationships)

    element.find('a').click()

    $httpBackend.flush()

