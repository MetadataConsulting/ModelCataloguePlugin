describe "mc.core.ui.catalogueElementTreeview", ->

  beforeEach module 'mc.core.ui.bs.catalogueElementTreeview'
  beforeEach module 'mc.core.ui.bs.catalogueElementTreeviewItem'

  it "treeview with single element",  inject ($compile, $rootScope, enhance) ->

    catEl = enhance angular.copy(fixtures.dataType.showOne.dataType)

    $rootScope.element = catEl
    $rootScope.descend = ['dataTypes']

    element = $compile('''
      <catalogue-element-treeview element="element" descend="descend" id="tree-widget"></catalogue-element-treeview>
    ''')($rootScope)

    $rootScope.$digest()

    expect(element.find(':first-child').prop('tagName').toLowerCase()).toBe('ul')
    expect(element.prop('id').toLowerCase()).toBe('tree-widget')


  it "treeview with list",  inject ($compile, $rootScope, enhance) ->

    elements = enhance angular.copy(fixtures.dataType.list1)

    $rootScope.list = elements
    $rootScope.descend = ['relationships']

    element = $compile('''
      <catalogue-element-treeview list="list" descend="descend"></catalogue-element-treeview>
    ''')($rootScope)

    $rootScope.$digest()

    expect(element.find(':first-child').prop('tagName').toLowerCase()).toBe('ul')

    console.log element.find('li')

    expect(element.find('li').length).toBe(2)
