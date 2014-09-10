catalogueModule = angular.module('mc.core.catalogue', ['mc.util.names', 'mc.util.rest', 'mc.core.modelCatalogueApiRoot'])

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
  catalogueProvider.setIcon = (type, icon) ->
    writeMetadata type, 'icon', icon

  catalogueProvider.setInstanceOf = (type, supertype) ->
    supertypes = angular.copy(readMetadata(supertype, 'supertypes') ? [])
    supertypes.push supertype
    writeMetadata type, 'supertypes', supertypes, true


  # factory function
  catalogueProvider.$get = ['rest', 'modelCatalogueApiRoot', (rest, modelCatalogueApiRoot)->
    catalogue = {}

    catalogue.getIcon = (type) ->
      readMetadata type, 'icon'

    catalogue.isInstanceOf = (type, supertype) ->
      type = names.getPropertyNameFromType(type)
      supertype = names.getPropertyNameFromType(supertype)
      return true if type == supertype
      supertypes = readMetadata(type, 'supertypes')
      return false if not supertypes
      for theSuperType in supertypes
        return true if theSuperType == supertype
      return false

    catalogue.getStatistics = ->
      rest method: 'GET', url: "#{modelCatalogueApiRoot}/dashboard"

    catalogue
  ]

  catalogueProvider
]

# make catalogue property of the root scope (i.e. global property)
catalogueModule.run ['$rootScope', 'catalogue', ($rootScope, catalogue) ->
  $rootScope.catalogue = catalogue
]