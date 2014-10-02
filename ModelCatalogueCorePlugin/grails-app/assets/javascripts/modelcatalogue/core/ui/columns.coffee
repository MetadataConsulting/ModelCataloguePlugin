angular.module('mc.core.ui.columns', []).provider 'columns', ->
  columns  = {}
  defaultColumns = [
    {header: "ID",          value: 'id',                    classes: 'col-md-2', show: true, href: 'href()'}
    {header: "Name",        value: 'name',                  classes: 'col-md-4', show: true, href: 'href()'}
    {header: "Description", value: 'description',           classes: 'col-md-6'}
    {header: "Type",        value: 'getElementTypeName()',  classes: 'col-md-6'}
  ]

  registerColumns = (type, cols) ->
    columns[type] = angular.copy(cols)

  setDefaultColumns = (cols) ->
    defaultColumns = angular.copy cols


  $get = ->
    (name, userDefaults) -> angular.copy (columns[name] ? userDefaults ? defaultColumns)

  columnsProvider = {
    $get:               $get
    registerColumns:    registerColumns
    setDefaultColumns:  setDefaultColumns
  }