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

    controller: ['$scope', 'enhance', '$stateParams', ($scope, enhance, $stateParams) ->
      listEnhancer = enhance.getEnhancer('list')

      $scope.mode     = if $scope.element then 'element' else 'list'
      $scope.id       = null  if !$scope.id
      $scope.repeat   = false if !$scope.repeat
      $scope.list    ?= listEnhancer.createEmptyList()

      nextFun = -> {then: (callback) -> callback($scope.list)}

      addItemsFromList = (list) ->
        return if list.$$children
        list.$$children = []
        for item in list.list
          $scope.list.$$children.push item

      onListChange = (list) ->
        return if not list
        addItemsFromList(list)
        nextFun = list.next

      $scope.showMore = () ->
        return unless $scope.list.total > $scope.list.$$children.length
        params = {}
        params.classification = $stateParams.classification if $stateParams.classification

        nextFun(null, params).then (list) ->
          addItemsFromList(list)
          nextFun = list.next

      if $scope.mode == 'list'
        onListChange $scope.list
        $scope.$watch 'list', onListChange
    ]
  }
]