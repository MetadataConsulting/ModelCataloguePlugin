angular.module('mc.core.ui.catalogueElementProperties', ['mc.util.names']).provider 'catalogueElementProperties', ->
  propertiesToConvert = ['hidden']

  propertyConfigurationRegistry = {}

  catalogueElementPropertiesProvider = {}

  catalogueElementPropertiesProvider.convertProperty = (name) ->
    propertiesToConvert.push name

  catalogueElementPropertiesProvider.configureProperty = (name, config) ->
    for property in propertiesToConvert
      value = config[property]
      if not angular.isFunction(value)
        if value?
          config[property] = -> value


    old = propertyConfigurationRegistry[name] ? {}
    propertyConfigurationRegistry[name] = angular.extend(angular.copy(old), config)

  catalogueElementPropertiesProvider.$get = ['names', '$filter', (names, $filter) ->
    catalogueElementProperties = {}
    # shortcut for $filter, especially for columns
    catalogueElementProperties.filter = (name) -> $filter(name)
    catalogueElementProperties.getConfigurationFor = (name) ->
      propertyName          = names.getPropertyNameFromQualifier(name)
      defaultPropertyConfig = angular.extend({
        hidden: -> false
        actions: -> []
        label:  names.getNaturalName(propertyName)
      }, propertyConfigurationRegistry[propertyName] ? {})
      propertyConfig = propertyConfigurationRegistry[name] ? defaultPropertyConfig
      angular.extend(angular.copy(defaultPropertyConfig), propertyConfig)

    catalogueElementProperties
  ]

  catalogueElementPropertiesProvider