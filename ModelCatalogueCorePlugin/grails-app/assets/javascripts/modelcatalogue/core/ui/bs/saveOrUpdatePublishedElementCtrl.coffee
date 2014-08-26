angular.module('mc.core.ui.bs.saveOrUpdatePublishedElementCtrl', []).controller 'saveOrUpdatePublishedElementCtrl', ['$scope', 'messages', 'names', 'catalogueElementResource', '$modalInstance', 'args', ($scope, messages, names, catalogueElementResource, $modalInstance, args) ->
  $scope.$modalInstance = $modalInstance
  $scope.copy           = angular.copy(args.element ? {})
  $scope.original       = args.element ? {}
  $scope.messages       = messages.createNewMessages()
  $scope.create         = args.create

  # required by save and update action
  $scope.hasChanged   = ->
    $scope.copy.name != $scope.original.name or $scope.copy.description != $scope.original.description

  $scope.saveElement = (newVersion)->
    $scope.messages.clearAllMessages()
    if not $scope.copy.name
      $scope.messages.error 'Empty Name', 'Please fill the name'
      return


    promise = null

    if args?.create
      promise = catalogueElementResource(args.create).save($scope.copy)
    else
      promise = catalogueElementResource($scope.copy.elementType).update($scope.copy, {newVersion: newVersion})

    promise.then (result) ->
      if args?.create
        messages.success('Created ' + result.elementTypeName, "You have created #{result.elementTypeName} #{result.name}.")
      else
        messages.success('Updated ' + result.elementTypeName, "You have updated #{result.elementTypeName} #{result.name}.")
      $modalInstance.close(result)
    , (response) ->
      for err in response.data.errors
        $scope.messages.error err.message

]
