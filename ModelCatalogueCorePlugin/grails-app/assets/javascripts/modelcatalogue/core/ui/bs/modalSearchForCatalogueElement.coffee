module = angular.module('mc.core.ui.bs.modalSearchForCatalogueElement', ['mc.util.messages', 'mc.util.ui.focusMe'])
module.config ['messagesProvider', (messagesProvider)->
  messagesProvider.setPromptFactory 'search-catalogue-element',  [ '$modal', 'names',  ($modal, names) ->
    (title, body, args) ->
      dialog = $modal.open {
        size: 'lg'
        template: """
        <div class="modal-header" ng-if="title">
          <h4>{{title}}</h4>
        </div>
        <div class="modal-body" ng-keydown="keydown($event)">
            <div class="search-lg">
              <div class="input-group input-group-lg">
                <span class="input-group-addon"><span class="fa fa-fw fa-search"></span></span>
                <input id="value" class="form-control" ng-model="query" placeholder="Search for #{names.getNaturalName(names.getPropertyNameFromType(args.resource ? 'catalogueElement'))}" ng-model-options="{debounce: 500}" focus-me="true" autofocus='true'>
              </div>
            </div>
            <div class="search-divider" ng-if="loading || elements.length > 0"></div>
            <div>
              <div class class="list-group">
                <a ng-repeat="element in elements" class="list-group-item with-pointer item-found" ng-class="{'list-group-item-warning': element.status == 'DRAFT', 'list-group-item-info': element.status == 'PENDING', 'list-group-item-danger': element.status == 'DEPRECATED', 'active': $index == selected}" ng-click="$close(element)">
                    <h4 class="list-group-item-heading"><catalogue-element-icon type="element.elementType"></catalogue-element-icon> {{element.classifiedName}}</h4>
                    <p ng-if="element.description" class="list-group-item-text preserve-new-lines modal-search-for-catalogue-element-description">{{element.description}}</p>
                </a>
                <a class="list-group-item disabled" ng-if="loading">
                  <div class="text-center"><span class="fa fa-refresh fa-spin"></span></div>
                </a>
                <a class="list-group-item disabled with-pointer" ng-if="!loading &amp;&amp; list.next.size" ng-click="loadMore()">
                  <div class="text-center"><span class="fa fa-angle-double-down"></span></div>
                </a>
              </ul>
            </div>
        </div>
        """

        controller: ['$scope', 'catalogueElementResource', '$modalInstance', '$window', '$state', ($scope, catalogueElementResource, $modalInstance, $window, $state) ->
          $scope.title = title

          appendToElements = (list) ->
            $scope.list     = list
            $scope.elements = $scope.elements.concat list.list
            $scope.loading  = false

          replaceElements = (list) ->
            $scope.elements = []
            $scope.selected = -1
            appendToElements(list)

          reset = ->
            $scope.elements = []
            $scope.loading  = not args.empty
            $scope.selected = -1

          listOrSearch = (query, callback) ->
            params = {}
            params.status = args.status if args.status

            if $state.params.dataModelId and $state.params.dataModelId != 'catalogue'
              params.dataModel = $state.params.dataModelId

            if query
              catalogueElementResource(args.resource ? 'catalogueElement').search(query, params).then(callback)
            else unless args.empty
              catalogueElementResource(args.resource ? 'catalogueElement').list(params).then(callback)

          reset()
          listOrSearch($scope.query ? args.query, replaceElements)

          $scope.loadMore = ->
            $scope.loading = true
            $scope.list.next().then(appendToElements)

          $scope.$watch 'query', (query) ->
            $scope.loading  = if query then true else not args.empty
            listOrSearch(query, replaceElements)

          ARROW_DOWN = 40
          ARROW_UP   = 38
          ENTER      = 13

          $scope.keydown = ($event) ->
            if $event.keyCode == ARROW_UP
              $scope.selected = Math.max($scope.selected - 1, 0)
            else if $event.keyCode == ARROW_DOWN
              if $scope.selected < $scope.elements.length - 1
                $scope.selected = $scope.selected + 1
              else if not $scope.loading and $scope.list.next.size
                $scope.loadMore().then ->
                  $scope.selected = $scope.selected + 1
            else if $event.keyCode == ENTER and $scope.selected >= 0
              $modalInstance.close($scope.elements[$scope.selected])

            $scope.$evalAsync ->
              return unless $
              element = angular.element('.list-group-item.with-pointer.item-found.active')
              if element.length
                $('.modal').scrollTop(element[0].offsetTop - 100);

        ]
      }

      dialog.result
  ]
]