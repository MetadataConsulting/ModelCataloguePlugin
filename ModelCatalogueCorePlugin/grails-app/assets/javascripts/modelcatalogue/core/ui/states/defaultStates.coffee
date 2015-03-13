angular.module('mc.core.ui.states.defaultStates', ['ui.router', 'mc.util.ui'])

.controller('mc.core.ui.states.DashboardCtrl', ['$rootScope', '$scope', '$stateParams', '$state', 'security', 'catalogue', 'modelCatalogueApiRoot', 'user', 'messages', 'applicationTitle', 'names', 'statistics', ($rootScope, $scope, $stateParams, $state, security, catalogue, modelCatalogueApiRoot, user, messages, applicationTitle, names, statistics) ->
    applicationTitle "Model Catalogue"

    angular.extend $scope, statistics

    updateDashboard = (userName) ->
      $scope.user  = userName
      catalogue.getStatistics().then ((result)->
        angular.extend $scope,  result
      )

    $scope.$on('userLoggedIn', (ignored, user) ->
      if user?.data?.error
        updateDashboard undefined
      else
        updateDashboard user?.data?.displayName
    )

    $scope.convert = ->
      messages.prompt('', '', {type: 'convert-with-value-domain'})

    $scope.validate = ->
      messages.prompt('', '', {type: 'validate-value-by-domain'})

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
      $scope.totalAssetCount = ''
      $scope.draftAssetCount = ''
      $scope.finalizedAssetCount = ''


  ])

