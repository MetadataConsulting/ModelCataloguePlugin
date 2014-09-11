describe "mc.core.ui.dataImportView", ->

  beforeEach module 'mc.core.ui.states'
  beforeEach module 'mc.core.ui.bs.importView'
  beforeEach module 'mc.core.ui.bs.decoratedListTable'
  beforeEach module 'mc.core.ui.bs.propertiesPane'
  beforeEach module 'mc.core.ui.bs.simpleObjectEditor'

  xit "element get compiled",  inject ($compile, $rootScope, enhance,  $httpBackend) ->
    $httpBackend.when('GET', /.*/).respond({ok: true})

    catEl = enhance angular.copy(fixtures.Importer.list1)
    catEl.description = "Hello World!"

    $rootScope.element = catEl

    element = $compile('''
      <import-view element="element"></import-view>
    ''')($rootScope)

    $rootScope.$digest()

    expect(element.prop('tagName').toLowerCase()).toBe('div')
    expect(element.find('h3.ce-name').text()).toBe("#{catEl.name} (#{catEl.getElementTypeName()}: #{catEl.id})")
    expect(element.find('blockquote.ce-description').text()).toBe(catEl.description)

    expect(element.find('ul.nav.nav-tabs li').length).toBe(4)
    expect(element.find('div.tab-pane').length).toBe(4)

    expect(element.find('.dl-table-item-row').length).toBe(0)

    $rootScope.$digest()

    expect(element.find('.dl-table-item-row').length).toBe(0)

