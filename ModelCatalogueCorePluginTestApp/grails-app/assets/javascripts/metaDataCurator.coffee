#= require jquery/dist/jquery
#= require bootstrap/dist/js/bootstrap
#= require ng-file-upload-shim/angular-file-upload-shim
#= require angular/angular
#= require ng-file-upload/angular-file-upload
#= require angular-ui-router/release/angular-ui-router
#= require modelcatalogue/util/index
#= require modelcatalogue/core/index
#= require modelcatalogue/core/ui/index
#= require modelcatalogue/core/ui/states/index
#= require modelcatalogue/core/ui/bs/index

@grailsAppName = 'model_catalogue'

metadataCurator = angular.module('metadataCurator', [
  'demo.config'
  'mc.core.ui.bs'
  'mc.core.ui.states'
  'ui.bootstrap'
])

metadataCurator.config ($stateProvider, $urlRouterProvider)->
  $urlRouterProvider.otherwise("/catalogue/model/all")

metadataCurator.controller('metadataCurator.searchCtrl', ['catalogueElementResource', 'modelCatalogueSearch', '$scope', '$log', '$q', '$state', 'names', (catalogueElementResource, modelCatalogueSearch, $scope, $log, $q, $state, names)->
  $scope.search = () ->
    unless (typeof $scope.searchSelect == 'string')
#      $state.go('search', {searchString: $scope.searchSelect })
#    else
      $state.go('mc.resource.show', {resource: names.getPropertyNameFromType($scope.searchSelect.elementType) , id: $scope.searchSelect.id})
])