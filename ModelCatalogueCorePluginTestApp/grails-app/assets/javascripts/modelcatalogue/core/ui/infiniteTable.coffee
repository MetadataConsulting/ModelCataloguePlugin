angular.module('mc.core.ui.infiniteTable', ['mc.core.ui.infiniteListCtrl', 'mc.core.ui.columnsSupportCtrl', 'ngAnimate', 'mc.util.ui.sortable']).directive 'infiniteTable',  [-> {
    restrict: 'E'
    replace: true
    scope:
      list: '='
      reorder: '&?'
      isSortable: '=?'
      columns: '=?'
      transform: '&?'
      manualLoad: '=?'

    templateUrl: '/mc/core/ui/infiniteTable.html'

    compile: (element, attrs) ->
      attrs.transform = '$element' unless attrs.transform

    controller: ['$scope', '$animate', '$window', '$controller', '$element', '$state', '$stateParams', '$q', '$timeout', ($scope, $animate, $window, $controller, $element, $state, $stateParams, $q, $timeout) ->
      angular.extend(this, $controller('infiniteListCtrl', {$scope: $scope, $element: $element}))

      header = $element.find('.inf-table-header')
      body   = $element.find('.inf-table-body')
#      spacer = $element.find('.inf-table-spacer')

      $scope.pageSizeInit = 10
      $scope.pageSize = $scope.pageSizeInit
      $scope.pageSizeDelta = $scope.pageSizeInit
      $scope.pageOffset = 0
      $scope.manualLoad = true
      manualLoad = $scope.manualLoad
      windowEl = angular.element($window)
      handler = ($scope) -> $scope.scroll = windowEl.scrollTop()

      unless manualLoad # $scope.manualLoad == true
        windowEl.on 'scroll', ->
          $timeout -> handler($scope)

        handler($scope)
        windowEl.resize -> loadMoreIfNeeded

        $scope.$watch 'scroll', ->
          loadMoreIfNeeded()

      $scope.sortBy = (column) ->
        $state.go '.', {sort: column.sort.property, order: if $scope.list.order == 'desc' then 'asc' else 'desc'}

      $scope.getSortClass = (column) ->
        return 'fa-sort'                                          if column.sort.property != $scope.list.sort
        return "fa-sort-#{column.sort.type}-#{$scope.list.order}" if column.sort.type
        return "fa-sort-#{$scope.list.order}"

      checkLoadingPromise = $q.when true

      loadMoreIfNeeded = ->
        unless manualLoad # $scope.manualLoad == true
          checkLoadingPromise = checkLoadingPromise.then ->
            windowBottom = $scope.scroll + windowEl.height()
            tableBodyBottom = body.offset().top + body.height()
            if $scope.isVisible() and windowBottom > tableBodyBottom - Math.max(600, windowEl.height())
              unless $scope.loading
                $q.when $scope.loadMore()
            $q.when true

      loadMoreIfNeeded()

      $scope.$watch 'list', loadMoreIfNeeded
      $scope.$watch 'columns', loadMoreIfNeeded
      $scope.$watch 'loading', (loading) ->
        loadMoreIfNeeded() unless loading

      $scope.doLoadLess = ->
        $scope.pageSize -= $scope.pageSizeDelta
        $scope.incSize(-$scope.pageSizeDelta)

      $scope.doLoadMore = ->
        # $scope.manualLoad = false
        $scope.pageSize += $scope.pageSizeDelta
        $scope.incSize($scope.pageSizeDelta)

      $scope.doLoadPrevious = ->
        if ($scope.pageOffset > 0)
          $scope.pageOffset -= $scope.pageSize
          if ($scope.pageOffset < 0)
            $scope.pageSize += $scope.pageOffset
            $scope.pageOffset = 0
          $scope.loadPrevious($scope.pageSize)

      $scope.doLoadNext = ->
        if (($scope.pageOffset + $scope.pageSize) < $scope.list.total)
          $scope.pageOffset += $scope.pageSize
#          if ($scope.pageOffset )
          $scope.loadNext($scope.pageSize)

      getRowAndIndexBefore = (tableRowIndex, originalRowAndIndex) ->
        return {row: null, index: -1} unless $scope.rows
        return {row: null, index: -1} unless $scope.rows.length > 0
        return {index: -1, row: null} if tableRowIndex == 0

        counter = tableRowIndex
        for row, i in $scope.rows
          counter++ if i == originalRowAndIndex.index
          counter--
          counter-- if row.$$expanded
          if counter <= 0
            return {index: i, row: row}
        return { index: $scope.rows.length - 1, row: $scope.rows[-1] }

      $scope.sortableOptions =
        cursor: 'move'
        handle: '.handle'
        update: ($event, $ui) ->
          original =
            row: $ui.item.scope().row
            index: 0

          for row, i in $scope.rows
            if row.$$hashKey == original.row.$$hashKey
              original.index = i
              break

          rowAndIndex = getRowAndIndexBefore $ui.item.index(), original

          return if original.index is rowAndIndex.index

          $timeout ->
            $q.when($scope.reorder($row: original, $current: rowAndIndex))
            .then ->
              insertIndex = if original.index < rowAndIndex.index then rowAndIndex.index else rowAndIndex.index + 1

              $scope.rows.splice(original.index, 1)
              $scope.rows.splice(insertIndex, 0, original.row)

    ]
  }
]
