angular.module('mc.core.ui.catalogueElementTreeviewItem', ['mc.util.names', 'mc.core.catalogueElementEnhancer', 'mc.core.listReferenceEnhancer', 'mc.core.listEnhancer', 'mc.util.recursiveCompile']).directive 'catalogueElementTreeviewItem',  [ 'recursiveCompile', 'names', (recursiveCompile, names) -> {
    restrict: 'E'
    replace: true
    scope:
      element:  '='
      descend:  '='
      repeat:   '='
      rootId:   '='

    templateUrl: 'modelcatalogue/core/ui/catalogueElementTreeviewItem.html'

    compile: recursiveCompile.compile

    controller: ['$scope', '$rootScope', ($scope, $rootScope) ->
      $scope.loadingChildren = false

      endsWith = (text, suffix) -> text.indexOf(suffix, text.length - suffix.length) != -1

      isEqual = (a, b) ->
        return false if not a? or not b?
        return false if not a.elementType? or not b.elementType?
        return false if not a.id? or not b.id?
        return false if a.elementType != b.elementType
        a.id == b.id

      createShowMore  = (list) ->
        ->
          list.next().then (nextList) ->
            for item in nextList.list when item.relation
              $scope.children.push(angular.extend(item.relation, {metadata: item.ext}))
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
              $scope.nextDescend = if $scope.repeat then $scope.descend else []
            else
              $scope.nextDescend = $scope.descend.slice(1)
              if $scope.repeat
                $scope.nextDescend.push($scope.currentDescend)
        else
          $scope.currentDescend = $scope.descend
          $scope.nextDescend    = $scope.descend

        if element? and element[$scope.currentDescend]
          $scope.numberOfChildren = $scope.element[$scope.currentDescend].total
        else
          $scope.numberOfChildren = 0
        $scope.children   = []
        $scope.collapsed  = true
        $scope.hasMore    = false
        $scope.showMore   = ->
        $scope.active     = false


      $scope.select = (element) ->
        $rootScope.$broadcast 'treeviewElementSelected', element, $scope.rootId

      $rootScope.$on '$stateChangeSuccess', (event, state, params) ->
        return if state.name != 'mc.catalogue.show'
        $scope.active = $scope.element and $scope.element.id == parseInt(params.id ? 0, 10) and names.getPropertyNameFromType($scope.element.elementType) == params.resource

      $rootScope.$on 'treeviewElementSelected', (event, element, id) ->
        return if id and $scope.rootId and id != $scope.rootId
        $scope.active = isEqual($scope.element, element)

      $rootScope.$on 'catalogueElementDeleted', (event, element) ->
        indexesToRemove = []
        for item, i in $scope.children when element.relation and item.id == element.relation.id and item.elementType == element.relation.elementType
          indexesToRemove.push i

        for index, i in indexesToRemove
          $scope.children.splice index - i, 1
          $scope.numberOfChildren--


      $rootScope.$on 'catalogueElementCreated', (_, __, result) ->
        if result and result.destination and result.source and result.type
          currentDescend = $scope.element[$scope.currentDescend]
          if result.destination.link == $scope.element.link and endsWith(currentDescend.link, "/incoming/#{result.type.name}")
            $scope.numberOfChildren++
            result.source.refresh().then (newOne) ->
              $scope.children = [newOne].concat $scope.children
          if result.source.link == $scope.element.link and endsWith(currentDescend.link, "/outgoing/#{result.type.name}")
            $scope.numberOfChildren++
            result.destination.refresh().then (newOne) ->
              $scope.children = [newOne].concat $scope.children


      $scope.collapseOrExpand = ->
        return if $scope.loadingChildren
        if $scope.collapsed
          if $scope.children.length == 0 and $scope.numberOfChildren > 0
            $scope.loadingChildren = true
            fun = $scope.element[$scope.currentDescend]
            if angular.isFunction(fun)
              fun().then (list) ->

                newChildren = []
                for item in list.list when item.relation
                  newChildren.push(angular.extend(item.relation, {metadata: item.ext}))

                $scope.children = newChildren
                $scope.collapsed  = false
                $scope.hasMore    = $scope.numberOfChildren > $scope.children.length
                if $scope.hasMore
                  $scope.showMore = createShowMore(list)
                else
                  $scope.showMore = ->
                $scope.loadingChildren = false
            else
              $scope.loadingChildren = false
          else
            $scope.collapsed = false
            $scope.select($scope.element)
        else
          $scope.collapsed = true

      $scope.$watch 'element', onElementUpdate
      $scope.$watch 'descend', ->
        onElementUpdate($scope.element)

      onElementUpdate($scope.element)
    ]
  }
]