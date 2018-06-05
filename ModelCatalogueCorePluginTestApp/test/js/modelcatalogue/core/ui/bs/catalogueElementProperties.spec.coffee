describe "mc.core.ui.catalogueElementProperties", ->

  beforeEach module 'mc.core.ui.catalogueElementProperties'

  beforeEach module (catalogueElementPropertiesProvider) ->

    catalogueElementPropertiesProvider.configureProperty 'alwaysHidden',                          hidden: true
    catalogueElementPropertiesProvider.configureProperty 'org.modelcatalogue.core.Model.history', hidden: true
    catalogueElementPropertiesProvider.configureProperty 'org.modelcatalogue.core.Model.ext', label: 'Metadata'
    catalogueElementPropertiesProvider.configureProperty 'org.modelcatalogue.core.Model.contains', label: 'Data Elements', columns: [
      {header: 'Name',            value: "relation.name",   classes: 'col-md-2', show: "relation.show()"}
      {header: 'Identification',  value: "relation.getElementTypeName() + ': ' + relation.id", classes: 'col-md-3', show: "relation.show()"}
    ]
    # it's additive, the last value wins
    catalogueElementPropertiesProvider.configureProperty 'org.modelcatalogue.core.Model.history', label: 'Previous Versions'

    return

  it "retrieves preferences",  inject (catalogueElementProperties) ->
    hidden = catalogueElementProperties.getConfigurationFor 'org.modelcatalogue.core.Model.alwaysHidden'

    expect(hidden).toBeDefined()
    expect(hidden.hidden).toBeFunction()
    expect(hidden.hidden()).toBeTruthy()
    expect(hidden.label).toBe('Always Hidden')


    history = catalogueElementProperties.getConfigurationFor 'org.modelcatalogue.core.Model.history'

    expect(history).toBeDefined()
    expect(history.hidden).toBeFunction()
    expect(history.hidden()).toBeTruthy()
    expect(history.label).toBe('Previous Versions')

    ext = catalogueElementProperties.getConfigurationFor 'org.modelcatalogue.core.Model.ext'

    expect(ext).toBeDefined()
    expect(ext.label).toBe('Metadata')
    expect(ext.hidden).toBeFunction()
    expect(ext.hidden()).toBeFalsy()

    contains = catalogueElementProperties.getConfigurationFor 'org.modelcatalogue.core.Model.contains'

    expect(contains).toBeDefined()
    expect(contains.label).toBe('Data Elements')
    expect(contains.hidden).toBeFunction()
    expect(contains.hidden()).toBeFalsy()
    expect(contains.columns).toBeArray()

    something = catalogueElementProperties.getConfigurationFor 'org.modelcatalogue.core.Model.someThing'

    expect(something).toBeDefined()
    expect(something.label).toBe('Some Thing')
    expect(something.hidden).toBeFunction()
    expect(something.hidden()).toBeFalsy()

  it "converts names of relationship types from camel case to spaced capitalized", inject (catalogueElementProperties) ->
    isBaseFor = catalogueElementProperties.getConfigurationFor("org.modelcatalogue.core.DataModel.isBaseFor")
    expect(isBaseFor.label).toEqual('Is Base For')
