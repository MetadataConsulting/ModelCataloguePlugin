angular.module('mc.core.ui.infiniteListCtrl', ['mc.core.listEnhancer']).controller 'infiniteListCtrl',  ['$scope', 'columns', '$timeout', '$element', 'modelCatalogueApiRoot', 'actions', '$controller', 'names', 'security', 'enhance',($scope, columns, $timeout, $element, modelCatalogueApiRoot, actions, $controller, names, security, enhance) ->
  angular.extend(this, $controller('columnsSupportCtrl', {$scope: $scope}))

  columnsDefined = $scope.columns?

  getEnumerations = (enumeratedType) ->
    return '' if not enumeratedType
    return enumeratedType.description if not enumeratedType.enumerations
    return enumeratedType.description if not enumeratedType.enumerations.values
    enumerations = []
    enumerations.push "#{enumeration.key}: #{enumeration.value}" for enumeration in enumeratedType.enumerations.values
    enumerations.join('\n')




  getCellForColumn = (element, column) ->
    cell =
      value:    $scope.evaluateValue(column.value, element)
      href:     $scope.evaluateValue(column.href, element)
      classes:  $scope.evaluateClasses(column.classes, element)
    cell.type = 'link'  if cell.href and cell.value
    cell.type = 'html'  if not cell.type and cell.value and cell.value.indexOf('<') < cell.value.indexOf('>')
    cell.type = 'plain' if not cell.type and cell.value

    cell

  getPropertiesForElement = (element) ->
    properties = []
    if element and angular.isFunction(element.isInstanceOf)
      if element.isInstanceOf('catalogueElement') and not element.isInstanceOf('classification')
        properties.push label: 'Classifications', value: -> element.classifications
      if element.isInstanceOf('dataElement')
        properties.push label: 'Value Domain', value: -> element.valueDomain
      if element.isInstanceOf('valueDomain')
        properties.push label: 'Data Type', value: -> element.dataType
        properties.push label: 'Unit of Measure', value: -> element.unitOfMeasure
        properties.push label: 'Rule', value: -> element.rule
      if element.isInstanceOf('enumeratedType')
        properties.push label: 'Enumerations', value: getEnumerations

      if element and element.ext
        properties.push label: ''
        properties.push label: "#{element.getElementTypeName()} Metadata"

        if enhance.isEnhancedBy(element.ext, 'orderedMap')
          angular.forEach element.ext.values, (value) ->
            properties.push label: names.getNaturalName(value.key), value: -> value.value
        else
          angular.forEach element.ext, (value, key) ->
            properties.push label: names.getNaturalName(key), value: -> value
      if security.hasRole('ADMIN') and element and element.changed and element.latestVersion
        properties.push label: 'Type', value: -> element.type
        properties.push label: 'Changed Element', value: -> element.changed
        properties.push label: 'Root Element', value: -> element.latestVersion
        properties.push label: 'Author', value: -> element.author
        properties.push label: 'Property', value: -> element.property
        properties.push label: 'Old Value', value: -> if element.oldValue?.value then element.oldValue.value else element.oldValue
        properties.push label: 'New Value', value: -> if element.newValue?.value then element.newValue.value else element.newValue
    properties

  getRowForElement = (element) ->
    row = {element: element, properties: getPropertiesForElement(if element.relation then element.relation else element), sortable: $scope.isSortable, classesForStatus: $scope.classesForStatus(element), tail: [], $$expanded: $scope.$$expandAll}

    if $scope.columns
      row.head = getCellForColumn(element, $scope.columns[0])
      if $scope.columns.length > 1
        for column in $scope.columns.slice(1)
          row.tail.push getCellForColumn(element, column)

    if element.relation and element.ext
      row.properties.push label: ''
      row.properties.push label: 'Relationship Metadata'

      if enhance.isEnhancedBy(element.ext, 'orderedMap')
        angular.forEach element.ext.values, (value) ->
          row.properties.push label: names.getNaturalName(value.key), value: -> value.value
      else
        angular.forEach element.ext, (value, key) ->
          row.properties.push label: names.getNaturalName(key), value: -> value

    row

  addElements = (elements) ->
    return if not elements?.length
    $scope.elements ?= []
    $scope.rows     ?= []

    for element in elements
      $scope.elements.push element
      $scope.rows.push getRowForElement($scope.transform $element: element)



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
    return unless $scope.list
    return unless $scope.list.itemType
    return unless newElement
    return unless newElement.isInstanceOf and newElement.isInstanceOf($scope.list.itemType)
    return unless url
    return unless $scope.list.base
    return unless url.indexOf("#{modelCatalogueApiRoot}#{$scope.list.base}") >= 0 \
      or "#{modelCatalogueApiRoot}#{$scope.list.base}".indexOf(url) >= 0 \
      or url.indexOf("#{modelCatalogueApiRoot}#{$scope.list.base.replace('/relationships/', '/outgoing/')}") >= 0 \
      or "#{modelCatalogueApiRoot}#{$scope.list.base.replace('/relationships/', '/outgoing/')}".indexOf(url) >= 0 \
      or "#{modelCatalogueApiRoot}#{$scope.list.base.replace('/dataType/', '/enumeratedType/')}".indexOf(url) >= 0

    $scope.total++
    $scope.elements.unshift newElement
    $scope.rows.unshift getRowForElement($scope.transform $element: newElement)
    $scope.list.total = $scope.total if $scope.list


  $scope.$on 'catalogueElementDeleted', (ignored, deleted) ->
    # TODO: play nicely with transform attr
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

  $scope.$on 'actionPerformed', (_, __, result) ->
    result.then ->
      elements          = $scope.elements
      $scope.elements   = []
      $scope.rows       = []

      addElements elements

]