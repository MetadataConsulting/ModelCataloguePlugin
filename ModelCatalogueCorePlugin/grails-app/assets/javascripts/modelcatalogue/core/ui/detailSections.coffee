angular.module('mc.core.ui.detailSections', ['mc.core.catalogue']).provider 'detailSections', ['catalogueProvider', (catalogueProvider) ->

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
    @param template       template to be used for rendering
    @param position       position in the view (negative numbers goes before the description, positive after the description
    @param title          optional title to be displayed
    @param hideIfNoData   optional hide template when no data are in there
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

    # TODO should handle $$relationship

    configuration.isAvailableFor = (owner) ->
      return false if not owner
      return false if not owner.ext
      return false if not owner.ext.type == 'orderedMap'
      for type in configuration.types
        if type.indexOf('=>') > -1
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
            template:     configuration.template
            title:        configuration.title
            position:     configuration.position
            hideIfNoData: configuration.hideIfNoData?
            keys:         angular.copy configuration.keys
            data:         angular.copy configuration.data
            handlesKey:   (key) -> key in @keys
            hasData:      (element) -> configuration.keys.some (key) -> element.ext.get(key)?
            isTemplateHidden: (element) ->
              if @hasOwnProperty('templateHidden')
                return @templateHidden
              return @templateHidden = @hideIfNoData and not @hasData(element)
            toggleTemplateHidden: (element) -> @templateHidden = not @isTemplateHidden(element)

          # assign values to the view
          angular.forEach configuration, (value, key) ->
            return unless angular.isFunction(value) and key isnt "isAvailableFor"
            view[key] = value

          available.push(view)

        return $filter('orderBy')(available, 'position')

      createFakeOwner: (criteria) -> createFakeOwner(criteria)

    detailSections
  ]

  detailSectionsProvider
]
