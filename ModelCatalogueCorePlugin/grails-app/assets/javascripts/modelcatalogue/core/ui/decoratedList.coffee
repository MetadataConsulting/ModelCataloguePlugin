angular.module('mc.core.ui.decoratedList', ['mc.core.listEnhancer']).directive 'decoratedList',  [-> {
    restrict: 'E'
    replace: true
    scope:
      list: '='
      columns: '=?'
      selection: '=?'

    templateUrl: 'modelcatalogue/core/ui/decoratedList.html'

    controller: ['$scope' , ($scope) ->
      emptyList =
        list: []
        next: {size: 0}
        previous: {size: 0}
        total: 0
        empty: true
        source: 'directive'

      updatePages = (list) ->
        if list.total is 0
          $scope.pages = []
          $scope.hasMorePrevPages = false
          $scope.hasMoreNextPages = false
        else if list.total <= list.page
          $scope.pages = [1]
          $scope.hasMorePrevPages = false
          $scope.hasMoreNextPages = false
        else
          pages = []
          page = 1
          lowerTen = Math.floor(list.currentPage / 10) * 10
          upperTen = lowerTen + 10
          while (page - 1) * list.page <= list.total
            if lowerTen <= page  < upperTen
              pages.push page
            page++
          $scope.hasMorePrevPages = lowerTen != 0
          $scope.hasMoreNextPages = (Math.floor(list.total / list.page) + 1) >= upperTen
          $scope.pages = pages

      $scope.hasSelection = () -> $scope.selection?

      $scope.allSelected = false

      $scope.updateSelectAll = (val) ->
        $scope.allSelected = val
        element._selected = $scope.allSelected for element in $scope.list.list
        $scope.updateSelection()

      $scope.updateSelection = () ->
        newSelection = []
        newSelection.push(element) for element in $scope.list.list when element._selected
        $scope.selection = newSelection

      nextOrPrev = (nextOrPrevFn) ->
        return if nextOrPrevFn.size == 0
        return if $scope.loading
        $scope.loading = true
        nextOrPrevFn().then (result) ->
          $scope.loading = false
          $scope.list = result

      $scope.goto = (page) ->
        return if $scope.loading
        $scope.loading = true
        $scope.list.goto(page).then (result) ->
          $scope.loading = false
          $scope.list = result

      hasNextOrPrev = (nextOrPrevFn) -> nextOrPrevFn.size? and nextOrPrevFn.size != 0

      $scope.previous       = -> nextOrPrev($scope.list.previous)
      $scope.next           = -> nextOrPrev($scope.list.next)
      $scope.hasPrevious    = -> hasNextOrPrev($scope.list.previous)
      $scope.hasNext        = -> hasNextOrPrev($scope.list.next)

      $scope.columns ?= [
        {header: 'Name', value: 'name', classes: 'col-md-4', show: true}
        {header: 'Descripton', value: 'description', classes: 'col-md-8'}
      ]


      $scope.list ?= emptyList


      $scope.$watch 'list', updatePages

      $scope.evaluateClasses = (classes, element) ->
        if angular.isFunction(classes) then classes(element) else classes

      $scope.evaluateValue = (value, element) ->
        if angular.isFunction(value) then value(element) else $scope.$eval(value, element)

      $scope.showItem = (show, element) ->
        show = 'show()' if show == true
        if angular.isFunction(show) then show(element) else $scope.$eval(show, element)

      $scope.showEnabled = (show) ->
        show?

    ]
  }
]