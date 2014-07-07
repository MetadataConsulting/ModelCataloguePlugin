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

metadataCurator.run ($templateCache) ->
  $templateCache.put 'modelcatalogue/core/ui/omnisearchItem.html', '''
  <a>
      <span class="glyphicon omnisearch-icon" ng-class="'glyphicon-' + match.model.icon"></span>
      <span ng-if="!match.model.highlight" bind-html-unsafe="match.label"></span>
      <span ng-if=" match.model.highlight" bind-html-unsafe="match.label | typeaheadHighlight:query"></span>
  </a>
'''

metadataCurator.controller('metadataCurator.searchCtrl',
  ['catalogueElementResource', 'modelCatalogueSearch', '$scope', '$log', '$q', '$state', 'names'
    (catalogueElementResource, modelCatalogueSearch, $scope, $log, $q, $state, names)->
      actions = []

      $scope.search = (item, model, label) ->
        if angular.isString(item)
          $state.go('mc.search', {q: model })
        else
          item?.action item, model, label

      initActions = ->
        actions = []
        actions.push {
          condition: (term) -> term
          label: (term) ->
            "Search <strong>Catalogue Element</strong> for <strong>#{term}</strong>"

          action: (term) -> ->
              $state.go('mc.search', {q: term})

          icon: 'search'
        }

        actions.push {
          condition: (term) -> term and $state.current.name != 'mc.search' and  $state.$current.params.indexOf('q') >= 0 and $state.params.resource
          label: (term) ->
            naturalName = names.getNaturalName($state.params.resource)
            "Search <strong>#{naturalName}</strong> for <strong>#{term}</strong>"
          action: (term) ->
            ->
              $state.go($state.current.name, {q: term})
          icon: 'search'
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
                label:      searchResult.name
                action:     searchResult.show
                icon:       'file'
                term:       term
                highlight:  true
              }

            deferred.resolve results
        else
          deferred.resolve results

        deferred.promise

      initActions()

      $scope.$on '$stateChangeSuccess', (event, toState, toParams) ->
        $scope.searchSelect = toParams.q

  ])

metadataCurator.controller('metadataCurator.logoutCtrl', ['$scope', 'security', ($scope, security)->
  $scope.logout = ->
    security.logout()
])

metadataCurator.controller('metadataCurator.loginCtrl', ['security', '$scope', (security, $scope)->
  $scope.login = ->
    security.requireLogin()
])