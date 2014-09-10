angular.module('mc.core.ui.states.defaultStates', ['ui.router', 'mc.util.ui'])

.controller('mc.core.ui.states.DashboardCtrl', ['$rootScope', '$scope', '$stateParams', '$state', '$log', 'security', 'catalogue', 'modelCatalogueApiRoot', 'user', 'messages', 'applicationTitle', 'names', 'statistics', ($rootScope, $scope, $stateParams, $state, $log, security, catalogue, modelCatalogueApiRoot, user, messages, applicationTitle, names, statistics) ->
    applicationTitle "Model Catalogue"

    angular.extend $scope, statistics

    updateDashboard = (userName) ->
      $scope.user  = userName
      catalogue.getStatistics().then ((result)->
        angular.extend $scope,  result
      )

    $rootScope.$on('userLoggedIn', (ignored, user) ->
      updateDashboard(user.data.displayName)
    )

    $scope.create = (what) ->
      dialogType = "create-#{what}"
      if not messages.hasPromptFactory(dialogType)
        dialogType = "edit-#{what}"
      messages.prompt("New #{names.getNaturalName(what)}", '', {type: dialogType, create: what}).then (element)->
        element.show()

    if user!=''
      updateDashboard(user)
    else
      $scope.user = user
      $scope.totalDataElementCount = ''
      $scope.draftDataElementCount = ''
      $scope.finalizedDataElementCount = ''
      $scope.totalDataSetCount = ''
      $scope.draftDataSetCount = ''
      $scope.finalizedDataSetCount = ''
      $scope.totalModelCount = ''
      $scope.draftModelCount = ''
      $scope.finalizedModelCount = ''
      $scope.pendingActionCount = ''
      $scope.failedActionCount = ''
      $scope.activeBatchCount = ''
      $scope.archivedBatchCount = ''
      $scope.uninstantiatedDataElements = ''
      $scope.relationshipTypeCount = ''
      $scope.measurementUnitCount = ''
      $scope.dataTypeCount = ''
      $scope.valueDomainCount = ''
      $scope.incompleteValueDomainsCount = ''
      $scope.conceptualDomainCount = ''
      $scope.totalAssetCount = ''
      $scope.draftAssetCount = ''
      $scope.finalizedAssetCount = ''


  ])

.controller('mc.core.ui.states.ShowCtrl', ['$scope', '$stateParams', '$state', '$log', 'element', ($scope, $stateParams, $state, $log, element) ->
    $scope.element  = element
])

.controller('mc.core.ui.states.DataImportCtrl', ['$scope', '$stateParams', '$state', '$log', 'element', ($scope, $stateParams, $state, $log, element) ->
    $scope.element  = element
])

.controller('mc.core.ui.states.BatchCtrl', ['$scope', '$stateParams', '$state', '$log', 'element', 'applicationTitle', ($scope, $stateParams, $state, $log, element, applicationTitle) ->
    $scope.element  = element
    applicationTitle "Actions in batch #{element.name}"
])

