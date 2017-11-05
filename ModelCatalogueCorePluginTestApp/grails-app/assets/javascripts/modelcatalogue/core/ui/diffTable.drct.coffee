angular.module('mc.core.ui.diffTable', ['diff-match-patch', 'mc.util.ui.actions']).directive 'diffTable',  [-> {
  restrict: 'E'
  replace: true
  scope:
    elements: '='
  templateUrl: 'modelcatalogue/core/ui/diffTable.html'

  controller: ['$scope', 'names', 'catalogueElementProperties', 'security', 'enhance', '$filter', '$state', '$q', 'modelCatalogueApiRoot', '$http', 'actionRoleAccess', ($scope, names, catalogueElementProperties, security, enhance, $filter, $state, $q, modelCatalogueApiRoot, $http, actionRoleAccess)->
    $scope.actionRoleAccess = actionRoleAccess
    propExcludes = ['version', 'id', 'link', 'modelCatalogueId', 'elementType', 'incomingRelationships', 'outgoingRelationships', 'relationships', 'availableReports', 'archived', 'dateCreated', 'lastUpdated', 'versionCreated',  '__enhancedBy', 'defaultExcludes', 'updatableProperties']

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


    entityMap =
        "&": "&amp;"
        "<": "&lt;"
        ">": "&gt;"
        '"': '&quot;'
        "'": '&#39;'
        "/": '&#x2F;'

    escapeString    = (string) -> String(string).replace /[&<>"'\/]/g, (s) -> entityMap[s]
    unescapeString  = (string) ->
      ret = string
      for key, value of entityMap
        ret = String(ret).split(value).join(key)
      ret

    getLabel = (element) ->
      return element.getLabel()             if angular.isFunction(element.getLabel)
      return element.relation.getLabel()    if element.relation and angular.isFunction(element.relation.getLabel)
      return element.destination.getLabel() if element.destination and angular.isFunction(element.destination.getLabel)
      return ("  #{key}: #{value}" for key, value of element).join('\n')

    rows       = []
    rowsByKey  = []

    for element, i in $scope.elements
      for key, value of element when not (key in propExcludes)
        propCfg = catalogueElementProperties.getConfigurationFor("#{element.elementType}.#{key}")
        continue if propCfg.hidden(security)
        row = rowsByKey[key]
        unless row
          row = name: names.getNaturalName(key), values: [], hrefs: [], loaders: []
          rowsByKey[key] = row
          rows.push row

        if !value
          row.values[i] = ' ' # empty string won't trigger the diff
        else if key == 'downloadUrl'
          row.name = 'Content'
          row.values[i] = element.originalFileName
          row.hrefs[i] = modelCatalogueApiRoot + element.link + '/download'
          row.multiline = true
          row.html = false
          ((theRow, ii) ->
            theRow.loaders[ii] = ->
              $http(method: 'GET', url: theRow.hrefs[ii].replace('/download', '/content'), responseType: 'text').then (response) ->
                escapeString response.data
          )(row, i)

        else if angular.isString(value)
          row.values[i] = value
        else if angular.isNumber(value)
          row.values[i] = printNumber(value)
          row.multiline = true
        else if angular.isDate(value)
          row.values[i] = $filter('date')(value, 'medium')
          row.multiline = true
        else if angular.isFunction(value.getLabel)
          row.values[i] = value.getLabel()
          row.hrefs[i]  = value.href() if angular.isFunction(value.href)
        else if angular.isArray(value)
          row.values[i] = printArray value
          row.multiline = true
        else if angular.isFunction(value)
          if enhance.isEnhancedBy(value, 'listReference')
            row.values[i] = printNumber(value.total) + ' item(s)'
            row.multiline = true
            if value.total > 0
              row.loaders[i] = value
              row.hrefs[i]   = $state.href 'mc.resource.show.property', resource: element.getResourceName(), id: element.id, property: key




        else if angular.isObject(value)
          if not enhance.isEnhanced(value)
            row.values[i] = printObject value
            row.multiline = true
        else
          console.warn 'Cannot handle diff for', key, value




    handleChangesAndLoaders = (aRow) ->
      aRow.noChanges = []
      aRow.noChange = true
      aRow.hasLoaders = aRow.loaders[0]?
      referenceValue = aRow.values[0]
      for value, i in aRow.values.slice(1)
        aRow.noChanges[i+1] = angular.equals(referenceValue, value)
        aRow.noChange = aRow.noChange && angular.equals(referenceValue, value)
        aRow.hasLoaders = aRow.hasLoaders || aRow.loaders[i+1]?
      aRow.noChanges[0] = aRow.noChange
      aRow

    $scope.rows = []

    for row in rows when row.values.length > 0
      if row.values.length > 1
        handleChangesAndLoaders(row)
      if row.hasLoaders
        row.expand = ->
          @loading = true
          promises = []
          for loader in @loaders
            unless loader?
              promises.push $q.when(list: [])
            else
              promises.push loader(null, max: 100)

          $q.all(promises).then (results) =>

            @loading = false
            @hasLoaders = false

            newRow = values: [], loaders: [], multiline: true

            for result, j in results
              if enhance.isEnhancedBy(result, 'list')
                sorted = $filter('orderBy')((getLabel(item) for item in (result.list ? [])), 'toString()')
                newRow.values[j] = sorted.join('\n') + if result.total > 100 then '\n...' else ''
                newRow.values[j] = 'No items' unless newRow.values[j]
              else if angular.isString(result)
                newRow.values[j] = result ? 'Nothing'
              else if angular.equals(result, [])
                newRow.values[j] = 'No items'

            newRow = handleChangesAndLoaders(newRow)
            @noChanges = angular.copy newRow.noChanges
            @noChange  = newRow.noChange
            $scope.rows.splice($scope.rows.indexOf(@) + 1, 0, newRow)


      $scope.rows.push row
  ]
}]
