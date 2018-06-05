describe "mc.core.ui.propertiesPane", ->

  beforeEach module 'mc.core.ui.states.bs'
  beforeEach module 'mc.core.ui.states'
  beforeEach module 'mc.core.modelCatalogueApiRoot'
  beforeEach module 'mc.core.catalogueElementEnhancer'

  beforeEach module 'karmaTestingTemplates'

  return unless window.fixtures

  it "element get compiled",  inject ($compile, $rootScope, enhance) ->

    $rootScope.paneProperties = [
      {label: 'ID', value: 'id'}
      {label: 'Name', value: (element) -> element.name }
      {label: 'Description', value: (element) -> element.description }
      {label: 'Data Type', value: 'dataType' }
    ]

    $rootScope.element = enhance angular.copy(fixtures.dataType.showOne)
    $rootScope.element.description = "Hello World!"

    element = $compile('''
    <properties-pane item="element" properties="paneProperties"></properties-pane>
    ''')($rootScope)

    $rootScope.$digest()


    # table gets dl-table class
    expect(element.prop('tagName').toLowerCase()).toBe('table')
    expect(element.hasClass('pp-table')).toBeTruthy()

    # having only table body
    expect(element.find('thead').length).toBe(1)
    expect(element.find('tbody').length).toBe(1)
    expect(element.find('tfoot').length).toBe(0)

    # appropriate rows and cells count with expected classes
    expect(element.find('tr.pp-table-property-row').length).toBe(4)
    expect(element.find('th.pp-table-property-label').length).toBe(4)
    expect(element.find('td.pp-table-property-value').length).toBe(4)

    # appropriate cells with expected classes and content
    expect(element.find('tbody tr:nth-child(1) th.pp-table-property-label').text()).toBe('ID')
    expect(element.find('tbody tr:nth-child(2) th.pp-table-property-label').text()).toBe('Name')
    expect(element.find('tbody tr:nth-child(3) th.pp-table-property-label').text()).toBe('Description')
    expect(element.find('tbody tr:nth-child(4) th.pp-table-property-label').text()).toBe('Data Type')

    expect(element.find('tbody tr:nth-child(1) td.pp-table-property-value').text()).toBe("#{$rootScope.element.id}")
    expect(element.find('tbody tr:nth-child(2) td.pp-table-property-value').text()).toBe("#{$rootScope.element.name}")
    expect(element.find('tbody tr:nth-child(3) td.pp-table-property-value').text()).toBe("#{$rootScope.element.description}")








