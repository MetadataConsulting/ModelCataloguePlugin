class CatalogueElementTreeview
  constructor: ($scope, enhance, $stateParams, $rootScope, $element, $attrs, rx, TreeviewNodeFactory, DescendPath) ->
    "ngInject"
    selected = undefined
    treeview = @

    @id = $scope.id

    @select = (node, descendPath) ->
      if selected and selected isnt node
        selected.active = false
      selected = node
      node.active = true
      $scope.onSelect({$element: node.item, $descendPath: descendPath}) if angular.isFunction($scope.onSelect)

    @getNodeId = (link) -> "#{@id}:#{link}"
    @getDescend = -> $scope.descend



    # regular controller code
    if $attrs.hasOwnProperty('noResize')
      $element.addClass('no-resize')

    setLastListToRootScope = (newList) ->
      return unless newList
      $rootScope.$$lastTreeLists ?= {}
      $rootScope.$$lastTreeLists[newList.base] = newList


    getLastListFromRootScope = (base) ->
      return undefined unless base
      return undefined unless $rootScope.$$lastTreeLists
      return $rootScope.$$lastTreeLists[base]


    listEnhancer = enhance.getEnhancer('list')

    # Collect element from List
    $scope.mode = if $scope.element then 'element' else 'list'
    $scope.id = null  if !$scope.id
    $scope.list ?= listEnhancer.createEmptyList()
    $scope.path = {segments: if $stateParams.path then $stateParams.path.split('-') else []}
    $scope.createDescendPath = (id) -> new DescendPath([id])

    nextFun = -> {then: (callback) -> callback($scope.list)}

    addItemsFromList = (list) ->
      return if list.$$children
      return if not list.list
      list.$$children = []
      for item in list.list
        node = TreeviewNodeFactory.get(treeview.getNodeId(item.link))
        if node?.collapsed
          node.reset() if angular.isFunction(node.reset)
        else
          node.loadChildren() if angular.isFunction(node?.loadChildren)
        $scope.list.$$children.push(angular.extend(node?.item ? {}, item))

    onListChange = (list) ->
      return if not list

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
        args = arguments[0]
        if $scope.element || $scope.list.total == 1
          element = $scope.element || $scope.list.list[0]
          if args.length >= 2 and args[0].hasOwnProperty('name') and args[0].name == 'catalogueElementUpdated'
            other = args[2]
            if element?.link != other?.link
              return


        $scope.list.reload(status: $stateParams.status, toplevel: true).then (newList) ->
          $scope.list = newList

      DEBOUNCE_TIME = 500

      $scope.$eventToObservable('catalogueElementCreated').debounce(DEBOUNCE_TIME).subscribe refreshList
      $scope.$eventToObservable('catalogueElementUpdated').debounce(DEBOUNCE_TIME).subscribe refreshList
      $scope.$eventToObservable('catalogueElementDeleted').debounce(DEBOUNCE_TIME).subscribe refreshList
      $scope.$eventToObservable('newVersionCreated').debounce(DEBOUNCE_TIME).subscribe refreshList


      unless $stateParams.path
        $scope.$eventToObservable('expandTreeview').debounce(DEBOUNCE_TIME).subscribe (args) ->
          $scope.path.segments = args[1]

      $element.find('.catalogue-element-treeview-root-list-root').on 'scroll', loadMoreIfNeeded
angular.module('modelcatalogue.core.components.catalogueElementTreeview').directive 'catalogueElementTreeview',  [-> {
    restrict: 'E'
    replace: true
    scope:
      element: '=?'
      list:    '=?' # =: bidirectional, not using {{}} ?: optional
      prefetch:'=?'
      id:      '@'
      descend: '@' # one way from parent to child using {{}}
      onSelect: '&?' # lets directive invoke parent scope action

    templateUrl: '/modelcatalogue/core/components/catalogueElementTreeview/catalogueElementTreeview.html'

    controllerAs: 'treeview'

    controller: CatalogueElementTreeview


}
]
