angular.module('mc.core.ui.bs.catalogueElementProperties', []).config ['catalogueElementPropertiesProvider', (catalogueElementPropertiesProvider)->

  nameAndIdent = -> [
    {header: 'Name',            value: "relation.name",                                 classes: 'col-md-6', show: "relation.show()"}
    {header: 'Identification',  value: "relation.elementTypeName + ': ' + relation.id", classes: 'col-md-5', show: "relation.show()"}
  ]

  computeBytes = (relationship) ->
    asset = relationship.relation
    GIGA = 1024 * 1024 * 1024
    MEGA = 1024 * 1024
    KILO = 1024
    return "#{(asset.size / GIGA).toFixed(2)} GB" if asset.size > GIGA
    return "#{(asset.size / MEGA).toFixed(2)} MB" if asset.size > MEGA
    return "#{(asset.size / KILO).toFixed(2)} KB" if asset.size > KILO
    return "#{(asset.size)} B"

  attachmentColumns = -> [
    {header: "Name",        value: 'relation.name',              class: 'col-md-4', sort: {property: 'name', type: 'alphabet'}, show: true}
    {header: "File Name",   value: 'relation.originalFileName',  class: 'col-md-4', sort: {property: 'originalFileName', type: 'alphabet'}}
    {header: "Size",        value: computeBytes,        class: 'col-md-2', sort: {property: 'size', type: 'order'}}
    {header: "Mime Type",   value: 'relation.contentType',       class: 'col-md-2', sort: {property: 'contentType', type: 'alphabet'}}
  ]



  # global settings
  catalogueElementPropertiesProvider.configureProperty 'ext',             label: 'Metadata'
  catalogueElementPropertiesProvider.configureProperty 'parentOf',        label: 'Children',            columns: nameAndIdent()
  catalogueElementPropertiesProvider.configureProperty 'childOf',         label: 'Parent',              columns: nameAndIdent()
  catalogueElementPropertiesProvider.configureProperty 'isContextFor',    label: 'Models',              columns: nameAndIdent()
  catalogueElementPropertiesProvider.configureProperty 'includes',        label: 'Data Types',          columns: nameAndIdent()
  catalogueElementPropertiesProvider.configureProperty 'instantiatedBy',  label: 'Data Type',           columns: nameAndIdent()
  catalogueElementPropertiesProvider.configureProperty 'contains',        label: 'Data Elements',       columns: nameAndIdent()
  catalogueElementPropertiesProvider.configureProperty 'containedIn',     label: 'Models',              columns: nameAndIdent()
  catalogueElementPropertiesProvider.configureProperty 'hasAttachmentOf', label: 'Attachments',         columns: attachmentColumns()
  catalogueElementPropertiesProvider.configureProperty 'hasContextOf',    label: 'Conceptual Domains',  columns: nameAndIdent()

  catalogueElementPropertiesProvider.configureProperty 'history', {
    hidden: (security) ->
      !security.hasRole('CURATOR')
    columns: [
      {header: "Version", value: 'versionNumber', class: 'col-md-1', show: true}
      {header: "Name", value: 'name', class: 'col-md-5', show: true}
      {header: "Description", value: 'description', class: 'col-md-6'}
    ]
  }
  catalogueElementPropertiesProvider.configureProperty 'relationships',   hidden: (security) -> !security.hasRole('CURATOR')
  catalogueElementPropertiesProvider.configureProperty 'valueDomains',    hidden: (security) -> !security.hasRole('CURATOR')
  catalogueElementPropertiesProvider.configureProperty 'supersededBy',    hidden: true
  catalogueElementPropertiesProvider.configureProperty 'supersedes',      hidden: true


]