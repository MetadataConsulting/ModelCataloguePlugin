angular.module('mc.core.ui.states.landing', ['mc.core.ui.states.controllers.DashboardCtrl']).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'landing', {
      url: ''
      templateUrl: 'modelcatalogue/core/ui/state/dashboardWithNav.html',
      controller: 'mc.core.ui.states.controllers.DashboardCtrl'
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
      controller: 'mc.core.ui.states.controllers.DashboardCtrl'
      resolve:
        user: ['security', (security) ->
          if security.getCurrentUser() then return security.getCurrentUser() else return {displayName: ''}
        ]
        statistics: ['catalogue', 'security', '$stateParams', (catalogue, security, $stateParams) ->
          if security.getCurrentUser()?.id then return catalogue.getStatistics($stateParams.dataModelId) else return ''
        ]
    }
])