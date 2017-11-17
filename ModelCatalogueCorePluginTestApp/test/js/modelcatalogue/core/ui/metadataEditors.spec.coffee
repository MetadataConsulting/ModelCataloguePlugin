describe "modelcatalogue.core.sections.metadataEditors", ->

  beforeEach module 'modelcatalogue.core.sections.metadataEditors'

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
      title: 'model based on other model'
      types: ['model=[hierarchy]=>model']
      keys: ['foo']
      template: '<p>...template...</p>'
    }

    metadataEditorsProvider.register {
      title: 'model based on other model'
      types: ['model']
      keys: ['foo']
      template: '<p>...template...</p>'
    }

    return undefined

  relationship = (source, type, destination) ->
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

  element = (type) ->
    {
    elementType: "org.modelcatalogue.core.#{type}"
    ext:
      [type: 'orderedMap']
    }

  it "check enabled for relationship types",  inject (metadataEditors) ->
    expect(metadataEditors.getAvailableEditors(relationship 'Model', 'hierarchy', 'Model').length).toBe(5) # 3 from the test + 2 from metadataEditors.conf.coffee
    expect(metadataEditors.getAvailableEditors(relationship 'Model', 'contains', 'DataElement').length).toBe(4)
    expect(metadataEditors.getAvailableEditors(element 'DataElement').length).toBe(0)
    expect(metadataEditors.getAvailableEditors(element 'Model').length).toBe(1)
