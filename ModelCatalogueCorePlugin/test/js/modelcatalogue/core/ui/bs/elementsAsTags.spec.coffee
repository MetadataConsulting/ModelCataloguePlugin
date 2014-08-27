describe "mc.core.ui.elementsAsTags", ->

  beforeEach module 'mc.core.ui.bs.elementsAsTags'

  it "element get compiled",  inject ($compile, $rootScope) ->

    $rootScope.elements = angular.copy fixtures.model.list1.list


    element = $compile('''
      <elements-as-tags elements="elements" ></elements-as-tags>
    ''')($rootScope)

    $rootScope.$digest()

    # table gets dl-table class
    expect(element.prop('tagName').toLowerCase()).toBe('div')
    expect(element.hasClass('tags')).toBeTruthy()

    # having only table body
    expect(element.find('span.label').length).toBe($rootScope.elements.length)
