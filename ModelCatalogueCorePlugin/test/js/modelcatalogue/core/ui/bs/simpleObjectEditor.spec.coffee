describe "mc.core.ui.simpleObjectEditor", ->

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
    <simple-object-editor object="object" hints="hints" title="Metadata"></simple-object-editor>
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
    expect(element.find('th.soe-table-property-key').length).toBe(6)
    expect(element.find('td.soe-table-property-value').length).toBe(5)

    # appropriate rows and cells count with expected classes
    expect(element.find('th.soe-table-property-key input.form-control').length).toBe(5)
    expect(element.find('td.soe-table-property-value input.form-control').length).toBe(5)

    thirdKeyInput = element.find('tr:nth-child(3) th.soe-table-property-key input.form-control')
    thirdValueInput = element.find('tr:nth-child(3) td.soe-table-property-value input.form-control')

    expect(thirdKeyInput.val()).toBe('one')
    expect(thirdValueInput.val()).toBe('first')

    thirdValueInput.val('1st')
    thirdValueInput.change()

    expect($rootScope.object.one).toBe('1st')

    thirdKeyInput.val('key')
    thirdKeyInput.change()


    expect($rootScope.object.one).toBeUndefined()
    expect($rootScope.object.key).toBe('1st')

    $rootScope.object = {test: 'hi'}

    $rootScope.$digest()

    expect(element.find('tr.soe-table-property-row').length).toBe(3)