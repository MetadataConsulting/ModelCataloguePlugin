describe "modelcatalogue.core.components.catalogueElementTreeview.item", ->

  beforeEach module 'mc.core.ui.states.bs'

  beforeEach module 'sly'

  return unless window.fixtures

  it "element get compiled",  inject ($compile, $rootScope, enhance, modelCatalogueApiRoot, $httpBackend) ->
    catEl = enhance angular.copy(fixtures.dataType.showOne)
    catEl.description = "Hello World!"
    catEl.outgoingRelationships.total = 5

    $rootScope.element = catEl
    $rootScope.treeview =
      select: -> # do nothing
      getNodeId: (link) -> link
      getDescend: -> 'outgoingRelationships'

    element = $compile('''
      <catalogue-element-treeview-item element="element" treeview="treeview"></catalogue-element-treeview-item>
    ''')($rootScope)

    $rootScope.$digest()

    expect(element.prop('tagName').toLowerCase()).toBe('li')
    expect(element.find('span.catalogue-element-treeview-name').text().replace(/^\s+|\s+$/g, '').replace(/\s\s+/g, ' '))
      .toBe(catEl.name)
    # No longer test for total since we've removed it for now
    # expect(element.find('span.badge').text().replace(/^\s+|\s+$/g, '').replace(/\s\s+/g, ' ')).toBe("#{catEl.outgoingRelationships.total}")


    relationships = angular.copy(fixtures.dataType.outgoing1)

    $httpBackend.expect('GET', catEl.outgoingRelationships.link).respond(relationships)

    element.find('a').click()

    $httpBackend.flush()

