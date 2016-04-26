describe "mc.core.ui.catalogueElementTreeviewItem", ->

  beforeEach module 'mc.core.ui.states.bs'

  return unless window.fixtures

  it "element get compiled",  inject ($compile, $rootScope, enhance, modelCatalogueApiRoot, $httpBackend) ->
    catEl = enhance angular.copy(fixtures.dataType.showOne)
    catEl.description = "Hello World!"
    catEl.outgoingRelationships.total = 5

    $rootScope.element = catEl
    $rootScope.descend = ['outgoingRelationships']
    $rootScope.treeview =
      select: -> # do nothing

    element = $compile('''
      <catalogue-element-treeview-item element="element" descend="descend" treeview="treeview"></catalogue-element-treeview-item>
    ''')($rootScope)

    $rootScope.$digest()

    expect(element.prop('tagName').toLowerCase()).toBe('li')
    expect(element.find('span.catalogue-element-treeview-name').text().replace(/^\s+|\s+$/g, '').replace(/\s\s+/g, ' '))
      .toBe("#{catEl.name} None 0.0.0")
    expect(element.find('span.badge').text()).toBe("#{catEl.outgoingRelationships.total}")


    relationships = angular.copy(fixtures.dataType.outgoing1)

    $httpBackend.expect('GET', catEl.outgoingRelationships.link).respond(relationships)

    element.find('a').click()

    $httpBackend.flush()

