angular.module('mc.core.ui.messagesPanel', ['mc.util.messages']).directive 'messagesPanel',  [-> {
  restrict: 'E'
  replace: true

  templateUrl: 'modelcatalogue/core/ui/messagesPanel.html'

  scope:
    max: '=?'

  controller: ['$scope', 'messages', ($scope, messages) ->
    $scope.max ?= 5
    $scope.getMessages = () ->
      messages.getMessages().slice(-$scope.max)
  ]
}]