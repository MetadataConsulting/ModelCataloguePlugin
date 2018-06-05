describe "mc.core.ui.catalogueElementTreeview", ->

  beforeEach module 'mc.core.ui'
  beforeEach module 'sly'

  beforeEach module 'karmaTestingTemplates'

  return unless window.fixtures

  it "treeview with single element",  inject ($compile, $rootScope, enhance) ->

    catEl = enhance angular.copy(fixtures.dataType.showOne.dataType)

    $rootScope.element = catEl

    element = $compile('''
      <catalogue-element-treeview element="element" descend="dataTypes" id="tree-widget"></catalogue-element-treeview>
    ''')($rootScope)

    $rootScope.$digest()

    expect(element.find(':first-child').prop('tagName').toLowerCase()).toBe('ul')
    expect(element.prop('id').toLowerCase()).toBe('tree-widget')


  it "treeview with list",  inject ($compile, $rootScope, enhance) ->

    elements = enhance angular.copy(fixtures.dataType.list1)

    $rootScope.list = elements

    element = $compile('''
      <catalogue-element-treeview list="list" descend="relationships"></catalogue-element-treeview>
    ''')($rootScope)

    $rootScope.$digest()

    expect(element.find(':first-child').prop('tagName').toLowerCase()).toBe('ul')
    expect(element.find('li').length).toBe(3)
