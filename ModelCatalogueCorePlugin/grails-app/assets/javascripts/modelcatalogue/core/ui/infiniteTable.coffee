angular.module('mc.core.ui.infiniteTable', ['mc.core.ui.infiniteListCtrl', 'mc.core.ui.columnsSupportCtrl', 'ngAnimate']).directive 'infiniteTable',  [-> {
    restrict: 'E'
    replace: true
    scope:
      list: '='
      columns: '=?'

    templateUrl: 'modelcatalogue/core/ui/infiniteTable.html'

    controller: ['$scope', '$animate', '$window', '$controller', '$element', '$state', '$stateParams', ($scope, $animate, $window, $controller, $element, $state, $stateParams) ->
      angular.extend(this, $controller('infiniteListCtrl', {$scope: $scope, $element: $element}))
      angular.extend(this, $controller('columnsSupportCtrl', {$scope: $scope}))


      header = $element.find('.inf-table-header')
      body   = $element.find('.inf-table-body')
      spacer = $element.find('.inf-table-spacer')

      initialOffset = undefined

      updateOffset = (newOffset = angular.copy(header.offset())) ->
        return initialOffset  if newOffset.top == 0
        return initialOffset  if not header.is(':visible')
        initialOffset = angular.copy(header.offset())

      initialOffset = updateOffset()

      $scope.sortBy = (column) ->
        $state.go '.', {sort: column.sort.property, order: if $stateParams.order == 'desc' then undefined else 'desc'}

      $scope.getSortClass = (column) ->
        return 'glyphicon-sort' if column.sort.property != $scope.list.sort
        ret = "glyphicon-sort-by-#{if column.sort.type then column.sort.type else 'attributes'}"

        return ret if $scope.list.order == 'asc'
        ret + '-alt'

      windowEl = angular.element($window)
      handler = -> $scope.scroll = windowEl.scrollTop()
      windowEl.on('scroll', $scope.$apply.bind($scope, handler))

      updateHeader = (scroll) ->
        return if not initialOffset
        topPadding = angular.element('.navbar .container').outerHeight() + 1
        if scroll > initialOffset.top - topPadding
          header.css(position: 'fixed', width: body.width(), top: angular.element('.navbar .container').outerHeight() + 1)
          spacer.css('min-height': "#{header.outerHeight()}px")
        else
          updateOffset()
          header.css(position: 'static', width: body.width())
          spacer.css('min-height': "0px")


      $scope.$watch 'scroll', updateHeader
      $scope.$watch 'list', ->
        updateOffset()
        updateHeader(windowEl.scrollTop())

      windowEl.resize ->
        updateOffset()
        updateHeader(windowEl.scrollTop())

    ]
  }
]