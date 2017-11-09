#= require catalogueElementTreeviewItem.tpl.coffee
nodeid = 0

angular.module('modelcatalogue.core.components.catalogueElementTreeview.item')
.config(['$uibTooltipProvider', ($uibTooltipProvider) ->
  $uibTooltipProvider.setTriggers mouseover: 'mouseout'
])
.directive 'catalogueElementTreeviewItem',  (recursiveCompile) -> {
    restrict: 'E'
    replace: true
    scope:
      # The particular catalogue element shown by this directive
      catalogueElement:  '=' # bidirectional binding
      # Reference to the entire treeview
      treeview: '='
      extraParameters: '=?' # bidirectional optional binding

    templateUrl: '/modelcatalogue/core/components/catalogueElementTreeview/item/catalogueElementTreeviewItem.html'

    compile: recursiveCompile.compile

    controller: ($scope, $rootScope, $element, catalogue, TreeviewNodeFactory) ->
      $scope.nodeid = nodeid++
      $scope.path = {segments: []}
      ## Controller internal methods:
      # Whether element url starts with the first segment of segments
      startsWithSegment = (catalogueElement, segments) ->
        return false unless catalogueElement && catalogueElement.link && catalogueElement.id &&
          segments && segments.length > 0

        firstSegment = segments[0]

        return (catalogueElement.id == 'all' and
          catalogueElement.link.indexOf(firstSegment.substring(0, firstSegment.lastIndexOf('/all'))) == 0) or
          catalogueElement.link.indexOf(firstSegment) >= 0

      # Whether a broadcast event is for this element
      isForCurrentCatalogueElement = (data) -> data[1].link is $scope.catalogueElement.link

      getLocalName = (item) ->
        return undefined unless item
        return undefined unless item.ext
        return undefined unless angular.isFunction(item.ext.get)

        return item.ext.get('name') ? item.ext.get('Name')

      loadMoreIfNeeded = ->
        return if not $scope.node.numberOfChildren > $scope.node.children?.length
        return if $scope.node.showingMore
        showMore = angular.element('.show-more-' + $scope.nodeid)
        return if showMore.hasClass '.hide'
        return if showMore.offset()?.top < 0
        root = $element.closest('.catalogue-element-treeview-list-root')
        if showMore.offset()?.top < root.offset()?.top + 3 * root.height() and angular.isFunction($scope.node.showMore)
          $scope.node.showMore()

      onlyImportant = (extraParameters) ->
        extra = angular.copy(extraParameters ? {})
        extra.path = undefined
        extra.descendPath = undefined
        extra
      createShowMore  = (list) ->
        # function to load more items to existing $$children helper property
        ->
          $scope.node.showingMore = true
          list.next(onlyImportant($scope.extraParameters)).then (nextList) ->
            for item in nextList.list
              it = if item.relation then item.relation else item
              $scope.node.children.push(
                angular.extend(it,
                  {
                    $$relationship: (if item.relation then item else undefined),
                    $$metadata: item.ext,
                    $$archived: item.archived,
                    $$localName: getLocalName(item)
                  }
                )
              )
            $scope.node.showMore = createShowMore(nextList)
            loadMoreIfNeeded()
            $scope.node.showingMore = false

      loadNewChildren = (firstList) ->
        $scope.node.numberOfChildren = firstList.total

        newChildren = []
        for item in (firstList.list ? [])
          it = if item.relation then item.relation else item
          id = it.link
          objectToExtend = {}

          if id
            node = TreeviewNodeFactory.get($scope.treeview.getNodeId(id))

            if node?.collapsed
              node.reset() if angular.isFunction(node.reset)
            else
              node.loadChildren() if angular.isFunction(node?.loadChildren)

            if node?.item.id == it.id
              objectToExtend = node.item
            else if node?.item
              for key, prop of node.item
                if key.indexOf('$') == 0
                  objectToExtend[key] = prop

          newChildren.push(
            angular.extend(objectToExtend, it,
              {
                $$relationship: (if item.relation then item else undefined),
                $$metadata: item.ext,
                $$archived: item.archived,
                $$localName: getLocalName(item)
              })
          )

        $scope.node.children = newChildren
        $scope.node.collapsed  = false
        root = $element.closest('.catalogue-element-treeview-list-root')
        if $scope.node.numberOfChildren > $scope.node.children.length
          $scope.node.showMore = createShowMore(firstList)
          loadMoreIfNeeded()
          root.on 'scroll', loadMoreIfNeeded
        else
          $scope.node.showMore = -> $scope.node.loadChildren()

          root.off 'scroll', loadMoreIfNeeded

      metadataOccurrencesToAsterisk = (catalogueElement) ->
        # only Data Element and Data Class with some metadata and defined relationship
        if (angular.isFunction(catalogueElement.isInstanceOf) && (catalogueElement.isInstanceOf('dataElement') || catalogueElement.isInstanceOf('dataClass'))) && catalogueElement.$$metadata?.values? && catalogueElement.$$relationship
          min = '0'
          max = '*'
          for row in catalogueElement.$$metadata.values
            if (row.key == 'Min Occurs')
              min = row.value
            else if (row.key == 'Max Occurs')
              max = row.value
          return "#{min}..#{max}"
        else
          return ""

      onCatalogueElementUpdate = (catalogueElement) ->
        console.log($scope)

        $scope.node = TreeviewNodeFactory.create($scope.treeview.getNodeId("#{$scope.extraParameters?.root}:#{catalogueElement.link}"), catalogueElement)

        $scope.descendFun = $scope.catalogueElement[$scope.treeview.getDescend()]

        $scope.node.metadataOccurrencesToAsterisk = metadataOccurrencesToAsterisk(catalogueElement)

        $scope.node.reset = ->
          if $scope.node.item[$scope.treeview.getDescend()]
            $scope.node.numberOfChildren = $scope.catalogueElement[$scope.treeview.getDescend()].total
            $scope.node.loadingChildren = false
          else
            $scope.node.numberOfChildren = 0
            $scope.node.loadingChildren = false

          $scope.node.children ?= []
          $scope.node.collapsed  ?= true
          $scope.node.showMore   ?= ->
          $scope.node.active     ?= false

        $scope.node.reset()

        if $scope.node.numberOfChildren > $scope.node.children.length and $scope.node.showMore and not $scope.node.collapsed
          loadMoreIfNeeded()
          $element.closest('.catalogue-element-treeview-list-root').on 'scroll', loadMoreIfNeeded

        $scope.node.loadChildren = ->

          unless angular.isFunction($scope.descendFun)
            $scope.node.children = []
            $scope.node.numberOfChildren = 0
            return

          $scope.node.numberOfChildren = $scope.descendFun.total

          # first load
          $scope.node.loadingChildren = true
          $scope.descendFun(null, onlyImportant($scope.extraParameters)).then(loadNewChildren).finally ->
            $scope.node.loadingChildren = false

        if $scope.extraParameters?.prefetch or startsWithSegment($scope.catalogueElement, $scope.extraParameters?.path?.segments)
          $scope.node.loadChildren()
      reloadChildrenOnChange = (_, result, url) ->
        if result.link == $scope.catalogueElement.link
          $scope.catalogueElement.updateFrom result
          $scope.node.loadChildren()
          return
        if catalogue.isContentCandidate($scope.catalogueElement[$scope.treeview.getDescend()], result, owner: $scope.catalogueElement, url: url)
          $scope.node.loadChildren()
      # Making use of some "data" "broadcasted" from RootScope
      doReloadChildrenOnChange = (data) ->
        reloadChildrenOnChange(data[0], data[1])


      ## Scope methods:
      $scope.collapseOrExpand = ->
        return if $scope.extraParameters?.prefetch
        return if $scope.node.loadingChildren
        return unless $scope.node.numberOfChildren
        unless $scope.node.collapsed
          $scope.node.collapsed = true
          return

        unless $scope.node.children.length == 0 and $scope.node.numberOfChildren > 0
          $scope.node.collapsed = false
          return

        $scope.node.loadChildren()

      $scope.select = (catalogueElement) ->
        $scope.collapseOrExpand()
        $scope.treeview.select(catalogueElement, $scope.extraParameters?.descendPath)

      ## Event handling:
      DEBOUNCE_TIME = 100

      $scope.$eventToObservable('catalogueElementCreated').debounce(DEBOUNCE_TIME).subscribe doReloadChildrenOnChange
      $scope.$eventToObservable('catalogueElementUpdated').filter(isForCurrentCatalogueElement).debounce(DEBOUNCE_TIME)
      .subscribe doReloadChildrenOnChange

      $scope.$on 'catalogueElementDeleted', (event, deletedElement) ->
        indexesToRemove = []
        if $scope.catalogueElement.$$relationship == deletedElement
          delete $scope.catalogueElement.$$relationship
        for child, index in $scope.node.children
          if deletedElement.relation and child.link == deletedElement.relation.link or deletedElement.link == child.link
            indexesToRemove.push index

        for index, i in indexesToRemove
          $scope.node.children.splice index - i, 1
          $scope.node.numberOfChildren--

        reloadChildrenOnChange event, deletedElement

      $scope.$on 'listReferenceReordered', (ignored, listReference) ->
        reloadChildrenOnChange(ignored, listReference, listReference.link)

      $scope.$watch 'catalogueElement', onCatalogueElementUpdate
      onCatalogueElementUpdate($scope.catalogueElement)

      $scope.$watch 'extraParameters.path.segments', (segments) ->
        if startsWithSegment($scope.catalogueElement, segments)
          $scope.node.loadChildren()
          $scope.path.segments = segments.slice(1) if segments.length > 1
  }