.controller('mc.core.ui.states.ListCtrl', ['$scope', '$stateParams', '$state', '$log', 'list', 'names', 'enhance', 'applicationTitle', ($scope, $stateParams, $state, $log, list, names, enhance, applicationTitle) ->
    listEnhancer    = enhance.getEnhancer('list')

    if $stateParams.resource
      applicationTitle  "#{names.getNaturalName($stateParams.resource)}s"

    $scope.list                     = list
    $scope.title                    = names.getNaturalName($stateParams.resource)
    $scope.natural                  = (name) -> if name then names.getNaturalName(name) else "General"
    $scope.resource                 = $stateParams.resource
    $scope.contained                = $scope.$new(true)
    $scope.contained.noStatusSwitch = $scope.$new(true)
    $scope.contained.list           = listEnhancer.createEmptyList('org.modelcatalogue.core.DataElement')
    $scope.contained.element        = if list.size > 0 then list.list[0]

    printLocalIdentifiers = (relationship) ->
      result = ''
      ext     = relationship?.relation?.ext ? {}
      #local identifiers will be added as extensions (metadata) and may look like these:
      #"identifier","local identifier","local_identifier","local identifier","optional_local_identifier","optional local identifier"
      for key, value of ext
        if key.toLowerCase().indexOf("identifier") != -1
          if value?.length   #if value is not empty or null
            result += "#{value ? ''}, "

      if(result.indexOf(",") != -1)
        result = result.substring(0,result.lastIndexOf(","))
      result

    $scope.contained.columns        = [
      {header: 'Name',          value: "relation.name",        classes: 'col-md-3', show: "relation.show()"}
      {header: 'Description',   value: "relation.description", classes: 'col-md-6'}
      {header: 'Local Identifier', value:  printLocalIdentifiers,     classes: 'col-md-2'}
    ]


    if $scope.resource == 'model'
      for item in list
        item._containedElements_ = listEnhancer.createEmptyList('org.modelcatalogue.core.DataElement')

      $scope.$on 'treeviewElementSelected', (event, element) ->
        unless element._containedElements_?.size?
          element.contains().then (contained)->
            element._containedElements_ = contained
            $scope.contained.list       = contained
        $scope.contained.element        = element
        $scope.contained.list           = element._containedElements_ ? listEnhancer.createEmptyList('org.modelcatalogue.core.DataElement')

    else if $scope.resource == 'newRelationships'
      $scope.columns = [
        {header: "source",          value: 'source.name',           class: 'col-md-6' }
        {header: "destination",     value: 'destination.name',      class: 'col-md-6' }
      ]

  ])
