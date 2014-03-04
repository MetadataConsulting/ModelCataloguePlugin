describe "mc.core.ui.resourceTable", ->

  beforeEach module 'mc.core.catalogueElementResource'
  beforeEach module 'mc.core.modelCatalogueApiRoot'
  beforeEach module 'mc.core.ui.resourceTable'

  it "element get compiled",  inject ($compile, $rootScope, $httpBackend, modelCatalogueApiRoot) ->
    $httpBackend.when("GET", "#{modelCatalogueApiRoot}/measurementUnit").respond(fixtures.measurementUnit.list2)

    $rootScope.columns      =
      ID:     'id'
      Name:   'name'
      Symbol: 'symbol'


    element = $compile('''
    <table resource-table="measurement{{'Unit'}}" columns="columns"></table>
    ''')($rootScope)

    $rootScope.$digest()
    $httpBackend.flush()


    # table gets resource-table class
    expect(element.hasClass('resource-table')).toBeTruthy()

    # well formed table head and body with expected classes
    expect(element.find('thead').length).toBe(1)
    expect(element.find('tbody').length).toBe(1)

    # appropriate rows count with expected classes
    expect(element.find('tr.resource-table-header-row').length).toBe(1)
    expect(element.find('tr.resource-table-item-row').length).toBe(5)

    # appropriate cells with expected classes and content
    expect(element.find('thead tr th.resource-table-header-cell').length).toBe(3)
    expect(element.find('thead tr th.resource-table-header-cell:nth-child(1)').text()).toBe('ID')
    expect(element.find('thead tr th.resource-table-header-cell:nth-child(2)').text()).toBe('Name')
    expect(element.find('thead tr th.resource-table-header-cell:nth-child(3)').text()).toBe('Symbol')

    expect(element.find('tbody tr:first-child td.resource-table-item-cell').length).toBe(3)
    expect(element.find('tbody tr:first-child td.resource-table-item-cell:nth-child(1)').text()).toBe('1')
    expect(element.find('tbody tr:first-child td.resource-table-item-cell:nth-child(2)').text()).toBe('Degrees of Celsius')
    expect(element.find('tbody tr:first-child td.resource-table-item-cell:nth-child(3)').text()).toBe('°C')

    expect(element.find('tbody tr:last-child td.resource-table-item-cell').length).toBe(3)
    expect(element.find('tbody tr:last-child td.resource-table-item-cell:nth-child(1)').text()).toBe('5')
    expect(element.find('tbody tr:last-child td.resource-table-item-cell:nth-child(2)').text()).toBe('Measurement Unit 5')
    expect(element.find('tbody tr:last-child td.resource-table-item-cell:nth-child(3)').text()).toBe('MU5')

    # the columns are live
    delete $rootScope.columns.ID
    $rootScope.$digest()
    expect(element.find('thead tr th.resource-table-header-cell').length).toBe(2)
    expect(element.find('tbody tr:first-child td.resource-table-item-cell').length).toBe(2)
