angular.module('mc.core.ui.infiniteListCtrl', ['mc.core.listEnhancer']).controller 'infiniteListCtrl',  ['$scope', 'columns', '$timeout', '$element', ($scope, columns, $timeout, $element) ->
  columnsDefined = $scope.columns?

  onListUpdate = (newList) ->
    $scope.loading  = false
    if newList
      $scope.elements = []
      for element in newList.list
        $scope.elements.push element
      $scope.next     = newList.next
      $scope.total    = newList.total
      if !columnsDefined
        $scope.columns = columns(newList.itemType)
    else
      $scope.elements = []
      $scope.next     = undefined
      $scope.total    = 0

  onListUpdate($scope.list)

  $scope.timeBetweenLoading = 1000

  $scope.lastLoadTime = new Date().getTime()

  $scope.isVisible = -> $element.is(':visible')

  $scope.loadMore = ->
    if $scope.total > $scope.elements.length
      $scope.loading = true

      currentTime = new Date().getTime()

      if $scope.next
        $timeout($scope.next, Math.max(1, $scope.lastLoadTime + $scope.timeBetweenLoading - currentTime)).then (result) ->
          $scope.lastLoadTime = new Date().getTime()
          $scope.loading = false
          if not result?.list?
            $scope.next = undefined
          else
            for element in result.list
              $scope.elements.push element
            $scope.next = result.next
    else
      $scope.loading = false



  $scope.$watch 'list', onListUpdate
]