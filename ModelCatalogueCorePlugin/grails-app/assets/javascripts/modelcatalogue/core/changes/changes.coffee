window.modelcatalogue.registerModule 'mc.core.changes'

changes = angular.module('mc.core.changes', ['mc.core.ui.columns', 'mc.util.ui.actions', 'mc.core.catalogue','mc.util.rest', 'mc.util.enhance', 'mc.core.modelCatalogueApiRoot', 'mc.util.names', 'mc.core.ui.catalogueElementProperties'])

changes.run ['$templateCache', ($templateCache) ->
  $templateCache.put 'modelcatalogue/core/ui/catalogueElementView/history-tab.html', '''
     <catalogue-element-treeview  no-resize id="{{tab.name}}-table" list="tab.value" descend="'changes'"></catalogue-element-treeview>
    '''
]

changes.config ['enhanceProvider', (enhanceProvider)->
  condition = (item) -> item.hasOwnProperty('elementType') and item.elementType is 'org.modelcatalogue.core.audit.Change'
  factory   = ['modelCatalogueApiRoot', 'rest', '$rootScope', 'enhance', 'catalogueElementProperties', '$state', (modelCatalogueApiRoot, rest, $rootScope, enhance, catalogueElementProperties, $state) ->
    getProperty = (name, catalogueElementProperties) ->
      propertyName = catalogueElementProperties.getConfigurationFor(name)?.label
      propertyName = names.getNaturalName(name) unless propertyName
      """#{propertyName}"""


    getPlainValue = (value) ->
      return "null" unless value
      return """#{value}""" if angular.isString(value)
      return """#{value.value}""" if value.value
      return value.getLabel() if value.isInstanceOf and value.isInstanceOf('catalogueElement')

    getTitleChangeType = (change, catalogueElementProperties) ->
      switch change.type
        when 'EXTERNAL_UPDATE' then """#{change.property} (from #{change.changed.getLabel()} [#{change.changed.versionNumber}])"""
        when 'NEW_ELEMENT_CREATED' then """#{change.changed.getLabel()} [#{change.changed.versionNumber}] created"""
        when 'NEW_VERSION_CREATED' then """New version #{change.changed.getLabel()} [#{change.changed.versionNumber}] created"""
        when 'PROPERTY_CHANGED' then """Property #{getProperty(change.property, catalogueElementProperties)} of #{change.changed.getLabel()} [#{change.changed.versionNumber}] changed from #{getPlainValue(change.oldValue)} to #{getPlainValue(change.newValue)} """
        when 'ELEMENT_DELETED' then """#{change.changed.getLabel()} [#{change.changed.versionNumber}] deleted"""
        when 'ELEMENT_FINALIZED' then """#{change.changed.getLabel()} [#{change.changed.versionNumber}] finalized"""
        when 'ELEMENT_DEPRECATED' then """#{change.changed.getLabel()} [#{change.changed.versionNumber}] deprecated"""
        when 'METADATA_CREATED' then """Metadata #{change.property} of #{change.changed.getLabel()} [#{change.changed.versionNumber}] created with value #{getPlainValue(change.newValue)}"""
        when 'METADATA_UPDATED' then """Metadata #{change.property} of #{change.changed.getLabel()} [#{change.changed.versionNumber}] updated from value #{getPlainValue(change.oldValue)} to #{getPlainValue(change.newValue)}"""
        when 'METADATA_DELETED' then """Metadata #{change.property} of #{change.changed.getLabel()} [#{change.changed.versionNumber}] deleted value #{getPlainValue(change.oldValue)}"""
        when 'MAPPING_CREATED' then """Mapped #{change.newValue.source.getLabel()} [#{change.newValue.source.versionNumber}] to #{change.newValue.destination.getLabel()} [#{change.newValue.destination.versionNumber}] with rule #{getPlainValue(change.newValue.mapping)}"""
        when 'MAPPING_UPDATED' then """Changed mapping from #{change.newValue.source.getLabel()} [#{change.newValue.source.versionNumber}] to #{change.newValue.destination.getLabel()} [#{change.newValue.destination.versionNumber}] from #{getPlainValue(change.oldValue)} to #{getPlainValue(change.newValue.mapping)} """
        when 'MAPPING_DELETED' then """Removed mapping from #{change.oldValue.source.getLabel()} [#{change.oldValue.source.versionNumber}] to #{change.oldValue.destination.getLabel()} [#{change.oldValue.destination.versionNumber}] with rule #{getPlainValue(change.oldValue.mapping)}"""
        when 'RELATIONSHIP_CREATED' then """Created relationship #{change.changed.getLabel()} [#{change.changed.versionNumber}] #{change.property} #{getPlainValue(if change.otherSide then change.newValue.source else change.newValue.destination)}"""
        when 'RELATIONSHIP_DELETED' then """Deleted relationship #{change.changed.getLabel()} [#{change.changed.versionNumber}] #{change.property} #{getPlainValue(if change.otherSide then change.oldValue.source else change.oldValue.destination)}"""

        when 'RELATIONSHIP_METADATA_CREATED' then """Relationship #{change.changed.getLabel()} [#{change.changed.versionNumber}] #{change.property} #{getPlainValue(if change.otherSide then change.newValue.relationship.source else change.newValue.relationship.destination)} metadata #{change.newValue.name} created with value #{getPlainValue(change.newValue.extensionValue)}"""
        when 'RELATIONSHIP_METADATA_UPDATED' then """Relationship #{change.changed.getLabel()} [#{change.changed.versionNumber}] #{change.property} #{getPlainValue(if change.otherSide then change.newValue.relationship.source else change.newValue.relationship.destination)} metadata #{change.newValue.name} updated from value #{getPlainValue(change.oldValue)} to #{getPlainValue(change.newValue.extensionValue)}"""
        when 'RELATIONSHIP_METADATA_DELETED' then """Deleted relationship #{change.changed.getLabel()} [#{change.changed.versionNumber}] #{change.property} #{getPlainValue(if change.otherSide then change.oldValue.relationship.source else change.oldValue.relationship.destination)} #{change.oldValue.name} with value #{getPlainValue(change.oldValue.extensionValue)}"""

    getIconForChangeType = (change) ->
      switch change.type
        when 'EXTERNAL_UPDATE' then """fa fa-cloud-upload fa-fw text-success"""
        when 'NEW_ELEMENT_CREATED' then """fa fa-plus fa-fw text-success"""
        when 'NEW_VERSION_CREATED' then """fa fa-arrow-circle-up fa-fw text-success"""
        when 'PROPERTY_CHANGED' then """fa fa-edit fa-fw text-info"""
        when 'ELEMENT_DELETED' then """fa fa-remove fa-fw text-danger"""
        when 'ELEMENT_FINALIZED' then """fa fa-check-square-o fa-fw text-primary"""
        when 'ELEMENT_DEPRECATED' then """fa fa-ban fa-fw text-danger"""
        when 'METADATA_CREATED' then """fa fa-plus fa-fw text-success"""
        when 'METADATA_UPDATED' then """fa fa-edit fa-fw text-info"""
        when 'METADATA_DELETED' then """fa fa-remove fa-fw text-danger"""
        when 'MAPPING_CREATED' then """fa fa-plus fa-fw text-success"""
        when 'MAPPING_UPDATED' then """fa fa-edit fa-fw text-info"""
        when 'MAPPING_DELETED' then """fa fa-remove fa-fw text-danger"""
        when 'RELATIONSHIP_CREATED' then """fa fa-plus fa-fw text-success"""
        when 'RELATIONSHIP_DELETED' then """fa fa-remove fa-fw text-danger"""
        when 'RELATIONSHIP_METADATA_CREATED' then """fa fa-plus fa-fw text-success"""
        when 'RELATIONSHIP_METADATA_UPDATED' then """fa fa-edit fa-fw text-info"""
        when 'RELATIONSHIP_METADATA_DELETED' then """fa fa-remove fa-fw text-danger"""

    (element) ->
      element.getLabel = ->
        getTitleChangeType(this, catalogueElementProperties)

      element.name = element.getLabel()
      element.show = -> $state.go('mc.resource.show', {resource: 'change', id: element.id}); element
      element.href = -> $state.href('mc.resource.show', {resource: 'change', id: element.id})
      element.refresh = -> {then: (callback) -> callback(element) }
      element.getIcon = -> getIconForChangeType(element)

      element
  ]

  enhanceProvider.registerEnhancerFactory('change', condition, factory)
]

