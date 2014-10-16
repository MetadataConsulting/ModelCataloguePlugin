angular.module('mc.core.ui.infiniteTable', ['mc.core.ui.infiniteListCtrl', 'mc.core.ui.columnsSupportCtrl', 'ngAnimate']).directive 'infiniteTable',  [-> {
    restrict: 'E'
    replace: true
    scope:
      list: '='
      columns: '=?'
      transform: '&?'

    templateUrl: 'modelcatalogue/core/ui/infiniteTable.html'

    compile: (element, attrs) ->
      attrs.transform = '$element' unless attrs.transform

    controller: ['$scope', '$animate', '$window', '$controller', '$element', '$state', '$stateParams', '$q', ($scope, $animate, $window, $controller, $element, $state, $stateParams, $q) ->
      angular.extend(this, $controller('infiniteListCtrl', {$scope: $scope, $element: $element}))

      header = $element.find('.inf-table-header')
      body   = $element.find('.inf-table-body')
      spacer = $element.find('.inf-table-spacer')


      windowEl = angular.element($window)
      handler = -> $scope.scroll = windowEl.scrollTop()
      windowEl.on('scroll', $scope.$apply.bind($scope, handler))

      handler()

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
        topPadding = angular.element('.navbar .container').outerHeight() + 1
        if scroll > initialOffset.top - topPadding
          header.css(position: 'fixed', top: angular.element('.navbar .container').outerHeight() + 1)
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

      $scope.$on 'infiniteTableRedraw', ->
        updateHeader()

      windowEl.resize -> update

    ]
  }
]