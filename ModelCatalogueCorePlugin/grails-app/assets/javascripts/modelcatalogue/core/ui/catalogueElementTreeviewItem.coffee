angular.module('mc.core.ui.catalogueElementTreeviewItem', ['mc.util.names', 'mc.core.catalogueElementEnhancer', 'mc.core.listReferenceEnhancer', 'mc.core.listEnhancer', 'mc.util.recursiveCompile', 'ui.router']).directive 'catalogueElementTreeviewItem',  [ 'recursiveCompile', (recursiveCompile) -> {
    restrict: 'E'
    replace: true
    scope:
      element:  '='
      descend:  '='
      repeat:   '='
      rootId:   '='

    templateUrl: 'modelcatalogue/core/ui/catalogueElementTreeviewItem.html'

    compile: recursiveCompile.compile

    controller: ['$scope', '$rootScope', '$element', '$timeout', '$stateParams', ($scope, $rootScope, $element) ->
      endsWith = (text, suffix) -> text.indexOf(suffix, text.length - suffix.length) != -1

      handleDescendPaths = ->
        if angular.isArray($scope.descend)
          if $scope.descend.length == 0
            $scope.currentDescend = null
            $scope.nextDescend    = []
          else
            $scope.currentDescend = $scope.descend[0]
            if $scope.descend.length == 1
              $scope.nextDescend = if $scope.repeat then $scope.descend else []
            else
              $scope.nextDescend = $scope.descend.slice(1)
              if $scope.repeat
                $scope.nextDescend.push($scope.currentDescend)
        else
          $scope.currentDescend = $scope.descend
          $scope.nextDescend    = $scope.descend


      loadMoreIfNeeded = ->
        return if not $scope.element.$$numberOfChildren > $scope.element.$$children?.length
        return if $scope.element.$$showingMore
        showMore = $element.find('.catalogue-element-treeview-show-more')
        return if showMore.hasClass '.hide'
        root = $element.closest('.catalogue-element-treeview-list-root')
        if showMore.offset()?.top < root.offset()?.top + 3 * root.height() and angular.isFunction($scope.element.$$showMore)
          $scope.element.$$showMore().then ->
            root.hide()
            root.get(0).offsetHeight
            root.show()

      createShowMore  = (list) ->
        # function to load more items to existing $$children helper property
        ->
          $scope.element.$$showingMore = true
          list.next().then (nextList) ->
            for item in nextList.list when item.relation
              $scope.element.$$children.push(angular.extend(item.relation, {$$metadata: item.ext}))
            $scope.element.$$showMore = createShowMore(nextList)
            loadMoreIfNeeded()
            $scope.element.$$showingMore = false

      loadNewChildren = (firstList) ->
        $scope.element.$$cachedChildren = {}
        for child in $scope.element.$$children ? []
          $scope.element.$$cachedChildren[child.link] = child

        newChildren = []
        for item in firstList.list when item.relation
          cachedChild = $scope.element.$$cachedChildren[item.relation.link] ? {}

          if cachedChild.$$collapsed
            cachedChild.$$resetHelperProperties() if angular.isFunction(cachedChild.$$resetHelperProperties)
          else
            cachedChild.$$loadChildren() if angular.isFunction(cachedChild.$$loadChildren)
            
          newChildren.push(angular.extend(cachedChild, item.relation, {$$metadata: item.ext}))

        $scope.element.$$children = newChildren
        $scope.element.$$collapsed  = false
        root = $element.closest('.catalogue-element-treeview-list-root')
        if $scope.element.$$numberOfChildren > $scope.element.$$children.length
          $scope.element.$$showMore = createShowMore(firstList)
          loadMoreIfNeeded()
          root.on 'scroll', loadMoreIfNeeded
        else
          $scope.element.$$showMore = ->
          root.off 'scroll', loadMoreIfNeeded

      onElementUpdate = (element) ->
        handleDescendPaths()

        element.$$resetHelperProperties = ->
          if @[$scope.currentDescend]
            @$$numberOfChildren = $scope.element[$scope.currentDescend].total
          else
            @$$numberOfChildren = 0
    
          @$$children  ?= []
          @$$collapsed ?= true
          @$$showMore  ?= ->
          @$$active    ?= false

        element.$$resetHelperProperties()

        if $scope.element.$$numberOfChildren > $scope.element.$$children.length and $scope.element.$$showMore and not $scope.element.$$collapsed
          loadMoreIfNeeded()
          $element.closest('.catalogue-element-treeview-list-root').on 'scroll', loadMoreIfNeeded

        $scope.element.$$loadChildren = ->
          fun = $scope.element[$scope.currentDescend]

          unless angular.isFunction(fun)
            $scope.element.$$children = []
            $scope.element.$$numberOfChildren = 0
            return

          $scope.element.$$numberOfChildren = fun.total

          # first load
          $scope.element.$$loadingChildren = true
          fun().then(loadNewChildren).then ->
            $scope.element.$$loadingChildren = false

      $scope.collapseOrExpand = ->
        return if $scope.element.$$loadingChildren
        unless $scope.element.$$collapsed
          $scope.element.$$collapsed = true
          return

        unless $scope.element.$$children.length == 0 and $scope.element.$$numberOfChildren > 0
          $scope.element.$$collapsed = false
          $scope.select($scope.element)
          return

        $scope.element.$$loadChildren()


      $scope.$watch 'element', onElementUpdate
      $scope.$watch 'descend', ->
        onElementUpdate($scope.element)

      onElementUpdate($scope.element)

      # event broadcasters and listeners
      $scope.select = (element) ->
        $rootScope.$broadcast 'treeviewElementSelected', element, $scope.rootId

      $rootScope.$on 'treeviewElementSelected', (event, element, id) ->
        return if id and $scope.rootId and id != $scope.rootId
        $scope.element.$$active = $scope.element.link == element.link

      $rootScope.$on 'catalogueElementDeleted', (event, element) ->
        indexesToRemove = []
        for item, i in $scope.element.$$children
          if element.relation and item.link == element.relation.link
            indexesToRemove.push i

        for index, i in indexesToRemove
          $scope.element.$$children.splice index - i, 1
          $scope.element.$$numberOfChildren--


      $rootScope.$on 'catalogueElementCreated', (_, result) ->
        if result and result.relation and result.element and result.type and result.direction
          direction = if result.direction == 'destinationToSource' then 'incoming' else 'outgoing'
          oppositeDirection = if result.direction == 'destinationToSource' then 'outgoing' else 'incoming'
          currentDescend = $scope.element[$scope.currentDescend]
          if result.element.link == $scope.element.link and endsWith(currentDescend.link, "/#{direction}/#{result.type.name}")
            $scope.element.$$numberOfChildren++
            result.relation.refresh().then (newOne) ->
              $scope.element.$$children = [newOne].concat $scope.element.$$children
          if result.relation.link == $scope.element.link and endsWith(currentDescend.link, "/#{oppositeDirection}/#{result.type.name}")
            $scope.element.$$numberOfChildren++
            result.element.refresh().then (newOne) ->
              $scope.element.$$children = [newOne].concat $scope.element.$$children
    ]
  }
]