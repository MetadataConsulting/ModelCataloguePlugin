angular.module('mc.core.changeEnhancer', ['mc.util.rest', 'mc.util.enhance', 'mc.core.modelCatalogueApiRoot']).config ['enhanceProvider', (enhanceProvider)->
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
