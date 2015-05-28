describe "mc.core.ui.metadataEditors", ->

  beforeEach module 'mc.core.ui.metadataEditors'

  beforeEach module (metadataEditorsProvider) ->

    metadataEditorsProvider.register {
      title: 'any relationship'
      types: ['=>']
      keys: ['foo']
      template: '<p>...template...</p>'
    }

    metadataEditorsProvider.register {
      title: 'relationships to data types'
      types: ['=>dataElement']
      keys: ['foo']
      template: '<p>...template...</p>'
    }

    metadataEditorsProvider.register {
      title: 'relationships from models'
      types: ['model=>']
      keys: ['foo']
      template: '<p>...template...</p>'
    }

    metadataEditorsProvider.register {
      title: 'contained data elements'
      types: ['=[contains]=>dataElement']
      keys: ['foo']
      template: '<p>...template...</p>'
    }

    metadataEditorsProvider.register {
      title: 'value domain based on other value domain'
      types: ['model=[hierarchy]=>model']
      keys: ['foo']
      template: '<p>...template...</p>'
    }

    metadataEditorsProvider.register {
      title: 'value domain based on other value domain'
      types: ['model']
      keys: ['foo']
      template: '<p>...template...</p>'
    }

    return undefined

  rel = (source, type, destination) ->
    {
      elementType: 'org.modelcatalogue.core.Relationship'
      type:
        name: type
      element:
        elementType: "org.modelcatalogue.core.#{source}"
      relation:
        elementType: "org.modelcatalogue.core.#{destination}"
      ext:
        [type: 'orderedMap']
      direction: "sourceToDestination"
    }

  el = (type) ->
    {
    elementType: "org.modelcatalogue.core.#{type}"
    ext:
      [type: 'orderedMap']
    }

  it "check enabled for relationship types",  inject (metadataEditors) ->
    expect(metadataEditors.getAvailableEditors(rel 'Model', 'hierarchy', 'Model').length).toBe(3)
    expect(metadataEditors.getAvailableEditors(rel 'Model', 'contains', 'DataElement').length).toBe(4)
    expect(metadataEditors.getAvailableEditors(el 'DataElement').length).toBe(0)
    expect(metadataEditors.getAvailableEditors(el 'Model').length).toBe(1)