changes.config ['actionsProvider', (actionsProvider)->

  actionsProvider.registerActionInRole 'undo-change', actionsProvider.ROLE_ITEM_ACTION, ['$scope', 'messages', 'security', '$http', 'modelCatalogueApiRoot', '$state', ($scope, messages, security, $http, modelCatalogueApiRoot, $state) ->
    return undefined unless $scope.element
    return undefined unless $scope.element.changed
    return undefined unless $scope.element.changed.status == 'DRAFT' or ($scope.element.changed.isInstanceOf('asset') and $scope.element.changed.status == 'FINALIZED')
    return undefined if not security.hasRole('CURATOR')

    {
      position:   150
      label:      'Undo'
      icon:       'fa fa-undo'
      type:       'primary'
      watches:    'element.undone'
      disabled:   not $scope.element.undoSupported or $scope.element.undone
      action:     ->
        security.requireRole('CURATOR').then ->
          messages.confirm("Do you want to undo selected change?", "Current element will be reverted to the previous state if it is still possible. Undoing change does not check if the current state").then ->
            $http(url: "#{modelCatalogueApiRoot}/change/#{$scope.element.id}", method: 'DELETE').then ->
              messages.success "Change was reverted successfully"
              $state.go '.', {}, {inherit: true, reload: true}
            ,  ->
              msg = "Cannot undo selected change. It would leave catalogue in inconsistent state."
              if $scope.element.changes.total > 0
                msg += " Try undo child actions one by one first."
              messages.error msg
    }
  ]

  actionsProvider.registerChildAction 'currentDataModel', 'feed', ['$state', '$rootScope', ($state, $rootScope) ->
    action = {
      position:   500
      label: 'Activity'
      icon: 'fa fa-fw fa-rss'
      action: ->
        $state.go 'mc.resource.list', resource: 'change', dataModelId: if $rootScope.currentDataModel then $rootScope.currentDataModel.id else 'catalogue'
    }

    $rootScope.$on '$stateChangeSuccess', (ignored, state, params) ->
      action.active = state.name == 'mc.resource.list' and params.resource == 'change'

    action
  ], [actionsProvider.ROLE_NAVIGATION]
]

