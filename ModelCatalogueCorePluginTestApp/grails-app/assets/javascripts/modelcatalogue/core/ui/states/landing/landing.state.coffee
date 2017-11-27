angular.module('modelcatalogue.core.ui.states.landing', ['modelcatalogue.core.ui.states.landing.DashboardCtrl']).config(($stateProvider) ->
  'ngInject'

  $stateProvider.state 'landing', {
    url: '/'
    templateUrl: '/modelcatalogue/core/ui/states/landing/dashboard.html',
    controller: 'modelcatalogue.core.ui.states.landing.DashboardCtrl'
    resolve:
      user: ['security', (security) ->
        if security.getCurrentUser() then return security.getCurrentUser() else return {displayName: ''}
      ]
  }
)
