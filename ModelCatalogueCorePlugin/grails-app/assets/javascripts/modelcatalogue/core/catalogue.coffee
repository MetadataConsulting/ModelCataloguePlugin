catalogueModule = angular.module('mc.core.catalogue', ['mc.util.names'])

# facade service for easier access to many other services
catalogueModule.provider 'catalogue', ['names', (names) ->
  # registries
  icons  = {}

  # provider
  catalogueProvider = {}
  catalogueProvider.setIcon = (type, icon) ->
    icons[names.getPropertyNameFromType(type)] = icon


  # factory function
  catalogueProvider.$get = ['rest', 'modelCatalogueApiRoot', (rest, modelCatalogueApiRoot)->
    catalogue = {}

    catalogue.getIcon = (type) ->
      icons[names.getPropertyNameFromType(type)]

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