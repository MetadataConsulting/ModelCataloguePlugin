angular.module('mc.core.ui.catalogueElementTreeviewItem', ['mc.core.catalogueElementEnhancer', 'mc.core.listReferenceEnhancer', 'mc.core.listEnhancer', 'mc.util.recursiveCompile']).directive 'catalogueElementTreeviewItem',  [ 'recursiveCompile', (recursiveCompile) -> {
    restrict: 'E'
    replace: true
    scope:
      element:  '='
      descend:  '='

    templateUrl: 'modelcatalogue/core/ui/catalogueElementTreeviewItem.html'

    compile: recursiveCompile.compile

    controller: ['$scope', '$rootScope', ($scope, $rootScope) ->
      loadingChildren = false

      isEqual = (a, b) ->
        return false if not a? or not b?
        return false if not a.elementType? or not b.elementType?
        return false if not a.id? or not b.id?
        return false if a.elementType != b.elementType
        a.id == b.id

      createShowMore  = (list) ->
        ->
          $scope.hasMore = false
          list.next().then (nextList) ->
            for item in nextList.list
              $scope.children.push(item.relation)
            $scope.hasMore  = $scope.numberOfChildren > $scope.children.length
            $scope.showMore = createShowMore(nextList)


      onElementUpdate = (element) ->
        if angular.isArray($scope.descend)
          if $scope.descend.length == 0
            $scope.currentDescend = null
            $scope.nextDescend    = []
          else
            $scope.currentDescend = $scope.descend[0]
            if $scope.descend.length == 1
              $scope.nextDescend = []
            else
              $scope.nextDescend = $scope.descend.slice(1)
        else
          $scope.currentDescend = $scope.descend
          $scope.nextDescend    = []

        if element?  and element[$scope.currentDescend]
          $scope.numberOfChildren = $scope.element[$scope.currentDescend].total
        else
          $scope.numberOfChildren = 0
        $scope.children   = []
        $scope.collapsed  = true
        $scope.hasMore    = false
        $scope.showMore   = ->
        $scope.active     = false


      $scope.select = (element) ->
        $rootScope.$broadcast 'treeviewElementSelected', element

      $rootScope.$on 'showCatalogueElement', (event, element) ->
        $scope.active = isEqual($scope.element, element)

      $rootScope.$on 'treeviewElementSelected', (event, element) ->
        $scope.active = isEqual($scope.element, element)

      $scope.collapseOrExpand = ->
        return if loadingChildren
        if $scope.collapsed
          if $scope.children.length == 0 and $scope.numberOfChildren > 0
            loadingChildren = true
            fun = $scope.element[$scope.currentDescend]
            if angular.isFunction(fun)
              fun().then (list) ->

                newChildren = []
                for item in list.list
                  newChildren.push(item.relation)

                $scope.children = newChildren
                $scope.collapsed  = false
                $scope.hasMore    = $scope.numberOfChildren > $scope.children.length
                if $scope.hasMore
                  $scope.showMore = createShowMore(list)
                else
                  $scope.showMore = ->
                loadingChildren   = false
            else
              loadingChildren = false
          else
            $scope.collapsed = false
        else
          $scope.collapsed = true

      $scope.$watch 'element', onElementUpdate
      $scope.$watch 'descend', ->
        onElementUpdate($scope.element)

      onElementUpdate($scope.element)
    ]
  }
]