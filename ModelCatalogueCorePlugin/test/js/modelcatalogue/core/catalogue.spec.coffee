describe "mc.core.catalogue", ->
  beforeEach module 'mc.core.ui.bs.catalogue'

  it "icons can be fetched by full name and by the property name as well", inject (catalogue) ->
    expect(catalogue.getIcon('org.modelcatalogue.core.DataElement')).toBe("fa fa-fw fa-cube")


  it "test instance of", inject (catalogue) ->
    expect(catalogue.isInstanceOf('org.modelcatalogue.core.DataType', 'org.modelcatalogue.core.DataType')).toBeTruthy()
    expect(catalogue.isInstanceOf('org.modelcatalogue.core.DataType', 'org.modelcatalogue.core.CatalogueElement')).toBeTruthy()
    expect(catalogue.isInstanceOf('org.modelcatalogue.core.DataType', 'org.modelcatalogue.core.Model')).toBeFalsy()

    expect(catalogue.isInstanceOf('org.modelcatalogue.core.Model', 'org.modelcatalogue.core.DataType')).toBeFalsy()
    expect(catalogue.isInstanceOf('org.modelcatalogue.core.Model', 'org.modelcatalogue.core.MeasurementUnit')).toBeFalsy()
    expect(catalogue.isInstanceOf('org.modelcatalogue.core.Model', 'org.modelcatalogue.core.CatalogueElement')).toBeTruthy()
    expect(catalogue.isInstanceOf('org.modelcatalogue.core.Model', 'org.modelcatalogue.core.Model')).toBeTruthy()
    expect(catalogue.isInstanceOf('org.modelcatalogue.core.Model',
      'org.modelcatalogue.core.CatalogueElement')).toBeTruthy()


  it "test contains", inject (catalogue) ->
    expect(false).toBeTruthy()