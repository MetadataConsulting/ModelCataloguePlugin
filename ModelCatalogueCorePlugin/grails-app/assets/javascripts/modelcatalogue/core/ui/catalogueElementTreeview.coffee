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

    controller: ['$scope', 'enhance', '$stateParams', '$rootScope', '$element', '$attrs', ($scope, enhance, $stateParams, $rootScope, $element, $attrs) ->
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
        list.$$children = []
        for item in list.list
          cachedChild  = if list.$$cachedChildren then list.$$cachedChildren[item.link]
          cachedChild ?= {}
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
            list.$$cachedChildren[child.link] = child

        addItemsFromList(list)
        nextFun = list.next
        setLastListToRootScope(list)

      $scope.showMore = () ->
        return unless $scope.list.total > $scope.list.$$children.length
        params = {}
        params.classification = $stateParams.classification if $stateParams.classification

        nextFun(null, params).then (list) ->
          addItemsFromList(list)
          nextFun = list.next

      if $scope.mode == 'list'
        onListChange $scope.list, getLastListFromRootScope($scope.list?.base)
        $scope.$watch 'list', onListChange

        refreshList = ->
          $scope.list.reload(status: $stateParams.status, toplevel: true).then (newList) ->
            $scope.list = newList

        $scope.$on 'catalogueElementCreated', refreshList
        $scope.$on 'catalogueElementDeleted', refreshList
        $scope.$on 'newVersionCreated', refreshList
        $scope.$on 'catalogueElementUpdated', refreshList
    ]
  }
]