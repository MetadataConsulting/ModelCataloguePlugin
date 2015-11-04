angular.module('mc.core.ui.columns', ['mc.util.names']).provider 'columns', ['names', (names)->
  columns  = {}
  defaultColumns = [
    {header: "Model Catalogue ID", value: "modelCatalogueId", classes: "col-md-4", show: true, href: 'modelCatalogueId'}
    {header: "Name",        value: 'name',                  classes: 'col-md-4', show: true, href: 'href()'}
    {header: "Type",        value: ((element)-> element.getElementTypeName()),  classes: 'col-md-4', show: true}
  ]

  columnsProvider = {}

  columnsProvider.registerColumns = (type, cols) ->
    columns[names.getPropertyNameFromType(type)] = angular.copy(cols)

  columnsProvider.setDefaultColumns = (cols) ->
    defaultColumns = angular.copy cols


  columnsProvider.$get = ->
    (name, userDefaults) -> angular.copy (columns[names.getPropertyNameFromType(name)] ? userDefaults ? defaultColumns)

  columnsProvider
]