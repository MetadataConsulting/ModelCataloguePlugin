angular.module('mc.core.ui.states.simple.feedback', ['mc.core.ui.states.controllers.FeedbackCtrl']).config(($stateProvider, actionsProvider, actionRole) ->

    $stateProvider.state 'simple.feedback', {

      templateUrl: '/mc/core/ui/states/feedback.html'
      controller: 'mc.core.ui.states.controllers.FeedbackCtrl'

      url: '/feedback/{id:\\d+}'

      onEnter: ['applicationTitle', (applicationTitle) ->
        applicationTitle "Feedback"
      ]

      resolve:
        feedback: ($http, modelCatalogueApiRoot, $stateParams) ->
          $http.get("#{modelCatalogueApiRoot}/feedback/#{$stateParams.id}").then (result) ->
            result.data

    }

    actionsProvider.registerActionInRole 'refresh-feedback', actionRole.ROLE_FEEDBACK,  ($scope, $http, modelCatalogueApiRoot, $stateParams) ->
      "ngInject"
      {
        position:   -100
        label:      ''
        icon:       'glyphicon glyphicon-refresh'
        type:       'primary'
        action:     ->
          $http.get("#{modelCatalogueApiRoot}/feedback/#{$stateParams.id}").then (result) ->
            $scope.feedback = result.data

      }

)
