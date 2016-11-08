angular.module('mc.core.ui.columns', ['mc.util.names']).provider 'columns', ['names', (names)->
  LONG_TEXT_BREAK = '__LONG_TEXT_BREAK__'

  columnsRegistry = {}

  defaultColumns = [
    {header: "Model Catalogue ID", value: "modelCatalogueId", classes: "col-md-4", show: true, href: 'href()'}
    {header: "Name",        value: 'name',                  classes: 'col-md-4', show: true, href: 'href()'}
    {header: "Type",        value: ((element)-> element.getElementTypeName()),  classes: 'col-md-4', show: true}
  ]

  columnsProvider = { LONG_TEXT_BREAK : LONG_TEXT_BREAK }

  columnsProvider.registerColumns = (type, cols) ->
    columnsRegistry[names.getPropertyNameFromType(type)] = angular.copy(cols)

  columnsProvider.setDefaultColumns = (cols) ->
    defaultColumns = angular.copy cols


  columnsProvider.$get = ->
    columns = (name, userDefaults) -> angular.copy (columnsRegistry[names.getPropertyNameFromType(name)] ? userDefaults ? defaultColumns)
    columns.LONG_TEXT_BREAK = LONG_TEXT_BREAK
    columns

  columnsProvider
]
