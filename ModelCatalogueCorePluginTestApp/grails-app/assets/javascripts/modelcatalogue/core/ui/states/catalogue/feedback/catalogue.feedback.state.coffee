angular.module('modelcatalogue.core.ui.states.catalogue.feedback', ['modelcatalogue.core.ui.states.catalogue.feedback.FeedbackCtrl']).config(($stateProvider, actionsProvider, actionRoleRegister, actionClass) ->
    Action = actionClass
    $stateProvider.state 'catalogue.feedback', {

      templateUrl: '/modelcatalogue/core/ui/states/catalogue/feedback/feedback.html'
      controller: 'modelcatalogue.core.ui.states.catalogue.feedback.FeedbackCtrl'

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
