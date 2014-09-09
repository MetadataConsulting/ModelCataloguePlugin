angular.module('mc.core.ui.columns', []).provider 'columns', ->
  columns  = {}
  defaultColumns = [
    {header: "ID",          value: 'id',          class: 'col-md-2', show: true}
    {header: "Name",        value: 'name',        class: 'col-md-4', show: true}
    {header: "Description", value: 'description', class: 'col-md-6'}
    {header: "Type", value: 'getElementTypeName()', class: 'col-md-6'}
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