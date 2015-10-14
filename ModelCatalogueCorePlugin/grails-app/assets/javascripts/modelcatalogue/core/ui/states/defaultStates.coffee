angular.module('mc.core.ui.states.defaultStates', ['ui.router', 'mc.util.ui'])

.controller('mc.core.ui.states.DashboardCtrl', ['$rootScope', '$scope', '$stateParams', '$state', 'security', 'catalogue', 'modelCatalogueApiRoot', 'user', 'messages', 'applicationTitle', 'names', 'statistics', 'catalogueElementResource', ($rootScope, $scope, $stateParams, $state, security, catalogue, modelCatalogueApiRoot, user, messages, applicationTitle, names, statistics) ->
    applicationTitle "Model Catalogue"

    angular.extend $scope, statistics

    updateDashboard = (user) ->
      $scope.user  = user
      if user?.id
        $state.go 'mc.resource.list', resource: 'dataModel', status: undefined

    $scope.$on('userLoggedIn', (ignored, user) ->
      if user?.error
        updateDashboard undefined
      else
        updateDashboard user
    )

    $scope.convert = ->
      messages.prompt('', '', {type: 'convert-with-value-domain'})

    $scope.validate = ->
      messages.prompt('', '', {type: 'validate-value-by-domain'})

    if security.allowRegistration
      $scope.registrationUrl = "#{security.contextPath}/register/"

    $scope.create = (what) ->
      dialogType = "create-#{what}"
      if not messages.hasPromptFactory(dialogType)
        dialogType = "edit-#{what}"
      messages.prompt("New #{names.getNaturalName(what)}", '', {type: dialogType, create: what}).then (element)->
        element.show()

    if user != ''
      updateDashboard(user)
    else
      $scope.user = user

    $scope.welcome = modelcatalogue.welcome

    $scope.image = (relativePath) -> "#{security.contextPath}/assets#{relativePath}"


    $scope.dataModelHref = (dataModel) ->
      $state.href 'mc.resource.list', {dataModelId: dataModel.id, resource: 'dataClass'}

    $scope.createDataModel = ->
      messages.prompt("New Data Model", '', {type: 'create-dataModel', create: 'dataModel'}).then (element)->
        catalogue.select(element)

  ])

.controller('mc.core.ui.states.ShowCtrl', ['$scope', '$stateParams', '$state', 'element', '$rootScope', 'names' , ($scope, $stateParams, $state, element, $rootScope) ->
    $scope.element = element
    $rootScope.elementToShow = element
  ])

.controller('mc.core.ui.states.DataImportCtrl', ['$scope', '$stateParams', '$state', 'element', ($scope, $stateParams, $state, element) ->
    $scope.element  = element
  ])

.controller('mc.core.ui.states.BatchCtrl', ['$scope', '$stateParams', '$state', 'element', 'applicationTitle', ($scope, $stateParams, $state, element, applicationTitle) ->
    $scope.element  = element
    applicationTitle "Actions in batch #{element.name}"
  ])

.controller('mc.core.ui.states.CsvTransformationCtrl', ['$scope', '$stateParams', '$state', 'element', ($scope, $stateParams, $state, element) ->
    $scope.element  = element
  ])

.controller('mc.core.ui.states.FavoritesCtrl', ['$scope', 'modelCatalogueApiRoot', 'user', 'enhance', 'rest', 'columns', ($scope, modelCatalogueApiRoot, user, enhance, rest, columns) ->
    $scope.title = 'Favorites'
    $scope.user = user

    listEnhancer = enhance.getEnhancer('list')
    $scope.list = angular.extend(listEnhancer.createEmptyList(), base: "/user/#{user.id}/outgoing/favourite")

    $scope.columns = columns()

    enhance(rest(url: "#{modelCatalogueApiRoot}#{user.link}/outgoing/favourite")).then (list)->
      $scope.list = list
  ])

