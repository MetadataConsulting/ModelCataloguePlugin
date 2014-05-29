angular.module('mc.core.ui.states.defaultStates', ['ui.router'])
.controller('mc.core.ui.states.ShowCtrl', ['$scope', '$stateParams', '$state', '$log', 'element', ($scope, $stateParams, $state, $log, element) ->
    $scope.element  = element
])
.controller('mc.core.ui.states.ListCtrl', ['$scope', '$stateParams', '$state', '$log', 'list', 'names', 'enhance', ($scope, $stateParams, $state, $log, list, names, enhance) ->
    listEnhancer    = enhance.getEnhancer('list')

    $scope.list                     = list
    $scope.title                    = names.getNaturalName($stateParams.resource)
    $scope.natural                  = (name) -> if name then names.getNaturalName(name) else "General"
    $scope.resource                 = $stateParams.resource
    $scope.contained                = {}
    $scope.contained.elements       = listEnhancer.createEmptyList('org.modelcatalogue.core.DataElement')
    $scope.selectedElement          = if list.size > 0 then list.list[0] else {name: 'No Selection'}
    $scope.contained.columns        = [
      {header: 'Name',          value: "relation.name",        classes: 'col-md-6', show: "relation.show()"}
      {header: 'Description',   value: "relation.description", classes: 'col-md-6'}
    ]

    if $scope.resource == 'model'
      for item in list
        item._containedElements_ = listEnhancer.createEmptyList('org.modelcatalogue.core.DataElement')

      $scope.$on 'treeviewElementSelected', (event, element) ->
        unless element._containedElements_?.empty
          element.contains().then (contained)->
            element._containedElements_ = contained
            $scope.contained.elements   = contained
        $scope.selectedElement          = element
        $scope.contained.elements       = element._containedElements_ ? listEnhancer.createEmptyList('org.modelcatalogue.core.DataElement')
])
.config(['$stateProvider', ($stateProvider) ->

  DEFAULT_ITEMS_PER_PAGE = 10

  $stateProvider.state 'mc', {
    abstract: true
    url: '/catalogue'
    templateUrl: 'modelcatalogue/core/ui/state/parent.html'
  }
  $stateProvider.state 'mc.resource', {
    abstract: true
    url: '/:resource'
    templateUrl: 'modelcatalogue/core/ui/state/parent.html'
  }
  $stateProvider.state 'mc.resource.list', {
    url: '/all?page&order&sort'

    templateUrl: 'modelcatalogue/core/ui/state/list.html'

    resolve:
        list: ['$stateParams','catalogueElementResource', ($stateParams,catalogueElementResource) ->
          page = parseInt($stateParams.page ? 1, 10)
          page = 1 if isNaN(page)
          # it's safe to call top level for each controller, only model controller will respond on it
          params = offset: (page - 1) * DEFAULT_ITEMS_PER_PAGE, toplevel: true
          params.order = $stateParams.order ? 'asc'
          params.sort  = $stateParams.sort ? 'name'
          catalogueElementResource($stateParams.resource).list(params)
        ]

    controller: 'mc.core.ui.states.ListCtrl'
  }
  $stateProvider.state 'mc.resource.show', {
    url: '/{id:\\d+}'

    templateUrl: 'modelcatalogue/core/ui/state/show.html'

    resolve:
        element: ['$stateParams','catalogueElementResource', ($stateParams, catalogueElementResource) ->
          catalogueElementResource($stateParams.resource).get($stateParams.id)
        ]

    controller: 'mc.core.ui.states.ShowCtrl'
  }

  $stateProvider.state 'mc.resource.show.property', {url: '/:property?page'}

  $stateProvider.state('mc.search', {
      url: "/search/{searchString}",
      templateUrl: 'modelcatalogue/core/ui/state/list.html'
      resolve: {
        list: ['$stateParams','modelCatalogueSearch', ($stateParams, modelCatalogueSearch) ->
          $stateParams.resource = "dataElement"
          return modelCatalogueSearch($stateParams.searchString)
        ]
      },
      controller: 'mc.core.ui.states.ListCtrl'
  })


  $stateProvider.state('mc.dataArchitect', {
      abstract: true,
      url: "/dataArchitect"
      templateUrl: 'modelcatalogue/core/ui/state/parent.html'
  })

  $stateProvider.state 'mc.dataArchitect.uninstantiatedDataElements', {
    url: "/uninstantiatedDataElements",
    templateUrl: 'modelcatalogue/core/ui/state/list.html'
    resolve:
      list: ['$stateParams', 'modelCatalogueDataArchitect', ($stateParams, modelCatalogueDataArchitect) ->
        $stateParams.resource = "dataElement"
        # it's safe to call top level for each controller, only model controller will respond on it
        modelCatalogueDataArchitect.uninstantiatedDataElements()
      ]

    controller: 'mc.core.ui.states.ListCtrl'
  }

#  $stateProvider.state 'mc.dataArchitect.findSynonym', {
#    url: "/findSynonymWithKeyValue",
#    templateUrl: 'modelcatalogue/core/ui/state/parent.html'
#    controller: ['$state','$modal',($state, $modal)->
#      $state.go('mc.dataArchitect.findSynonymWithKeyValue', {"Data item No.","Optional_Local_Identifier"})
#
#    ]
#  }


  $stateProvider.state 'mc.dataArchitect.metadataKey', {
    url: "/metadataKeyCheck",
    templateUrl: 'modelcatalogue/core/ui/state/parent.html'
    controller: ['$state','$modal',($state, $modal)->
      dialog = $modal.open {
        windowClass: 'messages-modal-prompt'
        template: '''
         <div class="modal-header">
            <h4>please enter metadata key</h4>
        </div>
        <div class="modal-body">
            <form role="form" ng-submit="$close(value)">
            <div class="form-group">
                <label for="value">metadata key</label>
                <input type="text" id="value" ng-model="value" class="form-control">
            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" ng-click="$close(value)">OK</button>
            <button class="btn btn-warning" ng-click="$dismiss()">Cancel</button>
        </div>
        '''
      }
      dialog.result.then (result) ->
        $state.go('mc.dataArchitect.metadataKeyCheck', {'metadata':result})

    ]
  }

  $stateProvider.state 'mc.dataArchitect.metadataKeyCheck', {
    url: "/metadataKey/{metadata}",
    templateUrl: 'modelcatalogue/core/ui/state/list.html'
    resolve:
      list: ['$stateParams', 'modelCatalogueDataArchitect', ($stateParams, modelCatalogueDataArchitect) ->
        $stateParams.resource = "dataElement"
        # it's safe to call top level for each controller, only model controller will respond on it
        return modelCatalogueDataArchitect.metadataKeyCheck($stateParams.metadata)
      ]

    controller: 'mc.core.ui.states.ListCtrl'
  }

  $stateProvider.state 'mc.dataArchitect.findRelationsByMetadataKeys', {
    url: "/findRelationsByMetadataKeys",
    templateUrl: 'modelcatalogue/core/ui/state/parent.html',
    controller: ['$scope','$state','$modal',($scope, $state, $modal)->
      dialog = $modal.open {
        windowClass: 'messages-modal-prompt'
        template: '''
       <div class="modal-header">
          <h4>please enter metadata key</h4>
      </div>
      <div class="modal-body">
          <form role="form">
          <div class="form-group">
              <label for="keyOne">metadata key one</label>
              <input type="text" id="keyOne" ng-model="result.keyOne" class="form-control">
              <label for="keyTwo">metadata key two</label>
              <input type="text" id="keyTwo" ng-model="result.keyTwo" class="form-control">
          </form>
      </div>
      <div class="modal-footer">
          <button class="btn btn-primary" ng-click="$close(result)">OK</button>
          <button class="btn btn-warning" ng-click="$dismiss()">Cancel</button>
      </div>
      '''
      }

      dialog.result.then (result) ->
        $state.go('mc.dataArchitect.showMetadataRelations', {'keyOne':result.keyOne, 'keyTwo':result.keyTwo})
    ]
  }

  $stateProvider.state 'mc.dataArchitect.showMetadataRelations', {
    url: "/showMetadataRelations/{keyOne}/{keyTwo}",
    templateUrl: 'modelcatalogue/core/ui/state/list.html'
    resolve:
      list: ['$stateParams', 'modelCatalogueDataArchitect', ($stateParams, modelCatalogueDataArchitect) ->
        $stateParams.resource = "dataElement"
        # it's safe to call top level for each controller, only model controller will respond on it
        return modelCatalogueDataArchitect.findRelationsByMetadataKeys($stateParams.keyOne, $stateParams.keyTwo)
      ]

    controller: 'mc.core.ui.states.ListCtrl'
  }

])
.run(['$rootScope', '$state', '$stateParams', ($rootScope, $state, $stateParams) ->
    # It's very handy to add references to $state and $stateParams to the $rootScope
    # so that you can access them from any scope within your applications.For example,
    # <li ui-sref-active="active }"> will set the <li> // to active whenever
    # 'contacts.list' or one of its decendents is active.
    $rootScope.$state = $state
    $rootScope.$stateParams = $stateParams
])
.run(['$templateCache', ($templateCache) ->

  $templateCache.put 'modelcatalogue/core/ui/state/parent.html', '''
    <ui-view></ui-view>
  '''

  $templateCache.put 'modelcatalogue/core/ui/state/list.html', '''
    <div ng-if="list.total &amp;&amp; resource != 'model'">
      <span class="pull-right">
        <div class="btn-group btn-group-sm">
          <button type="button" class="btn btn-primary dropdown-toggle" ng-disabled="list.availableReports &amp;&amp; list.availableReports.length == 0">
            <span class="glyphicon glyphicon-download-alt"></span> Export <span class="caret"></span>
          </button>
          <ul class="dropdown-menu" role="menu">
            <li><a ng-href="{{report.url}}" target="_blank" ng-repeat="report in list.availableReports">{{natural(report.name)}}</a></li>
          </ul>
        </div>
      </span>
      <h2>{{title}} List</h2>
      <decorated-list list="list"></decorated-list>
    </div>
    <div ng-if="resource == 'model'">
      <div class="row">
        <div class="col-md-4"><h2>Model Hierarchy</h2></div>
        <div class="col-md-8"><h3 ng-show="selectedElement">{{selectedElement.name}} Data Elements</h3></div>
      </div>
      <div class="row">
        <div class="col-md-4">
          <catalogue-element-treeview list="list" descend="'parentOf'"></catalogue-element-treeview>
        </div>
        <div class="col-md-8">
          <decorated-list list="contained.elements" columns="contained.columns" stateless="true"></decorated-list>
        </div>
        <hr/>
      </div>
    </div>
  '''

  $templateCache.put 'modelcatalogue/core/ui/state/show.html', '''
    <div ng-show="element">
      <catalogue-element-view element="element"></catalogue-element-view>
    </div>
  '''
])
# debug states
#.run(['$rootScope', '$log', ($rootScope, $log) ->
#  $rootScope.$on '$stateChangeSuccess', (event, toState, toParams, fromState, fromParams) ->
#    $log.info "$stateChangeSuccess", toState, toParams, fromState, fromParams
#])