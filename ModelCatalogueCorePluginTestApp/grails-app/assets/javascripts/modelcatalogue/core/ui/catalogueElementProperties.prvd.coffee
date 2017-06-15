###
  This module is confusing because it refers to two kinds of properties.
  The first is the kind of property like 'org.modelcatalogue.core.DataModel.isSynonymFor',
  which is a key of propertyConfigurationRegistry. Let us refer to these as property.

  The second is a property of the first property, like 'hidden'.
  The values/configurations of propertyConfigurationRegistry are themselves maps from this second kind of property to e.g. boolean
  values for hidden. Let us refer to these as property2.

  A listing of possible configurations (from bs/catalogueElementProperties.coffee):
  hidden: true
  tabDefinition: hideTab
  columns: [list of maps which are column sets]
  actions: [list which is an action description]
###
angular.module('mc.core.ui.catalogueElementProperties', ['mc.util.names']).provider 'catalogueElementProperties', ->

  ###
  List of properties in configurations that should be converted to functions when configurations are added.
  ###
  propertiesToConvert = ['hidden']

  ###
  A registry of property configurations. A map from names to
  ###
  propertyConfigurationRegistry = {}

  catalogueElementPropertiesProvider = {}

  ###
  Add property to list of properties which should be converted
  ###
  catalogueElementPropertiesProvider.convertProperty = (property) ->
    propertiesToConvert.push property

  ###
  A property is e.g. 'org.modelcatalogue.core.DataModel.isSynonymFor'
  a config is e.g. the map {hidden: true}
  ###
  catalogueElementPropertiesProvider.configureProperty = (property, config) ->
    for property2 in propertiesToConvert
      value = config[property2]
      if not angular.isFunction(value)
        if value?
          config[property2] = -> value


    old = propertyConfigurationRegistry[property] ? {}
    propertyConfigurationRegistry[property] = angular.extend(angular.copy(old), config)

  catalogueElementPropertiesProvider.$get = ['names', '$filter', (names, $filter) ->
    catalogueElementProperties = {}
    # shortcut for $filter, especially for columns
    catalogueElementProperties.filter = (name) -> $filter(name)
    ###
    If you get a configuration for e.g. 'org.modelcatalogue.core.DataModel.isSynonymFor'
      you will get that that of 'isSynonymFor' as well.
    ###
    catalogueElementProperties.getConfigurationFor = (name) ->
      propertyName          = names.getPropertyNameFromQualifier(name)
      return angular.extend({
        hidden: -> false
        actions: -> []
        label:  names.getNaturalName(propertyName)
      }, propertyConfigurationRegistry[propertyName] ? {}, propertyConfigurationRegistry[name] ? {})


    return catalogueElementProperties
  ]

  return catalogueElementPropertiesProvider