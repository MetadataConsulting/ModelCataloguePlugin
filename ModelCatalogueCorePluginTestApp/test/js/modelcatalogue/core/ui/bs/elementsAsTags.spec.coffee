describe "mc.core.ui.elementsAsTags", ->

  beforeEach module 'mc.core.ui'

  beforeEach module 'karmaTestingTemplates'

  return unless window.fixtures

  it "element get compiled",  inject ($compile, $rootScope) ->

    $rootScope.elements = angular.copy fixtures.dataType.list1.list


    element = $compile('''
      <elements-as-tags elements="elements" ></elements-as-tags>
    ''')($rootScope)

    $rootScope.$digest()

    expect(element.prop('tagName').toLowerCase()).toBe('div')
    expect(element.hasClass('tags')).toBeTruthy()

    expect(element.find('span.label:not(.ng-hide)').length).toBe($rootScope.elements.length)
