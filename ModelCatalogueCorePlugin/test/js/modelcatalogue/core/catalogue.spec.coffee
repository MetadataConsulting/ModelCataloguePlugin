describe "mc.core.catalogue", ->
  beforeEach module 'mc.core.ui.bs.catalogue'

  it "icons can be fetched by full name and by the property name as well", inject (catalogue) ->
    expect(catalogue.getIcon('org.modelcatalogue.core.DataElement')).toBe("fa fa-fw fa-cube")