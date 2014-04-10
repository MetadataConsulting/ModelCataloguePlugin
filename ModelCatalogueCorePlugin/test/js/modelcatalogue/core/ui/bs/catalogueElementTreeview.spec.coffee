describe "mc.core.ui.catalogueElementTreeview", ->

  beforeEach module 'mc.core.ui.bs.catalogueElementTreeview'
  beforeEach module 'mc.core.ui.bs.catalogueElementTreeviewItem'

  it "element get compiled",  inject ($compile, $rootScope, enhance) ->

    catEl = enhance angular.copy(fixtures.valueDomain.showOne.dataType)
    catEl.description = "Hello World!"

    $rootScope.element = catEl
    $rootScope.descend = ['valueDomains']

    element = $compile('''
      <catalogue-element-treeview element="element" descend="descend"></catalogue-element-treeview>
    ''')($rootScope)

    $rootScope.$digest()

    expect(element.prop('tagName').toLowerCase()).toBe('ul')

