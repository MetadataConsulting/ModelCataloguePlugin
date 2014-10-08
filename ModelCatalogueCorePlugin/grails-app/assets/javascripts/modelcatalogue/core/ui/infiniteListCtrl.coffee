angular.module('mc.core.ui.infiniteListCtrl', ['mc.core.listEnhancer']).controller 'infiniteListCtrl',  ['$scope', 'columns', '$timeout', '$element', 'modelCatalogueApiRoot', 'actions', ($scope, columns, $timeout, $element, modelCatalogueApiRoot, actions) ->
  columnsDefined = $scope.columns?

  onListUpdate = (newList) ->
    if newList
      $scope.loading  = newList.empty and newList.total > 0
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
      $scope.loading  = false

  onListUpdate($scope.list)


  footerActions = actions.getActions($scope, actions.ROLE_LIST_FOOTER_ACTION)

  $scope.footerAction = if footerActions then footerActions[0]

  $scope.getFooterCentralIconClass = ->
    return 'fa-refresh fa-spin'                         if $scope.loading
    return "#{$scope.footerAction.icon} text-success"   if $scope.footerAction
    return 'fa-times-circle'                            if $scope.total == 0

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
    if $scope.list and $scope.list.itemType and newElement and newElement.isInstanceOf and newElement.isInstanceOf($scope.list.itemType) and url and $scope.list.base and (url.indexOf("#{modelCatalogueApiRoot}#{$scope.list.base}") >= 0 or "#{modelCatalogueApiRoot}#{$scope.list.base}".indexOf(url) >= 0 or url.indexOf("#{modelCatalogueApiRoot}#{$scope.list.base.replace('/relationships/', '/outgoing/')}") >= 0 or "#{modelCatalogueApiRoot}#{$scope.list.base.replace('/relationships/', '/outgoing/')}".indexOf(url) >= 0)
      $scope.total++
      $scope.elements.unshift newElement
      $scope.list.total = $scope.total if $scope.list


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
      $scope.list.total = $scope.total if $scope.list
      return


]