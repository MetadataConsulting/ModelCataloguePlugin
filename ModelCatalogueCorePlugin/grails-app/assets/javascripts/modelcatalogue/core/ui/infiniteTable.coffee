angular.module('mc.core.ui.infiniteTable', ['mc.core.ui.infiniteListCtrl', 'mc.core.ui.columnsSupportCtrl', 'ngAnimate', 'mc.util.ui.sortable']).directive 'infiniteTable',  [-> {
    restrict: 'E'
    replace: true
    scope:
      list: '='
      reorder: '&?'
      isSortable: '=?'
      columns: '=?'
      transform: '&?'

    templateUrl: 'modelcatalogue/core/ui/infiniteTable.html'

    compile: (element, attrs) ->
      attrs.transform = '$element' unless attrs.transform

    controller: ['$scope', '$animate', '$window', '$controller', '$element', '$state', '$stateParams', '$q', '$timeout', ($scope, $animate, $window, $controller, $element, $state, $stateParams, $q, $timeout) ->
      angular.extend(this, $controller('infiniteListCtrl', {$scope: $scope, $element: $element}))

      header = $element.find('.inf-table-header')
      body   = $element.find('.inf-table-body')
      spacer = $element.find('.inf-table-spacer')


      windowEl = angular.element($window)
      handler = ($scope) -> $scope.scroll = windowEl.scrollTop()
      windowEl.on 'scroll', ->
        $scope.$apply (scope) -> handler(scope)

      handler($scope)

      initialOffset = undefined

      updateOffset = (newOffset = angular.copy(header.offset())) ->
        return initialOffset  if newOffset.top == 0
        return initialOffset  if not header.is(':visible')
        initialOffset = angular.copy(header.offset())

      initialOffset = updateOffset()

      $scope.sortBy = (column) ->
        $state.go '.', {sort: column.sort.property, order: if $scope.list.order == 'desc' then 'asc' else 'desc'}

      $scope.getSortClass = (column) ->
        return 'fa-sort'                                          if column.sort.property != $scope.list.sort
        return "fa-sort-#{column.sort.type}-#{$scope.list.order}" if column.sort.type
        return "fa-sort-#{$scope.list.order}"


      $scope.$$headerExpanded = false
      $scope.triggerHeaderExpanded = ->
        $scope.$$headerExpanded = !$scope.$$headerExpanded
        if header.css('position') == 'fixed'
          header.find('.contextual-actions button.dropdown-toggle').parent().removeClass('dropup')
        else
          header.find('.contextual-actions button.dropdown-toggle').parent().addClass('dropup')

        return false

      updateHeader = (scroll) ->
        header.css(width: body.width())
        if not initialOffset
          header.find('.contextual-actions button.dropdown-toggle').parent().addClass('dropup')
          return
        topPadding = angular.element('.navbar .container-fluid').outerHeight() + 1
        if scroll > initialOffset.top - topPadding
          header.css(position: 'fixed', top: angular.element('.navbar .container-fluid').outerHeight() + 1)
          spacer.css('min-height': "#{header.outerHeight()}px")
          header.find('.contextual-actions button.dropdown-toggle').parent().removeClass('dropup')
        else
          updateOffset()
          header.css(position: 'static')
          header.find('.contextual-actions button.dropdown-toggle').parent().addClass('dropup')
          spacer.css('min-height': "0px")



      checkLoadingPromise = $q.when true

      loadMoreIfNeeded = ->
        checkLoadingPromise = checkLoadingPromise.then ->
          windowBottom = $scope.scroll + windowEl.height()
          tableBodyBottom = body.offset().top + body.height()
          if $scope.isVisible() and windowBottom > tableBodyBottom - Math.max(600, windowEl.height())
            unless $scope.loading
              $q.when $scope.loadMore()
          $q.when true

      loadMoreIfNeeded()


      update = ->
        updateOffset()
        updateHeader(windowEl.scrollTop())
        loadMoreIfNeeded()

      $scope.$watch 'scroll', (scroll) ->
        updateHeader(scroll)
        loadMoreIfNeeded()

      $scope.$watch 'list', update
      $scope.$watch 'columns', update
      $scope.$watch 'loading', (loading) ->
        loadMoreIfNeeded() unless loading
      $scope.$watch 'filters', (-> loadMoreIfNeeded()), true

      windowEl.on 'resize', ->
        updateHeader(windowEl.scrollTop())


      $scope.$on 'infiniteTableRedraw', ->
        updateHeader()
        $timeout updateHeader, 100



      getRowAndIndexBefore = (tableRowIndex, originalRowAndIndex) ->
        return {row: null, index: -1} unless $scope.rows
        return {row: null, index: -1} unless $scope.rows.length > 0

        return {index: -1, row: null} if tableRowIndex == 0

        counter = tableRowIndex

        for row, i in $scope.rows
          continue unless $scope.isNotFiltered(row)
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
            row: $ui.item.scope().$parent.row
            index: 0

          for row, i in $scope.rows
            if row.$$hashKey == original.row.$$hashKey
              original.index = i
              break

          rowAndIndex = getRowAndIndexBefore $ui.item.index(), original



          return if original.index is rowAndIndex.index

          $scope.$apply (scope) ->
            $q.when(scope.reorder($row: original, $current: rowAndIndex))
            .then ->
              insertIndex = if original.index < rowAndIndex.index then rowAndIndex.index else rowAndIndex.index + 1

              scope.rows.splice(original.index, 1)
              scope.rows.splice(insertIndex, 0, original.row)

      windowEl.resize -> update

    ]
  }
]