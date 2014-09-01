angular.module('mc.core.ui.bs.saveOrUpdatePublishedElementCtrl', []).controller 'saveOrUpdatePublishedElementCtrl', ['$scope', 'messages', '$controller', '$modalInstance', 'args', ($scope, messages, $controller, $modalInstance, args) ->
  $scope.$modalInstance = $modalInstance
  $scope.copy           = angular.copy(args.element ? {classifications: []})
  $scope.original       = args.element ? {}
  $scope.messages       = messages.createNewMessages()
  $scope.create         = args.create

  angular.extend(this, $controller('saveAndCreateAnotherCtrlMixin', {$scope: $scope, $modalInstance: $modalInstance}))

  # required by save and update action
  $scope.hasChanged   = ->
    $scope.copy.name != $scope.original.name or $scope.copy.description != $scope.original.description or $scope.copy.classifications != $scope.original.classifications

]
