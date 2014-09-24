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
      stateDriven: '=?'
      reports: '=?'
      pageParam: '@'
      sortParam: '@'
      orderParam: '@'
      maxParam: '@'

    templateUrl: 'modelcatalogue/core/ui/decoratedList.html'

    controller: ['$scope', 'columns', '$q', '$rootScope', '$state', '$stateParams' , ($scope, columns, $q, $rootScope, $state, $stateParams) ->
      pageParam   = $scope.pageParam ? 'page'
      sortParam   = $scope.sortParam ? 'sort'
      orderParam  = $scope.orderParam ? 'order'
      maxParam    = $scope.maxParam ? 'max'

      $scope.id = null if !$scope.id

      columnsDefined = $scope.columns?

      emptyList =
        list: []
        next: {size: 0}
        previous: {size: 0}
        total: 0
        empty: true
        source: 'directive'

      changeStateFor = (list, paramsOverrides) ->
        newParams = angular.copy $stateParams

        # initialization
        newParams[pageParam] = list.currentPage
        newParams[sortParam]  = list.sort  if list.sort
        newParams[orderParam] = list.order if list.order

        # overrides
        angular.extend newParams, paramsOverrides

        # normalization
        if newParams[pageParam] == 1 or isNaN(newParams[pageParam])
          newParams[pageParam] = undefined

        if newParams[sortParam] =='name'
          newParams[sortParam] = undefined

        if newParams[orderParam] =='asc'
          newParams[orderParam] = undefined

        if newParams[maxParam] == 10
          newParams[maxParam] = undefined

        $state.go '.', newParams

      onListChange = (list) ->
        $scope.currentMax = list?.page
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

        $scope.reports = list.availableReports

        if not $scope.stateDriven and not $state.current.abstract and not $scope.stateless
          changeStateFor list


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
        , -> $scope.loading = false

      $scope.goto = (page) ->
        return if $scope.loading

        if $scope.list and $scope.stateDriven and not $state.current.abstract and not $scope.stateless
          changeStateFor $scope.list, page: page
        else
          $scope.loading = true
          $scope.list.goto(page).then (result) ->
            $scope.loading = false
            $scope.list = result
          , -> $scope.loading = false

      $scope.setMax = (newMax) ->
        if $scope.list and $scope.stateDriven and not $state.current.abstract and not $scope.stateless
          changeStateFor $scope.list, max: newMax
        else
          $scope.list.reload(max: newMax).then (newList)->
            $scope.list = newList

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

      $scope.showEnabled = (show, element) ->
        return 'link' if show == true && angular.isFunction(element?.href)
        return show?

      $scope.getColumnsCount = () ->
        count = $scope.columns.length
        if $scope.hasSelection()
          count++
        if $scope.actions? and $scope.actions.length > 0
          count++
        count

      $scope.getActionsClass = () ->
        return ' col-md-1' if $scope.actions.length == 1
        return ' col-md-2' if $scope.actions.length  > 1

      $scope.getActionClass = (action) ->
        return "btn-#{action.type}" if action.type?
        "btn-primary"

      $scope.performAction = (fn, element, list) ->
        $q.when(fn(element, list)).then (result) ->
          # only boolean value is the one we expect
          if result == true
            $scope.goto($scope.list.currentPage)

      $scope.sortBy = (column) ->
        return if $scope.loading

        if $scope.list and $scope.stateDriven and not $state.current.abstract and not $scope.stateless
          changeStateFor $scope.list, sort: column.sort.property, order: if $scope.list.order == 'asc' then 'desc' else 'asc'
        else
          $scope.loading = true
          $scope.list.reload({
            sort: column.sort.property,
            order: if $scope.list.order == 'asc' then 'desc' else 'asc'
          }).then (result) ->
            $scope.loading = false
            $scope.list = result

      $scope.getSortClass = (column) ->
        return 'glyphicon-sort' if column.sort.property != $scope.list.sort
        ret = "glyphicon-sort-by-#{if column.sort.type then column.sort.type else 'attributes'}"

        return ret if $scope.list.order == 'asc'
        ret + '-alt'


      $scope.$on '$stateChangeSuccess', (event, state, params) ->
        return if not $scope.list or not $scope.list.goto
        page = parseInt(params[pageParam] ? 1, 10)
        if page != $scope.list?.currentPage and not isNaN(page)
          $scope.goto page
    ]
  }
]