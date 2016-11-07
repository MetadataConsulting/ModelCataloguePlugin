angular.module('mc.core.ui.messagesPanel', ['mc.util.messages']).directive 'messagesPanel',  [-> {
  restrict: 'E'
  replace: true

  templateUrl: 'modelcatalogue/core/ui/messagesPanel.html'

  scope:
    max: '=?'
    messages: '=?'
    growl: '@?'

  controller: ['$scope', 'messages', ($scope, messages) ->
    $scope.max ?= 5
    $scope.messages ?= messages

    $scope.getMessages = () ->
      $scope.messages.getMessages().slice(-$scope.max)
  ]
}]