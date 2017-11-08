angular.module('mc.core.ui.states.simple.feedback', ['mc.core.ui.states.controllers.FeedbackCtrl']).config(($stateProvider, actionsProvider, actionRoleRegister, actionClass) ->
    Action = actionClass
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

    actionsProvider.registerActionInRole 'refresh-feedback', actionRoleRegister.ROLE_FEEDBACK_ACTION,  ($scope, $http, modelCatalogueApiRoot, $stateParams) ->
      "ngInject"
      Action.createStandardAction(
        position:   -100
        label:      ''
        icon:       'glyphicon glyphicon-refresh'
        type:       'primary'
        action:     ->
          $http.get("#{modelCatalogueApiRoot}/feedback/#{$stateParams.id}").then (result) ->
            $scope.feedback = result.data
      )

)
