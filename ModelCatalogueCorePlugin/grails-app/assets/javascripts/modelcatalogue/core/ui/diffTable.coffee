angular.module('mc.core.ui.diffTable', ['diff-match-patch']).directive 'diffTable',  [-> {
  restrict: 'E'
  replace: true
  scope:
    elements: '='
  templateUrl: 'modelcatalogue/core/ui/diffTable.html'

  controller: ['$scope', 'names', 'catalogueElementProperties', 'security', 'enhance', '$filter', '$state', ($scope, names, catalogueElementProperties, security, enhance, $filter, $state)->
    propExcludes = ['version', 'id', 'link', 'modelCatalogueId', 'elementType', 'incomingRelationships', 'outgoingRelationships', 'relationships', 'availableReports', 'downloadUrl', 'archived', 'dateCreated', 'lastUpdated', 'versionCreated',  '__enhancedBy', 'defaultExcludes', 'updatableProperties']

    printNumber = (number) -> '' + number

    printArray = (array) ->
      labels = []
      for item in array
        if angular.isString(item)
          labels.push value: item
        else if angular.isObject(item) and angular.isFunction(item.getLabel)
          labels.push value: item.getLabel()
      (label.value for label in $filter('orderBy')(labels, 'value')).join('\n')

    printObject = (obj)->
      entries = []
      for key, value of obj
        entries.push "#{key}: #{value}"

      printArray entries


    rows       = []
    rowsByKey  = []

    for element, i in $scope.elements
      for key, value of element when not (key in propExcludes)
        propCfg = catalogueElementProperties.getConfigurationFor("#{element.elementType}.#{key}")
        continue if propCfg.hidden(security)
        row = rowsByKey[key]
        unless row
          row = name: names.getNaturalName(key), values: [], hrefs: []
          rowsByKey[key] = row
          rows.push row

        if !value
          row.values[i] = ' ' # empty string won't trigger the diff
        else if angular.isString(value)
          row.values[i] = value
        else if angular.isNumber(value)
          row.values[i] = printNumber(value)
        else if angular.isDate(value)
          row.values[i] = $filter('date')(value, 'medium')
        else if angular.isFunction(value.getLabel)
          row.values[i] = value.getLabel()
          row.hrefs[i]  = value.href() if angular.isFunction(value.href)
        else if angular.isArray(value)
          row.values[i] = printArray value
        else if angular.isFunction(value)
          if enhance.isEnhancedBy(value, 'listReference')
            row.values[i] = printNumber(value.total) + ' item(s)'
            row.hrefs[i]  = $state.href 'mc.resource.show.property', resource: element.getResourceName(), id: element.id, property: key
        else if angular.isObject(value)
          if not enhance.isEnhanced(value)
            row.values[i] = printObject value
        else
          console.warn key, value




    $scope.rows = []

    for row in rows when row.values.length > 0
      if row.values.length > 1
        row.noChanges = []
        row.noChange = true
        referenceValue = row.values[0]
        for value, i in row.values.slice(1)
          row.noChanges[i+1] = angular.equals(referenceValue, value)
          row.noChange = row.noChange && angular.equals(referenceValue, value)
        row.noChanges[0] = row.noChange
      $scope.rows.push row
  ]
}]