.controller('mc.core.ui.states.DiffCtrl', ['$scope', '$stateParams', '$state', 'elements', 'applicationTitle', ($scope, $stateParams, $state, elements, applicationTitle) ->
    $scope.elements = elements
    applicationTitle "Comparison of #{((element.getLabel?.apply(element) ? element.name) for element in elements).join(' and ')}"
  ])

.controller('mc.core.ui.states.ListCtrl', ['$scope', '$stateParams', '$state', 'list', 'names', 'enhance', 'applicationTitle', '$rootScope', 'catalogueElementResource', 'catalogue', 'messages', ($scope, $stateParams, $state, list, names, enhance, applicationTitle, $rootScope, catalogueElementResource, catalogue, messages) ->
    if $stateParams.resource
      applicationTitle  "#{names.getNaturalName($stateParams.resource)}s"

    $scope.list                     = list
    $scope.title                    = names.getNaturalName($stateParams.resource) + ' List'
    $scope.natural                  = (name) -> if name then names.getNaturalName(name) else "General"
    $scope.resource                 = $stateParams.resource

    $scope.dataModelHref = (dataModel) ->
        $state.href 'mc.resource.list', {dataModelId: dataModel.id, resource: 'dataClass'}

    $scope.createDataModel = ->
        messages.prompt("New Data Model", '', {type: 'create-dataModel', create: 'dataModel'}).then (element)->
          catalogue.select(element)

    getLastModelsKey = (status = $stateParams.status)->
      "#{status ? 'finalized'}"

    if $scope.resource == 'dataClass' || $scope.resource == 'model' || $scope.resource == 'dataModel'
      if $rootScope.$$lastModels and $rootScope.$$lastModels[getLastModelsKey()]
        if $rootScope.$$lastModels[getLastModelsKey()].element
          $rootScope.$$lastModels[getLastModelsKey()].element.refresh().then (element) ->
            $scope.element                = element
            $scope.elementSelectedInTree  = $rootScope.$$lastModels[getLastModelsKey()]?.elementSelectedInTree
            $scope.property               = $rootScope.$$lastModels[getLastModelsKey()]?.property
          , ->
            $scope.element                = if list.size > 0 then list.list[0]
            $scope.elementSelectedInTree  = false
            $scope.property               = 'contains'

      else
        $rootScope.$$lastModels       = {}
        $scope.elementSelectedInTree  = false
        $scope.element                = if list.size > 0 then list.list[0]
        $scope.property               =  'contains'

      $scope.onTreeviewSelected = (element) ->
        $scope.element                  = element
        $scope.elementSelectedInTree    = true
        $rootScope.$$lastModels ?= {}
        $rootScope.$$lastModels[getLastModelsKey()] = element: element, elementSelectedInTree: true, property: 'contains'
        $rootScope.$broadcast 'redrawContextualActions'

      $scope.$on 'newVersionCreated', (ignored, element) ->
        if element
          $rootScope.$$lastModels ?= {}
          $rootScope.$$lastModels[getLastModelsKey('draft')] = element: element, elementSelectedInTree: true, property: 'history'
          $state.go '.', {status: 'draft'}, { reload: true }

      $scope.$on 'catalogueElementFinalized', (ignored, element) ->
        if element
          if element.childOf.total == 0
            $rootScope.$$lastModels ?= {}
            $rootScope.$$lastModels[getLastModelsKey('finalized')] = element: element, elementSelectedInTree: true, property: 'history'
            $state.go '.', {status: undefined}, { reload: true }
  ])
