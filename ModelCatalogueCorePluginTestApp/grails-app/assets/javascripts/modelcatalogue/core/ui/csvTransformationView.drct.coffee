angular.module('mc.core.ui.csvTransformationView', ['modelcatalogue.core.enhancersConf.catalogueElementEnhancer', 'modelcatalogue.core.enhancersConf.listReferenceEnhancer', 'modelcatalogue.core.enhancersConf.listEnhancer', 'mc.util.names', 'mc.util.messages', 'mc.core.ui.columns', 'mc.util.ui.actions', 'mc.util.ui.applicationTitle', 'ui.router', 'mc.core.ui.catalogueElementProperties', 'ngSanitize']).directive 'csvTransformationView',  [-> {
    restrict: 'E'
    replace: true
    scope:
      element: '='
      property: '=?'
      id: '@'

    templateUrl: 'modelcatalogue/core/ui/csvTransformationView.html'

    controller: ['$scope', ($scope) ->
      $scope.reset = ->
        $scope.columns = angular.copy $scope.element.columns
        $scope.columns.push source: null, destination: null, header: null if $scope.columns.length == 0

      $scope.hasChanged = ->
        return true if $scope.element.columns.length == 0 and $scope.columns.length > 1
        return true if $scope.element.columns.length >  0 and $scope.columns.length != $scope.element.columns.length
        for column, i in $scope.columns
          originalColumn = $scope.element.columns[i]
          continue if angular.equals column, originalColumn
          return true if column.header != originalColumn.header
          return true if column.source?.id != originalColumn.source?.id
          return true if column.destination?.id != originalColumn.destination?.id
        false

      $scope.removeColumn = (index) ->
        return if index == 0
        $scope.columns.splice index, 1

      $scope.addColumn = (index, current = $scope.columns[index]) ->
        return if $scope.isEmpty current
        $scope.columns.splice(index + 1, 0, {source: null, destination: null, header: null})
        $scope.lastAddedRow = index + 1

      $scope.addNewRowOnTab = ($event, index, last)->
        $scope.addColumn(index) if $event.keyCode == 9 and last

      $scope.hasSource = (definition) ->
        definition.source and not angular.isString(definition.source)

      $scope.isEmpty = (definition) ->
        !($scope.hasSource(definition) or (definition.destination and not angular.isString(definition.destination)))


      $scope.update = ->
        return if $scope.updating
        $scope.updating = true

        $scope.element.columns = []
        angular.forEach $scope.columns, (definition) ->
          $scope.element.columns.push definition if not $scope.isEmpty definition

        $scope.element.update().then (updated) ->
          $scope.element = updated
          $scope.reset()
          $scope.updating = false

      $scope.reset()

    ]
  }
]
