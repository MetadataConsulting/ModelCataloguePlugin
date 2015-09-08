catalogueModule = angular.module('mc.core.catalogue', ['mc.util.security', 'mc.util.names', 'mc.util.rest', 'mc.core.modelCatalogueApiRoot'])

# facade service for easier access to many other services
catalogueModule.provider 'catalogue', ['names', (names) ->
  # registries
  typesMetadata = {}

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
    return undefined if not meta?
    meta[name]



  # provider
  catalogueProvider = {}

  catalogueProvider.getDefaultSort = (type) ->
    readMetadata type, 'sort'

  catalogueProvider.setDefaultSort = (type, sort) ->
    writeMetadata type, 'sort', sort

  catalogueProvider.setIcon = (type, icon) ->
    writeMetadata type, 'icon', icon

  catalogueProvider.getIcon = (type) ->
    readMetadata type, 'icon'

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


  # factory function
  catalogueProvider.$get = ['rest', 'modelCatalogueApiRoot', 'security', '$q', (rest, modelCatalogueApiRoot, security, $q)->
    catalogue = {}

    catalogue.getIcon = (type) ->
      catalogueProvider.getIcon(type)

    catalogue.getDeprecationWarning = (type) ->
      catalogueProvider.getDeprecationWarning(type) ? ->

    catalogue.isInstanceOf = (type, supertype) -> catalogueProvider.isInstanceOf(type, supertype)

    catalogue.getStatistics = ->
      rest method: 'GET', url: "#{modelCatalogueApiRoot}/dashboard"

    catalogue.isFilteredByDataModel = ->
      user = security.getCurrentUser()
      return false if user.dataModels.includes?.length != 1
      return false if user.dataModels.excludes?.length != 0
      return false if user.dataModels.unclassifiedOnly
      return true

    catalogue.select = (dataModel) ->
      deferred = $q.defer()
      rest(method: 'POST', url: "#{modelCatalogueApiRoot}/user/classifications", data: {includes: [dataModel]}).then (user)->
        security.getCurrentUser().dataModels = user.dataModels
        deferred.resolve(security.getCurrentUser())
      , (error) ->
        deferred.reject(error)

      deferred.promise

    catalogue
  ]

  catalogueProvider
]

# make catalogue property of the root scope (i.e. global property)
catalogueModule.run ['$rootScope', 'catalogue', '$state', 'security', ($rootScope, catalogue, $state, security) ->
  $rootScope.catalogue = catalogue

  $rootScope.$on 'newVersionCreated', (ignored, element) ->
    if angular.isFunction(element.isInstanceOf) and element.isInstanceOf('dataModel') and catalogue.isFilteredByDataModel()
      catalogue.select(element).then ->
        $state.go '.', {}, {reload: true}
        security.requireUser().then ->
          $rootScope.$broadcast 'redrawContextualActions'
]