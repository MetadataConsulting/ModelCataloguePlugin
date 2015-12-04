angular.module('mc.core.ui.catalogueElementTreeviewItem', ['mc.util.names', 'mc.core.catalogueElementEnhancer', 'mc.core.listReferenceEnhancer', 'mc.core.listEnhancer', 'mc.util.recursiveCompile', 'ui.router']).directive 'catalogueElementTreeviewItem',  [ 'recursiveCompile', (recursiveCompile) -> {
    restrict: 'E'
    replace: true
    scope:
      element:  '='
      descend:  '='
      repeat:   '='
      treeview: '='

    templateUrl: 'modelcatalogue/core/ui/catalogueElementTreeviewItem.html'

    compile: recursiveCompile.compile

    controller: ['$scope', '$rootScope', '$element', 'catalogue', ($scope, $rootScope, $element, catalogue) ->
      endsWith = (text, suffix) -> text.indexOf(suffix, text.length - suffix.length) != -1

      getLocalName = (item) ->
        return undefined if not item
        return undefined if not item.ext
        return undefined if not angular.isFunction(item.ext.get)

        return item.ext.get('name') ? item.ext.get('Name')

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
          $scope.element.$$showMore()

      createShowMore  = (list) ->
        # function to load more items to existing $$children helper property
        ->
          $scope.element.$$showingMore = true
          list.next().then (nextList) ->
            for item in nextList.list
              it = if item.relation then item.relation else item
              $scope.element.$$children.push(angular.extend(it, {$$relationship: (if item.relation then item else undefined), $$metadata: item.ext, $$archived: item.archived, $$localName: getLocalName(item) }))
            $scope.element.$$showMore = createShowMore(nextList)
            loadMoreIfNeeded()
            $scope.element.$$showingMore = false

      loadNewChildren = (firstList) ->
        $scope.element.$$numberOfChildren = firstList.total
        $scope.element.$$cachedChildren = {}
        for child in $scope.element.$$children ? []
          $scope.element.$$cachedChildren[child.latestVersionId ? child.link] = child # beware of id, it's 'all' for containers

        newChildren = []
        for item in firstList.list
          it = if item.relation then item.relation else item
          id = it.latestVersionId ? it.link
          objectToExtend = {}



          if id
            cachedChild = $scope.element.$$cachedChildren[id] ? {}

            if cachedChild.$$collapsed
              cachedChild.$$resetHelperProperties() if angular.isFunction(cachedChild.$$resetHelperProperties)
            else
              cachedChild.$$loadChildren() if angular.isFunction(cachedChild.$$loadChildren)

            if cachedChild.id == it.id
              objectToExtend = cachedChild
            else
              for key, prop of cachedChild
                if key.indexOf('$') == 0
                  objectToExtend[key] = prop

          newChildren.push(angular.extend(objectToExtend, it, {$$relationship: (if item.relation then item else undefined), $$metadata: item.ext, $$archived: item.archived, $$localName: getLocalName(item) }))

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

        $scope.descendFun = $scope.element[$scope.currentDescend]

        if angular.isFunction(element.href)
          element.$$href = element.href()

        element.$$resetHelperProperties = ->
          if @[$scope.currentDescend]
            @$$numberOfChildren = $scope.element[$scope.currentDescend].total
            @$$loadingChildren = false
          else
            @$$numberOfChildren = 0
            @$$loadingChildren = false

          @$$children  ?= []
          @$$collapsed ?= true
          @$$showMore  ?= ->
          @$$active    ?= false

        element.$$resetHelperProperties()

        if $scope.element.$$numberOfChildren > $scope.element.$$children.length and $scope.element.$$showMore and not $scope.element.$$collapsed
          loadMoreIfNeeded()
          $element.closest('.catalogue-element-treeview-list-root').on 'scroll', loadMoreIfNeeded

        $scope.element.$$loadChildren = ->

          unless angular.isFunction($scope.descendFun)
            $scope.element.$$children = []
            $scope.element.$$numberOfChildren = 0
            return

          $scope.element.$$numberOfChildren = $scope.descendFun.total

          # first load
          $scope.element.$$loadingChildren = true
          $scope.descendFun().then(loadNewChildren).then ->
            $scope.element.$$loadingChildren = false

      $scope.collapseOrExpand = ->
        return if $scope.element.$$loadingChildren
        unless $scope.element.$$collapsed
          $scope.element.$$collapsed = true
          return

        unless $scope.element.$$children.length == 0 and $scope.element.$$numberOfChildren > 0
          $scope.element.$$collapsed = false
          return

        $scope.element.$$loadChildren()


      $scope.$watch 'element', onElementUpdate
      $scope.$watch 'descend', ->
        onElementUpdate($scope.element)

      onElementUpdate($scope.element)

      # event broadcasters and listeners
      $scope.select = (element) ->
        $scope.collapseOrExpand()
        $scope.treeview.select(element)

      reloadChildrenOnChange = (_, result, url) ->
        if catalogue.isContentCandidate($scope.element[$scope.currentDescend], result, owner: $scope.element, url: url)
          $scope.element.$$loadChildren()

      $scope.$on 'catalogueElementDeleted', (event, element) ->
        indexesToRemove = []
        if $scope.element.$$relationship == element
          delete $scope.element.$$relationship
        for item, i in $scope.element.$$children
          if element.relation and item.link == element.relation.link
            indexesToRemove.push i

        for index, i in indexesToRemove
          $scope.element.$$children.splice index - i, 1
          $scope.element.$$numberOfChildren--

        reloadChildrenOnChange event, element

      $scope.$on 'catalogueElementCreated', reloadChildrenOnChange
      $scope.$on 'catalogueElementUpdated', reloadChildrenOnChange
      $scope.$on 'listReferenceReordered', (ignored, listReference) ->
        $scope.element.$$loadChildren() if $scope.descendFun.link == listReference.link
    ]
  }
]