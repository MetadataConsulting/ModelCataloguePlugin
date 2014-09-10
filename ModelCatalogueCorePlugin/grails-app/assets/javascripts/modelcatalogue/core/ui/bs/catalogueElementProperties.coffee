angular.module('mc.core.ui.bs.catalogueElementProperties', []).config ['catalogueElementPropertiesProvider', (catalogueElementPropertiesProvider)->

  nameAndIdent = -> [
    {header: 'Name',            value: "relation.name",                                 classes: 'col-md-5', show: "relation.show()"}
    {header: 'Identification',  value: "relation.getElementTypeName() + ': ' + relation.id", classes: 'col-md-5', show: "relation.show()"}
  ]

  nameAndIdAndMetadata = -> [
    {header: 'Name',            value: "relation.name",                                 classes: 'col-md-3', show: "relation.show()"}
    {header: 'Identification',  value: "relation.modelCatalogueId", classes: 'col-md-3', show: "relation.show()"}
    {header: 'Metadata',  value: printMetadata, classes: 'col-md-4'}
  ]

  publishedElement = ->  [
    { header: "Model Catalogue ID", value: "modelCatalogueId", classes: "col-md-3", show: true }
    { header: "Name", value: "name", classes: "col-md-3", show: true, sort: {property: 'name', type: 'alphabet'} }
    { header: "Description", value: "description" , classes: "col-md-4"}
    { header: "Value Domain", value: "valueDomain.name", classes: "col-md-3", show: true}
  ]



  printMetadata = (relationship) ->
    result  = ''
    ext     = relationship.ext ? {}
    for key, value of ext
      result += "#{key}: #{value ? ''}\n"
    result

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
    {header: "Name",        value: 'relation.name',              class: 'col-md-4', sort: {property: 'name', type: 'alphabet'}, show: 'relation.show()'}
    {header: "File Name",   value: 'relation.originalFileName',  class: 'col-md-4', sort: {property: 'originalFileName', type: 'alphabet'}}
    {header: "Size",        value: computeBytes,                 class: 'col-md-2', sort: {property: 'size', type: 'order'}}
    {header: "Mime Type",   value: 'relation.contentType',       class: 'col-md-2', sort: {property: 'contentType', type: 'alphabet'}}
  ]



  # global settings
  catalogueElementPropertiesProvider.configureProperty 'ext',             label: 'Metadata'
  catalogueElementPropertiesProvider.configureProperty 'parentOf',        label: 'Children',            columns: nameAndIdent()
  catalogueElementPropertiesProvider.configureProperty 'childOf',         label: 'Parent',              columns: nameAndIdent()
  catalogueElementPropertiesProvider.configureProperty 'isContextFor',    label: 'Models',              columns: nameAndIdent()
  catalogueElementPropertiesProvider.configureProperty 'includes',        label: 'Value Domains',          columns: nameAndIdent()
  catalogueElementPropertiesProvider.configureProperty 'instantiatedBy',  label: 'Value Domains',           columns: nameAndIdAndMetadata()
  catalogueElementPropertiesProvider.configureProperty 'contains',        label: 'Data Elements',       columns: nameAndIdAndMetadata()
  catalogueElementPropertiesProvider.configureProperty 'containedIn',     label: 'Models',              columns: nameAndIdAndMetadata()
  catalogueElementPropertiesProvider.configureProperty 'hasAttachmentOf', label: 'Attachments',         columns: attachmentColumns()
  catalogueElementPropertiesProvider.configureProperty 'hasContextOf',    label: 'Conceptual Domains',  columns: nameAndIdent()
  catalogueElementPropertiesProvider.configureProperty 'classifies',    label: 'Classifies',  columns: publishedElement()

  catalogueElementPropertiesProvider.configureProperty 'conceptualDomains',      label: 'Conceptual Domains',  columns: nameAndIdent()
  catalogueElementPropertiesProvider.configureProperty 'instantiates',    label: 'Data Elements'     ,  columns: nameAndIdAndMetadata()

  catalogueElementPropertiesProvider.configureProperty 'history', {
    hidden: (security) ->
      !security.hasRole('CURATOR')
    columns: [
      {header: "Version", value: 'versionNumber', class: 'col-md-1', show: true}
      {header: "Name", value: 'name', class: 'col-md-5', show: true}
      {header: "Description", value: 'description', class: 'col-md-6'}
    ]
  }

  catalogueElementPropertiesProvider.configureProperty 'org.modelcatalogue.core.Asset.history', {
    hidden: (security) ->
      !security.hasRole('CURATOR')
    columns: [
      {header: "Version",   value: 'versionNumber',     class: 'col-md-1', show: true}
      {header: "Name",      value: 'name',              class: 'col-md-4', show: true}
      {header: "File Name", value: 'originalFileName',  class: 'col-md-4', show: true}
      {header: "Size",      class: 'col-md-3', value: (it) -> computeBytes({relation: it})}
    ]
    actions: ['security', '$window', (security, $window) -> [
      {
        title:      'Download'
        icon:       'download'
        type:       'primary'
        action:     (element) ->
          $window.open element.downloadUrl, '_blank'; return true

      }
    ]]
  }

  catalogueElementPropertiesProvider.configureProperty 'org.modelcatalogue.core.actions.Batch.pending', {
    actions: ['$http', '$state', ($http, $state) -> [
      {
        title:      'Perform'
        icon:       'play'
        type:       'success'
        action:     (action) ->
          action.run().then ->
            $state.go '.', {property: 'performed', sort: 'lastUpdated', order: 'desc'}, {reload: true}
      }
      {
        title:      'Dismiss'
        icon:       'pause'
        type:       'danger'
        action:     (action) ->
          action.dismiss().then ->
            $state.go '.', {property: 'dismissed', sort: 'lastUpdated', order: 'desc'}, {reload: true}
      }
    ]]
  }


  catalogueElementPropertiesProvider.configureProperty 'org.modelcatalogue.core.actions.Batch.failed', {
    columns: [
      {header: "Created"     , value: "dateCreated | date:'short'"           , class: 'col-md-2', sort: {property: 'dateCreated', type: 'order'}}
      {header: "Message"     , value: 'message + "\n\nOutput:\n" + outcome'  , class: 'col-md-7' }
    ]
    actions: ['$http', '$state', ($http, $state) -> [
      {
        title:      'Queue Again'
        icon:       'play'
        type:       'success'
        action:     (action) ->
          action.reactivate().then ->
            $state.go '.', {property: 'pending', sort: 'lastUpdated', order: 'desc'}, {reload: true}
      }
    ]]
  }

  catalogueElementPropertiesProvider.configureProperty 'org.modelcatalogue.core.actions.Batch.performed', {
    columns: [
      {header: "Created"     , value: "dateCreated | date:'short'"          , class: 'col-md-2', sort: {property: 'dateCreated', type: 'order'}}
      {header: "Message"     , value: 'message + "\n\nOutput:\n" + outcome' , class: 'col-md-7' }
    ]
  }

  catalogueElementPropertiesProvider.configureProperty 'org.modelcatalogue.core.actions.Batch.dismissed', {
    actions: ['$http', '$state', ($http, $state) -> [
      {
        title:      'Reactivate'
        icon:       'play'
        type:       'success'
        action:     (action) ->
          action.reactivate().then ->
            $state.go '.', {property: 'pending', sort: 'lastUpdated', order: 'desc'}, {reload: true}
      }
    ]]
  }


  catalogueElementPropertiesProvider.configureProperty 'relationships',   {
    hidden: true
  }

  catalogueElementPropertiesProvider.configureProperty 'org.modelcatalogue.core.DataElement.relationships',   {
    hidden: false
  }

  catalogueElementPropertiesProvider.configureProperty 'org.modelcatalogue.core.Asset.synonyms',   {
    hidden: true
  }

#  catalogueElementPropertiesProvider.configureProperty 'valueDomains',    hidden: (security) -> !security.hasRole('CURATOR')
  catalogueElementPropertiesProvider.configureProperty 'supersededBy',    hidden: true
  catalogueElementPropertiesProvider.configureProperty 'supersedes',      hidden: true


]