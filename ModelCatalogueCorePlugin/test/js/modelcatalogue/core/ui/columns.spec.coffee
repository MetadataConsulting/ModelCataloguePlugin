describe "mc.core.ui.columns", ->

  beforeEach module 'mc.core.ui.columns'

  beforeEach module (columnsProvider) ->
    columnsProvider.registerColumns 'spell', [
      {header: "Name", value: 'name', class: 'col-md-4', show: true}
      {header: "Description", value: 'description', class: 'col-md-8', show: true}
    ]
    columnsProvider.setDefaultColumns [
      {header: "ID",          value: 'id',          class: 'col-md-2', show: true}
      {header: "Name",        value: 'name',        class: 'col-md-4', show: true}
      {header: "Description", value: 'description', class: 'col-md-6'}
    ]
    return

  it "returns copy of columns if defined", inject (columns) ->
    cols = columns('spell')

    expect(cols).toBeDefined()
    expect(cols.length).toBe(2)
    expect(cols[0]).toEqual(header: "Name", value: 'name', class: 'col-md-4', show: true)
    expect(cols[1]).toEqual(header: "Description", value: 'description', class: 'col-md-8', show: true)

    cols[1].header = "Recipe"

    newColumns = columns('spell')

    expect(newColumns[1]).toEqual(header: "Description", value: 'description', class: 'col-md-8', show: true)

  it "returns fallback columns if undefined", inject (columns) ->
    cols = columns('something')

    expect(cols).toBeDefined()
    expect(cols.length).toBe(3)
    expect(cols[0]).toEqual(header: "ID",          value: 'id',          class: 'col-md-2', show: true)
    expect(cols[1]).toEqual(header: "Name",        value: 'name',        class: 'col-md-4', show: true)
    expect(cols[2]).toEqual(header: "Description", value: 'description', class: 'col-md-6')

  it "returns user supplied fallback columns if undefined", inject (columns) ->
    cols = columns 'something', [
      {header: "TEST"}
    ]

    expect(cols).toBeDefined()
    expect(cols.length).toBe(1)
    expect(cols[0]).toEqual(header: 'TEST')

