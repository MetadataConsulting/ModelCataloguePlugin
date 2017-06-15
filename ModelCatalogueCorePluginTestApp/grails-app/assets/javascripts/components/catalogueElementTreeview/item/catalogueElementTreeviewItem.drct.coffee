nodeid = 0

angular.module('mc.core.ui.catalogueElementTreeview.item')
.config(['$uibTooltipProvider', ($uibTooltipProvider) ->
  $uibTooltipProvider.setTriggers mouseover: 'mouseout'
])
.directive 'catalogueElementTreeviewItem',  (recursiveCompile) -> {
    restrict: 'E'
    replace: true
    scope:
      element:  '='
      treeview: '='
      extraParameters: '=?'

    templateUrl: '''<li class="catalogue-element-treeview-item">
  <div class="catalogue-element-treeview-text-content"
       ng-class="{'active': node.active, 'archived': element.$$archived}">
    <!--
     The following displays a "total" number of children of a node in the treeview.
     It doesn't seem to display what we want for certain elements such as the Data Model. It does seem to be useful for Data Classes and Data Element Tags.
     At the top level numberOfChildren seems to come from DataModelController's "content" method. (descend="content").
     We're turning it off for now since we don't understand how to fine-tune it. --James 7 June 2017
     -->
    <!--
    <span class="badge pull-right" ng-if="node.numberOfChildren" ng-switch="node.numberOfChildren">
      <span ng-switch-default>{{::node.numberOfChildren}}</span>
      <span ng-switch-when="2147483647" class="fa fa-question fa-inverse"></span>
    </span>
    -->
    <span class="catalogue-element-treeview-labels">
      <span ng-if="::!element.elementType">
        <a class="catalogue-element-treeview-icon btn btn-link">
          <span class="fa fa-fw fa-ban"></span>
        </a>
        No Data
      </span>
      <a ng-if="::element.elementType" class="catalogue-element-treeview-icon  btn btn-link"
         ng-click="select(node)">
        <span ng-if="!extraParameters.prefetch &amp;&amp; node.numberOfChildren &amp;&amp; !node.loadingChildren">
          <span class="fa fa-fw fa-caret-right text-muted" ng-if="node.collapsed">
          </span>
          <span class="fa fa-fw fa-caret-down text-muted" ng-if="!node.collapsed">
          </span>
        </span>

        <span class="fa fa-fw fa-refresh text-muted" ng-if="node.loadingChildren"></span>
        <span class="fa fa-fw text-muted" ng-if="extraParameters.prefetch"></span>
        <span class="fa fa-fw" ng-if="!node.numberOfChildren && !node.loadingChildren"></span>
      </a>
      <span class="catalogue-element-treeview-name"
            ng-class="{
              'text-warning': element.status == 'DRAFT',
              'text-info': element.status == 'PENDING',
              'text-danger': (element.status == 'DEPRECATED' || element.undone)
            }"
            ng-click="select(node)">
        <span ng-class="node.icon" class="text-muted"></span>
        {{node.name}}
        <span class="text-muted">
          {{node.metadataOccurrencesToAsterisk}}
        </span>
        <small class="text-muted" ng-if="element.$$localName">{{element.name}}</small>
        <small class="text-muted">
          <span ng-if="::element.latestVersionId"
                class="catalogue-element-treeview-version-number">
            {{node.dataModelWithVersion}}
          </span>
        </small>
      </span>
      <small class="text-muted" ng-if="::node.href">
        <a class="catalogue-element-treeview-link"
           ng-href="{{::node.href}}"
           title="{{node.name}}"
           target="_blank">
          <span class="fa fa-external-link text-muted"></span>
        </a>
      </small>
    </span>
  </div>
  <ul ng-if="node.children" ng-hide="node.collapsed" class="catalogue-element-treeview-list">
    <catalogue-element-treeview-item treeview="::treeview" sly-repeat="child in node.children"
                                     extra-parameters="::{'path': path, 'descendPath': extraParameters.descendPath.concat(child.id)}"
                                     element="child">
    </catalogue-element-treeview-item>
    <li ng-if="node.numberOfChildren > node.children.length" class="catalogue-element-treeview-item">
      <span class="catalogue-element-treeview-labels" ng-click="node.showMore()">
        <a class="catalogue-element-treeview-icon btn btn-link catalogue-element-treeview-show-more"
           ng-class="'show-more-' + nodeid">
          <span class="fa fa-fw fa-chevron-down"></span>
        </a>
        <a class="text-muted">
          Show more
        </a>
      </span>
    </li>
  </ul>
</li>
'''

    compile: recursiveCompile.compile

    controller: ($scope, $rootScope, $element, catalogue, TreeviewNodeFactory) ->
      $scope.nodeid = nodeid++
      $scope.path = {segments: []}

      startsWithSegment = (element, segments) ->
        return false unless element
        return false unless element.link
        return false unless element.id
        return false unless segments
        return false unless segments.length > 0

        firstSegment = segments[0]

        return true if element.id == 'all' and element.link.indexOf(firstSegment.substring(0, firstSegment.lastIndexOf('/all'))) == 0
        return true if element.link.indexOf(firstSegment) >= 0
        return false

      getLocalName = (item) ->
        return undefined if not item
        return undefined if not item.ext
        return undefined if not angular.isFunction(item.ext.get)

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

      metadataOccurrencesToAsterisk = (element) ->
        # only Data Element and Data Class with some metadata and defined relationship
        if (angular.isFunction(element.isInstanceOf) && (element.isInstanceOf('dataElement') || element.isInstanceOf('dataClass'))) && element.$$metadata?.values? && element.$$relationship
          min = '0'
          max = '*'
          for row in element.$$metadata.values
            if (row.key == 'Min Occurs')
              min = row.value
            else if (row.key == 'Max Occurs')
              max = row.value
          return "#{min}..#{max}"
        else
          return ""

      onElementUpdate = (element) ->

        $scope.node = TreeviewNodeFactory.create($scope.treeview.getNodeId("#{$scope.extraParameters?.root}:#{element.link}"), element)

        $scope.descendFun = $scope.element[$scope.treeview.getDescend()]

        $scope.node.metadataOccurrencesToAsterisk = metadataOccurrencesToAsterisk(element)

        $scope.node.reset = ->
          if $scope.node.item[$scope.treeview.getDescend()]
            $scope.node.numberOfChildren = $scope.element[$scope.treeview.getDescend()].total
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

        if $scope.extraParameters?.prefetch or startsWithSegment($scope.element, $scope.extraParameters?.path?.segments)
          $scope.node.loadChildren()

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


      $scope.$watch 'element', onElementUpdate

      onElementUpdate($scope.element)

      # event broadcasters and listeners
      $scope.select = (element) ->
        $scope.collapseOrExpand()
        $scope.treeview.select(element, $scope.extraParameters?.descendPath)

      reloadChildrenOnChange = (_, result, url) ->
        if result.link == $scope.element.link
          $scope.element.updateFrom result
          $scope.node.loadChildren()
          return
        if catalogue.isContentCandidate($scope.element[$scope.treeview.getDescend()], result, owner: $scope.element, url: url)
          $scope.node.loadChildren()

      $scope.$on 'catalogueElementDeleted', (event, element) ->
        indexesToRemove = []
        if $scope.element.$$relationship == element
          delete $scope.element.$$relationship
        for item, i in $scope.node.children
          if element.relation and item.link == element.relation.link or element.link == item.link
            indexesToRemove.push i

        for index, i in indexesToRemove
          $scope.node.children.splice index - i, 1
          $scope.node.numberOfChildren--

        reloadChildrenOnChange event, element

      doReloadChildrenOnChange = (data) ->
        reloadChildrenOnChange(data[0], data[1])

      isForCurrentElement = (data) -> data[1].link is $scope.element.link

      DEBOUNCE_TIME = 100

      $scope.$eventToObservable('catalogueElementCreated').debounce(DEBOUNCE_TIME).subscribe doReloadChildrenOnChange
      $scope.$eventToObservable('catalogueElementUpdated').filter(isForCurrentElement).debounce(DEBOUNCE_TIME)
      .subscribe doReloadChildrenOnChange

      $scope.$on 'listReferenceReordered', (ignored, listReference) ->
        reloadChildrenOnChange(ignored, listReference, listReference?.link)

      $scope.$watch 'extraParameters.path.segments', (segments) ->
        if startsWithSegment($scope.element, segments)
          $scope.node.loadChildren()
          $scope.path.segments = segments.slice(1) if segments.length > 1
  }
