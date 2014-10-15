angular.module('mc.core.ui.catalogueElementTreeviewItem', ['mc.util.names', 'mc.core.catalogueElementEnhancer', 'mc.core.listReferenceEnhancer', 'mc.core.listEnhancer', 'mc.util.recursiveCompile', 'ui.router']).directive 'catalogueElementTreeviewItem',  [ 'recursiveCompile', 'names', (recursiveCompile, names) -> {
    restrict: 'E'
    replace: true
    scope:
      element:  '='
      descend:  '='
      repeat:   '='
      rootId:   '='

    templateUrl: 'modelcatalogue/core/ui/catalogueElementTreeviewItem.html'

    compile: recursiveCompile.compile

    controller: ['$scope', '$rootScope', '$element', '$timeout', '$stateParams', ($scope, $rootScope, $element, $timeout, $stateParams) ->
      $scope.element.$$loadingChildren = false

      endsWith = (text, suffix) -> text.indexOf(suffix, text.length - suffix.length) != -1

      loadMoreIfNeeded = ->
        return if not $scope.element.$$numberOfChildren > $scope.element.$$children.length
        return if $scope.element.$$showingMore
        showMore = $element.find('.catalogue-element-treeview-show-more')
        return if showMore.hasClass '.hide'
        root = $element.closest('.catalogue-element-treeview-list-root')
        if showMore.offset()?.top < root.offset()?.top + 3 * root.height()
          $scope.element.$$showingMore = true
          $scope.element.$$showMore().then ->
            root.hide()
            root.get(0).offsetHeight
            root.show()
            $scope.element.$$showingMore = false



      createShowMore  = (list) ->
        ->
          params = {}
          params.classification = $stateParams.classification if $stateParams.classification
          list.next(null, params).then (nextList) ->
            $scope.element.$$children ?= []
            for item in nextList.list when item.relation
              $scope.element.$$children.push(angular.extend(item.relation, {metadata: item.ext}))
            $scope.element.$$showMore = createShowMore(nextList)
            loadMoreIfNeeded()


      onElementUpdate = (element) ->
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

        if element? and element[$scope.currentDescend]
          $scope.element.$$numberOfChildren = $scope.element[$scope.currentDescend].total
        else
          $scope.element.$$numberOfChildren = 0
        $scope.element.$$children         ?= []
        $scope.element.$$collapsed        ?= true
        $scope.element.$$showMore         ?= ->
        $scope.element.$$active           ?= false


        if $scope.element.$$numberOfChildren > $scope.element.$$children.length and $scope.element.$$showMore and not $scope.element.$$collapsed
          root = $element.closest('.catalogue-element-treeview-list-root')
          $timeout loadMoreIfNeeded, 100
          root.on 'scroll', loadMoreIfNeeded


      $scope.select = (element) ->
        $rootScope.$broadcast 'treeviewElementSelected', element, $scope.rootId

      $rootScope.$on '$stateChangeSuccess', (event, state, params) ->
        return if state.name != 'mc.catalogue.show'
        $scope.element.$$active = $scope.element and $scope.element.id == parseInt(params.id ? 0, 10) and names.getPropertyNameFromType($scope.element.elementType) == params.resource

      $rootScope.$on 'treeviewElementSelected', (event, element, id) ->
        return if id and $scope.rootId and id != $scope.rootId
        $scope.element.$$active = $scope.element.link == element.link

      $rootScope.$on 'catalogueElementDeleted', (event, element) ->
        indexesToRemove = []
        for item, i in $scope.element.$$children when element.relation and item.id == element.relation.id and item.elementType == element.relation.elementType
          indexesToRemove.push i

        for index, i in indexesToRemove
          $scope.element.$$children.splice index - i, 1
          $scope.element.$$numberOfChildren--


      $rootScope.$on 'catalogueElementCreated', (_, result) ->
        if result and result.destination and result.source and result.type
          currentDescend = $scope.element[$scope.currentDescend]
          if result.destination.link == $scope.element.link and endsWith(currentDescend.link, "/incoming/#{result.type.name}")
            $scope.element.$$numberOfChildren++
            result.source.refresh().then (newOne) ->
              $scope.element.$$children = [newOne].concat $scope.element.$$children
          if result.source.link == $scope.element.link and endsWith(currentDescend.link, "/outgoing/#{result.type.name}")
            $scope.element.$$numberOfChildren++
            result.destination.refresh().then (newOne) ->
              $scope.element.$$children = [newOne].concat $scope.element.$$children


      $scope.collapseOrExpand = ->

        return if $scope.element.$$loadingChildren
        if $scope.element.$$collapsed
          if $scope.element.$$children.length == 0 and $scope.element.$$numberOfChildren > 0
            $scope.element.$$loadingChildren = true
            fun = $scope.element[$scope.currentDescend]
            if angular.isFunction(fun)
              params = {}
              params.classification = $stateParams.classification if $stateParams.classification

              fun(null, params).then (list) ->

                newChildren = []
                for item in list.list when item.relation
                  newChildren.push(angular.extend(item.relation, {metadata: item.ext}))

                $scope.element.$$children = newChildren
                $scope.element.$$collapsed  = false
                root = $element.closest('.catalogue-element-treeview-list-root')
                if $scope.element.$$numberOfChildren > $scope.element.$$children.length
                  $scope.element.$$showMore = createShowMore(list)
                  $timeout loadMoreIfNeeded, 100
                  root.on 'scroll', loadMoreIfNeeded
                else
                  $scope.element.$$showMore = ->
                  root.off 'scroll', loadMoreIfNeeded
                $scope.element.$$loadingChildren = false
            else
              $scope.element.$$loadingChildren = false
          else
            $scope.element.$$collapsed = false
            $scope.select($scope.element)
        else
          $scope.element.$$collapsed = true

      $scope.$watch 'element', onElementUpdate
      $scope.$watch 'descend', ->
        onElementUpdate($scope.element)

      onElementUpdate($scope.element)
    ]
  }
]