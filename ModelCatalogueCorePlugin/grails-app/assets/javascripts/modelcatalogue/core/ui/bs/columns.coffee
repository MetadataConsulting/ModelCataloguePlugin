angular.module('mc.core.ui.bs.columns', []).config ['columnsProvider', (columnsProvider)->


  idNameAndDescription = -> [
    { header: "Model Catalogue ID", value: "modelCatalogueId", classes: "col-md-2", show: true, href: 'modelCatalogueId'}
    { header: "Name", value: "name", classes: "col-md-3", show: true, href: 'href()', sort: {property: 'name', type: 'alpha'} }
    { header: "Description", value: "description" }
  ]

  getEnumerations = (enumeratedType) ->
    return '' if not enumeratedType
    return enumeratedType.description if not enumeratedType.enumerations
    return enumeratedType.description if not enumeratedType.enumerations.values
    enumerations = []
    enumerations.push "#{enumeration.key}: #{enumeration.value}" for enumeration in enumeratedType.enumerations.values
    enumerations.join('\n')

  getStatusClass = (status) ->
    return 'label-warning'  if status == 'DRAFT'
    return 'label-info'     if status == 'PENDING'
    return 'label-danger'   if status == 'DEPRECATED'
    return 'label-primary'

  getClassificationsForDataElement = (dataElement) ->
    return '' if not dataElement?.classifications

    classificationNames = for classification in dataElement.classifications
      """<a class="label #{getStatusClass(classification.status)}"  href='#/catalogue/classification/#{classification.id}'><span class="#{classification.getIcon()}"></span> #{classification.name}</a>"""
    classificationNames.join(' ')

  # default
  columnsProvider.registerColumns 'org.modelcatalogue.core.Model', idNameAndDescription()

  columnsProvider.registerColumns 'org.modelcatalogue.core.DataElement', [
    { header: 'Classifications',  value: getClassificationsForDataElement,  classes: 'col-md-2'}
    { header: "Model Catalogue ID", value: "modelCatalogueId", classes: "col-md-3", show: true, href: 'modelCatalogueId'}
    { header: "Name", value: "name", classes: "col-md-3", show: true, href: 'href()', sort: {property: 'name', type: 'alpha'} }
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

  columnsProvider.registerColumns 'org.modelcatalogue.core.Asset', [
    {header: "Last Updated",value: "lastUpdated | date:'short'",  classes: 'col-md-2', sort: {property: 'lastUpdated', type: 'numeric'}}
    {header: "Name",        value: 'name',                        classes: 'col-md-3', sort: {property: 'name', type: 'alpha'}, show: true, href: 'href()'}
    {header: "File Name",   value: 'originalFileName',            classes: 'col-md-3', sort: {property: 'originalFileName', type: 'alpha'}}
    {header: "Size",        value: computeBytes,                  classes: 'col-md-2', sort: {property: 'size', type: 'numeric'}}
    {header: "Mime Type",   value: 'contentType',                 classes: 'col-md-2', sort: {property: 'contentType', type: 'alpha'}}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.Mapping', [
    {header: 'Destination',         value: "destination.name",                 classes: 'col-md-4', show: 'destination.show()', href: 'destination.href()'}
    {header: 'Mapping',             value: 'mapping',                          classes: 'col-md-6 preserve-all'}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.MeasurementUnit', [
    {header: "Symbol",      value: 'symbol',      classes: 'col-md-2', show: true, href: 'href()', sort: {property: 'symbol', type: 'alpha'}}
    {header: "Name",        value: 'name',        classes: 'col-md-4', show: true, href: 'href()', sort: {property: 'name', type: 'alpha'}}
    {header: "Description", value: 'description', classes: 'col-md-6'}
  ]

  printMetadata = (relationship) ->
    result  = ''
    ext     = relationship.ext ? {}
    for key, value of ext
      result += "#{key}: #{value ? ''}\n"
    result

  relationTypeName = (relationship) ->
    return '' unless relationship
    return '' unless relationship.relation
    return '' unless angular.isFunction(relationship.relation.getElementTypeName)
    relationship.relation.getElementTypeName()



  columnsProvider.registerColumns 'org.modelcatalogue.core.Relationship', [
    {header: 'Relation',        value: 'type[direction]',  classes: 'col-md-3'}
    {header: 'Destination',     value: "relation.classifiedName",    classes: 'col-md-3', show: "relation.show()", href: 'relation.href()'}
    {header: 'Type',            value: relationTypeName,     classes: 'col-md-2'}
    {header: 'Metadata',        value: printMetadata,     classes: 'col-md-3'}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.RelationshipType', [
    {header: 'Name', value: 'name', classes: 'col-md-2', show: true, href: 'href()', sort: {property: 'name', type: 'alpha'}}
    {header: 'Source to Destination', value: 'sourceToDestination', classes: 'col-md-2', sort: {property: 'sourceToDestination', type: 'alpha'}}
    {header: 'Destination to Source', value: 'destinationToSource', classes: 'col-md-2', sort: {property: 'destinationToSource', type: 'alpha'}}
    {header: 'Source Class', value: 'sourceClass', classes: 'col-md-3', sort: {property: 'sourceClass', type: 'alpha'}}
    {header: 'Destination Class', value: 'destinationClass', classes: 'col-md-3', sort: {property: 'destinationClass', type: 'alpha'}}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.ValueDomain', [
    {header: 'Classifications', value: getClassificationsForDataElement, classes: 'col-md-3'}
    {header: 'Name',                value: 'name',                                classes: 'col-md-3', href: 'href()',                show: true, href: 'href()', sort: {property: 'name', type: 'alpha'}}
    {header: 'Unit',                value: 'unitOfMeasure.name',                  classes: 'col-md-3', href: 'unitOfMeasure.href()',  show: 'unitOfMeasure.show()'}
    {header: 'Data Type',           value: 'dataType.name',                       classes: 'col-md-3', href: 'dataType.href()',       show: 'dataType.show()'}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.EnumeratedType', [
    {header: "Name",                        value: 'name',          classes: 'col-md-6', show: true, href: 'href()', sort: {property: 'name', type: 'alpha'}}
    {header: "Enumerations or Description", value: getEnumerations, classes: 'col-md-6'}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.DataType', [
    {header: "Name",                        value: 'name',          classes: 'col-md-6', show: true, href: 'href()', sort: {property: 'name', type: 'alpha'}}
    {header: "Enumerations or Description", value: getEnumerations, classes: 'col-md-6'}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.actions.Batch', [
    {header: "Last Updated", value: "lastUpdated | date:'short'"   , classes: 'col-md-2',                              sort: {property: 'lastUpdated', type: 'numeric'}}
    {header: "Name"        , value: 'name'                         , classes: 'col-md-4', show: true, href: 'href()' , sort: {property: 'name', type: 'alpha'}}
    {header: "Pending"     , value: "pending.total"                , classes: 'col-md-1'}
    {header: "Running"     , value: "performing.total"             , classes: 'col-md-1'}
    {header: "Performed"   , value: "performed.total"              , classes: 'col-md-1'}
    {header: "Failed"      , value: "failed.total"                 , classes: 'col-md-1'}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.actions.Action', [
    {header: "Created"     , value: "dateCreated | date:'short'"   , classes: 'col-md-2', sort: {property: 'dateCreated', type: 'numeric'}}
    {header: "Message"     , value: 'message'                      , classes: 'col-md-7' }
  ]

]