describe "mc.core.ui.propertiesPane", ->

  beforeEach module 'mc.core.ui.bs.simpleObjectEditor'

  it "element get compiled",  inject ($compile, $rootScope) ->

    $rootScope.object =
      one:    "first"
      two:    "second"
      three:  "third"
      func:   ->
      nested: { prop: "val" }

    $rootScope.hints = [
      'x'
      'y'
    ]


    element = $compile('''
    <simple-object-editor object="object" hints="hints"></simple-object-editor>
    ''')($rootScope)

    $rootScope.$digest()


    # table gets dl-table class
    expect(element.prop('tagName').toLowerCase()).toBe('table')
    expect(element.hasClass('soe-table')).toBeTruthy()

    # having only table body
    expect(element.find('thead').length).toBe(1)
    expect(element.find('tbody').length).toBe(1)
    expect(element.find('tfoot').length).toBe(0)

    # appropriate rows and cells count with expected classes
    expect(element.find('tr.soe-table-property-row').length).toBe(5)
    expect(element.find('th.soe-table-property-key').length).toBe(5)
    expect(element.find('td.soe-table-property-value').length).toBe(5)

    # appropriate rows and cells count with expected classes
    expect(element.find('th.soe-table-property-key input.form-control').length).toBe(5)
    expect(element.find('td.soe-table-property-value input.form-control').length).toBe(5)

    firstKeyInput = element.find('tr:first-child th.soe-table-property-key input.form-control')
    firstValueInput = element.find('tr:first-child td.soe-table-property-value input.form-control')

    expect(firstKeyInput.val()).toBe('one')
    expect(firstValueInput.val()).toBe('first')

    firstValueInput.val('1st')
    firstValueInput.change()

    expect($rootScope.object.one).toBe('1st')

    firstKeyInput.val('key')
    firstKeyInput.change()


    expect($rootScope.object.one).toBeUndefined()
    expect($rootScope.object.key).toBe('1st')

    $rootScope.object = {test: 'hi'}

    $rootScope.$digest()

    expect(element.find('tr.soe-table-property-row').length).toBe(3)


#    # appropriate cells with expected classes and content
#    expect(element.find('tbody tr:nth-child(1) th.pp-table-property-label').text()).toBe('ID')
#    expect(element.find('tbody tr:nth-child(2) th.pp-table-property-label').text()).toBe('Name')
#    expect(element.find('tbody tr:nth-child(3) th.pp-table-property-label').text()).toBe('Description')
#    expect(element.find('tbody tr:nth-child(4) th.pp-table-property-label').text()).toBe('Data Type')
#
#    expect(element.find('tbody tr:nth-child(1) td.pp-table-property-value').text()).toBe("#{$rootScope.element.id}")
#    expect(element.find('tbody tr:nth-child(2) td.pp-table-property-value').text()).toBe("#{$rootScope.element.name}")
#    expect(element.find('tbody tr:nth-child(3) td.pp-table-property-value').text()).toBe("#{$rootScope.element.description}")
#    expect(element.find('tbody tr:nth-child(4) td.pp-table-property-value').text()).toBe("#{$rootScope.element.dataType.name}")
#
#    shown = null
#
#    $httpBackend.expect('GET', /\/api\/modelCatalogue\/core\/dataType\/\d+/).respond({ok: true})
#
#    $rootScope.$on '$stateChangeSuccess', (ignored, ignored2, params) ->
#      shown = params
#
#    expect(shown).toBeNull()
#
#    link = element.find('tbody tr:nth-child(4) td.pp-table-property-element-value a')
#
#    expect(link.length).toBe(1)
#
#    link.click()
#
#    $rootScope.$digest()
#    $httpBackend.flush()
#
#    expect(shown).not.toBeNull()









