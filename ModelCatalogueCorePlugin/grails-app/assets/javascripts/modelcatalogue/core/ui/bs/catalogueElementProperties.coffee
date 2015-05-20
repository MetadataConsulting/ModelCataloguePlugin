angular.module('mc.core.ui.bs.catalogueElementProperties', []).config ['catalogueElementPropertiesProvider', (catalogueElementPropertiesProvider)->


  localNameAndIdent = -> [
    {header: 'Name', value: "ext.get('name') || ext.get('Name') || relation.name ", classes: 'col-md-5', show: "relation.show()", href: 'relation.href()', href: 'relation.href()'}
    {header: 'Identification',  value: "relation.getElementTypeName() + ': ' + relation.id", classes: 'col-md-5', show: "relation.show()", href: 'relation.href()'}
  ]

  nameAndIdent = -> [
    {header: 'Name', value: "relation.classifiedName ", classes: 'col-md-5', show: "relation.show()", href: 'relation.href()'}
    {header: 'Identification',  value: "relation.getElementTypeName() + ': ' + relation.id", classes: 'col-md-5', show: "relation.show()", href: 'relation.href()'}
  ]

  nameAndIdAndMetadata = -> [
    {header: 'Name', value: "relation.classifiedName", classes: 'col-md-3', show: "relation.show()", href: 'relation.href()'}
    {header: 'Identification',  value: "relation.modelCatalogueId", classes: 'col-md-3', show: "relation.show()", href: 'relation.href()'}
    {header: 'Metadata',  value: printMetadata, classes: 'col-md-4'}
  ]

  containsDataElements= -> [
    {header: 'Name', value: "relation.name", classes: 'col-md-3', show: "relation.show()", href: 'relation.href()'}
    {header: "Description", value: "relation.description" , classes: "col-md-5"}
    {header: "Value Domain", value: printDataType, classes: "col-md-3", show: true, href: 'href()'}
    {header: 'Metadata',  value: printMetadata, classes: 'col-md-2'}
  ]

  printDataType = (relationship) ->
    result  = ''
    dataType = relationship?.relation?.valueDomain?.dataType
    if dataType?.enumerations?.values
      ext     = dataType?.enumerations?.values ? []
      for e in ext
        result += "#{e.key} \n"
    else if dataType
      result = dataType?.name
    result


  printMetadata = (relationship) ->
    result  = ''
    ext     = relationship?.ext ? {values: []}
    for row in ext.values
      result += "#{row.key}: #{row.value ? ''}\n"
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
    {header: "Name",        value: 'relation.name', class: 'col-md-4', sort: {property: 'name', type: 'alphabet'}, show: 'relation.show()', href: 'relation.href()'}
    {header: "File Name",   value: 'relation.originalFileName',  class: 'col-md-4', sort: {property: 'originalFileName', type: 'alphabet'}}
    {header: "Size",        value: computeBytes,                 class: 'col-md-2', sort: {property: 'size', type: 'order'}}
    {header: "Mime Type",   value: 'relation.contentType',       class: 'col-md-2', sort: {property: 'contentType', type: 'alphabet'}}
  ]



  # global settings
  catalogueElementPropertiesProvider.configureProperty 'ext', label: 'Metadata'
  catalogueElementPropertiesProvider.configureProperty 'parentOf', label: 'Children', columns: localNameAndIdent()
  catalogueElementPropertiesProvider.configureProperty 'childOf', label: 'Parents', columns: nameAndIdent()
  catalogueElementPropertiesProvider.configureProperty 'isContextFor', label: 'Models', columns: nameAndIdent()
  catalogueElementPropertiesProvider.configureProperty 'includes', label: 'Value Domains', columns: nameAndIdent()
  catalogueElementPropertiesProvider.configureProperty 'instantiatedBy', label: 'Value Domains', columns: nameAndIdAndMetadata()
  catalogueElementPropertiesProvider.configureProperty 'contains', label: 'Data Elements', columns: containsDataElements()
  catalogueElementPropertiesProvider.configureProperty 'containedIn', label: 'Models', columns: nameAndIdAndMetadata()
  catalogueElementPropertiesProvider.configureProperty 'hasAttachmentOf', label: 'Attachments', columns: attachmentColumns()
  catalogueElementPropertiesProvider.configureProperty 'hasContextOf', label: 'Conceptual Domains', columns: nameAndIdent()
  catalogueElementPropertiesProvider.configureProperty 'classifies', label: 'Classifies', columns: localNameAndIdent()

  catalogueElementPropertiesProvider.configureProperty 'instantiates', label: 'Data Elements', columns: nameAndIdAndMetadata()

  catalogueElementPropertiesProvider.configureProperty 'history', {
    columns: [
      {header: "Version", value: 'versionNumber', class: 'col-md-1', show: true, href: 'href()'}
      {header: "Name", value: 'name', class: 'col-md-5', show: true, href: 'href()'}
      {header: "Description", value: 'description', class: 'col-md-6'}
    ]
  }

  catalogueElementPropertiesProvider.configureProperty 'org.modelcatalogue.core.Asset.history', {
    hidden: (security) ->
      !security.hasRole('CURATOR')
    columns: [
      {header: "Version",   value: 'versionNumber',     class: 'col-md-1', show: true, href: 'href()'}
      {header: "Name",      value: 'name',              class: 'col-md-4', show: true, href: 'href()'}
      {header: "File Name", value: 'originalFileName',  class: 'col-md-4', show: true, href: 'href()'}
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

  catalogueElementPropertiesProvider.configureProperty 'org.modelcatalogue.core.DataElement.relationships', hidden: true
  catalogueElementPropertiesProvider.configureProperty 'org.modelcatalogue.core.Asset.synonyms', hidden: true

  catalogueElementPropertiesProvider.configureProperty 'relationships', hidden: true
  catalogueElementPropertiesProvider.configureProperty '$$metadata', hidden: true
  catalogueElementPropertiesProvider.configureProperty '$$cachedChildren', hidden: true
  catalogueElementPropertiesProvider.configureProperty 'supersededBy', hidden: true
  catalogueElementPropertiesProvider.configureProperty 'supersedes', hidden: true


  catalogueElementPropertiesProvider.configureProperty 'enhanced:listReference', tabDefinition: [ '$element', '$name', '$value', 'catalogueElementProperties', 'enhance', 'security', 'columns', ($element, $name, $value, catalogueElementProperties, enhance, security, columns) ->
    listEnhancer = enhance.getEnhancer('list')
    propertyConfiguration = catalogueElementProperties.getConfigurationFor("#{$element.elementType}.#{$name}")

    {
      heading:  propertyConfiguration.label
      value:    angular.extend(listEnhancer.createEmptyList($value.itemType, $value.total), {base: $value.base})
      disabled: $value.total == 0
      loader:   $value
      type:     'decorated-list'
      columns:   propertyConfiguration.columns ? columns($value.itemType)
      name:     $name
      reports:  []
    }

  ]

  getPropertyVal  = (propertyName) ->
    (element) -> element[propertyName]

  objectTabDefinition = (editTabType) ->
    [ '$element', '$name', '$value', 'catalogueElementProperties', 'enhance', 'security', 'catalogueElementResource', 'messages', 'names', ($element, $name, $value, catalogueElementProperties, enhance, security, catalogueElementResource, messages, names) ->


      getSortedMapPropertyVal = (propertyName) ->
        (element) ->
          for value in element.values
            if value.key == propertyName
              return value.value

      updateFrom = (original, update) ->
        for originalKey of original
          if originalKey.indexOf('$') != 0 # keep the private fields such as number of children in tree view
            delete original[originalKey]

        for newKey of update
          original[newKey] = update[newKey]
        original

      resource = catalogueElementResource($element.elementType) if $element and $element.elementType
      propertyConfiguration = catalogueElementProperties.getConfigurationFor("#{$element.elementType}.#{$name}")

      tabDefinition =
        name:       $name
        heading:    propertyConfiguration.label
        value:      $value ? {}
        original:   angular.copy($value ? {})
        properties: []
        type:       if security.hasRole('CURATOR') and $element.status == 'DRAFT' then editTabType else 'properties-pane'
        isDirty:    ->
          if @value and enhance.isEnhancedBy(@value, 'orderedMap') and @original and enhance.isEnhancedBy(@original, 'orderedMap')
            return false if angular.equals(@value.values, @original.values)
            return false if @original.values.length == 0 and @value.values.length == 1 and not @value.values[0].value and not @value.values[0].key
          !angular.equals(@original, @value)
        reset:      -> @value = angular.copy @original
        update:     ->
          if not resource
            messages.error("Cannot update property #{names.getNaturalName(self.name)} of #{$element.name}. See application logs for details.")
            return

          payload = {
            id: $element.id
          }
          payload[@name] = angular.copy(@value)
          self = @
          resource.update(payload).then (updated) ->
            updateFrom($element, updated)
            messages.success("Property #{if propertyConfiguration.label then propertyConfiguration.label else names.getNaturalName(self.name)} of #{$element.name} successfully updated")
            updated
          ,  (response) ->
            if response.data.errors
              if angular.isString response.data.errors
                messages.error response.data.errors
              else
                for err in response.data.errors
                  messages.error err.message
            else
              messages.error("Cannot update property #{if propertyConfiguration.label then propertyConfiguration.label else names.getNaturalName(self.name)} of #{$element.name}. See application logs for details.")


      if $value?.type == 'orderedMap'
        for value in $value.values when not angular.isObject(value.value)
          tabDefinition.properties.push {
            label: value.key
            value: getSortedMapPropertyVal(value.key)
          }
      else
        for key, value of $value when not angular.isObject(value)
          tabDefinition.properties.push {
            label: key
            value: getPropertyVal(key)
          }

      tabDefinition
    ]

  catalogueElementPropertiesProvider.configureProperty 'properties', tabDefinition: [ '$element', 'catalogueElementProperties', 'enhance', 'security', 'names', ($element, catalogueElementProperties, enhance, security, names) ->

    return [hide: true] unless enhance.isEnhancedBy($element, 'catalogueElement')

    getObjectSize = (object) ->
      size = 0
      angular.forEach object, () ->
        size++
      size

    newProperties = []
    for prop in $element.getUpdatableProperties()

      obj = $element[prop]
      config = catalogueElementProperties.getConfigurationFor("#{$element.elementType}.#{prop}")

      if angular.isFunction(obj)
        continue
      if config and config.hidden(security)
        continue
      if enhance.isEnhancedBy(obj, 'listReference')
        continue
      if enhance.isEnhancedBy(obj, 'orderedMap')
        continue
      if (angular.isObject(obj) and !angular.isArray(obj) and !enhance.isEnhanced(obj))
        continue
      newProperties.push(label: names.getNaturalName(prop), value: getPropertyVal(prop))

    {
      heading: 'Properties'
      name: 'properties'
      value: $element
      disabled: getObjectSize(newProperties) == 0
      properties: newProperties
      type: 'properties-pane-for-properties'
    }
  ]

  hideTab = -> [hide: true]

  catalogueElementPropertiesProvider.configureProperty 'enhanced:orderedMap', tabDefinition: objectTabDefinition('ordered-map-editor')
  catalogueElementPropertiesProvider.configureProperty 'type:object', tabDefinition: objectTabDefinition('simple-object-editor')

  catalogueElementPropertiesProvider.configureProperty 'enhanced:catalogueElement', tabDefinition: hideTab

  catalogueElementPropertiesProvider.configureProperty 'type:array', tabDefinition: hideTab
  catalogueElementPropertiesProvider.configureProperty 'type:function', tabDefinition: hideTab
  catalogueElementPropertiesProvider.configureProperty 'type:date', tabDefinition: hideTab
  catalogueElementPropertiesProvider.configureProperty 'type:string', tabDefinition: hideTab

  catalogueElementPropertiesProvider.configureProperty 'version', hidden: true
  catalogueElementPropertiesProvider.configureProperty 'name', hidden: true
  catalogueElementPropertiesProvider.configureProperty 'classifiedName', hidden: true
  catalogueElementPropertiesProvider.configureProperty 'description', hidden: true
  catalogueElementPropertiesProvider.configureProperty 'incomingRelationships', hidden: true
  catalogueElementPropertiesProvider.configureProperty 'outgoingRelationships', hidden: true
  catalogueElementPropertiesProvider.configureProperty 'relationships', hidden: true
  catalogueElementPropertiesProvider.configureProperty 'availableReports', hidden: true
  catalogueElementPropertiesProvider.configureProperty 'downloadUrl', hidden: true
  catalogueElementPropertiesProvider.configureProperty 'archived', hidden: true
  catalogueElementPropertiesProvider.configureProperty 'status', hidden: true
  catalogueElementPropertiesProvider.configureProperty '__enhancedBy', hidden: true
  catalogueElementPropertiesProvider.configureProperty 'parent', hidden: true
  catalogueElementPropertiesProvider.configureProperty 'oldValue', hidden: true
  catalogueElementPropertiesProvider.configureProperty 'newValue', hidden: true


  catalogueElementPropertiesProvider.configureProperty 'enumerations', tabDefinition: ['$element', '$name', '$value', '$scope', 'catalogueElementProperties', '$injector', 'security', ($element, $name, $value, $scope, catalogueElementProperties, $injector, security) ->
    definition = $injector.invoke(catalogueElementProperties.getConfigurationFor('type:object').tabDefinition, undefined, $element: $element, $name: $name, $value: $value, $scope: $scope)
    definition.type = if security.hasRole('CURATOR') and $element.status == 'DRAFT' then 'ordered-map-editor-for-enumerations' else 'properties-pane-for-enumerations'
    definition
  ]

]