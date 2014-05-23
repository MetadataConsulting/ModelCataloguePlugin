angular.module('mc.core.ui.bs.columns', []).config ['columnsProvider', (columnsProvider)->
  nameAndDescription = -> [
    {header: "Name",        value: 'name',        class: 'col-md-4', show: true}
    {header: "Description", value: 'description', class: 'col-md-6'}
  ]

  publishedElementColumns = -> [
    { header: "Model Catalogue ID", value: "modelCatalogueId", classes: "col-md-2", show: true }
    { header: "Name", value: "name", classes: "col-md-3", show: true }
    { header: "Description", value: "description" }
  ]

  # default
  columnsProvider.registerColumns 'org.modelcatalogue.core.ConceptualDomain', nameAndDescription()
  columnsProvider.registerColumns 'org.modelcatalogue.core.DataType', nameAndDescription()
  columnsProvider.registerColumns 'org.modelcatalogue.core.EnumeratedType', nameAndDescription()

  columnsProvider.registerColumns 'org.modelcatalogue.core.PublishedElement', publishedElementColumns()
  columnsProvider.registerColumns 'org.modelcatalogue.core.ExtendibleElement', publishedElementColumns()
  columnsProvider.registerColumns 'org.modelcatalogue.core.Model', publishedElementColumns()
  columnsProvider.registerColumns 'org.modelcatalogue.core.DataElement', publishedElementColumns()

  # special

  columnsProvider.registerColumns 'org.modelcatalogue.core.Mapping', [
    {header: 'Destination',     value: "destination.name",                                    classes: 'col-md-4', show: 'destination.show()'}
    {header: 'Mapping',         value: 'mapping',                                             classes: 'col-md-5'}
    {header: 'Identification',  value: "destination.elementTypeName + ': ' + destination.id", classes: 'col-md-3', show: 'destination.show()'}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.MeasurementUnit', [
    {header: "Symbol",      value: 'symbol',      class: 'col-md-1', show: true}
    {header: "Name",        value: 'name',        class: 'col-md-4', show: true}
    {header: "Description", value: 'description', class: 'col-md-6'}
  ]

  printMetadata = (relationship) ->
    result  = ''
    ext     = relationship.ext ? {}
    for key, value of ext
      result += "#{key}: #{value ? ''}\n"
    result


  columnsProvider.registerColumns 'org.modelcatalogue.core.Relationship', [
    {header: 'Relation',        value: 'type[direction]',                               classes: 'col-md-3'}
    {header: 'Destination',     value: "relation.name",                                 classes: 'col-md-3', show: "relation.show()"}
    {header: 'Metadata',        value: printMetadata,                                   classes: 'col-md-5'}
    #{header: 'Identification',  value: "relation.elementTypeName + ': ' + relation.id", classes: 'col-md-3', show: "relation.show()"}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.RelationshipType', [
    {header: 'Name', value: 'name', classes: 'col-md-2'}
    {header: 'Source to Destination', value: 'sourceToDestination', classes: 'col-md-2'}
    {header: 'Destination to Source', value: 'destinationToSource', classes: 'col-md-2'}
    {header: 'Source Class', value: 'sourceClass', classes: 'col-md-3'}
    {header: 'Destination Class', value: 'destinationClass', classes: 'col-md-3'}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.ValueDomain', [
    {header: 'Name',        value: 'name',          classes: 'col-md-4', show: true}
    {header: 'Unit',        value: 'unitOfMeasure', classes: 'col-md-4', show: 'unitOfMeasure.show()'}
    {header: 'Data Type',   value: 'dataType.name', classes: 'col-md-4', show: 'dataType.show()'}
  ]


]