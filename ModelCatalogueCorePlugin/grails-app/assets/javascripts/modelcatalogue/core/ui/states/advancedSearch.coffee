angular.module('mc.core.ui.states.advancedSearch', ['ui.router', 'mc.util.ui'])
.config(['$stateProvider', 'catalogueProvider', ($stateProvider, catalogueProvider) ->

  $stateProvider.state 'mc.advancedSearch', {
    url: '/advancedSearch'
    templateUrl: 'modelcatalogue/core/ui/state/advancedSearch.html'
    resolve:
      elementClasses: ['$http', 'modelCatalogueApiRoot', ($http, modelCatalogueApiRoot) ->
        $http.get("#{modelCatalogueApiRoot}/relationshipType/elementClasses").then (response) -> response.data
      ]
    controller: 'mc.core.ui.states.AdvancedSearchCtrl'

  }
])

.run(['$templateCache', ($templateCache) ->

  $templateCache.put 'modelcatalogue/core/ui/state/advancedSearch.html', '''
    <h2>Advanced Search</h2>
    <h3>Type</h3>
    <div class="btn-group">
        <label title="{{names.getNaturalName(names.getPropertyNameFromType(class))}}" class="btn btn-default" ng-repeat="class in elementClasses" ng-click="selectClass(class)" ng-class="{active: isClassSelected(class)}"><span class="fa-3x text-muted"ng-class="catalogue.getIcon(class)"></span></label>
    </div>
    <h3>Criteria</h3>
    <div class="advanced-search-criteria">
      <div class="row">
        <div class="col-md-3">
          <label class="text-muted">Property</label>
          <select class="form-control" ng-options="value for value in ['name', 'description']" ng-model="criteria.property"></select>
        </div>
        <div class="col-md-3">
          <label class="text-muted">Operation</label>
          <select class="form-control col-md-4" ng-options="value for value in ['is', 'is not', 'contains', 'not contains']" ng-model="criteria.operator"></select>
        </div>
        <div class="col-md-6">
          <label class="text-muted">Query</label>
          <input class="form-control col-md-4" type="text" ng-model="criteria.query">
        </div>
      </div>
      <hr/>
      <div class="row">
        <div class="col-md-3">
          <label class="text-muted">Metadata</label>
          <input class="form-control col-md-4" type="text" ng-model="criteria.metadata">
        </div>
        <div class="col-md-3">
          <label class="text-muted">Operation</label>
          <select class="form-control col-md-4" ng-options="value for value in ['is', 'is not', 'contains', 'not contains']" ng-model="criteria.operator"></select>
        </div>
        <div class="col-md-6">
          <label class="text-muted">Query</label>
          <input class="form-control col-md-4" type="text" ng-model="criteria.query">
        </div>
      </div>
      <hr/>
      <div class="row">
        <div class="col-md-2">
          <label class="text-muted">Relation</label>
          <select class="form-control" ng-options="value for value in ['is base for', 'contains']" ng-model="criteria.relation"></select>
        </div>
        <div class="col-md-2">
          <label class="text-muted">Property</label>
          <select class="form-control" ng-options="value for value in ['name', 'description']" ng-model="criteria.property"></select>
        </div>
        <div class="col-md-2">
          <label class="text-muted">Operation</label>
          <select class="form-control col-md-4" ng-options="value for value in ['is', 'is not', 'contains', 'not contains']" ng-model="criteria.operator"></select>
        </div>
        <div class="col-md-6">
          <label class="text-muted">Query</label>
          <input class="form-control col-md-4" type="text" ng-model="criteria.query">
        </div>
      </div>
    </div>
  '''
])


.controller('mc.core.ui.states.AdvancedSearchCtrl', ['$scope', '$stateParams', '$state', 'applicationTitle', 'elementClasses', 'catalogue', 'names', ($scope, $stateParams, $state, applicationTitle, elementClasses, catalogue, names) ->
    applicationTitle "Advanced Search"

    $scope.elementClasses = elementClasses
    $scope.catalogue = catalogue
    $scope.names = names
    $scope.selectedClass = 'org.modelcatalogue.core.CatalogueElement'

    $scope.selectClass = (cls) ->
      $scope.selectedClass = cls

    $scope.isClassSelected = (cls) ->
      $scope.selectedClass == cls
])