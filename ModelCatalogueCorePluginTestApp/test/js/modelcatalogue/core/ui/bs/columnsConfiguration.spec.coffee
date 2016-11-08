describe "mc.core.ui.bs.columnsConfiguration", ->

  beforeEach module 'mc.core.ui.bs.columnsConfiguration'

  it "element is compiled", inject ($rootScope, $compile) ->
    $rootScope.columns = [
      {header: "Name",        value: 'name',        class: 'col-md-4', show: true}
      {header: "Description", value: 'description', class: 'col-md-6'}
    ]

    element = $compile('''
    <columns-configuration columns="columns"></columns-configuration>
    ''')($rootScope)

    $rootScope.$digest()

    expect(element.prop('tagName').toLowerCase()).toBe('table')
    expect(element.find('tr').length).toBe(3)

    element.find('tr:last-child .btn-success').click()
    $rootScope.$digest()
    expect(element.find('tr').length).toBe(4)

    element.find('tr:last-child .btn-danger').click()
    $rootScope.$digest()
    expect(element.find('tr').length).toBe(3)
