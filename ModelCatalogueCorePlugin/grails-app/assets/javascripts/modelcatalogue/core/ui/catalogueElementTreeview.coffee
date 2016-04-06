class CatalogueElementTreeview
  constructor: ($scope, enhance, $stateParams, $rootScope, $element, $attrs, rx) ->
    selected = undefined

    @id = $scope.id

    @select = (element) ->
      if selected and selected isnt element
        selected.$$active = false
      selected = element
      element.$$active = true
      $scope.onSelect({$element: element}) if angular.isFunction($scope.onSelect)



    # regular controller code
    if $attrs.hasOwnProperty('noResize')
      $element.addClass('no-resize')

    setLastListToRootScope = (newList) ->
      return unless newList
      $rootScope.$$lastTreeLists ?= {}
      $rootScope.$$lastTreeLists[newList.base] = newList


    getLastListFromRootScope = (base) ->
      return undefined unless base
      return undefined if not $rootScope.$$lastTreeLists
      return $rootScope.$$lastTreeLists[base]


    listEnhancer = enhance.getEnhancer('list')

    $scope.mode     = if $scope.element then 'element' else 'list'
    $scope.id       = null  if !$scope.id
    $scope.repeat   = false if !$scope.repeat
    $scope.list    ?= listEnhancer.createEmptyList()

    nextFun = -> {then: (callback) -> callback($scope.list)}

    addItemsFromList = (list) ->
      return if list.$$children
      return if not list.list
      list.$$children = []
      for item in list.list
        cachedChild  = if list.$$cachedChildren then list.$$cachedChildren[item.link]
        cachedChild ?= {}
        delete cachedChild.$$relationship
        if cachedChild.$$collapsed
          cachedChild.$$resetHelperProperties() if angular.isFunction(cachedChild.$$resetHelperProperties)
        else
          cachedChild.$$loadChildren() if angular.isFunction(cachedChild.$$loadChildren)
        $scope.list.$$children.push(angular.extend(cachedChild, item))

    onListChange = (list, oldList) ->
      return if not list

      if oldList
        list.$$cachedChildren = oldList.$$cachedChildren ? {}

        for child in oldList.$$children
          delete child.$$relationship
          list.$$cachedChildren[child.link] = child

      $scope.$$showingMore = false

      addItemsFromList(list)
      nextFun = list.next
      setLastListToRootScope(list)
      $scope.$evalAsync ->
        loadMoreIfNeeded()


    loadMoreIfNeeded = ->
      return if not $scope.list.total > $scope.list.$$children.length
      return if $scope.$$showingMore
      showMore = $element.find('.catalogue-element-treeview-root-show-more')
      return if showMore.hasClass '.hide'
      root = $element.find('.catalogue-element-treeview-root-list-root')
      if showMore.offset()?.top < root.offset()?.top + 3 * root.height()
        $scope.showMore()

    $scope.showMore = () ->
      return if $scope.$$showingMore
      return if not nextFun
      $scope.$$showingMore = true
      return unless $scope.list.total > $scope.list.$$children.length
      params = {}
      params.classification = $stateParams.classification if $stateParams.classification

      nextFun(null, params).then (list) ->
        addItemsFromList(list)
        nextFun = list.next
        $scope.$$showingMore = false
        loadMoreIfNeeded()


    if $scope.mode == 'list'
      onListChange $scope.list, getLastListFromRootScope($scope.list?.base)
      $scope.$watch 'list', onListChange

      refreshList = ->
        $scope.list.reload(status: $stateParams.status, toplevel: true).then (newList) ->
          $scope.list = newList

      DEBOUNCE_TIME = 500

      $scope.$eventToObservable('catalogueElementCreated').debounce(DEBOUNCE_TIME).subscribe refreshList
      $scope.$eventToObservable('catalogueElementUpdated').debounce(DEBOUNCE_TIME).subscribe refreshList
      $scope.$eventToObservable('catalogueElementDeleted').debounce(DEBOUNCE_TIME).subscribe refreshList
      $scope.$eventToObservable('newVersionCreated').debounce(DEBOUNCE_TIME).subscribe refreshList
      $scope.$eventToObservable('expandTreeview').debounce(DEBOUNCE_TIME).subscribe (args) ->
        path = args[1]
        # TODO: expand tree


      $element.find('.catalogue-element-treeview-root-list-root').on 'scroll', loadMoreIfNeeded


angular.module('mc.core.ui.catalogueElementTreeview', ['mc.core.ui.catalogueElementTreeviewItem', 'rx']).directive 'catalogueElementTreeview',  [-> {
    restrict: 'E'
    replace: true
    scope:
      element: '=?'
      list:    '=?'
      descend: '='
      repeat:  '=?'
      prefetch:'=?'
      id:      '@'
      onSelect: '&?'

    templateUrl: '/mc/core/ui/catalogueElementTreeview.html'

    controllerAs: 'treeview'

    controller: ['$scope', 'enhance', '$stateParams', '$rootScope', '$element', '$attrs', 'rx', CatalogueElementTreeview]
  }
]
