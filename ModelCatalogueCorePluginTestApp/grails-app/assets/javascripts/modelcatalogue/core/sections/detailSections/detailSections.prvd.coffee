angular.module('modelcatalogue.core.sections.detailSections').provider 'detailSections', ['catalogueProvider', (catalogueProvider) ->
# detailSections gives a provider that relies on catalogueProvider
  # are these providers dependency-injected?
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

  configurations = []

  detailSectionsProvider = {}

  ###
    Type is either element type or relationship pattern definition "sourceType=[relationshipType]=>destinationType"
    where every part is optional e.g. "model=>", "=[base]=>dataType", "=>"

    @param types          supported types
    @param keys           supported metadata keys
    @param typeKeys       supported metadata keys for certain catalogue element type
    @param template       template to be used for rendering
    @param position       position in the view (negative numbers goes before the description, positive after the description
    @param title          optional title to be displayed
    @param hideIfNoData   optional hide template when no data are in there
    @param hideByDefault  optional hide the template by default
  ###
  detailSectionsProvider.register = (configuration) ->
    throw new Error('Please provide supported types configuration ("types" configuration property)') unless configuration.types?
    throw new Error('Provided types configuration must be an array ("types" configuration property)') unless angular.isArray(configuration.types)
    throw new Error('Please provide supported keys configuration ("keys" configuration property)') unless configuration.keys?
    throw new Error('Provided keys configuration must be an array ("keys" configuration property)') unless angular.isArray(configuration.keys)
    throw new Error('Please provide supported template configuration ("template" configuration property)') unless configuration.template?
    throw new Error('Provided template configuration must be a string ("template" configuration property)') unless angular.isString(configuration.template)
    throw new Error('Please provide supported template configuration ("position" configuration property)') unless configuration.position?
    throw new Error('Provided position configuration must be a number ("position" configuration property)') unless angular.isNumber(configuration.position)
    throw new Error('Provided title configuration must be a string ("title" configuration property)') unless angular.isString(configuration.title)
    if (configuration.autoSave)
      throw new Error('Provided autoSave configuration must be an object ("autoSave" configuration property)') unless angular.isObject(configuration.autoSave)

    # TODO should handle $$relationship

    configuration.isAvailableFor = (owner) -> # whether a particular element should use a particular configuration
      return false if not owner
      return false if not owner.ext
      return false if not owner.ext.type == 'orderedMap'
      for type in configuration.types
        if type.indexOf('=>') > -1 # if the type has => in it which means it's a relationship
          continue if not catalogueProvider.isInstanceOf(owner.elementType, 'relationship')
          return true if isMeetingRelationshipCriteria(owner, type)
        else if catalogueProvider.isInstanceOf(owner.elementType, type)
          return true

      return false

    configurations.push configuration

  detailSectionsProvider.createFakeOwner = (criteria) -> createFakeOwner(criteria)

  detailSectionsProvider.$get = [ '$filter', ($filter) ->
    detailSections =
      getAvailableViews: (owner) ->
        available = []
        angular.forEach configurations, (configuration) ->
          return unless configuration.isAvailableFor(owner)
          view =
            template: configuration.template
            title: configuration.title
            position: configuration.position
            hideInOverview: configuration.hideInOverview
            hideIfNoData: configuration.hideIfNoData
            hideByDefault: configuration.hideByDefault
            keys: angular.copy configuration.keys
            typeKeys: angular.copy configuration.typeKeys
            data: angular.copy configuration.data
            autoSave: angular.copy configuration.autoSave
            actions: angular.copy configuration.actions
            handlesKey:   (key, element = null) ->
              if (element == null || @typeKeys == null)
                key in @keys
              else
                keys = []
                angular.forEach(@typeKeys, (values, type) ->
                  if(element.isInstanceOf(type))
                    keys = values
                )
                key in keys
            hasData:      (element) -> configuration.keys.some (key) -> element.ext.get(key)?
            isTemplateHidden: (element) ->
              if @hasOwnProperty('templateHidden')
                return @templateHidden
              else if angular.isDefined(@hideByDefault)
                return @templateHidden = @hideByDefault
              else if angular.isDefined(@hideIfNoData)
                return @templateHidden = @hideIfNoData and not @hasData(element)
              else
                return @templateHidden = false
            toggleTemplateHidden: (element) -> @templateHidden = not @isTemplateHidden(element)

          # assign values to the view
          angular.forEach configuration, (value, key) -> # Isn't this the wrong way around? value, key?
            return unless angular.isFunction(value) and key isnt "isAvailableFor"
            view[key] = value

          available.push(view)

        return $filter('orderBy')(available, 'position')

      createFakeOwner: (criteria) -> createFakeOwner(criteria)

    detailSections
  ]

  detailSectionsProvider
]