.config(['$stateProvider', 'catalogueProvider', ($stateProvider, catalogueProvider) ->

    DEFAULT_ITEMS_PER_PAGE = 10

    $stateProvider.state 'landing', {
      url: ''
      templateUrl: 'modelcatalogue/core/ui/state/dashboardWithNav.html',
      controller: 'mc.core.ui.states.DashboardCtrl'
      resolve:
        user: ['security', (security) ->
          if security.getCurrentUser() then return security.getCurrentUser() else return {displayName: ''}
        ]
        statistics: ['catalogue', 'security', '$stateParams', (catalogue, security, $stateParams) ->
          if security.getCurrentUser()?.id then return catalogue.getStatistics($stateParams.dataModelId) else return ''
        ]
    }

    $stateProvider.state 'landing2', {
      url: '/'
      templateUrl: 'modelcatalogue/core/ui/state/dashboardWithNav.html',
      controller: 'mc.core.ui.states.DashboardCtrl'
      resolve:
        user: ['security', (security) ->
          if security.getCurrentUser() then return security.getCurrentUser() else return {displayName: ''}
        ]
        statistics: ['catalogue', 'security', '$stateParams', (catalogue, security, $stateParams) ->
          if security.getCurrentUser()?.id then return catalogue.getStatistics($stateParams.dataModelId) else return ''
        ]
    }

    $stateProvider.state 'mc', {
      abstract: true
      url: '/{dataModelId:[0-9]+|catalogue}'
      templateUrl: 'modelcatalogue/core/ui/layout.html'
      resolve:
        currentDataModel: ['catalogue', '$rootScope', '$stateParams', '$q', 'catalogueElementResource', (catalogue, $rootScope, $stateParams, $q, catalogueElementResource) ->
          if !$stateParams.dataModelId or $stateParams.dataModelId == 'catalogue'
            $rootScope.currentDataModel = undefined
            return undefined
          if $rootScope.currentDataModel and $stateParams.dataModelId == $rootScope.currentDataModel.id.toString
            return $rootScope.currentDataModel

          deferred = $q.defer()

          catalogueElementResource('dataModel').get($stateParams.dataModelId).then (dataModel) ->
            deferred.resolve(dataModel)
            $rootScope.currentDataModel = dataModel

          deferred.promise
        ]
    }

    $stateProvider.state 'mc.favorites', {
      url: '/favorites'
      templateUrl: 'modelcatalogue/core/ui/state/favorites.html'
      onEnter: ['applicationTitle', (applicationTitle) ->
        applicationTitle "Favorites"
      ]
      resolve:
        user: [ 'security', 'catalogueElementResource', '$q', (security, catalogueElementResource, $q) ->
          userId = security.getCurrentUser()?.id
          return $q.reject('Please, log in!') if not userId

          catalogueElementResource('user').get(userId)
        ]
      controller: 'mc.core.ui.states.FavoritesCtrl'

    }


    $stateProvider.state('mc.csvTransformations', {
      abstract: true,
      url: "/transformations/csv"
      templateUrl: 'modelcatalogue/core/ui/state/parent.html'
    })

    $stateProvider.state 'mc.csvTransformations.show', {
      url: '/{id:\\d+}'
      templateUrl: 'modelcatalogue/core/ui/state/csvTransformation.html'
      resolve:
        element: ['$stateParams','catalogueElementResource', ($stateParams, catalogueElementResource) ->
          $stateParams.resource = "csvTransformation"
          return catalogueElementResource('csvTransformation').get($stateParams.id)
        ]

      controller: 'mc.core.ui.states.CsvTransformationCtrl'
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


    $stateProvider.state 'mc.resource.diff', {
      url: '/diff/{ids:(?:\\d+)(?:\\~\\d+)+}'
      templateUrl: 'modelcatalogue/core/ui/state/diff.html'
      resolve:
        elements: ['$stateParams','catalogueElementResource', '$q', ($stateParams, catalogueElementResource, $q) ->
          $q.all (catalogueElementResource($stateParams.resource).get(id) for id in $stateParams.ids.split('~'))
        ]
      controller: 'mc.core.ui.states.DiffCtrl'
    }

    $stateProvider.state 'mc.resource.list', {
      url: '/all?page&order&sort&status&q&max&classification&display'

      templateUrl: 'modelcatalogue/core/ui/state/list.html'

      resolve:
        list: ['$stateParams','catalogueElementResource', ($stateParams, catalogueElementResource) ->
          page = parseInt($stateParams.page ? 1, 10)
          page = 1 if isNaN(page)
          # it's safe to call top level for each controller, only data class controller will respond on it

          defaultSorts = catalogueProvider.getDefaultSort($stateParams.resource) ? {sort: 'name', order: 'asc'}

          params                = offset: (page - 1) * DEFAULT_ITEMS_PER_PAGE, toplevel: true, system: true
          params.order          = $stateParams.order ? defaultSorts.order
          params.sort           = $stateParams.sort ? defaultSorts.sort
          params.status         = $stateParams.status ? (if $stateParams.resource is 'dataModel' then 'active' else 'finalized')
          params.max            = $stateParams.max ? 10
          params.classification = $stateParams.classification ? undefined

          if $stateParams.dataModelId and $stateParams.dataModelId != 'catalogue'
               params.dataModel = $stateParams.dataModelId

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
      onExit: ['$rootScope', ($rootScope) ->
        $rootScope.elementToShow = null
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

      onExit: ['$rootScope', ($rootScope) ->
        $rootScope.elementToShow = null
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
          params = {}
          if $stateParams.dataModelId and $stateParams.dataModelId isnt 'catalogue'
            params.dataModel = $stateParams.dataModelId

          return modelCatalogueSearch($stateParams.q, params)
        ]
      },
      controller: 'mc.core.ui.states.ListCtrl'
    })


    $stateProvider.state('mc.dataArchitect', {
      abstract: true,
      url: "/dataArchitect"
      templateUrl: 'modelcatalogue/core/ui/state/parent.html'
    })


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
  ])

.controller('defaultStates.searchCtrl', ['catalogueElementResource', 'modelCatalogueSearch', '$scope', '$rootScope', '$q', '$state', 'names', 'messages', 'actions', 'modelCatalogueApiRoot', '$http', 'enhance'
    (catalogueElementResource, modelCatalogueSearch, $scope, $rootScope, $q, $state, names, messages, actions, modelCatalogueApiRoot, $http, enhance)->
      actions = []

      $scope.search = (item, model, label) ->
        if angular.isString(item)
          $state.go('mc.search', {q: model })
        else
          item?.action item, model, label

      $scope.clearSelection = ->
        $state.searchSelect = undefined
        $rootScope.$stateParams.q = undefined
        $state.go('.', {q: undefined })

      initActions = ->
        actions = []
        actions.push {
          condition: (term) -> term
          label: (term) ->
            "Search <strong>Catalogue Element</strong> for <strong>#{term}</strong>"

          action: (term) -> ->
            $state.go('mc.search', {q: term})

          icon: 'fa fa-fw fa-search'
        }

        actions.push {
          condition: (term) -> term and $state.$current.params.hasOwnProperty('q') and $state.params.resource
          label: (term) ->
            naturalName = names.getNaturalName($state.params.resource)
            "Search any <strong>#{naturalName}</strong> for <strong>#{term}</strong>"
          action: (term) ->
            ->
              $state.go('mc.resource.list', {q: term})
          icon: 'fa fa-fw fa-search'
        }

        actions.push {
          condition: (term) ->
            term and $state.current.name == 'mc.resource.show.property' and  $state.$current.params.hasOwnProperty('q') and $rootScope.$$searchContext
          label: (term) ->
            "Search current <strong>#{$rootScope.$$searchContext}</strong> for <strong>#{term}</strong>"
          action: (term) ->
            ->
              $state.go('mc.resource.show.property', {q: term})
          icon: 'fa fa-fw fa-search'
        }

        actions.push {
          condition: -> true
          label: (term) ->
            if $rootScope.elementToShow?.isInstanceOf('dataType') and $rootScope.elementToShow?.rule
              "Validate <strong>#{term}</strong> by <strong>#{$rootScope.elementToShow.name}</strong>"
            else
              "Validate <strong>#{term}</strong>"

          action: (term) ->
            ->
              messages.prompt('', '', {type: 'validate-value-by-domain', value: term, domainHint: if $rootScope.elementToShow?.rule then $rootScope.elementToShow else undefined})
          icon: 'fa fa-fw fa-check-circle-o'
        }

        actions.push {
          condition: -> true
          label: (term) ->
            if $rootScope.elementToShow?.isInstanceOf('dataType') and $rootScope.elementToShow?.mappings?.total > 0
              "Convert <strong>#{term}</strong> from <strong>#{$rootScope.elementToShow.name}</strong>"
            else
              "Convert <strong>#{term}</strong>"
          action: (term) ->
            ->
              messages.prompt('', '', {type: 'convert-with-value-domain', value: term, sourceHint: if $rootScope.elementToShow?.mappings?.total > 0 then $rootScope.elementToShow else undefined})
          icon: 'fa fa-fw fa-long-arrow-right'
        }

      $scope.getResults = (term) ->
        deferred = $q.defer()

        results = []

        return if not term

        for action in actions when action.condition(term)
          results.push {
            label:  action.label(term)
            action: action.action(term)
            icon:   action.icon
            term:   term
          }

        deferred.notify results

        if term
          params = {}
          params.dataModel = $state.params.dataModelId if $state.params.dataModelId and $state.params.dataModelId != 'catalogue'
          p1 = modelCatalogueSearch(term, params).then (searchResults)->
            for searchResult in searchResults.list
              results.push {
                label:      if searchResult.getLabel then searchResult.getLabel() else searchResult.name
                action:     searchResult.show
                icon:       if searchResult.getIcon  then searchResult.getIcon()  else 'glyphicon glyphicon-file'
                term:       term
                highlight:  true
                element:    searchResult
              }

          p2 = $q.when true

          if term.match(/^\d+$/)
            p2 = $http.get("#{modelCatalogueApiRoot}/catalogueElement/#{term}").then (result) ->
              return unless result.data?.elementType
              searchResult = enhance result.data
              results.push {
                label:      if searchResult.getLabel then searchResult.getLabel() else searchResult.name
                action:     searchResult.show
                icon:       if searchResult.getIcon  then searchResult.getIcon()  else 'glyphicon glyphicon-file'
                term:       term
                highlight:  true
                element:    searchResult
              }
            , -> true

          $q.all([p1, p2]).then ->
            deferred.resolve results
        else
          deferred.resolve results

        deferred.promise

      initActions()

      $scope.$on '$stateChangeSuccess', (event, toState, toParams) ->
        $scope.searchSelect = toParams.q

  ])

.controller('defaultStates.userCtrl', ['$scope', 'security', ($scope, security)->
    $scope.logout = ->
      security.logout()
    $scope.login = ->
      security.requireLogin()
  ])

.run(['$rootScope', '$state', '$stateParams', 'messages', ($rootScope, $state, $stateParams, messages) ->
    # It's very handy to add references to $state and $stateParams to the $rootScope
    # so that you can access them from any scope within your applications.For example,
    # <li ui-sref-active="active }"> will set the <li> // to active whenever
    # 'contacts.list' or one of its decendents is active.
    $rootScope.$state = $state
    $rootScope.$stateParams = $stateParams

    $rootScope.$on 'applicationOffline', ->
      messages.error 'Application is not available at the moment, please, retry later'

    $rootScope.$on 'resourceNotFound', ->
      messages.error 'Selected resource cannot be found in the catalogue.'
      if $stateParams.resource
        $state.go 'mc.resource.list', resource: $stateParams.resource
      else
        $state.go 'landing'
  ])

.run(['$templateCache', ($templateCache) ->

    $templateCache.put 'modelcatalogue/core/ui/layout.html', layout = '''
    <div class="navbar navbar-default navbar-fixed-top" role="navigation">
        <div class="container-fluid">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>

                </button>
                <a class="navbar-brand" href="#" hide-if-logged-in><span class="fa fa-fw fa-book"></span><span class="hidden-sm">&nbsp; Model Catalogue</span></a>
            </div>

            <div class="navbar-collapse collapse">
                <contextual-menu></contextual-menu>
                <ul class="nav navbar-nav">
                    <li class="hidden-sm hidden-md hidden-lg" ng-controller="defaultStates.userCtrl">
                        <a show-if-logged-in ng-click="logout()" type="submit">Log out</a>
                    </li>

                </ul>

                <form class="navbar-form navbar-right hidden-xs" ng-controller="defaultStates.userCtrl">
                    <button show-if-logged-in ng-click="logout()" class="btn btn-danger"  type="submit"><i class="glyphicon glyphicon-log-out"></i></button>
                    <button hide-if-logged-in ng-click="login()"  class="btn btn-primary" type="submit"><i class="glyphicon glyphicon-log-in"></i></button>
                </form>

                <ng-include src="'modelcatalogue/core/ui/omnisearch.html'"></ng-include>

            </div><!--/.nav-collapse -->
        </div>
    </div>

    <div class="container-fluid container-main">
        <sidenav></sidenav>
        <div class="row content-row">
            <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                <div id="jserrors"></div>
                <ui-view></ui-view>
            </div>
        </div>
    </div>
    '''

    $templateCache.put 'modelcatalogue/core/ui/omnisearch.html', '''
    <form show-if-logged-in class="navbar-form navbar-right navbar-input-group search-form hidden-xs" role="search" autocomplete="off" ng-submit="search()" ng-controller="defaultStates.searchCtrl">
        <a ng-click="clearSelection()" ng-class="{'invisible': !$stateParams.q}" class="clear-selection btn btn-link"><span class="glyphicon glyphicon-remove"></span></a>
        <div class="form-group">
            <input
                   ng-model="searchSelect"
                   type="text"
                   name="search-term"
                   id="search-term"
                   placeholder="Search"
                   typeahead="result.term as result.label for result in getResults($viewValue)"
                   typeahead-on-select="search($item, $model, $label)"
                   typeahead-template-url="modelcatalogue/core/ui/omnisearchItem.html"
                   typeahead-wait-ms="300"
                   class="form-control"
                   ng-class="{'expanded': searchSelect}"
            >
        </div>
        <button class="btn btn-default" ng-click="select(searchSelect)"><i class="glyphicon glyphicon-search"></i></button>
    </form>
  '''

    $templateCache.put 'modelcatalogue/core/ui/omnisearchItem.html', '''
    <a>
        <span class="omnisearch-icon" ng-class="match.model.icon"></span>
        <span class="omnisearch-text" ng-if="!match.model.highlight" bind-html-unsafe="match.label" ng-class="{'text-warning': match.model.element.status == 'DRAFT', 'text-info': match.model.element.status == 'PENDING'}"></span>
        <span class="omnisearch-text" ng-if=" match.model.highlight" bind-html-unsafe="match.label | typeaheadHighlight:query" ng-class="{'text-warning': match.model.element.status == 'DRAFT', 'text-info': match.model.element.status == 'PENDING'}"></span>
    </a>
  '''

    $templateCache.put 'modelcatalogue/core/ui/state/parent.html', '''
    <ui-view></ui-view>
  '''

    $templateCache.put 'modelcatalogue/core/ui/state/diff.html', '''
    <span class="contextual-actions-right">
      <contextual-actions size="sm" no-colors="true" role="item"></contextual-actions>
    </span>
    <h2>Comparison</h2>
    <diff-table elements="elements"></diff-table>
  '''

    #language=HTML
    $templateCache.put 'modelcatalogue/core/ui/state/list.html', '''
    <div class="row" ng-if="(resource != 'dataClass' &amp;&amp;  resource != 'dataClass' &amp;&amp; resource != 'dataModel')|| $stateParams.display != undefined">
      <span class="contextual-actions-right">
        <contextual-actions size="sm" no-colors="true" role="list"></contextual-actions>
      </span>
      <h2><small ng-class="catalogue.getIcon(resource)"></small>&nbsp;<span ng-show="$stateParams.status">{{natural($stateParams.status)}}</span> {{title}}</h2>
      <infinite-list  ng-if="$stateParams.display == 'grid'"  list="list"></infinite-list>
      <infinite-table ng-if="$stateParams.display != 'grid'"  list="list" columns="columns" ></infinite-table>
    </div>
    <div ng-if="(resource == 'dataClass' || resource == 'model' || resource == 'xdataModel')&amp;&amp; $stateParams.display == undefined">
      <div class="row">
        <div class="col-xs-3 col-sm-3 col-md-3 col-lg-3 split-view-left" resizable="{'handles': 'e', 'mirror': '.split-view-right', 'maxWidth': 1000, 'minWidth': 200, 'windowWidthCorrection': 91}">
          <div class="split-view-content">
            <div class="row">
              <span class="contextual-actions-right">
                   <contextual-actions size="sm" icon-only="true" no-colors="true" role="list"></contextual-actions>
              </span>
              <div class="col-md-12">
                <h3>
                    <small ng-class="catalogue.getIcon('dataClass')"></small>&nbsp;<span ng-show="$stateParams.status">{{natural($stateParams.status)}}</span> {{ resource == 'dataClass' ? 'Data Classes' : 'Data Models' }}
                </h3>
                <catalogue-element-treeview list="list" descend="resource == 'xdataModel' ? 'content' : 'parentOf'" id="model-treeview" on-select="onTreeviewSelected($element)"></catalogue-element-treeview>
              </div>
            </div>
          </div>
        </div>
        <div class="col-xs-9 col-sm-9 col-md-9 col-lg-9 split-view-right" ng-if="element">
          <div class="split-view-content">
            <catalogue-element-view element="element" property="property"></catalogue-element-view>
          </div>
        </div>
      </div>
    </div>
    <div class="row" ng-if="resource == 'dataModel'">
      <div class="col-md-12">
        <infinite-list heading="'Data Models'" on-create-requested="createDataModel()" list="list" no-actions="true" item-href="dataModelHref($element)"></infinite-list>
      </div>
    </div>
  '''


    #language=HTML
    $templateCache.put 'modelcatalogue/core/ui/state/favorites.html', '''
    <div class="row">
      <span class="contextual-actions-right">
        <contextual-actions size="sm" no-colors="true" role="list"></contextual-actions>
      </span>
      <h2><small class="fa fa-fw fa-star text-muted"></small>Favourites</h2>
      <infinite-table list="list" columns="columns" transform="$element.relation"></infinite-table>
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

    $templateCache.put 'modelcatalogue/core/ui/state/csvTransformation.html', '''
    <div ng-show="element">
      <csv-transformation-view element="element"></csv-transformation-view>
    </div>
  '''

    #language=HTML
    $templateCache.put 'modelcatalogue/core/ui/state/dashboard.html', dashboard = '''
    		<!-- Jumbotron -->
  <div hide-if-logged-in>
		<div class="jumbotron">
      <!-- from config mc.welcome.jumbo -->
      <div ng-bind-html="welcome.jumbo"></div>
      <form ng-controller="defaultStates.userCtrl">
         <button ng-click="login()" class="btn btn-large btn-primary" type="submit">Login <i class="glyphicon glyphicon-log-in"></i></button>
         <a ng-href="{{registrationUrl}}" ng-if="registrationUrl" class="btn btn-large btn-primary">Sign Up <span class="fa fa-user"></span></a>
         <!--a href="" class="btn btn-large btn-primary" >Sign Up <i class="glyphicon glyphicon-pencil"></i></a-->
      </form>
    </div>

		<!-- from config mc.welcome.info -->
		<div id="info" class="row" ng-bind-html="welcome.info"></div>
</div>

    <div class="row">
        <div class="col-lg-12 col-sm-12 col-md-12">
            <div class="panel panel-default">
                <div class="panel-body">
                    <div class="row">
                      <div class=" col-xs-12 col-sm-12 col-md-4 col-lg-4"><p>Model catalogue development supported by</p></div>
                      <div class=" col-xs-3 col-sm-3 col-md-2 col-lg-2">
                        <p>
                          <a href="http://www.genomicsengland.co.uk/">
                            <img ng-src="{{image('/modelcatalogue/GEL.jpg')}}" class="img-thumbnail sponsor-logo-small" alt="Genomics England">
                          </a>
                        </p>
                        <p class="hidden-xs"><a href="http://www.genomicsengland.co.uk/" class="text-muted">Genomics England</a></p>
                      </div>
                      <div class=" col-xs-3 col-sm-3 col-md-2 col-lg-2">
                        <p><a href="http://www.mrc.ac.uk"><img ng-src="{{image('/modelcatalogue/MRC.png')}}" class="img-thumbnail sponsor-logo-small" alt="Medical Research Council"></a></p>
                        <p class="hidden-xs"><a href="http://www.mrc.ac.uk" class="text-muted">Medical Research Council</a></p>
                      </div>
                      <div class=" col-xs-3 col-sm-3 col-md-2 col-lg-2">
                        <p><a href="http://www.nihr.ac.uk/"><img ng-src="{{image('/modelcatalogue/NIHR.png')}}" class="img-thumbnail sponsor-logo-small" alt="NIHR"></a></p>
                        <p class="hidden-xs"><a href="http://www.nihr.ac.uk/" class="text-muted">National Institute for Health Research</a></p>
                      </div>
                      <div class=" col-xs-3 col-sm-3 col-md-2 col-lg-2">
                        <p><a href="http://www.metadataconsusting.co.uk"><img ng-src="{{image('/modelcatalogue/MDC.png')}}" class="img-thumbnail sponsor-logo-small" alt="Metadata Consulting Ltd"></a></p>
                        <p class="hidden-xs"><a href="http://www.metadataconsusting.co.uk" class="text-muted">Metadata Consulting</a></p>
                      </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
  '''

    $templateCache.put 'modelcatalogue/core/ui/state/dashboardWithNav.html', layout.replace('<ui-view></ui-view>', dashboard)

])
.config(['$provide', ($provide) ->
  fixStateParams = (state, params) ->
    return params if not state

    stateName = if angular.isString(state) then state else state.name

    return params if not state

    if stateName?.startsWith 'mc'
      return {dataModelId: 'catalogue' } if not params

      if not params.dataModelId
        params.dataModelId = 'catalogue'

    return params


  $provide.decorator('$state', ['$delegate', ($delegate) ->
    originalHref = $delegate.href

    $delegate.href = (stateOrName, params, options) ->
      return originalHref(stateOrName, fixStateParams(stateOrName, params), options)


    originalTransitionTo = $delegate.transitionTo

    $delegate.transitionTo = (to, toParams, options) ->
      return originalTransitionTo(to, fixStateParams(to, toParams), options)

    $delegate

  ])

])
# keep track of the data model used
.run(['$rootScope', 'catalogueElementResource', ($rootScope, catalogueElementResource) ->
  $rootScope.$on '$stateChangeStart', (event, toState, toParams, fromState, fromParams) ->
    if toParams.dataModelId isnt fromParams.dataModelId or (toParams.dataModelId and not $rootScope.currentDataModel)
      if not toParams.dataModelId or toParams.dataModelId is 'catalogue'
        $rootScope.currentDataModel = undefined
        $rootScope.$broadcast 'redrawContextualActions'
        return
      if toParams.dataModelId isnt $rootScope.currentDataModel?.id?.toString
        catalogueElementResource('dataModel').get(toParams.dataModelId).then (dataModel) ->
          $rootScope.currentDataModel = dataModel
          $rootScope.$broadcast 'redrawContextualActions'
])
# debug states
#.run(['$rootScope', '$log', ($rootScope, $log) ->
#  $rootScope.$on '$stateChangeSuccess', (event, toState, toParams, fromState, fromParams) ->
#    $log.info "$stateChangeSuccess", toState, toParams, fromState, fromParams
#])