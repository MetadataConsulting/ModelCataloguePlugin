angular.module('mc.core.ui.infiniteListCtrl', ['mc.core.listEnhancer']).controller 'infiniteListCtrl',  ['$scope', 'columns', '$timeout', '$element', 'modelCatalogueApiRoot', 'actions', '$controller', ($scope, columns, $timeout, $element, modelCatalogueApiRoot, actions, $controller) ->
  angular.extend(this, $controller('columnsSupportCtrl', {$scope: $scope}))

  columnsDefined = $scope.columns?

  getCellForColumn = (element, column) ->
    cell =
      value:    $scope.evaluateValue(column.value, element)
      href:     $scope.evaluateValue(column.href, element)
      classes:  $scope.evaluateClasses(column.classes, element)
    cell.type = 'link'  if cell.href and cell.value
    cell.type = 'html'  if not cell.type and cell.value and cell.value.indexOf('<') < cell.value.indexOf('>')
    cell.type = 'plain' if not cell.type and cell.value

    cell

  getRowForElement = (element) ->
    row = {element: element, classesForStatus: $scope.classesForStatus(element), tail: []}
    if $scope.columns
      row.head = getCellForColumn(element, $scope.columns[0])
      if $scope.columns.length > 1
        for column in $scope.columns.slice(1)
          row.tail.push getCellForColumn(element, column)
    row

  addElements = (elements) ->
    $scope.elements ?= []
    $scope.rows     ?= []

    for element in elements
      $scope.elements.push element
      $scope.rows.push getRowForElement(element)



  onListUpdate = (newList) ->
    if newList
      if !columnsDefined
        $scope.columns = columns(newList.itemType)
      $scope.loading  = newList.empty and newList.total > 0
      $scope.rows     = []
      $scope.elements = []
      addElements(newList.list)
      $scope.next     = newList.next
      $scope.total    = newList.total
      $scope.reports  = newList.availableReports
    else
      $scope.rows     = []
      $scope.elements = []
      $scope.next     = undefined
      $scope.total    = 0
      $scope.reports  = []
      $scope.loading  = false

  onListUpdate($scope.list)


  footerActions = actions.getActions($scope, actions.ROLE_LIST_FOOTER_ACTION)


  $scope.isNotFiltered = (row) ->
    for column, i in $scope.columns
      filter = $scope.filters[column.header]
      continue if not filter
      if i == 0
        return false if row.head.value?.toLowerCase().indexOf(filter.toLowerCase()) == -1
      else
        return false if row.tail[i - 1].value?.toLowerCase().indexOf(filter.toLowerCase()) == -1
    return true

  $scope.isFiltered = ->
    for column in $scope.columns
      filter = $scope.filters[column.header]
      continue if not filter
      return true
    return false

  $scope.initFilters = (columns) ->
    $scope.filters ?= {}
    for column in columns
      $scope.filters[column.header] = ''

  $scope.initFilters($scope.columns ? [])

  $scope.footerAction = if footerActions then footerActions[0]

  $scope.getFooterCentralIconClass = ->
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
            addElements(result.list)
            $scope.next = result.next
    else
      $scope.loading = false



  $scope.$watch 'list', onListUpdate

  $scope.$on 'catalogueElementCreated', (ignored, newElement, url) ->
    if $scope.list and $scope.list.itemType and newElement and newElement.isInstanceOf and newElement.isInstanceOf($scope.list.itemType) and url and $scope.list.base and (url.indexOf("#{modelCatalogueApiRoot}#{$scope.list.base}") >= 0 or "#{modelCatalogueApiRoot}#{$scope.list.base}".indexOf(url) >= 0 or url.indexOf("#{modelCatalogueApiRoot}#{$scope.list.base.replace('/relationships/', '/outgoing/')}") >= 0 or "#{modelCatalogueApiRoot}#{$scope.list.base.replace('/relationships/', '/outgoing/')}".indexOf(url) >= 0)
      $scope.total++
      $scope.elements.unshift newElement
      $scope.rows.unshift getRowForElement(newElement)
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
      $scope.rows.splice indexOfDeleted, 1
      $scope.list.total = $scope.total if $scope.list
      return

  $scope.$on 'actionPerformed', (_,__, result) ->
    result.then ->
      elements          = $scope.elements
      $scope.elements   = []
      $scope.rows       = []

      addElements elements

]