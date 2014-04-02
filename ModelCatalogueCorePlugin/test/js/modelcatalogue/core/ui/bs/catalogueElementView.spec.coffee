describe "mc.core.ui.catalogueElementView", ->

  beforeEach module 'mc.core.ui.bs.catalogueElementView'
  beforeEach module 'mc.core.ui.bs.decoratedListTable'

  it "element get compiled",  inject ($compile, $rootScope, enhance) ->

    catEl = enhance angular.copy(fixtures.valueDomain.showOne)
    catEl.description = "Hello World!"

    $rootScope.element = catEl

    element = $compile('''
      <catalogue-element-view element="element"></catalogue-element-view>
    ''')($rootScope)

    $rootScope.$digest()


    #console.log(element.description)

    expect(element.prop('tagName').toLowerCase()).toBe('div')
    expect(element.find('h3.ce-name').text()).toBe("#{catEl.name} (#{catEl.elementTypeName}: #{catEl.id})")
    expect(element.find('blockquote.ce-description').text()).toBe(catEl.description)

    expect(element.find('ul.nav.nav-tabs li').length).toBe(6)
    expect(element.find('div.tab-pane').length).toBe(6)

    expect(element.find('.dl-table-item-row').length).toBe(0)

    $rootScope.$digest()

    expect(element.find('.dl-table-item-row').length).toBe(0)