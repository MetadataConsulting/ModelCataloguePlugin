#= require jquery/dist/jquery
#= require bootstrap/dist/js/bootstrap
#= require ng-file-upload-shim/angular-file-upload-shim
#= require angular/angular
#= require angular-animate/angular-animate
#= require ng-file-upload/angular-file-upload
#= require angular-http-auth/src/http-auth-interceptor
#= require angular-loading-bar/build/loading-bar
#= require angular-ui-router/release/angular-ui-router
#= require modelcatalogue/util/index
#= require modelcatalogue/util/ui/index
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
  'angular-loading-bar'
  'ngAnimate'
])

metadataCurator.config ($stateProvider, $urlRouterProvider)->
  $urlRouterProvider.otherwise("/catalogue/model/all")

metadataCurator.controller('metadataCurator.searchCtrl',
  ['catalogueElementResource', 'modelCatalogueSearch', '$scope', '$log', '$q', '$state', 'names',
    (catalogueElementResource, modelCatalogueSearch, $scope, $log, $q, $state, names)->
      $scope.search = () ->
        unless (typeof $scope.searchSelect == 'string')
#      $state.go('search', {searchString: $scope.searchSelect })
#    else
          $state.go('mc.resource.show',
            {resource: names.getPropertyNameFromType($scope.searchSelect.elementType), id: $scope.searchSelect.id})
  ])

metadataCurator.controller('metadataCurator.logoutCtrl', ['$scope', 'security', ($scope, security)->
  $scope.logout = ->
    security.logout()
])

metadataCurator.controller('metadataCurator.loginCtrl', ['$rootScope', '$scope', ($rootScope, $scope)->
  $scope.login = ->
    $rootScope.$broadcast 'event:auth-loginRequired'
])