changes.config ['catalogueProvider', (catalogueProvider)->
  catalogueProvider.setDefaultSort 'change', sort: 'dateCreated', order: 'desc'
]

changes.config ['columnsProvider', 'names', (columnsProvider, names)->

  getIconForChangeType = (change) ->
    switch change.type
      when 'EXTERNAL_UPDATE' then """<span title="External Change"><span class="#{change.changed.getIcon()} fa-fw text-success"></span> <span class="fa fa-cloud-upload fa-fw text-success"></span></span>"""
      when 'NEW_ELEMENT_CREATED' then """<span title="New Element Created"><span class="#{change.changed.getIcon()} fa-fw text-success"></span> <span class="fa fa-plus fa-fw text-success"></span></span>"""
      when 'NEW_VERSION_CREATED' then """<span title="New Version Created"><span class="#{change.changed.getIcon()} fa-fw text-success"></span> <span class="fa fa-arrow-circle-up fa-fw text-success"></span></span>"""
      when 'PROPERTY_CHANGED' then """<span title="Property Changed"><span class="#{change.changed.getIcon()} fa-fw text-info"></span> <span class="fa fa-edit fa-fw text-info"></span></span>"""
      when 'ELEMENT_DELETED' then """<span title="Element Deleted"><span class="#{change.changed.getIcon()} fa-fw text-danger"></span> <span class="fa fa-remove fa-fw text-danger"></span></span>"""
      when 'ELEMENT_FINALIZED' then """<span title="Element Finalized"><span class="#{change.changed.getIcon()} fa-fw text-primary"></span> <span class="fa fa-check-square-o fa-fw text-primary"></span></span>"""
      when 'ELEMENT_DEPRECATED' then """<span title="Element Deprecated"><span class="#{change.changed.getIcon()} fa-fw text-danger"></span> <span class="fa fa-ban fa-fw text-danger"></span></span>"""
      when 'METADATA_CREATED' then """<span title="Metadata Created"><span class="fa fa-th-list fa-fw text-success"></span> <span class="fa fa-plus fa-fw text-success"></span></span>"""
      when 'METADATA_UPDATED' then """<span title="Metadata Updated"><span class="fa fa-th-list fa-fw text-info"></span> <span class="fa fa-edit fa-fw text-info"></span></span>"""
      when 'METADATA_DELETED' then """<span title="Metadata Deleted"><span class="fa fa-th-list fa-fw text-danger"></span> <span class="fa fa-remove fa-fw text-danger"></span></span>"""
      when 'MAPPING_CREATED' then """<span title="Mapping Created"><span class="fa fa-superscript fa-fw text-success"></span> <span class="fa fa-plus fa-fw text-success"></span></span>"""
      when 'MAPPING_UPDATED' then """<span title="Mapping Updated"><span class="fa fa-superscript fa-fw text-info"></span> <span class="fa fa-edit fa-fw text-info"></span></span>"""
      when 'MAPPING_DELETED' then """<span title="Mapping Deleted"><span class="fa fa-superscript fa-fw text-danger"></span> <span class="fa fa-remove fa-fw text-danger"></span></span>"""
      when 'RELATIONSHIP_CREATED' then """<span title="Relationship Created"><span class="fa fa-link fa-fw text-success"></span> <span class="fa fa-plus fa-fw text-success"></span></span>"""
      when 'RELATIONSHIP_DELETED' then """<span title="Relationship Deleted"><span class="fa fa-link fa-fw text-danger"></span> <span class="fa fa-remove fa-fw text-danger"></span></span>"""
      when 'RELATIONSHIP_METADATA_CREATED' then """<span title="Relationship Metadata Created"><span class="fa fa-list-ul fa-fw text-success"></span> <span class="fa fa-plus fa-fw text-success"></span></span>"""
      when 'RELATIONSHIP_METADATA_UPDATED' then """<span title="Relationship Metadata Updated"><span class="fa fa-list-ul fa-fw text-info"></span> <span class="fa fa-edit fa-fw text-info"></span></span>"""
      when 'RELATIONSHIP_METADATA_DELETED' then """<span title="Relationship Metadata Deleted"><span class="fa fa-list-ul fa-fw text-danger"></span> <span class="fa fa-remove fa-fw text-danger"></span></span>"""

  getLinkTo = (element) ->
    return "<code>null</code>" unless element
    return """<span class="text-muted"><span class="#{element.getIcon()}"></span> #{element.name} [#{element.versionNumber}]</span>""" if element.deleted
    return """<a href="#{element.href()}"><span class="#{element.getIcon()}"></span> #{element.name} [#{element.versionNumber}]</a>"""

  getValue = (value) ->
    return "<code>null</code>" unless value
    return """<pre>#{value}</pre>""" if angular.isString(value)
    return """<pre>#{value.value}</pre>""" if value.value
    return getLinkTo(value) if value.isInstanceOf and value.isInstanceOf('catalogueElement')

  getProperty = (name, catalogueElementProperties) ->
    propertyName = catalogueElementProperties.getConfigurationFor(name)?.label
    propertyName = names.getNaturalName(name) unless propertyName
    """<code>#{propertyName}</code>"""


  getChangeForChangeType = (change, catalogueElementProperties) ->
    switch change.type
      when 'EXTERNAL_UPDATE' then """#{change.property} (from #{getLinkTo(change.changed)})"""
      when 'NEW_ELEMENT_CREATED' then """#{getLinkTo(change.changed)} created"""
      when 'NEW_VERSION_CREATED' then """New version #{getLinkTo(change.changed)} created"""
      when 'PROPERTY_CHANGED' then """Property #{getProperty(change.property, catalogueElementProperties)} of #{getLinkTo(change.changed)} changed from #{getValue(change.oldValue)} to #{getValue(change.newValue)} """
      when 'ELEMENT_DELETED' then """#{getLinkTo(change.changed)} deleted"""
      when 'ELEMENT_FINALIZED' then """#{getLinkTo(change.changed)} finalized"""
      when 'ELEMENT_DEPRECATED' then """#{getLinkTo(change.changed)} deprecated"""
      when 'METADATA_CREATED' then """Metadata <code>#{change.property}</code> of #{getLinkTo(change.changed)} created with value #{getValue(change.newValue)}"""
      when 'METADATA_UPDATED' then """Metadata <code>#{change.property}</code> of #{getLinkTo(change.changed)} updated from value #{getValue(change.oldValue)} to #{getValue(change.newValue)}"""
      when 'METADATA_DELETED' then """Metadata <code>#{change.property}</code> of #{getLinkTo(change.changed)} deleted value #{getValue(change.oldValue)}"""
      when 'MAPPING_CREATED' then """Mapped #{getLinkTo(change.newValue.source)} to #{getLinkTo(change.newValue.destination)} with rule #{getValue(change.newValue.mapping)}"""
      when 'MAPPING_UPDATED' then """Changed mapping from #{getLinkTo(change.newValue.source)} to #{getLinkTo(change.newValue.destination)} from #{getValue(change.oldValue)} to #{getValue(change.newValue.mapping)} """
      when 'MAPPING_DELETED' then """Removed mapping from #{getLinkTo(change.oldValue.source)} to #{getLinkTo(change.oldValue.destination)} with rule #{getValue(change.oldValue.mapping)}"""
      when 'RELATIONSHIP_CREATED' then """Created relationship #{getLinkTo(change.changed)} <code>#{change.property}</code> #{getLinkTo(if change.otherSide then change.newValue.source else change.newValue.destination)}"""
      when 'RELATIONSHIP_DELETED' then """Deleted relationship #{getLinkTo(change.changed)} <code>#{change.property}</code> #{getLinkTo(if change.otherSide then change.oldValue.source else change.oldValue.destination)}"""

      when 'RELATIONSHIP_METADATA_CREATED' then """Relationship #{getLinkTo(change.changed)} <code>#{change.property}</code> #{getLinkTo(if change.otherSide then change.newValue.relationship.source else change.newValue.relationship.destination)} metadata <code>#{change.newValue.name}</code> created with value #{getValue(change.newValue.extensionValue)}"""
      when 'RELATIONSHIP_METADATA_UPDATED' then """Relationship #{getLinkTo(change.changed)} <code>#{change.property}</code> #{getLinkTo(if change.otherSide then change.newValue.relationship.source else change.newValue.relationship.destination)} metadata <code>#{change.newValue.name}</code> updated from value #{getValue(change.oldValue)} to #{getValue(change.newValue.extensionValue)}"""
      when 'RELATIONSHIP_METADATA_DELETED' then """Deleted relationship #{getLinkTo(change.changed)} <code>#{change.property}</code> #{getLinkTo(if change.otherSide then change.oldValue.relationship.source else change.oldValue.relationship.destination)} <code>#{change.oldValue.name}</code> with value #{getValue(change.oldValue.extensionValue)}"""


  getLinkToParent = (change) ->
    return '' unless change?.parent
    return """<a title="Parent Action: #{change.parent.name}" href="#/catalogue/change/#{change.parent.id}/changes"><span class="fa fa-fw fa-level-up"></span></a>"""

  getIcon = (change) -> "#{getIconForChangeType(change)}#{getLinkToParent(change)}"
  getChangeDescription = (change, catalogueElementProperties) -> """<a href="#/catalogue/change/#{change.id}"><span class="fa fa-fw fa-link"></span></a> #{getChangeForChangeType(change, catalogueElementProperties)}"""

  valueOrName = (property) ->
    (change) ->
      object = change[property]
      return '' if not object
      return object.value if object.value

      return """<a href=#{object.href()}><span class="#{object.getIcon()}"></span> #{object.name}</a>""" if object.isInstanceOf and object.isInstanceOf('catalogueElement')

  dateCreated = (change, catalogueElementProperties) ->
    catalogueElementProperties.filter('date')(change.dateCreated, 'short')

  columnsProvider.registerColumns 'org.modelcatalogue.core.audit.Change', [
    {header: "Type"       , value: getIcon                   , classes: 'col-md-1' }
    {header: "Created"    , value: dateCreated               , classes: 'col-md-2' }
    {header: "Author"     , value: valueOrName('author')     , classes: 'col-md-2' }
    {header: "Change"     , value: getChangeDescription      , classes: 'col-md-9' }
  ]
]

