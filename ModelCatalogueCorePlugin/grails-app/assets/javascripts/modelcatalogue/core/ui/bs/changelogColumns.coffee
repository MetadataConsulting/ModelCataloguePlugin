angular.module('mc.core.ui.bs.changelogColumns', ['mc.util.names']).config ['columnsProvider', 'names', (columnsProvider, names)->

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

  columnsProvider.registerColumns 'org.modelcatalogue.core.audit.Change', [
    {header: "Type"       , value: getIcon                   , classes: 'col-md-1' }
    {header: "Author"     , value: valueOrName('author')     , classes: 'col-md-2' }
    {header: "Change"     , value: getChangeDescription      , classes: 'col-md-9' }
  ]


]