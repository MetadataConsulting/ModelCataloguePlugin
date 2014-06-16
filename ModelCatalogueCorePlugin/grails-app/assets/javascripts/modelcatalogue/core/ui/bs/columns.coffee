angular.module('mc.core.ui.bs.columns', []).config ['columnsProvider', (columnsProvider)->
  nameAndDescription = -> [
    {header: "Name",        value: 'name',        class: 'col-md-4', show: true, sort: {property: 'name', type: 'alphabet'}}
    {header: "Description", value: 'description', class: 'col-md-6'}
  ]

  publishedElementColumns = -> [
    { header: "Model Catalogue ID", value: "modelCatalogueId", classes: "col-md-2", show: true }
    { header: "Name", value: "name", classes: "col-md-3", show: true, sort: {property: 'name', type: 'alphabet'} }
    { header: "Description", value: "description" }
  ]

  # default
  columnsProvider.registerColumns 'org.modelcatalogue.core.ConceptualDomain', nameAndDescription()
  columnsProvider.registerColumns 'org.modelcatalogue.core.DataType', nameAndDescription()
  columnsProvider.registerColumns 'org.modelcatalogue.core.PublishedElement', publishedElementColumns()
  columnsProvider.registerColumns 'org.modelcatalogue.core.ExtendibleElement', publishedElementColumns()
  columnsProvider.registerColumns 'org.modelcatalogue.core.Model', publishedElementColumns()
  columnsProvider.registerColumns 'org.modelcatalogue.core.DataElement', publishedElementColumns()

  # special

  computeBytes = (asset) ->
    GIGA = 1024 * 1024 * 1024
    MEGA = 1024 * 1024
    KILO = 1024
    return "#{(asset.size / GIGA).toFixed(2)} GB" if asset.size > GIGA
    return "#{(asset.size / MEGA).toFixed(2)} MB" if asset.size > MEGA
    return "#{(asset.size / KILO).toFixed(2)} KB" if asset.size > KILO
    return "#{(asset.size)} B"

  columnsProvider.registerColumns 'org.modelcatalogue.core.Asset', [
    {header: "Name",        value: 'name',              class: 'col-md-4', sort: {property: 'name', type: 'alphabet'}, show: true}
    {header: "File Name",   value: 'originalFileName',  class: 'col-md-4', sort: {property: 'originalFileName', type: 'alphabet'}}
    {header: "Size",        value: computeBytes,        class: 'col-md-2', sort: {property: 'size', type: 'order'}}
    {header: "Mime Type",   value: 'contentType',       class: 'col-md-2', sort: {property: 'contentType', type: 'alphabet'}}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.Mapping', [
    {header: 'Destination',     value: "destination.name",                                    classes: 'col-md-4', show: 'destination.show()', sort: {property: 'destination.name', type: 'alphabet'}}
    {header: 'Mapping',         value: 'mapping',                                             classes: 'col-md-5'}
    {header: 'Identification',  value: "destination.elementTypeName + ': ' + destination.id", classes: 'col-md-3', show: 'destination.show()'}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.MeasurementUnit', [
    {header: "Symbol",      value: 'symbol',      class: 'col-md-1', show: true, sort: {property: 'symbol', type: 'alphabet'}}
    {header: "Name",        value: 'name',        class: 'col-md-4', show: true, sort: {property: 'name', type: 'alphabet'}}
    {header: "Description", value: 'description', class: 'col-md-6'}
  ]

  printMetadata = (relationship) ->
    result  = ''
    ext     = relationship.ext ? {}
    for key, value of ext
      result += "#{key}: #{value ? ''}\n"
    result


  columnsProvider.registerColumns 'org.modelcatalogue.core.Relationship', [
    {header: 'Relation',        value: 'type[direction]',  classes: 'col-md-3'}
    {header: 'Destination',     value: "relation.name",    classes: 'col-md-3', show: "relation.show()"}
    {header: 'Metadata',        value:  printMetadata,     classes: 'col-md-5'}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.RelationshipType', [
    {header: 'Name', value: 'name', classes: 'col-md-2', sort: {property: 'name', type: 'alphabet'}}
    {header: 'Source to Destination', value: 'sourceToDestination', classes: 'col-md-2', sort: {property: 'sourceToDestination', type: 'alphabet'}}
    {header: 'Destination to Source', value: 'destinationToSource', classes: 'col-md-2', sort: {property: 'destinationToSource', type: 'alphabet'}}
    {header: 'Source Class', value: 'sourceClass', classes: 'col-md-3', sort: {property: 'sourceClass', type: 'alphabet'}}
    {header: 'Destination Class', value: 'destinationClass', classes: 'col-md-3', sort: {property: 'destinationClass', type: 'alphabet'}}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.ValueDomain', [
    {header: 'Name',        value: 'name',          classes: 'col-md-4', show: true, sort: {property: 'name', type: 'alphabet'}}
    {header: 'Unit',        value: 'unitOfMeasure.name', classes: 'col-md-4', show: 'unitOfMeasure.show()', sort: {property: 'unitOfMeasure.name', type: 'alphabet'}}
    {header: 'Data Type',   value: 'dataType.name', classes: 'col-md-4', show: 'dataType.show()', sort: {property: 'dataType.name', type: 'alphabet'}}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.EnumeratedType', [
    {header: "Name",        value: 'name',        class: 'col-md-4', show: true, sort: {property: 'name', type: 'alphabet'}}
    {header: "Enumerations", value: 'enumerations', class: 'col-md-6'}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.dataarchitect.Importer', [
    {header: "conceptualDomainName",      value: 'conceptualDomainName',      class: 'col-md-1' }
    {header: "Name",        value: 'name',        class: 'col-md-4', show: true, sort: {property: 'name', type: 'alphabet'}}
    {header: "Description", value: 'description', class: 'col-md-6'}
  ]

]