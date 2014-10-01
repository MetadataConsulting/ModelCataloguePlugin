angular.module('mc.core.ui.infiniteListCtrl', ['mc.core.listEnhancer']).controller 'infiniteListCtrl',  ['$scope', 'columns', '$timeout', ($scope, columns, $timeout) ->
  columnsDefined = $scope.columns?

  onListUpdate = (newList) ->
    $scope.loading  = false
    if newList
      $scope.elements = newList.list
      $scope.next     = newList.next
      $scope.total    = newList.total
      if !columnsDefined
        $scope.columns = columns(newList.itemType)
    else
      $scope.elements = []
      $scope.next     = ->
      $scope.total    = 0

  onListUpdate($scope.list)

  $scope.timeBetweenLoading = 1000

  $scope.lastLoadTime = new Date().getTime()

  $scope.loadMore = ->
    if $scope.total > $scope.elements.length
      $scope.loading = true

      currentTime = new Date().getTime()

      $timeout($scope.next, Math.max(1, $scope.lastLoadTime + $scope.timeBetweenLoading - currentTime)).then (result) ->
        for element in result.list
          $scope.elements.push element
        $scope.next = result.next
        $scope.loading = false
        $scope.lastLoadTime = new Date().getTime()
    else
      $scope.loading = false



  $scope.$watch 'list', onListUpdate
]