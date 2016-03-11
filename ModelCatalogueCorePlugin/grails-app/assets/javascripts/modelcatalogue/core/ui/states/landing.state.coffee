angular.module('mc.core.ui.states.landing', ['mc.core.ui.states.controllers.DashboardCtrl']).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'landing', {
      url: ''
      templateUrl: '/mc/core/ui/states/dashboard.html',
      controller: 'mc.core.ui.states.controllers.DashboardCtrl'
      resolve:
        user: ['security', (security) ->
          if security.getCurrentUser() then return security.getCurrentUser() else return {displayName: ''}
        ]
    }

    $stateProvider.state 'landing2', {
      url: '/'
      templateUrl: '/mc/core/ui/states/dashboard.html',
      controller: 'mc.core.ui.states.controllers.DashboardCtrl'
      resolve:
        user: ['security', (security) ->
          if security.getCurrentUser() then return security.getCurrentUser() else return {displayName: ''}
        ]
    }
])
