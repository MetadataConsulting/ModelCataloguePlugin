describe "mc.core.catalogue", ->
  beforeEach module 'mc.core.ui.bs.catalogue'

  it "icons can be fetched by full name and by the property name as well", inject (catalogue) ->
    expect(catalogue.getIcon('org.modelcatalogue.core.DataElement')).toBe("fa fa-fw fa-cube")


  it "test instance of", inject (catalogue) ->
    expect(catalogue.isInstanceOf('org.modelcatalogue.core.ValueDomain', 'org.modelcatalogue.core.ValueDomain')).toBeTruthy()
    expect(catalogue.isInstanceOf('org.modelcatalogue.core.ValueDomain', 'org.modelcatalogue.core.CatalogueElement')).toBeTruthy()
    expect(catalogue.isInstanceOf('org.modelcatalogue.core.ValueDomain', 'org.modelcatalogue.core.Model')).toBeFalsy()

    expect(catalogue.isInstanceOf('org.modelcatalogue.core.Model', 'org.modelcatalogue.core.ValueDomain')).toBeFalsy()
    expect(catalogue.isInstanceOf('org.modelcatalogue.core.Model', 'org.modelcatalogue.core.MeasurementUnit')).toBeFalsy()
    expect(catalogue.isInstanceOf('org.modelcatalogue.core.Model', 'org.modelcatalogue.core.CatalogueElement')).toBeTruthy()
    expect(catalogue.isInstanceOf('org.modelcatalogue.core.Model', 'org.modelcatalogue.core.Model')).toBeTruthy()
    expect(catalogue.isInstanceOf('org.modelcatalogue.core.Model', 'org.modelcatalogue.core.ExtendibleElement')).toBeTruthy()
    expect(catalogue.isInstanceOf('org.modelcatalogue.core.Model', 'org.modelcatalogue.core.PublishedElement')).toBeTruthy()