angular.module('mc.core.ui.catalogueElementTreeview', ['mc.core.ui.catalogueElementTreeviewItem']).directive 'catalogueElementTreeview',  [-> {
    restrict: 'E'
    replace: true
    scope:
      element: '=?'
      list:    '=?'
      descend: '='
      repeat:  '=?'
      id:      '@'

    templateUrl: 'modelcatalogue/core/ui/catalogueElementTreeview.html'

    controller: ($scope, enhance, $log) ->
      listEnhancer = enhance.getEnhancer('list')

      $scope.mode     = if $scope.element then 'element' else 'list'
      $scope.id       = null if !$scope.id
      $scope.repeat   = false if !$scope.repeat
      $scope.children = []

      $scope.list    ?= listEnhancer.createEmptyList()

      nextFun = -> {then: (callback) -> callback($scope.list)}

      addItemsFromList = (list) ->
        for item in list.list
          $scope.children.push item
          $scope.hasMore  = list.total > $scope.children.length

      onListChange = (list) ->
        $log.info "onListChange: ", list
        $scope.children = []
        return if not list
        addItemsFromList(list)
        nextFun = list.next

      $scope.showMore = () ->
        return unless $scope.hasMore
        nextFun().then (list) ->
          addItemsFromList(list)
          nextFun = list.next

      if $scope.mode == 'list'
        onListChange $scope.list
        $scope.$watch 'list', onListChange

      $log.info 'scope for treeview: ', $scope

  }
]