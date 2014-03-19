angular.module('mc.core.ui.decoratedList', ['mc.core.listEnhancer']).directive 'decoratedList',  [-> {
    restrict: 'E'
    replace: true
    scope:
      list: '='
      columns: '=?'
      selection: '=?'

    templateUrl: 'modelcatalogue/core/ui/decoratedList.html'

    controller: ['$scope', "$log" , ($scope, $log) ->
      $scope.hasSelection = () -> $scope.selection?

      $scope.allSelected = false

      $scope.updateSelectAll = (val) ->
        $scope.allSelected = val
        element._selected = $scope.allSelected for element in $scope.list.list
        $scope.updateSelection()

      $scope.updateSelection = () ->
        $log.info('update selection called')
        newSelection = []
        newSelection.push(element) for element in $scope.list.list when element._selected
        $scope.selection = newSelection

      nextOrPrev = (nextOrPrevFn) ->
        return if nextOrPrevFn.size == 0
        $scope.loading = true
        nextOrPrevFn().then (result) ->
          $scope.loading = false
          $scope.list = result

      hasNextOrPrev = (nextOrPrevFn) -> nextOrPrevFn.size? and nextOrPrevFn.size != 0

      $scope.previous       = -> nextOrPrev($scope.list.previous)
      $scope.next           = -> nextOrPrev($scope.list.next)
      $scope.hasPrevious    = -> hasNextOrPrev($scope.list.previous)
      $scope.hasNext        = -> hasNextOrPrev($scope.list.next)

      $scope.columns ?= [
        {header: 'Name', value: 'name', classes: 'col-md-4'}
        {header: 'Descripton', value: 'description', classes: 'col-md-8'}
      ]

      $scope.list ?=
        list: []
        next: {size: 0}
        previous: {size: 0}

      $scope.evaluateClasses = (classes, element) ->
        if angular.isFunction(classes) then classes(element) else classes

      $scope.evaluateValue = (value, element) ->
        if angular.isFunction(value) then value(element) else $scope.$eval(value, element)

    ]
  }
]