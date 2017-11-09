catalogueModule = angular.module('mc.core.catalogue', ['mc.util.security', 'mc.util.names', 'mc.util.rest', 'mc.core.modelCatalogueApiRoot'])

# facade service for easier access to many other services
catalogueModule.provider 'catalogue', ['names', (names) ->
  # registries
  typesMetadata = {}
  contentTests = []

  # helper functions

  writeMetadata = (type, name, value, isArray = false) ->
    type = names.getPropertyNameFromType(type)
    typesMetadata[type] ?= {}
    if isArray
      typesMetadata[type][name] ?= []
      if angular.isArray value
        typesMetadata[type][name] = value
      else
        typesMetadata[type][name].push value
    else
      typesMetadata[type][name] = value

  readMetadata = (type, name) ->
    type = names.getPropertyNameFromType(type)
    meta = typesMetadata[type]
    return undefined unless meta?
    meta[name]



  # provider
  catalogueProvider = {}

  catalogueProvider.getDefaultSort = (type) ->
    readMetadata type, 'sort'

  catalogueProvider.setDefaultSort = (type, sort) ->
    writeMetadata type, 'sort', sort

  catalogueProvider.getDefaultXslt = (type) ->
    readMetadata type, 'xslt'

  catalogueProvider.setDefaultXslt = (type, xslt) ->
    writeMetadata type, 'xslt', xslt

  catalogueProvider.setIcon = (type, icon) ->
    writeMetadata type, 'icon', icon

  catalogueProvider.getIcon = (type) ->
    readMetadata type, 'icon'

  catalogueProvider.setPlural = (type, plural) ->
    writeMetadata type, 'plural', plural

  catalogueProvider.getPlural = (type) ->
    readMetadata(type, 'plural') ? "#{names.getNaturalName(type)}s"

  catalogueProvider.setDeprecationWarning = (type, warningFunction) ->
    writeMetadata type, 'deprecationWarning', warningFunction

  catalogueProvider.getDeprecationWarning = (type) ->
    readMetadata type, 'deprecationWarning'

  catalogueProvider.setInstanceOf = (type, supertype) ->
    supertypes = angular.copy(readMetadata(supertype, 'supertypes') ? [])
    supertypes.push supertype
    writeMetadata type, 'supertypes', supertypes, true

  catalogueProvider.isInstanceOf = (type, supertype) ->
    return false if not type
    type = names.getPropertyNameFromType(type)
    supertype = names.getPropertyNameFromType(supertype)
    return true if type == supertype
    supertypes = readMetadata(type, 'supertypes')
    return false if not supertypes
    for theSuperType in supertypes
      return true if theSuperType == supertype
    return false


  ###
  # Adds test which can determine if element could be included in list provided.
  #
  # @param test function which handles 2 or 3 parameters (in particular order)
  # * list - required - object which should be enhanced by listEnhancer or listReferenceEnhancer.
  # * element - required - the element which is tested to be possible included in the list
  # * extra - optional - extra context available such as
  #   * owner - owning element of the list reference enhancer
  #   * url - the url where the newly created element were posted (useful with 'catalogueElementCreated' event)
  #
  # * returns number from 0 to 1 where 0 is no probability of being contained by the list and 1 means
  #     that the element is certainly contained by the list
  ###
  catalogueProvider.addContainsCandidateTest = (test) ->
    contentTests.push test


  # factory function
  catalogueProvider.$get = ['rest', 'modelCatalogueApiRoot', '$injector', (rest, modelCatalogueApiRoot, $injector)->
    catalogue = {}

    catalogue.getIcon = (type) ->
      catalogueProvider.getIcon(type)

    catalogue.getPlural = (type) ->
      catalogueProvider.getPlural(type)

    catalogue.getDefaultXslt = (type) ->
      catalogueProvider.getDefaultXslt(type)

    catalogue.getDeprecationWarning = (type) ->
      catalogueProvider.getDeprecationWarning(type) ? ->

    catalogue.isInstanceOf = (type, supertype) -> catalogueProvider.isInstanceOf(type, supertype)

    ###
    # Tests if the element is contained in particular list.
    # * list - required - object which should be enhanced by listEnhancer or listReferenceEnhancer.
    # * element - required - the element which is tested to be possible included in the list
    # * extra - optional - extra context available such as
    #   * owner - owning element of the list reference enhancer
    #   * url - the url where the newly created element were posted (useful with 'catalogueElementCreated' event)
    #
    # * returns number from 0 to 1 where 0 is no probability of being contained by the list and 1 means
    #     that the element is certainly contained by the list
    ###
    catalogue.isContentCandidate = (list, element, extra) ->
      for test in contentTests
        result = $injector.invoke(test, undefined , list: list, element: element, extra: extra ? {})
        return 1 if result == true
        return result if angular.isNumber(result) and result > 0
      return false

    catalogue
  ]

  catalogueProvider
]

# make catalogue property of the root scope (i.e. global property)
catalogueModule.run ['$rootScope', 'catalogue', ($rootScope, catalogue) ->
  $rootScope.catalogue = catalogue
]
