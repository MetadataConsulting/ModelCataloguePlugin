angular.module('mc.core.ui.bs.columns', []).config ['columnsProvider', (columnsProvider)->
  nameAndDescription = -> [
    {header: "Name",        value: 'name',        classes: 'col-md-4', show: true, href: 'href()',  sort: {property: 'name', type: 'alphabet'}}
    {header: "Description", value: 'description', classes: 'col-md-6'}
  ]

  publishedElementColumns = -> [
    { header: "Model Catalogue ID", value: "modelCatalogueId", classes: "col-md-2", show: true, href: 'href()'}
    { header: "Name", value: "name", classes: "col-md-3", show: true, href: 'href()', sort: {property: 'name', type: 'alphabet'} }
    { header: "Description", value: "description" }
  ]

  getEnumerations = (enumeratedType) ->
    return enumeratedType.description if not enumeratedType.enumerations
    enumerations = []
    enumerations.push "#{key}: #{value}" for key, value of enumeratedType.enumerations
    enumerations.join('\n')

  getConceptualDomainsForValueDomain = (valueDomain) ->
    return '' if not valueDomain.conceptualDomains
    domainNames = for domain in valueDomain.conceptualDomains
      "<a href='#/catalogue/conceptualDomain/#{domain.id}'>#{domain.name}</a>"
    domainNames.join(', ')

  getClassificationsForDataElement = (dataElement) ->
    return '' if not dataElement.classifications
    classificationNames = for classification in dataElement.classifications
      "<a href='#/catalogue/classification/#{classification.id}'>#{classification.name}</a>"
    classificationNames.join(', ')

#  getConceptualDomainsForDataElement = (dataElement) ->
#    return '' unless dataElement and dataElement.valueDomain
#    return getConceptualDomainsForValueDomain(dataElement.valueDomain)

  # default
  columnsProvider.registerColumns 'org.modelcatalogue.core.ConceptualDomain', nameAndDescription()
  columnsProvider.registerColumns 'org.modelcatalogue.core.PublishedElement', publishedElementColumns()
  columnsProvider.registerColumns 'org.modelcatalogue.core.ExtendibleElement', publishedElementColumns()
  columnsProvider.registerColumns 'org.modelcatalogue.core.Model', publishedElementColumns()

  columnsProvider.registerColumns 'org.modelcatalogue.core.DataElement', [
    { header: 'Classifications',  value: getClassificationsForDataElement,  classes: 'col-md-2'}
    { header: "Model Catalogue ID", value: "modelCatalogueId", classes: "col-md-3", show: true, href: 'href()'}
    { header: "Name", value: "name", classes: "col-md-3", show: true, href: 'href()', sort: {property: 'name', type: 'alphabet'} }
    { header: "Description", value: "description" , classes: "col-md-4"}
  ]


  # special

  computeBytes = (asset) ->
    GIGA = 1024 * 1024 * 1024
    MEGA = 1024 * 1024
    KILO = 1024
    return "#{(asset.size / GIGA).toFixed(2)} GB" if asset.size > GIGA
    return "#{(asset.size / MEGA).toFixed(2)} MB" if asset.size > MEGA
    return "#{(asset.size / KILO).toFixed(2)} KB" if asset.size > KILO
    return "#{(asset.size)} B"

  columnsProvider.registerColumns 'org.modelcatalogue.core.dataarchitect.ImportRow', [
    {header: "Model Path",        value: " parentModelName + ' -> ' + containingModelName",              classes: 'col-md-2', sort: {property: 'containingModelName', type: 'alphabet'}}
    {header: "Data Element",        value: 'dataElementName',              classes: 'col-md-2', sort: {property: 'dataElementName', type: 'alphabet'}}
    {
      header: "Data Type",
      value: (row) -> if row.dataType then row.dataType.replace /\|/g , "\n"
      classes: 'col-md-2',
      sort: {property: 'dataType', type: 'alphabet'}
    }
    {
      header: "Row Actions"
      value: (row) -> if row.actions then row.actions?.join('\n\n')
      classes: 'col-md-6'
      sort: {property: 'actions', type: 'alphabet'}
    }
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.dataarchitect.DataImport', [
    {header: "Name",        value: 'name',              classes: 'col-md-4', sort: {property: 'name', type: 'alphabet'}, show: true, href: 'href()'}
    {header: "Rows Imported",    value: "imported.total",  classes: 'col-md-3', sort: {property: "imported.total", type: 'alphabet'}}
    {header: "Rows Pending",    value: 'pendingAction.total',  classes: 'col-md-3', sort: {property: 'pendingAction.total', type: 'alphabet'}}
    {header: "Rows Queue",    value: 'importQueue.total',  classes: 'col-md-3', sort: {property: 'importQueue.total', type: 'alphabet'}}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.Asset', [
    {header: "Name",        value: 'name',              classes: 'col-md-4', sort: {property: 'name', type: 'alphabet'}, show: true, href: 'href()'}
    {header: "File Name",   value: 'originalFileName',  classes: 'col-md-4', sort: {property: 'originalFileName', type: 'alphabet'}}
    {header: "Size",        value: computeBytes,        classes: 'col-md-2', sort: {property: 'size', type: 'order'}}
    {header: "Mime Type",   value: 'contentType',       classes: 'col-md-2', sort: {property: 'contentType', type: 'alphabet'}}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.Mapping', [
    {header: 'Destination',         value: "destination.name",                 classes: 'col-md-4', show: 'destination.show()', href: 'destination.href()'}
    {header: 'Mapping',             value: 'mapping',                          classes: 'col-md-6 preserve-all'}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.MeasurementUnit', [
    {header: "Symbol",      value: 'symbol',      classes: 'col-md-2', show: true, href: 'href()', sort: {property: 'symbol', type: 'alphabet'}}
    {header: "Name",        value: 'name',        classes: 'col-md-4', show: true, href: 'href()', sort: {property: 'name', type: 'alphabet'}}
    {header: "Description", value: 'description', classes: 'col-md-6'}
  ]

  printMetadata = (relationship) ->
    result  = ''
    ext     = relationship.ext ? {}
    for key, value of ext
      result += "#{key}: #{value ? ''}\n"
    result


  columnsProvider.registerColumns 'org.modelcatalogue.core.Relationship', [
    {header: 'Relation',        value: 'type[direction]',  classes: 'col-md-3'}
    {header: 'Destination',     value: "relation.classifiedName",    classes: 'col-md-3', show: "relation.show()", href: 'relation.href()'}
    {header: 'Type',        value:  "relation.getElementTypeName()",     classes: 'col-md-2'}
    {header: 'Metadata',        value:  printMetadata,     classes: 'col-md-3'}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.RelationshipType', [
    {header: 'Name', value: 'name', classes: 'col-md-2', show: true, href: 'href()', sort: {property: 'name', type: 'alphabet'}}
    {header: 'Source to Destination', value: 'sourceToDestination', classes: 'col-md-2', sort: {property: 'sourceToDestination', type: 'alphabet'}}
    {header: 'Destination to Source', value: 'destinationToSource', classes: 'col-md-2', sort: {property: 'destinationToSource', type: 'alphabet'}}
    {header: 'Source Class', value: 'sourceClass', classes: 'col-md-3', sort: {property: 'sourceClass', type: 'alphabet'}}
    {header: 'Destination Class', value: 'destinationClass', classes: 'col-md-3', sort: {property: 'destinationClass', type: 'alphabet'}}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.ValueDomain', [
    {header: 'Conceptual Domains',  value: getConceptualDomainsForValueDomain,    classes: 'col-md-3'}
    {header: 'Name',                value: 'name',                                classes: 'col-md-3', href: 'href()',                show: true, href: 'href()', sort: {property: 'name', type: 'alphabet'}}
    {header: 'Unit',                value: 'unitOfMeasure.name',                  classes: 'col-md-3', href: 'unitOfMeasure.href()',  show: 'unitOfMeasure.show()'}
    {header: 'Data Type',           value: 'dataType.name',                       classes: 'col-md-3', href: 'dataType.href()',       show: 'dataType.show()'}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.EnumeratedType', [
    {header: "Name",        value: 'name',        classes: 'col-md-4', show: true, href: 'href()', sort: {property: 'name', type: 'alphabet'}}
    {header: "Enumerations or Description", value: getEnumerations, classes: 'col-md-6'}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.DataType', [
    {header: "Name",        value: 'name',        classes: 'col-md-4', show: true, href: 'href()', sort: {property: 'name', type: 'alphabet'}}
    {header: "Enumerations or Description", value: getEnumerations, classes: 'col-md-6'}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.actions.Batch', [
    {header: "Last Updated", value: "lastUpdated | date:'short'"   , classes: 'col-md-2',               sort: {property: 'lastUpdated', type: 'order'}}
    {header: "Name"        , value: 'name'                         , classes: 'col-md-4', show: true, href: 'href()' , sort: {property: 'name', type: 'alphabet'}}
    {header: "Pending"     , value: "pending.total"                , classes: 'col-md-1'}
    {header: "Running"     , value: "performing.total"             , classes: 'col-md-1'}
    {header: "Performed"   , value: "performed.total"              , classes: 'col-md-1'}
    {header: "Failed"      , value: "failed.total"                 , classes: 'col-md-1'}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.actions.Action', [
    {header: "Created"     , value: "dateCreated | date:'short'"   , classes: 'col-md-2', sort: {property: 'dateCreated', type: 'order'}}
    {header: "Message"     , value: 'message'                      , classes: 'col-md-7' }
  ]


]