angular.module('mc.core.ui.infiniteTable', ['mc.core.ui.infiniteListCtrl', 'mc.core.ui.columnsSupportCtrl', 'ngAnimate']).directive 'infiniteTable',  [-> {
    restrict: 'E'
    replace: true
    scope:
      list: '='
      columns: '=?'

    templateUrl: 'modelcatalogue/core/ui/infiniteTable.html'

    controller: ['$scope', '$animate', '$window', '$controller', '$element', '$state', '$stateParams', ($scope, $animate, $window, $controller, $element, $state, $stateParams) ->
      angular.extend(this, $controller('infiniteListCtrl', {$scope: $scope}))
      angular.extend(this, $controller('columnsSupportCtrl', {$scope: $scope}))


      header = $element.find('.inf-table-header')
      body   = $element.find('.inf-table-body')
      spacer = $element.find('.inf-table-header-spacer')

      initialOffset = angular.copy(header.offset())

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
        topPadding = angular.element('.navbar .container').outerHeight() + 1
        if scroll > initialOffset.top - topPadding
          header.css(position: 'fixed', width: body.width(), top: angular.element('.navbar .container').outerHeight() + 1)
          spacer.height(header.height())
        else
          header.css(position: 'static', width: body.width())
          spacer.height(0)

      $scope.$watch 'scroll', updateHeader

      windowEl.resize updateHeader

    ]
  }
]