changes.config ['catalogueElementPropertiesProvider', (catalogueElementPropertiesProvider)->

  catalogueElementPropertiesProvider.configureProperty 'changes', hidden: true
  catalogueElementPropertiesProvider.configureProperty 'changed', tabDefinition: -> [hide: true]
  catalogueElementPropertiesProvider.configureProperty 'latestVersion', tabDefinition: -> [hide: true]

  catalogueElementPropertiesProvider.configureProperty 'org.modelcatalogue.core.audit.Change.changes', hidden: false
  catalogueElementPropertiesProvider.configureProperty 'org.modelcatalogue.core.audit.Change.properties', tabDefinition: [ '$element', ($element) ->

    getPropertyVal = (propertyName) ->
      (element) -> element[propertyName]

    {
      heading:    'Properties'
      name:       'properties'
      value:      $element
      properties: [
        {label: 'Parent Change', value: getPropertyVal('parent')}
        {label: 'Change Type', value: getPropertyVal('type')}
        {label: 'Changed Element', value: getPropertyVal('changed')}
        {label: 'Root Element', value: getPropertyVal('latestVersion')}
        {label: 'Author', value: getPropertyVal('author')}
        {label: 'Undone', value: getPropertyVal('undone')}
        {label: 'System', value: getPropertyVal('system')}
      ]
      type:       'properties-pane-for-properties'
    }
  ]

  catalogueElementPropertiesProvider.configureProperty 'history', tabDefinition: ['$element', '$name', '$value', 'catalogueElementProperties', '$injector', ($element, $name, $value, catalogueElementProperties, $injector) ->
    definition = $injector.invoke(catalogueElementProperties.getConfigurationFor('enhanced:listReference').tabDefinition, undefined, $element: $element, $name: $name, $value: $value)
    definition.type = 'history-tab'
    definition
  ]

]