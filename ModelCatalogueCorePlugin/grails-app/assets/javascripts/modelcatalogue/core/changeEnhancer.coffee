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
        when 'EXTERNAL_UPDATE' then """#{change.property} (from #{change.changed.getLabel()})"""
        when 'NEW_ELEMENT_CREATED' then """#{change.changed.getLabel()} created"""
        when 'NEW_VERSION_CREATED' then """New version #{change.changed.getLabel()} created"""
        when 'PROPERTY_CHANGED' then """Property #{getProperty(change.property, catalogueElementProperties)} of #{change.changed.getLabel()} changed from #{getPlainValue(change.oldValue)} to #{getPlainValue(change.newValue)} """
        when 'ELEMENT_DELETED' then """#{change.changed.getLabel()} deleted"""
        when 'ELEMENT_FINALIZED' then """#{change.changed.getLabel()} finalized"""
        when 'ELEMENT_DEPRECATED' then """#{change.changed.getLabel()} deprecated"""
        when 'METADATA_CREATED' then """Metadata #{change.property} of #{change.changed.getLabel()} created with value #{getPlainValue(change.newValue)}"""
        when 'METADATA_UPDATED' then """Metadata #{change.property} of #{change.changed.getLabel()} updated from value #{getPlainValue(change.oldValue)} to #{getPlainValue(change.newValue)}"""
        when 'METADATA_DELETED' then """Metadata #{change.property} of #{change.changed.getLabel()} deleted value #{getPlainValue(change.oldValue)}"""
        when 'MAPPING_CREATED' then """Mapped #{change.newValue.source.getLabel()} to #{change.newValue.destination.getLabel()} with rule #{getPlainValue(change.newValue.mapping)}"""
        when 'MAPPING_UPDATED' then """Changed mapping from #{change.newValue.source.getLabel()} to #{change.newValue.destination.getLabel()} from #{getPlainValue(change.oldValue)} to #{getPlainValue(change.newValue.mapping)} """
        when 'MAPPING_DELETED' then """Removed mapping from #{change.oldValue.source.getLabel()} to #{change.oldValue.destination.getLabel()} with rule #{getPlainValue(change.oldValue.mapping)}"""
        when 'RELATIONSHIP_CREATED' then """Created relationship #{change.changed.getLabel()} #{change.property} #{getPlainValue(if change.otherSide then change.newValue.source else change.newValue.destination)}"""
        when 'RELATIONSHIP_DELETED' then """Deleted relationship #{change.changed.getLabel()} #{change.property} #{getPlainValue(if change.otherSide then change.oldValue.source else change.oldValue.destination)}"""

        when 'RELATIONSHIP_METADATA_CREATED' then """Relationship #{change.changed.getLabel()} #{change.property} #{getPlainValue(if change.otherSide then change.newValue.relationship.source else change.newValue.relationship.destination)} metadata #{change.newValue.name} created with value #{getPlainValue(change.newValue.extensionValue)}"""
        when 'RELATIONSHIP_METADATA_UPDATED' then """Relationship #{change.changed.getLabel()} #{change.property} #{getPlainValue(if change.otherSide then change.newValue.relationship.source else change.newValue.relationship.destination)} metadata #{change.newValue.name} updated from value #{getPlainValue(change.oldValue)} to #{getPlainValue(change.newValue.extensionValue)}"""
        when 'RELATIONSHIP_METADATA_DELETED' then """Deleted relationship #{change.changed.getLabel()} #{change.property} #{getPlainValue(if change.otherSide then change.oldValue.relationship.source else change.oldValue.relationship.destination)} #{change.oldValue.name} with value #{getPlainValue(change.oldValue.extensionValue)}"""

    (element) ->
      element.getLabel = ->
        getTitleChangeType(this, catalogueElementProperties)

      element.name = element.getLabel()
      element.show = -> $state.go('mc.resource.show', {resource: 'change', id: element.id}); element
      element.refresh = -> {then: (callback) -> callback(element) }
      element
  ]

  enhanceProvider.registerEnhancerFactory('change', condition, factory)
]