.config(['$stateProvider', ($stateProvider) ->

  DEFAULT_ITEMS_PER_PAGE = 10

  $stateProvider.state 'dashboard', {
      url: '/dashboard'
      templateUrl: 'modelcatalogue/core/ui/state/dashboard.html',
      controller: 'mc.core.ui.states.DashboardCtrl'
      resolve:
        user: ['security', (security) ->
          if security.getCurrentUser() then return security.getCurrentUser().displayName else return ''
        ]
        statistics: ['catalogue', (catalogue) ->
          catalogue.getStatistics()
        ]
  }

  $stateProvider.state 'mc', {
    abstract: true
    url: '/catalogue'
    templateUrl: 'modelcatalogue/core/ui/state/parent.html'
  }

  $stateProvider.state('mc.actions', {
    abstract: true,
    url: "/actions/batch"
    templateUrl: 'modelcatalogue/core/ui/state/parent.html'
  })

  $stateProvider.state 'mc.actions.show', {
    url: '/{id:\\d+}'
    templateUrl: 'modelcatalogue/core/ui/state/batch.html'
    resolve:
      element: ['$stateParams','catalogueElementResource', ($stateParams, catalogueElementResource) ->
        $stateParams.resource = "batch"
        return catalogueElementResource('batch').get($stateParams.id)
      ]

    controller: 'mc.core.ui.states.BatchCtrl'
  }

  $stateProvider.state 'mc.resource', {
    abstract: true
    url: '/:resource'
    templateUrl: 'modelcatalogue/core/ui/state/parent.html'
  }
  $stateProvider.state 'mc.resource.list', {
    url: '/all?page&order&sort&status&q&max&classification'

    templateUrl: 'modelcatalogue/core/ui/state/list.html'

    resolve:
        list: ['$stateParams','catalogueElementResource', ($stateParams,catalogueElementResource) ->
          page = parseInt($stateParams.page ? 1, 10)
          page = 1 if isNaN(page)
          # it's safe to call top level for each controller, only model controller will respond on it
          params                = offset: (page - 1) * DEFAULT_ITEMS_PER_PAGE, toplevel: true, system: true
          params.order          = $stateParams.order ? 'asc'
          params.sort           = $stateParams.sort ? 'name'
          params.status         = $stateParams.status ? 'finalized'
          params.status         = $stateParams.status ? 'finalized'
          params.max            = $stateParams.max ? 10
          params.classification = $stateParams.classification ? undefined

          if $stateParams.q
            return catalogueElementResource($stateParams.resource).search($stateParams.q, params)

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

  $stateProvider.state 'mc.resource.show.property', {url: '/:property?page&sort&order&max&q'}

  $stateProvider.state('mc.search', {
      url: "/search/{q}",
      templateUrl: 'modelcatalogue/core/ui/state/list.html'
      resolve: {
        list: ['$stateParams','modelCatalogueSearch', ($stateParams, modelCatalogueSearch) ->
          $stateParams.resource = "searchResult"
          return modelCatalogueSearch($stateParams.q)
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
    url: '/imports'
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

  #language=HTML
  $templateCache.put 'modelcatalogue/core/ui/state/list.html', '''
    <div ng-if="resource != 'model'">
      <span class="pull-right">
        <contextual-actions size="sm" no-colors="true"></contextual-actions>
      </span>
      <h2><small ng-class="catalogue.getIcon(resource)"></small>&nbsp; {{title}} List</h2>
      <decorated-list list="list" columns="columns" state-driven="true"></decorated-list>
    </div>
    <div ng-if="resource == 'model'">
      <div class="row">
        <div class="col-md-4">
          <h2>
            <small ng-class="catalogue.getIcon('model')"></small>&nbsp; Models
            <span class="pull-right">
            <contextual-actions size="sm" icon-only="true" no-colors="true"></contextual-actions>
            </span>
          </h2>
        </div>
        <div class="col-md-8">
          <h3 ng-show="contained.element">{{contained.element.name}}
            <span class="pull-right">
              <contextual-actions size="sm" no-colors="true" icon-only="true" scope="contained"></contextual-actions>
            </span>
          </h3>
          <h3 ng-hide="contained.element">No Selection</h3>
        </div>
      </div>
      <div class="row">
        <div class="col-md-4">
          <catalogue-element-treeview list="list" descend="'parentOf'"></catalogue-element-treeview>
        </div>
        <div class="col-md-8" ng-show="contained.element">
          <blockquote class="ce-description" ng-show="contained.element.description">{{contained.element.description}}</blockquote>
          <h4>Data Elements</h4>
          <decorated-list list="contained.list" columns="contained.columns" stateless="true"></decorated-list>
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

  $templateCache.put 'modelcatalogue/core/ui/state/batch.html', '''
    <div ng-show="element">
      <batch-view batch="element"></batch-view>
    </div>
  '''

  #language=HTML
  $templateCache.put 'modelcatalogue/core/ui/state/dashboard.html', '''
    		<!-- Jumbotron -->
  <div hide-if-logged-in>
		<div class="jumbotron">
			<h1>Model Catalogue</h1>
			<p class="lead">
				<b><em>Model</em></b> existing business processes and context. <b><em>Design</em></b>
				new pathways, forms, data storage, studies. <b><em>Generate</em></b> better
				software components
			</p>

      <form ng-controller="metadataCurator.userCtrl">
         <button ng-click="login()" class="btn btn-large btn-primary" type="submit">Login <i class="glyphicon glyphicon-log-in"></i></button>
         <a href="" class="btn btn-large btn-primary" >Sign Up <i class="glyphicon glyphicon-pencil"></i></a>
      </form>
    </div>

		<!-- Example row of columns -->
		<div id="info" class="row">
      <div class="col-sm-4">
				<h2>Architecture</h2>
				<p>Track your data elements from collection - model services,
					databases and warehouses. Generate your own feeds, and generate
					components for integration engines.</p>
				<p>
					<a href="#">More info&hellip;</a>
				</p>
			</div>
			<div class="col-sm-4">
				<h2>Forms</h2>
				<p>Build forms from standard data elements in our friendly
					drag-n-drop interface. Export your forms to your favourite tool.</p>
				<p>
					<a href="#">Coming soon&hellip;</a>
				</p>
			</div>
      <div class="col-sm-4">
				<h2>Pathways</h2>
				<p>Design your workflows and visualise your patient pathways.
					Annotate nodes with data elements, forms, and decisions.
					Automatically build databases, dashboard interfaces and reporting
					data.</p>
				<p>
					<a href="#">Coming soon&hellip;</a>
				</p>
			</div>

		</div>
</div>

<div show-if-logged-in>
      <div class="row">
                    <div class="col-lg-4 col-sm-6 col-md-4">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                <div class="row">
                                    <div class="col-xs-3">
                                        <a ui-sref="mc.resource.list({resource: 'classification'})" ui-sref-opts="{inherit: false}"><i class="fa fa-tags fa-5x fa-fw"></i></a>
                                    </div>
                                    <div class="col-xs-9 text-right">
                                        <div><a id="dataSetsLink" ui-sref="mc.resource.list({resource: 'classification'})" ui-sref-opts="{inherit: false}"> Classifications</a> {{totalDataSetCount}} </div>
                                    </div>
                                </div>
                            </div>

                            <a show-for-role="CURATOR" ng-click="create('classification')">
                                <div class="panel-footer">
                                    <span class="pull-left">Create Classification</span>
                                    <span class="pull-right"><i class="fa fa-magic"></i></span>
                                    <div class="clearfix"></div>
                                </div>
                            </a>
                        </div>
                    </div>
                    <div class="col-lg-4 col-sm-6 col-md-4">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                <div class="row">
                                    <div class="col-xs-3">
                                        <a ui-sref="mc.resource.list({resource: 'model'})" ui-sref-opts="{inherit: false}"><i class="fa fa-cubes fa-5x fa-fw"></i></a>
                                    </div>
                                    <div class="col-xs-9 text-right">
                                        <div><a id="modelsLink" ui-sref="mc.resource.list({resource: 'model'})" ui-sref-opts="{inherit: false}">Finalized Models</a> {{finalizedModelCount}} </div>
                                        <div><a id="modelsLink" ui-sref="mc.resource.list({resource: 'model', status:'draft'})" ui-sref-opts="{inherit: false}">Draft Models</a> {{draftModelCount}}</div>

                                    </div>
                                </div>
                            </div>
                            <a show-for-role="CURATOR" ng-click="create('model')">
                                <div class="panel-footer">
                                    <span class="pull-left">Create Model</span>
                                    <span class="pull-right"><i class="fa fa-magic"></i></span>
                                    <div class="clearfix"></div>
                                </div>
                            </a>
                        </div>
                    </div>
                    <div class="col-lg-4 col-sm-6 col-md-4">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                <div class="row">
                                    <div class="col-xs-3">
                                        <a ui-sref="mc.resource.list({resource: 'dataElement'})" ui-sref-opts="{inherit: false}"><i class="fa fa-cube fa-5x fa-fw"></i></a>
                                    </div>
                                    <div class="col-xs-9 text-right">
                                        <div><a id="modelsLink" ui-sref="mc.resource.list({resource: 'dataElement'})" ui-sref-opts="{inherit: false}">Finalized Data Elements</a> {{finalizedDataElementCount}} </div>
                                        <div><a id="modelsLink" ui-sref="mc.resource.list({resource: 'dataElement', status:'draft'})" ui-sref-opts="{inherit: false}">Draft Data Elements</a> {{draftDataElementCount}}</div>
                                        <div><a id="modelsLink" ui-sref="mc.dataArchitect.uninstantiatedDataElements" ui-sref-opts="{inherit: false}">Uninstantiated Data Elements</a>  {{uninstantiatedDataElementCount}}</div>
                                    </div>
                                </div>
                            </div>
                            <a show-for-role="CURATOR" ng-click="create('dataElement')">
                                <div class="panel-footer">
                                    <span class="pull-left">Create Data Element</span>
                                    <span class="pull-right"><i class="fa fa-magic"></i></span>
                                    <div class="clearfix"></div>
                                </div>
                            </a>
                        </div>
                    </div>
                    <div class="col-lg-4 col-sm-6 col-md-4">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                <div class="row">
                                    <div class="col-xs-3">
                                        <a ui-sref="mc.resource.list({resource: 'conceptualDomain'})" ui-sref-opts="{inherit: false}"><i class="fa fa-cogs fa-5x fa-fw"></i></a>
                                    </div>
                                    <div class="col-xs-9 text-right">
                                        <div><a id="conceptualDomainLink" ui-sref="mc.resource.list({resource: 'conceptualDomain'})" ui-sref-opts="{inherit: false}"> Conceptual Domains</a> {{conceptualDomainCount}} </div>
                                    </div>
                                </div>
                            </div>

                            <a show-for-role="CURATOR" ng-click="create('conceptualDomain')">
                                <div class="panel-footer">
                                    <span class="pull-left">Create Conceptual Domain</span>
                                    <span class="pull-right"><i class="fa fa-magic"></i></span>
                                    <div class="clearfix"></div>
                                </div>
                            </a>
                        </div>
                    </div>
                    <div class="col-lg-4 col-sm-6 col-md-4">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                <div class="row">
                                    <div class="col-xs-3">
                                        <a ui-sref="mc.resource.list({resource: 'valueDomain'})" ui-sref-opts="{inherit: false}"><i class="fa fa-cog fa-5x fa-fw"></i></a>
                                    </div>
                                    <div class="col-xs-9 text-right">
                                        <div><a id="valueDomainLink" ui-sref="mc.resource.list({resource: 'valueDomain'})" ui-sref-opts="{inherit: false}"> Value Domains</a> {{valueDomainCount}} </div>
                                        <div><a id="incompleteValueDomainLink" ui-sref="mc.resource.list({resource: 'valueDomain', status: 'incomplete'})" ui-sref-opts="{inherit: false}"> Incomplete Value Domains</a> {{incompleteValueDomainsCount}} </div>
                                    </div>
                                </div>
                            </div>

                            <a show-for-role="CURATOR" ng-click="create('valueDomain')">
                                <div class="panel-footer">
                                    <span class="pull-left">Create Value Domain</span>
                                    <span class="pull-right"><i class="fa fa-magic"></i></span>
                                    <div class="clearfix"></div>
                                </div>
                            </a>
                        </div>
                    </div>
                    <div class="col-lg-4 col-sm-6 col-md-4">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                <div class="row">
                                    <div class="col-xs-3">
                                        <a ui-sref="mc.resource.list({resource: 'dataType'})" ui-sref-opts="{inherit: false}"><i class="fa fa-th-large fa-5x fa-fw"></i></a>
                                    </div>
                                    <div class="col-xs-9 text-right">
                                        <div><a id="dataTypesLink" ui-sref="mc.resource.list({resource: 'dataType'})" ui-sref-opts="{inherit: false}">Data Types</a> {{dataTypeCount}} </div>
                                    </div>
                                </div>
                            </div>
                            <a show-for-role="CURATOR" ng-click="create('dataType')">
                                <div class="panel-footer">
                                    <span class="pull-left" >Create Data Type</span>
                                    <span class="pull-right"><i class="fa fa-magic"></i></span>
                                    <div class="clearfix"></div>
                                </div>
                            </a>
                        </div>
                    </div>
                    <div class="col-lg-4 col-sm-6 col-md-4">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                <div class="row">
                                    <div class="col-xs-3">
                                        <a ui-sref="mc.resource.list({resource: 'measurementUnit'})" ui-sref-opts="{inherit: false}"><i class="fa fa-tachometer fa-5x fa-fw"></i></a>
                                    </div>
                                    <div class="col-xs-9 text-right">
                                        <div><a id="modelsLink" ui-sref="mc.resource.list({resource: 'measurementUnit'})" ui-sref-opts="{inherit: false}">Measurement Units</a> {{measurementUnitCount}} </div>
                                    </div>
                                </div>
                            </div>
                            <a show-for-role="CURATOR" ng-click="create('measurementUnit')">
                                <div class="panel-footer">
                                    <span class="pull-left">Create Measurement Unit</span>
                                    <span class="pull-right"><i class="fa fa-magic"></i></span>
                                    <div class="clearfix"></div>
                                </div>
                            </a>
                        </div>
                    </div>
                    <div class="col-lg-4 col-sm-6 col-md-4">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                <div class="row">
                                    <div class="col-xs-3">
                                        <a ui-sref="mc.resource.list({resource: 'asset'})" ui-sref-opts="{inherit: false}"><i class="fa fa-file-o fa-5x fa-fw"></i></a>
                                    </div>
                                    <div class="col-xs-9 text-right">
                                        <div><a id="modelsLink" ui-sref="mc.resource.list({resource: 'asset'})" ui-sref-opts="{inherit: false}">Finalized Assets</a> {{finalizedAssetCount}} </div>
                                        <div><a id="modelsLink" ui-sref="mc.resource.list({resource: 'asset', status:'draft'})" ui-sref-opts="{inherit: false}">Draft Assets</a> {{draftAssetCount}}</div>

                                    </div>
                                </div>
                            </div>
                            <a show-for-role="CURATOR" ng-click="create('asset')">
                                <div class="panel-footer">
                                    <span class="pull-left">Create Asset</span>
                                    <span class="pull-right"><i class="fa fa-magic"></i></span>
                                    <div class="clearfix"></div>
                                </div>
                            </a>
                        </div>
                    </div>
                  <div show-for-role="ADMIN" class="col-lg-4 col-sm-6 col-md-4">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                <div class="row">
                                    <div class="col-xs-3">
                                        <a ui-sref="mc.resource.list({resource: 'relationshipType'})" ui-sref-opts="{inherit: false}"><i class="fa fa-link fa-5x fa-fw"></i></a>
                                    </div>
                                    <div class="col-xs-9 text-right">
                                        <div><a id="modelsLink" ui-sref="mc.resource.list({resource: 'relationshipType'})" ui-sref-opts="{inherit: false}"> Relationship Types </a> {{relationshipTypeCount}}</div>
                                    </div>
                                </div>
                            </div>
                            <a ng-click="create('relationshipType')">
                                <div class="panel-footer">
                                    <span class="pull-left">Create Relationship Type</span>
                                    <span class="pull-right"><i class="fa fa-magic"></i></span>
                                    <div class="clearfix"></div>
                                </div>
                            </a>
                        </div>
                    </div>
                    <div show-for-role="ADMIN" class="col-lg-4 col-sm-6 col-md-4 hide">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                <div class="row">
                                    <div class="col-xs-3">
                                        <a ui-sref="mc.resource.list({resource: 'batch'})" ui-sref-opts="{inherit: false}"><i class="fa fa-flash fa-5x fa-fw"></i></a>
                                    </div>
                                    <div class="col-xs-9 text-right">
                                        <div><a id="batchesLink" ui-sref="mc.resource.list({resource: 'batch'})" ui-sref-opts="{inherit: false}">Active Batches</a> {{activeBatchCount}}</div>
                                        <div><a id="archivedbatchesLink" ui-sref="mc.resource.list({resource: 'batch', status: 'archived'})" ui-sref-opts="{inherit: false}">Archived Batches</a> {{archivedBatchCount}}</div>
                                        <!--<div><a>Pending Actions</a> {{pendingActionCount}} </div>-->
                                        <!--<div><a>Failed Actions</a> {{failedActionCount}} </div>-->
                                    </div>
                                </div>
                            </div>

                            <a ng-click="create('batch')">
                                <div class="panel-footer">
                                    <span class="pull-left">Create New Batch</span>
                                    <span class="pull-right"><i class="fa fa-magic"></i></span>
                                    <div class="clearfix"></div>
                                </div>
                            </a>
                        </div>
                    </div>
      </div>
    </div>
  '''

])
# debug states
#.run(['$rootScope', '$log', ($rootScope, $log) ->
#  $rootScope.$on '$stateChangeSuccess', (event, toState, toParams, fromState, fromParams) ->
#    $log.info "$stateChangeSuccess", toState, toParams, fromState, fromParams
#])