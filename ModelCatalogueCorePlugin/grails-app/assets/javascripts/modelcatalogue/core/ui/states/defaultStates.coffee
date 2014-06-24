angular.module('mc.core.ui.states.defaultStates', ['ui.router'])
.controller('mc.core.ui.states.ShowCtrl', ['$scope', '$stateParams', '$state', '$log', 'element', ($scope, $stateParams, $state, $log, element) ->
    $scope.element  = element
])

.controller('mc.core.ui.states.DataImportCtrl', ['$scope', '$stateParams', '$state', '$log', 'element', ($scope, $stateParams, $state, $log, element) ->
    $scope.element  = element
])

.controller('mc.core.ui.states.ListCtrl', ['$scope', '$stateParams', '$state', '$log', 'list', 'names', 'enhance', 'messages', ($scope, $stateParams, $state, $log, list, names, enhance, messages) ->
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

    $scope.canCreate = ->
      messages.hasPromptFactory('edit-' + $scope.resource) || messages.hasPromptFactory('new-' + $scope.resource)

    $scope.create = (resource = null) ->
      resource ?= $scope.resource

      if resource=="import"
        messages.prompt('New Import', '', {type: 'new-import', create: resource}).then (created)->
          created.show()

      else
        messages.prompt('Create ' + names.getNaturalName(resource), '', {type: 'edit-' + resource, create: (resource)}).then (created)->
          created.show()
#        $scope.list?.reload().then (result) ->
#          $scope.list = result


    $scope.getStatusButtonClass = ->
      return 'btn-info' if $stateParams.status == 'draft'
      return 'btn-warning' if $stateParams.status == 'pending'
      return 'btn-primary'


    $scope.getStatusIconClass = ->
      return 'glyphicon-pencil' if $stateParams.status == 'draft'
      return 'glyphicon-time'   if $stateParams.status == 'pending'
      return 'glyphicon-ok'


    $scope.switchStatus = (status) ->
      newParams = angular.copy($stateParams)
      if status == 'finalized'
        newParams.status = undefined
      else
        newParams.status = status
      $state.go 'mc.resource.list', newParams


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

    else if $scope.resource == 'newRelationships'
      $scope.columns = [
        {header: "source",          value: 'source.name',          class: 'col-md-6' }
        {header: "destination",        value: 'destination.name',        class: 'col-md-6' }
      ]

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
    url: '/all?page&order&sort&status'

    templateUrl: 'modelcatalogue/core/ui/state/list.html'

    resolve:
        list: ['$stateParams','catalogueElementResource', ($stateParams,catalogueElementResource) ->
          page = parseInt($stateParams.page ? 1, 10)
          page = 1 if isNaN(page)
          # it's safe to call top level for each controller, only model controller will respond on it
          params        = offset: (page - 1) * DEFAULT_ITEMS_PER_PAGE, toplevel: true
          params.order  = $stateParams.order ? 'asc'
          params.sort   = $stateParams.sort ? 'name'
          params.status = $stateParams.status ? 'finalized'
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

  $stateProvider.state 'mc.resource.uuid', {
    url: '/uuid/:uuid'

    templateUrl: 'modelcatalogue/core/ui/state/show.html'

    resolve:
      element: ['$stateParams','catalogueElementResource', ($stateParams, catalogueElementResource) ->
        catalogueElementResource($stateParams.resource).getByUUID($stateParams.uuid)
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
        $stateParams.resource = "newRelationships"
        # it's safe to call top level for each controller, only model controller will respond on it
        return modelCatalogueDataArchitect.findRelationsByMetadataKeys($stateParams.keyOne, $stateParams.keyTwo)
      ]

    controller: 'mc.core.ui.states.ListCtrl'
  }

  $stateProvider.state 'mc.dataArchitect.imports', {
    abstract: true
    url: '/:imports'
    templateUrl: 'modelcatalogue/core/ui/state/parent.html'
  }

  $stateProvider.state 'mc.dataArchitect.imports.list', {
    url: '/all?page&order&sort&status'
    templateUrl: 'modelcatalogue/core/ui/state/list.html'
    resolve:
      list: ['$stateParams','modelCatalogueDataArchitect', ($stateParams, modelCatalogueDataArchitect) ->
        $stateParams.resource = "import"
        page = parseInt($stateParams.page ? 1, 10)
        page = 1 if isNaN(page)
        # it's safe to call top level for each controller, only model controller will respond on it
        params        = offset: (page - 1) * DEFAULT_ITEMS_PER_PAGE, toplevel: true
        params.order  = $stateParams.order ? 'asc'
        params.sort   = $stateParams.sort ? 'name'
        return modelCatalogueDataArchitect.imports(params)
      ]

    controller: 'mc.core.ui.states.ListCtrl'
  }

  $stateProvider.state 'mc.dataArchitect.imports.show', {
    url: '/{id:\\d+}'
    templateUrl: 'modelcatalogue/core/ui/state/dataImport.html'
    resolve:
      element: ['$stateParams','modelCatalogueDataArchitect', ($stateParams, modelCatalogueDataArchitect) ->
        $stateParams.resource = "Import"
        return modelCatalogueDataArchitect.getImport($stateParams.id)
      ]

    controller: 'mc.core.ui.states.DataImportCtrl'
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
    <div ng-if="resource != 'model'">
      <span class="pull-right">
        <a  ng-click="create()" ng-show="canCreate() &amp;&amp; resource != 'dataType' &amp;&amp; $security.hasRole('CURATOR')" class="btn btn-sm btn-success"><span class="glyphicon glyphicon-plus-sign"></span> New {{title}}</a>
        <div class="btn-group btn-group-sm" ng-show="canCreate() &amp;&amp; resource == 'dataType' &amp;&amp; $security.hasRole('CURATOR')">
          <button type="button" class="btn btn-success dropdown-toggle">
            <span class="glyphicon glyphicon-download-alt"></span> New Data Type <span class="caret"></span>
          </button>
          <ul class="dropdown-menu" role="menu">
            <li><a ng-click="create('dataType')">New Data Type</a></li>
            <li><a ng-click="create('enumeratedType')">New Enumerated Type</a></li>
          </ul>
        </div>
        <div class="btn-group btn-group-sm" ng-show="(resource == 'dataElement' || resource == 'asset') &amp;&amp; $security.hasRole('CURATOR')">
          <button type="button" class="btn dropdown-toggle" ng-class="getStatusButtonClass()">
            <span class="glyphicon" ng-class="getStatusIconClass()"></span> {{natural($stateParams.status || 'finalized')}} <span class="caret"></span>
          </button>
          <ul class="dropdown-menu" role="menu">
            <li><a ng-click="switchStatus('draft')"><span class="glyphicon glyphicon-pencil"> Draft</a></li>
            <li><a ng-click="switchStatus('pending')"><span class="glyphicon glyphicon-time"> Pending</a></li>
            <li><a ng-click="switchStatus('finalized')"><span class="glyphicon glyphicon-ok"> Finalized</a></li>
          </ul>
        </div>
        <div class="btn-group btn-group-sm">
          <button type="button" class="btn btn-primary dropdown-toggle" ng-disabled="list.availableReports &amp;&amp; list.availableReports.length == 0">
            <span class="glyphicon glyphicon-download-alt"></span> Export <span class="caret"></span>
          </button>
          <ul class="dropdown-menu" role="menu">
            <li><a ng-href="{{report.url}}" target="_blank" ng-repeat="report in list.availableReports">{{report.title}}</a></li>
          </ul>
        </div>
      </span>
      <h2>{{title}} List</h2>
      <decorated-list list="list" columns="columns"></decorated-list>
    </div>
    <div ng-if="resource == 'model'">
      <div class="row">
        <div class="col-md-4">
          <h2>
            Model Hierarchy
            <span show-for-role="CURATOR" class="pull-right btn-group">
              <a  ng-click="create()" ng-show="canCreate() &amp;&amp; resource != 'dataType'" class="btn btn-sm btn-success"><span class="glyphicon glyphicon-plus-sign"></span></a>
              <div show-for-role="CURATOR" class="btn-group btn-group-sm">
                <button type="button" class="btn dropdown-toggle" ng-class="getStatusButtonClass()" title="Show">
                  <span class="glyphicon" ng-class="getStatusIconClass()"></span>
                </button>
                <ul class="dropdown-menu" role="menu">
                  <li><a ng-click="switchStatus('draft')"><span class="glyphicon glyphicon-pencil"> Draft</a></li>
                  <li><a ng-click="switchStatus('pending')"><span class="glyphicon glyphicon-time"> Pending</a></li>
                  <li><a ng-click="switchStatus('finalized')"><span class="glyphicon glyphicon-ok"> Finalized</a></li>
                </ul>
              </div>
            </span>
          </h2>
        </div>
        <div class="col-md-8">
          <h3 ng-show="selectedElement">{{selectedElement.name}} Data Elements
            <span class="pull-right">
              <div class="btn-group btn-group-sm">
                <button type="button" class="btn btn-primary dropdown-toggle" ng-disabled="contained &amp;&amp; contained.elements.availableReports &amp;&amp; contained.elements.availableReports.length == 0  &amp;&amp; !selectedElement.availableReports">
                  <span class="glyphicon glyphicon-download-alt"></span> Export <span class="caret"></span>
                </button>
                <ul class="dropdown-menu" role="menu">
                  <li role="presentation" class="dropdown-header">{{selectedElement.name}} Exports</li>
                  <li><a ng-href="{{report.url}}" target="_blank" ng-repeat="report in selectedElement.availableReports">{{report.title || 'Export'}}</a></li>
                  <li class="divider" role="presentation" ng-show="contained.elements.availableReports &amp;&amp; contained.elements.availableReports.length != 0 &amp;&amp; selectedElement.availableReports"></li>
                  <li role="presentation" class="dropdown-header" ng-show="contained.elements.availableReports &amp;&amp; contained.elements.availableReports.length != 0">Exports for Data Elements</li>
                  <li><a ng-href="{{report.url}}"  target="_blank" ng-repeat="report in contained.elements.availableReports">{{report.title || 'Export'}}</a></li>
                </ul>
              </div>
            </span>
          </h3>
        </div>
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

  $templateCache.put 'modelcatalogue/core/ui/state/dataImport.html', '''
    <div ng-show="element">
      <import-view element="element"></import-view>
    </div>
  '''

])
# debug states
#.run(['$rootScope', '$log', ($rootScope, $log) ->
#  $rootScope.$on '$stateChangeSuccess', (event, toState, toParams, fromState, fromParams) ->
#    $log.info "$stateChangeSuccess", toState, toParams, fromState, fromParams
#])