angular.module('mc.core.ui.decoratedList', ['mc.core.listEnhancer', 'mc.core.ui.columns']).directive 'decoratedList',  [-> {
    restrict: 'E'
    replace: true
    scope:
      list: '='
      columns: '=?'
      selection: '=?'
      actions: '=?'
      id: '@'
      stateless: '=?'
      pageParam: '@'

    templateUrl: 'modelcatalogue/core/ui/decoratedList.html'

    controller: ['$scope', 'columns', '$q', '$rootScope', '$state', '$stateParams' , ($scope, columns, $q, $rootScope, $state, $stateParams) ->
      pageParam = $scope.pageParam ? 'page'

      $scope.id = null if !$scope.id

      columnsDefined = $scope.columns?

      emptyList =
        list: []
        next: {size: 0}
        previous: {size: 0}
        total: 0
        empty: true
        source: 'directive'

      onListChange = (list) ->
        if !columnsDefined
          $scope.columns = columns(list.itemType)
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

        if not $state.current.abstract and not $scope.stateless
          newParams = angular.copy $stateParams
          newParams[pageParam] = list.currentPage
          if newParams[pageParam] == 1
            newParams[pageParam] = undefined
          $state.go '.', newParams

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



      $scope.list ?= emptyList

      if !columnsDefined
        $scope.columns = columns($scope.list.itemType)

      $scope.$watch 'list', onListChange

      $scope.evaluateClasses = (classes, element) ->
        if angular.isFunction(classes) then classes(element) else classes

      $scope.evaluateValue = (value, element) ->
        if angular.isFunction(value) then value(element) else $scope.$eval(value, element)

      $scope.showItem = (show, element) ->
        show = 'show()' if show == true
        if angular.isFunction(show) then show(element) else $scope.$eval(show, element)

      $scope.showEnabled = (show) ->
        show?

      $scope.getColumnsCount = () ->
        count = $scope.columns.length
        if $scope.hasSelection()
          count++
        if $scope.actions? and $scope.actions.length > 0
          count++
        count

      $scope.getActionsClass = () ->
        ' col-md-2'

      $scope.getActionClass = (action) ->
        return "btn-#{action.type}" if action.type?
        "btn-primary"

      $scope.performAction = (fn, element, list) ->
        $q.when(fn(element, list)).then (result) ->
          # only boolan value is the one we expect
          if result == true
            $scope.goto($scope.list.currentPage)


      $scope.$on '$stateChangeSuccess', (event, state, params) ->
        return if not $scope.list or not $scope.list.goto
        page = parseInt(params[pageParam] ? 1, 10)
        if page != $scope.list?.currentPage and not isNaN(page)
          $scope.goto page
    ]
  }
]