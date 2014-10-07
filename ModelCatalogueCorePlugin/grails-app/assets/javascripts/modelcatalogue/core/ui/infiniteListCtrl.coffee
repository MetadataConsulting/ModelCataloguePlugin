angular.module('mc.core.ui.infiniteListCtrl', ['mc.core.listEnhancer']).controller 'infiniteListCtrl',  ['$scope', 'columns', '$timeout', '$element', 'modelCatalogueApiRoot', ($scope, columns, $timeout, $element, modelCatalogueApiRoot) ->
  originalList = undefined

  columnsDefined = $scope.columns?

  onListUpdate = (newList) ->
    originalList = newList
    $scope.loading  = newList.empty and newList.total > 0
    if newList
      $scope.elements = []
      for element in newList.list
        $scope.elements.push element
      $scope.next     = newList.next
      $scope.total    = newList.total
      $scope.reports  = newList.availableReports
      if !columnsDefined
        $scope.columns = columns(newList.itemType)
    else
      $scope.elements = []
      $scope.next     = undefined
      $scope.total    = 0
      $scope.reports  = []

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

  $scope.$on 'catalogueElementCreated', (ignored, newElement, url) ->
    if originalList and originalList.itemType and newElement and newElement.isInstanceOf and newElement.isInstanceOf(originalList.itemType) and url and originalList.base and (url.indexOf("#{modelCatalogueApiRoot}#{originalList.base}") >= 0 or "#{modelCatalogueApiRoot}#{originalList.base}".indexOf(url) >= 0 or url.indexOf("#{modelCatalogueApiRoot}#{originalList.base.replace('/relationships/', '/outgoing/')}") >= 0 or "#{modelCatalogueApiRoot}#{originalList.base.replace('/relationships/', '/outgoing/')}".indexOf(url) >= 0)
      $scope.total++
      $scope.elements.unshift newElement
      originalList.total = $scope.total if originalList


  $scope.$on 'catalogueElementDeleted', (ignored, deleted) ->
    indexOfDeleted = $scope.elements.indexOf(deleted)
    if indexOfDeleted == -1 and deleted.link
      for element, i in $scope.elements
        if element.link == deleted.link
          indexOfDeleted = i
          break

    if indexOfDeleted >= 0
      $scope.total--
      $scope.elements.splice indexOfDeleted, 1
      originalList.total = $scope.total if originalList
      return


]