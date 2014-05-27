describe "mc.core.ui.decoratedListTable", ->

  beforeEach module 'mc.core.ui.states'
  beforeEach module 'mc.core.catalogueElementResource'
  beforeEach module 'mc.core.catalogueElementEnhancer'
  beforeEach module 'mc.core.modelCatalogueApiRoot'
  beforeEach module 'mc.core.ui.bs.decoratedListTable'

  it "element get compiled",  inject ($compile, $rootScope, $httpBackend, modelCatalogueApiRoot, catalogueElementResource) ->
    $httpBackend.when("GET", new RegExp("#{modelCatalogueApiRoot}/valueDomain/\\d")).respond(fixtures.valueDomain.showOne)
    $httpBackend.when("GET", "#{modelCatalogueApiRoot}/valueDomain").respond(fixtures.valueDomain.list2)
    $httpBackend.when("GET", "#{modelCatalogueApiRoot}/valueDomain").respond(fixtures.valueDomain.list2)
    $httpBackend.when("GET", "#{modelCatalogueApiRoot}/valueDomain/?max=5&offset=5").respond(fixtures.valueDomain.list3)
    $httpBackend.when("GET", "#{modelCatalogueApiRoot}/valueDomain/?max=5&offset=0").respond(fixtures.valueDomain.list2)

    valueDomains = catalogueElementResource('valueDomain')

    $rootScope.muList = null

    valueDomains.list().then (response) ->
      $rootScope.muList = response

    $httpBackend.flush()

    $rootScope.columns = [
      {header: 'ID', classes: 'col-md-4', value: 'id', show: 'show()'}
      {header: 'Name', classes: 'col-md-8', show: 'show()', value: (element) -> element.name }
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
    expect(element.find('thead tr th.dl-table-header-cell:nth-child(1)').text().trim()).toBe('ID')
    expect(element.find('thead tr th.dl-table-header-cell:nth-child(2)').text().trim()).toBe('Name')
    expect(element.find('thead tr th.dl-table-header-cell:nth-child(1)').hasClass('col-md-4')).toBeTruthy()
    expect(element.find('thead tr th.dl-table-header-cell:nth-child(2)').hasClass('col-md-8')).toBeTruthy()

    expect(element.find('tbody tr:first-child td.dl-table-item-cell').length).toBe(2)
    expect(element.find('tbody tr:first-child td.dl-table-item-cell:nth-child(1)').text()).toBeDefined()
    expect(element.find('tbody tr:first-child td.dl-table-item-cell:nth-child(2)').text()).toBe(fixtures.valueDomain.list1.list[0].name)

    expect(element.find('tbody tr:last-child td.dl-table-item-cell').length).toBe(2)
    expect(element.find('tbody tr:last-child td.dl-table-item-cell:nth-child(1)').text()).toBeDefined()
    expect(element.find('tbody tr:last-child td.dl-table-item-cell:nth-child(2)').text()).toBe(fixtures.valueDomain.list1.list[4].name)

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

    expect(element.find('thead tr th.dl-table-header-cell').length).toBe(4)
    expect(element.find('tbody tr:first-child td.dl-table-item-cell').length).toBe(4)
    expect(element.find('thead tr th.dl-table-header-cell:first-child').text().trim()).toBe('ID')


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
    <decorated-list list="muList" selection="selection" columns="columns"></decorated-list>
    ''')($rootScope)
    $rootScope.$digest()

# fails because of abstract state

#    event   = null
#    state   = null
#    params  = null
#    $rootScope.$on '$stateChangeSuccess', (_event_, _state_, _params_) ->
#      event   = _event_
#      state   = _state_
#      params  = _params_
#
#
#    expect(event).toBeNull()
#
#    $rootScope.$digest()
#    expect(event).toBeNull()
#    expect(state).toBeNull()
#    expect(params).toBeNull()
#
#    link = element.find('tbody tr:first-child td a')
#
#    expect(link.length).toBe(1)
#
#    link.click()
#
#    $rootScope.$digest()
#
#    expect(event).not.toBeNull()
#    expect(state).not.toBeNull()
#    expect(state.name).toEqual('mc.resource.show')




