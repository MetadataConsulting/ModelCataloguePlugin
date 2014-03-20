describe "mc.core.ui.decoratedListTable", ->

  beforeEach module 'mc.core.catalogueElementResource'
  beforeEach module 'mc.core.catalogueElementEnhancer'
  beforeEach module 'mc.core.modelCatalogueApiRoot'
  beforeEach module 'mc.core.ui.bs.decoratedListTable'

  it "element get compiled",  inject ($compile, $rootScope, $httpBackend, modelCatalogueApiRoot, catalogueElementResource) ->
    $httpBackend.when("GET", "#{modelCatalogueApiRoot}/valueDomain").respond(fixtures.valueDomain.list2)
    $httpBackend.when("GET", "#{modelCatalogueApiRoot}/valueDomain/?max=5&offset=5").respond(fixtures.valueDomain.list3)
    $httpBackend.when("GET", "#{modelCatalogueApiRoot}/valueDomain/?max=5&offset=0").respond(fixtures.valueDomain.list2)

    valueDomains = catalogueElementResource('valueDomain')

    $rootScope.muList = null

    valueDomains.list().then (response) ->
      $rootScope.muList = response

    $httpBackend.flush()

    $rootScope.columns = [
      {header: 'ID', classes: 'col-md-4', value: 'id'}
      {header: 'Name', classes: 'col-md-8', value: (element) -> element.name }
    ]

    element = $compile('''
    <decorated-list list="muList" columns="columns"></decorated-list>
    ''')($rootScope)

    $rootScope.$digest()


    # table gets dl-table class
    expect(element.prop('tagName').toLowerCase()).toBe('table')
    expect(element.hasClass('dl-table')).toBeTruthy()

    # well formed table head and body with expected classes
    expect(element.find('thead').length).toBe(1)
    expect(element.find('tbody').length).toBe(1)
    expect(element.find('tfoot').length).toBe(1)

    # appropriate rows count with expected classes
    expect(element.find('tr.dl-table-header-row').length).toBe(1)
    expect(element.find('tr.dl-table-item-row').length).toBe(5)

    # appropriate cells with expected classes and content
    expect(element.find('thead tr th.dl-table-header-cell').length).toBe(2)
    expect(element.find('thead tr th.dl-table-header-cell:nth-child(1)').text()).toBe('ID')
    expect(element.find('thead tr th.dl-table-header-cell:nth-child(2)').text()).toBe('Name')
    expect(element.find('thead tr th.dl-table-header-cell:nth-child(1)').hasClass('col-md-4')).toBeTruthy()
    expect(element.find('thead tr th.dl-table-header-cell:nth-child(2)').hasClass('col-md-8')).toBeTruthy()

    expect(element.find('tbody tr:first-child td.dl-table-item-cell').length).toBe(2)
    expect(element.find('tbody tr:first-child td.dl-table-item-cell:nth-child(1)').text()).toBe('1')
    expect(element.find('tbody tr:first-child td.dl-table-item-cell:nth-child(2)').text()).toBe('ground_speed')

    expect(element.find('tbody tr:last-child td.dl-table-item-cell').length).toBe(2)
    expect(element.find('tbody tr:last-child td.dl-table-item-cell:nth-child(1)').text()).toBe('5')
    expect(element.find('tbody tr:last-child td.dl-table-item-cell:nth-child(2)').text()).toBe('ground_speed_5')

    # next and previous links
    expect(element.find('li.dl-table-prev.disabled').length).toBe(1)
    expect(element.find('li.dl-table-next:not(.disabled)').length).toBe(1)

    expect($rootScope.muList.offset).toBe(0)

    element.find('li.dl-table-prev.disabled a').click()
    expect($rootScope.muList.offset).toBe(0)

    element.find('li.dl-table-next:not(.disabled) a').click()
    $httpBackend.flush()
    expect($rootScope.muList.offset).toBe(5)

    element.find('li.dl-table-prev:not(.disabled) a').click()
    $httpBackend.flush()
    expect($rootScope.muList.offset).toBe(0)


    # the columns are live
    $rootScope.columns.pop()
    $rootScope.$digest()

    expect(element.find('thead tr th.dl-table-header-cell').length).toBe(1)
    expect(element.find('tbody tr:first-child td.dl-table-item-cell').length).toBe(1)



    element = $compile('''
    <decorated-list list="muList"></decorated-list>
    ''')($rootScope)
    $rootScope.$digest()

    expect(element.find('thead tr th.dl-table-header-cell').length).toBe(2)
    expect(element.find('tbody tr:first-child td.dl-table-item-cell').length).toBe(2)
    expect(element.find('thead tr th.dl-table-header-cell:first-child').text()).toBe('Name')


    $rootScope.selection = []

    element = $compile('''
    <decorated-list list="muList" selection="selection"></decorated-list>
    ''')($rootScope)
    $rootScope.$digest()

    expect(element.find('thead tr th:first-child.dl-table-select-all-cell.col-md-1').length).toBe(1)
    expect(element.find('tbody tr:first-child td:first-child.dl-table-select-item-cell').length).toBe(1)

    element.find('tbody tr:first-child td:first-child.dl-table-select-item-cell input').click()
    $rootScope.$digest()

    expect($rootScope.selection.length).toBe(1)

    element.find('thead tr th:first-child.dl-table-select-all-cell input').click()
    $rootScope.$digest()


    expect($rootScope.selection.length).toBe(5)

    element.find('thead tr th:first-child.dl-table-select-all-cell input').click()
    $rootScope.$digest()


    expect($rootScope.selection.length).toBe(0)

    element = $compile('''
    <decorated-list list="muList" selection="selection"></decorated-list>
    ''')($rootScope)
    $rootScope.$digest()

    event = null
    $rootScope.$on 'showCatalogueElement', (_event_) -> event = _event_

    expect(event).toBeNull()

    $rootScope.$digest()
    expect(event).toBeNull()

    element.find('tbody tr:first-child').click()

    $rootScope.$digest()

    expect(event).not.toBeNull()




