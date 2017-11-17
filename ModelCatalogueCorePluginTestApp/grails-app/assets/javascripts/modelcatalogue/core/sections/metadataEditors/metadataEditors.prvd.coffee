angular.module('modelcatalogue.core.sections.metadataEditors').provider 'metadataEditors', ['catalogueProvider', (catalogueProvider) ->
  isMeetingRelationshipCriteria = (owner, criteria) ->
    match = criteria.match(/(\w+)?(?:=\[(\w+)\])?=>(\w+)?/)
    source = match[1]
    type = match[2]
    destination = match[3]

    sourceElement = if owner.direction == 'sourceToDestination' then owner.element else owner.relation
    destinationElement = if owner.direction == 'sourceToDestination' then owner.relation else owner.element

    return false if source and not catalogueProvider.isInstanceOf(sourceElement?.elementType, source)
    return false if type and owner.type?.name != type
    return false if destination and not catalogueProvider.isInstanceOf(destinationElement?.elementType, destination)
    return true

  fakeOwners = {}

  createFakeOwner = (criteria) ->
    fakeOwner = fakeOwners[criteria]

    return fakeOwner if fakeOwner

    fakeOwner =
      ext:
        type: 'orderedMap'

    match = criteria.match(/(\w+)?(?:=\[(\w+)\])?=>(\w+)?/)

    unless match
      fakeOwner.elementType = criteria
      fakeOwners[criteria] = fakeOwner
      return fakeOwner

    fakeOwner.elementType = 'relationship'
    fakeOwner.direction = 'sourceToDestination'


    source = match[1]
    type = match[2]
    destination = match[3]

    fakeOwner.type = {name: type}
    fakeOwner.element = {elementType: source}
    fakeOwner.relation = {elementType: destination}

    fakeOwners[criteria] = fakeOwner

    return fakeOwner

  editors = []

  metadataEditorsProvider = {}

  ###
    Type is either element type or relationship pattern definition "sourceType=[relationshipType]=>destinationType"
    where every part is optional e.g. "model=>", "=[base]=>dataType", "=>"
  ###
  metadataEditorsProvider.register = (editorConfiguration) ->
    throw new Error('Please provide supported types configuration ("types" configuration property)') unless editorConfiguration.types?
    throw new Error('Provided types configuration must be an array ("types" configuration property)') unless angular.isArray(editorConfiguration.types)
    throw new Error('Please provide supported keys configuration ("keys" configuration property)') unless editorConfiguration.keys?
    throw new Error('Provided keys configuration must be an array ("keys" configuration property)') unless angular.isArray(editorConfiguration.keys)
    throw new Error('Please provide supported template configuration ("template" configuration property)') unless editorConfiguration.template?
    throw new Error('Provided template configuration must be a string ("template" configuration property)') unless angular.isString(editorConfiguration.template)
    throw new Error('Please provide supported template configuration ("title" configuration property)') unless editorConfiguration.title?
    throw new Error('Provided title configuration must be a string ("title" configuration property)') unless angular.isString(editorConfiguration.title)

    editors.push {
      isAvailableFor: (owner) ->
        return false if not owner?.ext?.type == 'orderedMap'
        for editorType in editorConfiguration.types
          if editorType.indexOf('=>') > -1
            continue if not catalogueProvider.isInstanceOf(owner.elementType, 'relationship')
            return true if isMeetingRelationshipCriteria(owner, editorType)
          else if catalogueProvider.isInstanceOf(owner.elementType, editorType)
            return true

        return false
      isEnabledFor: (orderedMap) ->
        return false if not orderedMap
        return false if not orderedMap.type == 'orderedMap'
        for key in editorConfiguration.keys
          return true if orderedMap.get(key)?
        return false
      getTemplate: -> editorConfiguration.template
      getTitle: -> editorConfiguration.title
      getKeys: -> angular.copy editorConfiguration.keys
    }

  metadataEditorsProvider.createFakeOwner = (criteria) -> createFakeOwner(criteria)

  metadataEditorsProvider.$get = [ ->
    metadataEditors =
      getEnabledEditors: (owner, orderedMap) ->
        enabled = []
        enabled.push(editor) for editor in @getAvailableEditors(owner) when editor.isEnabledFor(orderedMap)
        enabled

      getAvailableEditors: (owner) ->
        available = []
        available.push(editor) for editor in editors when editor.isAvailableFor(owner)
        available

      createFakeOwner: (criteria) -> createFakeOwner(criteria)

    metadataEditors
  ]

  metadataEditorsProvider
]
