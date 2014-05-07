describe "mc.core.ui.catalogueElementTreeview", ->

  beforeEach module 'mc.core.ui.bs.catalogueElementTreeview'
  beforeEach module 'mc.core.ui.bs.catalogueElementTreeviewItem'

  it "treeview with single element",  inject ($compile, $rootScope, enhance) ->

    catEl = enhance angular.copy(fixtures.valueDomain.showOne.dataType)

    $rootScope.element = catEl
    $rootScope.descend = ['valueDomains']

    element = $compile('''
      <catalogue-element-treeview element="element" descend="descend" id="tree-widget"></catalogue-element-treeview>
    ''')($rootScope)

    $rootScope.$digest()

    expect(element.find(':first-child').prop('tagName').toLowerCase()).toBe('ul')
    expect(element.prop('id').toLowerCase()).toBe('tree-widget')


  it "treeview with list",  inject ($compile, $rootScope, enhance) ->

    elements = enhance angular.copy(fixtures.valueDomain.list1)

    $rootScope.list = elements
    $rootScope.descend = ['relationships']

    element = $compile('''
      <catalogue-element-treeview list="list" descend="descend"></catalogue-element-treeview>
    ''')($rootScope)

    $rootScope.$digest()

    expect(element.find(':first-child').prop('tagName').toLowerCase()).toBe('ul')
    expect(element.find('li').length).toBe(11) # one per item plus
