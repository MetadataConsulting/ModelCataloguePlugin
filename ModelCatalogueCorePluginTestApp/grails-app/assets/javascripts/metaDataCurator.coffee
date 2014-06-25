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
      actions = []

      initActions = ->
        actions = []
        actions.push {
          label: (term) ->
            "Search <strong>Catalogue</strong> for #{term}"
          action: (item, model, label) ->
            $state.go('mc.search', {q: model})
          icon: 'search'
        }

      $scope.$on '$stateChangeSuccess', (event, newState) ->
        $scope.searchSelect = if $state.params.q then $state.params.q else undefined
        initActions()
        if $state.$current.params.indexOf('q') >= 0 and $state.params.resource
          naturalName = names.getNaturalName($state.params.resource)
          actions.push {
             label: (term) ->
               "Search <strong>#{naturalName}</strong> for #{term}"
             action: (item, model, label) ->
                 $state.go(newState.name, {q: model})
             icon: 'search'
           }

      $scope.search = (item, model, label) ->
        if angular.isString(item)
          $state.go('mc.search', {q: model })
        else
          item.action item, model, label


      $scope.getResults = (term) ->
        deferred = $q.defer()

        results = []

        return if not term

        for action in actions
          value =
            label:  action.label(term)
            action: action.action
            term: term

          results.push value

        modelCatalogueSearch(term).then (searchResults)->
          for searchResult in searchResults.list
            results.push {
              label:  searchResult.name
              action: -> searchResult.show()
              term: term
            }

          deferred.resolve(results)

        deferred.promise


      initActions()
  ])



metadataCurator.controller('metadataCurator.logoutCtrl', ['$scope', 'security', ($scope, security)->
  $scope.logout = ->
    security.logout()
])

metadataCurator.controller('metadataCurator.loginCtrl', ['security', '$scope', (security, $scope)->
  $scope.login = ->
    security.requireLogin()
])