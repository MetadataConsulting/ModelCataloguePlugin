angular.module('mc.core.ui.bs.columns', ['mc.util.names']).config ['columnsProvider', (columnsProvider)->
  getEnumerations = (enumeratedType) ->
    return '' if not enumeratedType
    return """
      <a href="#/catalogue/dataClass/#{enumeratedType.dataClass.id}">
        <span class="fa fa-fw fa-cubes"></span> #{enumeratedType.dataClass.name}
      </a>
    """ if enumeratedType.dataClass
    return """
      <a href="#/catalogue/measurementUnit/#{enumeratedType.measurementUnit.id}">
        <span class="fa fa-fw fa-dashboard"></span> #{enumeratedType.measurementUnit.name}
      </a>
    """ if enumeratedType.measurementUnit
    return enumeratedType.description if not enumeratedType.enumerations
    return enumeratedType.description if not enumeratedType.enumerations.values
    enumerations = []
    for enumeration, i in enumeratedType.enumerations.values
      if i == 10 and enumeratedType.enumerations.values.length isnt 10
        enumerations.push(columnsProvider.LONG_TEXT_BREAK)
      enumerations.push "#{enumeration.key}: #{enumeration.value}"
    enumerations.join('\n')

  getStatusClass = (status) ->
    return 'label-warning'  if status == 'DRAFT'
    return 'label-info'     if status == 'PENDING'
    return 'label-danger'   if status == 'DEPRECATED'
    return 'label-primary'

  getDataModelAndDataElement = (dataElement) ->
    result = ''

    if dataElement.dataModel
      result +=
      """
        <span>
          <a class="label #{getStatusClass(dataElement.getDataModelStatus())}" href='#{dataElement.dataModel.href()}'>
            <span class="#{dataElement.dataModel.getIcon()}"></span> #{dataElement.dataModel.name}
          </a>
        </span>
      """

    result +=
    """
        <a class="small text-muted text-nowrap" href="#{dataElement.href()}">#{dataElement.getVersionAndId()}</a>
      """

  getNameIdAndVersion = (dataElement) ->
    "
      <a href='#{dataElement.href()}'>#{dataElement.name}</a>
      <small class='text-muted'>#{dataElement.getExternalId()}</small>
    "

  # column definitions
  idNameAndDescriptionColumns = -> [
    {header: "Model Catalogue ID", value: "modelCatalogueId", classes: "col-md-2", show: true, href: 'href()'}
    {
      header: "Name"
      value: "name"
      classes: "col-md-3"
      show: true
      href: 'href()'
      sort: {property: 'name', type: 'alpha'}
    }
    {header: "Description", value: "description", textEllipsis: true}
  ]

  modelIdNameAndDescriptionColumns = -> [
    {
      header: "Name"
      value: getNameIdAndVersion
      classes: "col-md-4"
      show: true
      sort: {property: 'name', type: 'alpha'}
    }
    {header: "Description", value: "description", classes: "col-md-6", textEllipsis: true}
  ]

  dataTypeColumns = [
    {
      header: "Name"
      value: getNameIdAndVersion
      classes: 'col-md-6'
      show: true
      sort: {property: 'name', type: 'alpha'}
    }
    {header: "Enumerations or Description", value: getEnumerations, classes: 'col-md-6', textEllipsis: true}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.Model', idNameAndDescriptionColumns()
  columnsProvider.registerColumns 'org.modelcatalogue.core.DataClass', modelIdNameAndDescriptionColumns()
  columnsProvider.registerColumns 'org.modelcatalogue.core.DataElement', modelIdNameAndDescriptionColumns()
  columnsProvider.registerColumns 'org.modelcatalogue.core.ValidationRule', modelIdNameAndDescriptionColumns()
  columnsProvider.registerColumns 'org.modelcatalogue.core.Tags', modelIdNameAndDescriptionColumns()

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
    {
      header: "Last Updated"
      value: "lastUpdated | date:'short'"
      classes: 'col-md-2'
      sort: {property: 'lastUpdated', type: 'numeric'}
    }
    {
      header: "Name"
      value: 'name'
      classes: 'col-md-3'
      sort: {property: 'name', type: 'alpha'}
      show: true
      href: 'href()'
    }
    {
      header: "File Name"
      value: 'originalFileName'
      classes: 'col-md-3'
      sort: {property: 'originalFileName', type: 'alpha'}
    }
    {header: "Size", value: computeBytes, classes: 'col-md-2', sort: {property: 'size', type: 'numeric'}}
    {header: "Mime Type", value: 'contentType', classes: 'col-md-2', sort: {property: 'contentType', type: 'alpha'}}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.Mapping', [
    {
      header: 'Destination'
      value: "destination.name"
      classes: 'col-md-4'
      show: 'destination.show()'
      href: 'destination.href()'
    }
    {header: 'Mapping', value: 'mapping', classes: 'col-md-6 preserve-all'}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.MeasurementUnit', [
    {
      header: "Symbol"
      value: 'symbol'
      classes: 'col-md-2'
      show: true
      href: 'href()'
      sort: {property: 'symbol', type: 'alpha'}
    }
    {
      header: "Name"
      value: 'name'
      classes: 'col-md-4'
      show: true
      href: 'href()'
      sort: {property: 'name', type: 'alpha'}
    }
    {header: "Description", value: 'description', classes: 'col-md-6', textEllipsis: true}
  ]

  printMetadata = (relationship) ->
    result = ''
    ext = relationship?.ext ? {values: []}
    ext.values = ext.values ? []
    for row in ext.values
      result += "#{row.key}: #{row.value ? ''}\n"
    result

  relationTypeName = (relationship) ->
    return '' unless relationship
    return '' unless relationship.relation
    return '' unless angular.isFunction(relationship.relation.getElementTypeName)
    relationship.relation.getElementTypeName()


  columnsProvider.registerColumns 'org.modelcatalogue.core.Relationship', [
    {header: 'Relation', value: 'type[direction]', classes: 'col-md-3'}
    {
      header: 'Destination'
      value: "relation.classifiedName"
      classes: 'col-md-3'
      show: "relation.show()"
      href: 'relation.href()'
    }
    {header: 'Type', value: relationTypeName, classes: 'col-md-2'}
    {header: 'Metadata', value: printMetadata, classes: 'col-md-3'}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.RelationshipType', [
    {
      header: 'Name'
      value: 'name'
      classes: 'col-md-2'
      show: true
      href: 'href()'
      sort: {property: 'name', type: 'alpha'}
    }
    {
      header: 'Source to Destination'
      value: 'sourceToDestination'
      classes: 'col-md-2'
      sort: {property: 'sourceToDestination', type: 'alpha'}
    }
    {
      header: 'Destination to Source'
      value: 'destinationToSource'
      classes: 'col-md-2'
      sort: {property: 'destinationToSource', type: 'alpha'}
    }
    {header: 'Source Class', value: 'sourceClass', classes: 'col-md-3', sort: {property: 'sourceClass', type: 'alpha'}}
    {
      header: 'Destination Class'
      value: 'destinationClass'
      classes: 'col-md-3'
      sort: {property: 'destinationClass', type: 'alpha'}
    }
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.DataType', dataTypeColumns
  columnsProvider.registerColumns 'org.modelcatalogue.core.EnumeratedType', dataTypeColumns
  columnsProvider.registerColumns 'org.modelcatalogue.core.ReferenceType', dataTypeColumns
  columnsProvider.registerColumns 'org.modelcatalogue.core.PrimitiveType', dataTypeColumns

  columnsProvider.registerColumns 'org.modelcatalogue.core.actions.Batch', [
    {
      header: "Last Updated"
      value: "lastUpdated | date:'short'"
      classes: 'col-md-2'
      sort: {property: 'lastUpdated', type: 'numeric'}
    }
    {
      header: "Name"
      value: 'name'
      classes: 'col-md-4'
      show: true
      href: 'href()'
      sort: {property: 'name', type: 'alpha'}
    }
    {header: "Pending", value: "pending.total", classes: 'col-md-1'}
    {header: "Running", value: "performing.total", classes: 'col-md-1'}
    {header: "Performed", value: "performed.total", classes: 'col-md-1'}
    {header: "Failed", value: "failed.total", classes: 'col-md-1'}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.actions.Action', [
    {
      header: "Created"
      value: "dateCreated | date:'short'"
      classes: 'col-md-2'
      sort: {property: 'dateCreated', type: 'numeric'}
    }
    {header: "Message", value: 'message', classes: 'col-md-7'}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.DataModelPolicy', [
    {
      header: "Name"
      value: 'name'
      classes: 'col-md-6'
      show: true
      href: 'href()'
      sort: {property: 'name', type: 'alpha'}
    }
    {header: "Policy Text", value: 'policyText', classes: 'col-md-6'}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.util.builder.ProgressMonitor', [
    {header: "Name", value: 'name', classes: 'col-md-11', href: '"#/catalogue/feedback/" + key'}
  ]

  columnsProvider.registerColumns 'org.modelcatalogue.core.security.User', [
    {header: 'Username', value: "username", classes: 'col-md-3', show: 'show()', href: 'href()'}
    {header: 'Email', value: 'email', classes: 'col-md-3'}
    {header: 'Role', value: 'role', classes: 'col-md-3'}
    {header: 'Enabled', value: 'enabled', classes: 'col-md-3'}
  ]

]