.controller('mc.core.ui.states.ShowCtrl', ['$scope', '$stateParams', '$state', 'element', '$rootScope', 'names' , ($scope, $stateParams, $state, element, $rootScope, names) ->
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

.controller('mc.core.ui.states.ListCtrl', ['$scope', '$stateParams', '$state', 'list', 'names', 'enhance', 'applicationTitle', '$rootScope', 'catalogueElementResource', ($scope, $stateParams, $state, list, names, enhance, applicationTitle, $rootScope, catalogueElementResource) ->
    if $stateParams.resource
      applicationTitle  "#{names.getNaturalName($stateParams.resource)}s"

    $scope.list                     = list
    $scope.title                    = names.getNaturalName($stateParams.resource) + ' List'
    $scope.natural                  = (name) -> if name then names.getNaturalName(name) else "General"
    $scope.resource                 = $stateParams.resource

    getLastModelsKey = (status = $stateParams.status)->
      "#{status ? 'finalized'}"

    if $scope.resource == 'model'
      if $rootScope.$$lastModels and $rootScope.$$lastModels[getLastModelsKey()]
        $scope.element                = $rootScope.$$lastModels[getLastModelsKey()]?.element
        $scope.elementSelectedInTree  = $rootScope.$$lastModels[getLastModelsKey()]?.elementSelectedInTree
        $scope.property               = $rootScope.$$lastModels[getLastModelsKey()]?.property
      else
        $rootScope.$$lastModels       = {}
        $scope.elementSelectedInTree  = false
        $scope.element                = if list.size > 0 then list.list[0]
        $scope.property               =  'contains'


      $scope.$on 'treeviewElementSelected', (event, element) ->
        $scope.element                  = element
        $scope.elementSelectedInTree    = true
        $rootScope.$$lastModels ?= {}
        $rootScope.$$lastModels[getLastModelsKey()] = element: element, elementSelectedInTree: true, property: 'contains'

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

    $stateProvider.state 'dashboard', {
      url: ''
      templateUrl: 'modelcatalogue/core/ui/state/dashboard.html',
      controller: 'mc.core.ui.states.DashboardCtrl'
      resolve:
        user: ['security', (security) ->
          if security.getCurrentUser() then return security.getCurrentUser().displayName else return ''
        ]
        statistics: ['catalogue', 'security', (catalogue, security) ->
          if security.getCurrentUser() then return catalogue.getStatistics() else return ''
        ]
    }

    $stateProvider.state 'mc', {
      abstract: true
      url: '/catalogue'
      templateUrl: 'modelcatalogue/core/ui/state/parent.html'
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
        list: ['$stateParams','catalogueElementResource', ($stateParams,catalogueElementResource) ->
          page = parseInt($stateParams.page ? 1, 10)
          page = 1 if isNaN(page)
          # it's safe to call top level for each controller, only model controller will respond on it

          defaultSorts = catalogueProvider.getDefaultSort($stateParams.resource) ? {sort: 'name', order: 'asc'}

          params                = offset: (page - 1) * DEFAULT_ITEMS_PER_PAGE, toplevel: true, system: true
          params.order          = $stateParams.order ? defaultSorts.order
          params.sort           = $stateParams.sort ? defaultSorts.sort
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
  ])

.controller('defaultStates.searchCtrl', ['catalogueElementResource', 'modelCatalogueSearch', '$scope', '$rootScope', '$q', '$state', 'names', 'messages', 'actions'
    (catalogueElementResource, modelCatalogueSearch, $scope, $rootScope, $q, $state, names, messages, actions)->
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
            if $rootScope.elementToShow?.isInstanceOf('valueDomain') and $rootScope.elementToShow?.rule
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
            if $rootScope.elementToShow?.isInstanceOf('valueDomain') and $rootScope.elementToShow?.mappings?.total > 0
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
          modelCatalogueSearch(term).then (searchResults)->
            for searchResult in searchResults.list
              results.push {
                label:      if searchResult.getLabel then searchResult.getLabel() else searchResult.name
                action:     searchResult.show
                icon:       if searchResult.getIcon  then searchResult.getIcon()  else 'glyphicon glyphicon-file'
                term:       term
                highlight:  true
                element:    searchResult
              }

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
  ])

.run(['$templateCache', ($templateCache) ->

    $templateCache.put 'modelcatalogue/core/ui/omnisearch.html', '''
    <form class="navbar-form navbar-right navbar-input-group search-form hidden-xs" role="search" autocomplete="off" ng-submit="search()" ng-controller="defaultStates.searchCtrl">
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
    <div ng-if="resource != 'model' || $stateParams.display != undefined">
      <span class="contextual-actions-right">
        <contextual-actions size="sm" no-colors="true" role="list"></contextual-actions>
      </span>
      <h2><small ng-class="catalogue.getIcon(resource)"></small>&nbsp;<span ng-show="$stateParams.status">{{natural($stateParams.status)}}</span> {{title}}</h2>
      <infinite-list  ng-if="$stateParams.display == 'grid'"  list="list"></infinite-list>
      <infinite-table ng-if="$stateParams.display != 'grid'"  list="list" columns="columns" ></infinite-table>
    </div>
    <div ng-if="resource == 'model' &amp;&amp; $stateParams.display == undefined">

      <div class="row">

        <div class="col-md-4">
          <span class="contextual-actions-right">
             <contextual-actions size="sm" icon-only="true" no-colors="true" role="list"></contextual-actions>
          </span>
          <h2>
            <small ng-class="catalogue.getIcon('model')"></small>&nbsp;<span ng-show="$stateParams.status">{{natural($stateParams.status)}}</span> Models
          </h2>
          <catalogue-element-treeview list="list" descend="'parentOf'"></catalogue-element-treeview>
        </div>
        <div class="col-md-8" ng-if="element">
          <catalogue-element-view element="element" property="property"></catalogue-element-view>
        </div>
        <hr/>
      </div>
    </div>
  '''


    #language=HTML
    $templateCache.put 'modelcatalogue/core/ui/state/favorites.html', '''
    <div>
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
    $templateCache.put 'modelcatalogue/core/ui/state/dashboard.html', '''
    		<!-- Jumbotron -->
  <div hide-if-logged-in>
		<div class="jumbotron">
			<h1>Model Catalogue</h1>
      <h2>The Tumtum Tree Project</h2>
			<p class="lead">
				<b><em>Model</em></b> existing business processes and context. <b><em>Design</em></b>
				and version new datasets <b><em>Generate</em></b> better
				software components
			</p>

      <form ng-controller="defaultStates.userCtrl">
         <button ng-click="login()" class="btn btn-large btn-primary" type="submit">Login <i class="glyphicon glyphicon-log-in"></i></button>
         <!--a href="" class="btn btn-large btn-primary" >Sign Up <i class="glyphicon glyphicon-pencil"></i></a-->
      </form>
    </div>

		<!-- Example row of columns -->
		<div id="info" class="row">
      <div class="col-sm-4">
				<h2>Data Quality</h2>
				<p>Build up datasets using existing data elements from existing datasets and add them to new data elements to compose new data models.</p>
				<p>

				</p>
			</div>
			<div class="col-sm-4">
				<h2>Dataset Curation</h2>
				<p>Link and compose data-sets to create uniquely identified and versioned "metadata-sets", thus ensuring preservation of data semantics between applications</p>
				<p>

				</p>
			</div>
      <div class="col-sm-4">
				<h2>Dataset Comparison</h2>
				<p>Discover synonyms, hyponyms and duplicate data elements within datasets, and compare data elements from differing datasets.</p>
				<p>

				</p>
			</div>
    </div>
		<div class="row">
        <div class="col-lg-12 col-sm-12 col-md-12">
            <div class="panel panel-default">
                <div class="panel-body">
                    <div class="row">
                      <div class="col-lg-12 col-sm-12 col-md-12 center-block">Kindy sponsored by</div>
                      <div class="col-lg-4 col-sm-4 col-md-4">
                        <p><a href="http://oxfordbrc.nihr.ac.uk/">Oxford BRC</a></p>
                        <img class="img-rectangle" src="images/OxBRClogo_tiny.png" style="width: 373px; height: 26px;">
                      </div>
                      <div class="col-lg-4 col-sm-4 col-md-4">
                        <p> <a href="http://www.nihr.ac.uk/about/nihr-hic.htm">NHIC</a></p>
                        <img class="img-rectangle" src="images/nhic_small.png" style="width: 59px; height: 26px;">
                      </div>
                      <div class="col-lg-4 col-sm-4 col-md-4">
                        <p><a href="http://www.metadataconsulting.co.uk">Metadata Consulting Limited </a></p>
                        <img class="img-rectangle" src="images/metadatalogo_small.png" style="width: 49px; height: 26px;">
                      </div>
                      <div class="col-lg-12 col-sm-12 col-md-12">
                        <p>&copy; 2015 The Tumtumtree Project &middot; Released under the <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache license</a></p>
                      </div>
                    </div>
                </div>
            </div>
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
                                        <div><a id="modelsLink" ui-sref="mc.resource.list({resource: 'dataElement', status:'uninstantiated'})" ui-sref-opts="{inherit: false}">Uninstantiated Data Elements</a>  {{uninstantiatedDataElementCount}}</div>
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
                                        <a ui-sref="mc.resource.list({resource: 'valueDomain'})" ui-sref-opts="{inherit: false}"><i class="fa fa-cog fa-5x fa-fw"></i></a>
                                    </div>
                                    <div class="col-xs-9 text-right">
                                        <div><a id="valueDomainLink" ui-sref="mc.resource.list({resource: 'valueDomain'})" ui-sref-opts="{inherit: false}"> Value Domains</a> {{valueDomainCount}} <span ng-show="incompleteValueDomainsCount"> / <a id="incompleteValueDomainLink" ui-sref="mc.resource.list({resource: 'valueDomain', status: 'incomplete'})" ui-sref-opts="{inherit: false}">Incomplete </a> {{incompleteValueDomainsCount}}</span></div>
                                        <div><a ng-click="validate()">Validate</a> / <a ng-click="convert()">Convert</a></div>
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
                                        <a ui-sref="mc.resource.list({resource: 'asset'})" ui-sref-opts="{inherit: false}"><i class="fa fa-file-code-o fa-5x fa-fw"></i></a>
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
                    <div show-for-role="CURATOR" class="col-lg-4 col-sm-6 col-md-4">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                <div class="row">
                                    <div class="col-xs-3">
                                        <a ui-sref="mc.resource.list({resource: 'csvTransformation'})" ui-sref-opts="{inherit: false}"><i class="fa fa-long-arrow-right fa-5x fa-fw"></i></a>
                                    </div>
                                    <div class="col-xs-9 text-right">
                                        <div><a id="batchesLink" ui-sref="mc.resource.list({resource: 'csvTransformation'})" ui-sref-opts="{inherit: false}">CSV Transformations</a> {{transformationsCount}}</div>
                                    </div>
                                </div>
                            </div>

                            <a ng-click="create('csvTransformation')">
                                <div class="panel-footer">
                                    <span class="pull-left">Create CSV Transformation</span>
                                    <span class="pull-right"><i class="fa fa-magic"></i></span>
                                    <div class="clearfix"></div>
                                </div>
                            </a>
                        </div>
                    </div>
                    <div show-for-role="CURATOR" class="col-lg-4 col-sm-6 col-md-4">
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
                                    <span class="pull-left">Create Batch</span>
                                    <span class="pull-right"><i class="fa fa-magic"></i></span>
                                    <div class="clearfix"></div>
                                </div>
                            </a>
                        </div>
                    </div>
                    <div class="col-lg-12 col-sm-12 col-md-12">
                        <div class="panel panel-default">
                            <div class="panel-body">
                                <div class="row">
                                  <div class="col-lg-12 col-sm-12 col-md-12 center-block">Kindy sponsored by</div>
                                  <div class="col-lg-4 col-sm-4 col-md-4">
                                    <p><a href="http://oxfordbrc.nihr.ac.uk/">Oxford BRC</a></p>
                                    <img class="img-rectangle" src="images/OxBRClogo_tiny.png" style="width: 373px; height: 26px;">
                                  </div>
                                  <div class="col-lg-4 col-sm-4 col-md-4">
                                    <p> <a href="http://www.nihr.ac.uk/about/nihr-hic.htm">NHIC</a></p>
                                    <img class="img-rectangle" src="images/nhic_small.png" style="width: 59px; height: 26px;">
                                  </div>
                                  <div class="col-lg-4 col-sm-4 col-md-4">
                                    <p><a href="http://www.metadataconsulting.co.uk">Metadata Consulting Limited </a></p>
                                    <img class="img-rectangle" src="images/metadatalogo_small.png" style="width: 49px; height: 26px;">
                                  </div>
                                  <div class="col-lg-12 col-sm-12 col-md-12">
                                    <p>&copy; 2015 The Tumtumtree Project &middot; Released under the <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache license</a></p>
                                  </div>
                                </div>
                            </div>
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