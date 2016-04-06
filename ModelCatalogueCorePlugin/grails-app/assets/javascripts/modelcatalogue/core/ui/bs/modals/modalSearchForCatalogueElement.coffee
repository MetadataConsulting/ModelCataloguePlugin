module = angular.module('mc.core.ui.bs.modalSearchForCatalogueElement', ['mc.util.messages', 'mc.util.ui.focusMe'])
module.config ['messagesProvider', (messagesProvider)->
  messagesProvider.setPromptFactory 'search-catalogue-element',  [ '$modal', 'names',  ($modal, names) ->
    (title, body, args) ->
      dialog = $modal.open {
        size: 'lg'
        backdrop: true
        template: """
        <div class="modal-header" ng-if="title">
          <h4>{{title}}</h4>
        </div>
        <div class="modal-body" ng-keydown="keydown($event)">
            <div class="search-lg">
              <div class="input-group input-group-lg">
                <span class="input-group-addon"><span class="fa fa-fw fa-search"></span></span>
                <input id="value" class="form-control" ng-model="query" placeholder="Search for #{names.getNaturalName(names.getPropertyNameFromType(args.resource ? 'catalogueElement'))}" ng-model-options="{debounce: 500}" focus-me="true" autofocus='true'>
                <div class="input-group-addon with-pointer" ng-click='$dismiss()'><span class='fa fa-fw fa-close'></span></div>
              </div>
              <p class='help-block' ng-if='currentDataModel'>
                <span ng-if="!global">Showing only results from {{currentDataModel.name}} and its imports. <a ng-if="allowGlobal" ng-click='setGlobal(true)'>Show All</a><span ng-if='allowGlobal &amp;&amp; canAddImports'> or </span><a ng-if='canAddImports' ng-click='addImport()'>Add Import</a></span>
                <span ng-if="global">Showing all results. <a ng-click='setGlobal(false)'>Show only results from {{currentDataModel.name}} and its imports </a></span>
              </p>
            </div>
            <div ng-if="elements.length == 0 &amp;&amp; !loading">
              <div class="leave-10-before"></div>
              <alert type="warning" >No Results</alert>
            </div>
            <div>
              <div class="list-group">
                <div class="leave-10-before"></div>
                <a ng-repeat="element in elements" class="list-group-item with-pointer item-found" ng-class="{'list-group-item-warning': element.status == 'DRAFT', 'list-group-item-info': element.status == 'PENDING', 'list-group-item-danger': element.status == 'DEPRECATED', 'active': $index == selected}" ng-click="$close(element)">
                    <h4 class="list-group-item-heading"><catalogue-element-icon type="element.elementType"></catalogue-element-icon> <span class='classified-name'>{{element.classifiedName}}</span></h4>
                    <p ng-if="element.modelCatalogueId || element.internalModelCatalogueId" class="search-model-catalogue-id small">{{element.modelCatalogueId || element.internalModelCatalogueId}}</p>
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

        controller: ['$scope', 'catalogueElementResource', '$modalInstance', '$window', '$state', 'security', 'messages', ($scope, catalogueElementResource, $modalInstance, $window, $state, security, messages) ->
          $scope.title = title
          $scope.currentDataModel = args.currentDataModel

          if args.global == 'allow'
            $scope.allowGlobal = true
            $scope.global = false
          else if args.global == true
            $scope.allowGlobal = true
            $scope.global = true
          else
            $scope.allowGlobal = false
            $scope.global = false

          $scope.canAddImports = security.hasRole('CURATOR')

          $scope.setGlobal = (global) ->
            $scope.global = global
            listOrSearch($scope.query ? args.query, replaceElements)

          $scope.addImport = ->
            unless $scope.currentDataModel
              messages.info('There is no contextual data model present at the moment')
              return
            messages.prompt('Add Data Model Import', 'If you want to reuse data classes, data types or measurement units form different data models you need to import the containing data model first.', {type: 'catalogue-elements', resource: 'dataModel', status: 'finalized' }).then (elements) ->
              angular.forEach elements, (element) ->
                unless angular.isString(element)
                  $scope.currentDataModel.imports.add(element).then ->
                    listOrSearch($scope.query ? args.query, replaceElements)

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

            if not $scope.global and $state.params.dataModelId and $state.params.dataModelId != 'catalogue'
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

          if $state.params.dataModelId and not $scope.currentDataModel
            catalogueElementResource('dataModel').get($state.params.dataModelId).then (dataModel) ->
              $scope.currentDataModel = dataModel

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