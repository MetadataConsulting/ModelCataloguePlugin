#= require jquery/dist/jquery
#= require bootstrap/dist/js/bootstrap
#= require angular/angular
#= require angular-ui-router/release/angular-ui-router
#= require modelcatalogue/util/index
#= require modelcatalogue/core/index
#= require modelcatalogue/core/ui/index
#= require modelcatalogue/core/ui/bs/index
#= require modelcatalogue/core/ui/bs/elementViews/index
#= require partials/metadata

@grailsAppName = 'model_catalogue'

metadataCurator = angular.module('metadataCurator', [
  'ui.router',
  'demo.config'
  'mc.core.ui.bs'
  'ui.bootstrap'
])

metadataCurator.controller('metadataCurator.elementTypeList', ['catalogueElementResource', 'modelCatalogueSearch', 'modelCatalogueDataArchitect', '$scope', '$log', '$q', '$stateParams','$state', 'list', 'type', 'columns', (catalogueElementResource, modelCatalogueSearch, modelCatalogueDataArchitect, $scope, $log, $q, $stateParams, $state, list, type, columns)->
  emptyList =
    list: []
    next: {size: 0}
    previous: {size: 0}
    total: 0
    empty: true
    source: 'metadataCurator'

  $scope.list = list

  $scope.type = type

  $scope.selection = []

  $scope.columns = columns()

  $scope.removeColumn = (index) ->
    return if $scope.columns.length <= 1
    $scope.columns.splice(index, 1)

  $scope.addColumn = (index, column = {header: 'ID', value: 'id', classes: 'col-md-2'}) ->
    $scope.columns.splice(index + 1, 0, angular.copy(column))


  # TODO: rewrite to states when finished
  $scope.$on 'showCatalogueElement', (event, element) ->
    $state.go('catalogueElement.show', {elementType: element.elementType.split(".").pop(), elementId: element.id})


])

metadataCurator.run(['$rootScope', '$state', '$stateParams', '$templateCache', ($rootScope,   $state,   $stateParams, $templateCache) ->
# It's very handy to add references to $state and $stateParams to the $rootScope
# so that you can access them from any scope within your applications.For example,
# <li ui-sref-active="active }"> will set the <li> // to active whenever
# 'contacts.list' or one of its decendents is active.
  $rootScope.$state = $state
  $rootScope.$stateParams = $stateParams

  $templateCache.put('metadata.html', '''
    <div ui-view class="span9 slide"></div>
  ''')

  $templateCache.put('list.html', '''
    <div ui-view class="span9 slide"></div>
    <div ng-show="!list.empty">
        <h2 style="text-transform: capitalize">{{type}}</h2>
        <decorated-list list="list" columns="columns" selection="selection"></decorated-list>
    </div>
  ''');

  $templateCache.put('show.html', '''
    <div ng-show="element">
      <catalogue-element-view element="element"></catalogue-element-view>
    </div>
  ''');

])

metadataCurator.config(($stateProvider, $urlRouterProvider)->
# For any unmatched url, send to /dataelement
  $urlRouterProvider.otherwise("/catalogueElement/dataElement")

  $stateProvider.state('catalogueElement', {
      abstract: true,
      url: "/catalogueElement"
      templateUrl: "metadata.html",
    }).state('catalogueElement.list', {
      url: "/{elementType}",
      templateUrl: "list.html",
      resolve: {
        list: ['$stateParams','catalogueElementResource', ($stateParams,catalogueElementResource) ->
          return catalogueElementResource($stateParams.elementType).list()
        ]
        type: [ '$stateParams', ($stateParams) ->
          return $stateParams.elementType.split(".").pop() + "s"
        ]
      },
      controller: "metadataCurator.elementTypeList"
    }).state('catalogueElement.show', {
      url: "/{elementType}/{elementId}",
      templateUrl: "show.html",
      resolve: {
        element: ['$stateParams','catalogueElementResource', ($stateParams, catalogueElementResource) ->
          return catalogueElementResource($stateParams.elementType).get($stateParams.elementId)
        ]
      },
      controller: "metadataCurator.elementTypeShow",
    }).state('search', {
      url: "/search/{searchString}",
      templateUrl: "list.html",
      resolve: {
        list: ['$stateParams','modelCatalogueSearch', ($stateParams, modelCatalogueSearch) ->
          return modelCatalogueSearch($stateParams.searchString)
        ]
        type: [() ->
          return "Search"
        ]
      },
      controller: "metadataCurator.elementTypeList",
  }).state('dataArchitect', {
    abstract: true,
    url: "/dataArchitect"
    templateUrl: "metadata.html",
  }).state('dataArchitect.uninstantiatedDataElements', {
    url: "/uninstantiatedDataElements",
    templateUrl: "list.html",
    resolve: {
      list: ['$stateParams','modelCatalogueDataArchitect', ($stateParams, modelCatalogueDataArchitect) ->
        return modelCatalogueDataArchitect.uninstantiatedDataElements()
      ]
      type: [ '$stateParams', ($stateParams) ->
        return "UninstantiatedDataElement"
      ]
    },
    controller: "metadataCurator.elementTypeList",
  }).state('dataArchitect.metadataKeyCheck', {
    url: "/metadataKeyCheck/{metadata}",
    templateUrl: "list.html",
    resolve: {
      list: ['$stateParams','modelCatalogueDataArchitect', ($stateParams, modelCatalogueDataArchitect) ->
        return modelCatalogueDataArchitect.metadataKeyCheck($stateParams.metadata)
      ]
      type: [ '$stateParams', ($stateParams) ->
        return "metadataKeyCheck"
      ]
    },
    controller: "metadataCurator.elementTypeList",
  })
)


metadataCurator.controller('metadataCurator.elementTypeShow', ['catalogueElementResource', 'modelCatalogueSearch', '$scope', '$log', '$q', '$state', 'element', 'columns', (catalogueElementResource, modelCatalogueSearch, $scope, $log, $q, $state, element, columns)->

  $scope.element = element
  $scope.columns = columns()

  # TODO: rewrite to states when finished
  $scope.$on 'showCatalogueElement', (event, element) ->
    $state.go('catalogueElement.show', {elementType: element.elementType.split(".").pop(), elementId: element.id})

])

metadataCurator.controller('metadataCurator.searchCtrl', ['catalogueElementResource', 'modelCatalogueSearch', '$scope', '$log', '$q', '$state', (catalogueElementResource, modelCatalogueSearch, $scope, $log, $q, $state)->

  $scope.searchSelect

  $scope.search = () ->
    if(typeof $scope.searchSelect == 'string')
      $state.go('search', {searchString: $scope.searchSelect })
    else
      $state.go('catalogueElement.show', {elementType: $scope.searchSelect.elementType.split(".").pop(), elementId: $scope.searchSelect.id})
])