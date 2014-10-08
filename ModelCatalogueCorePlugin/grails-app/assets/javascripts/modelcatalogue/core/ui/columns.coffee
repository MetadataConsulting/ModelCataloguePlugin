angular.module('mc.core.ui.columns', []).provider 'columns', ->
  columns  = {}
  defaultColumns = [
    {header: "ID",          value: 'id',                    classes: 'col-md-2', show: true, href: 'href()'}
    {header: "Name",        value: 'name',                  classes: 'col-md-4', show: true, href: 'href()'}
    {header: "Type",        value: 'getElementTypeName()',  classes: 'col-md-6'}
  ]

  columnsProvider = {}

  columnsProvider.registerColumns = (type, cols) ->
    columns[type] = angular.copy(cols)

  columnsProvider.setDefaultColumns = (cols) ->
    defaultColumns = angular.copy cols


  columnsProvider.$get = ->
    (name, userDefaults) -> angular.copy (columns[name] ? userDefaults ? defaultColumns)

  columnsProvider