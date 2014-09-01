angular.module('mc.core.ui.bs.saveAndCreateAnotherCtrlMixin', []).controller 'saveAndCreateAnotherCtrlMixin', ['$scope', '$modalInstance', 'messages', 'catalogueElementResource',  ($scope, $modalInstance, messages, catalogueElementResource) ->
  $scope.newEntity ?= -> {}

  if $scope.create
      $scope.saveAndCreateAnother ?= ->
        $scope.save().then (result)->
          result.show() if result.show
          $scope.copy = $scope.newEntity() ? {}

  $scope.saveElement ?= (newVersion) ->
     $scope.save(newVersion).then (result)->
       $modalInstance.close(result)

  $scope.hasChanged ?= ->
    not angular.equals $scope.copy, $scope.original

  $scope.beforeSave ?= ->

  $scope.validate ?= ->
    if not $scope.copy.name
      $scope.messages.error 'Empty Name', 'Please fill the name'
      return false
    return true

  $scope.save ?= (newVersion) ->
    $scope.messages.clearAllMessages()
    $scope.beforeSave()

    return unless $scope.validate()

    promise = null

    if $scope.create
      promise = catalogueElementResource($scope.create).save($scope.copy)
    else
      promise = catalogueElementResource($scope.copy.elementType).update($scope.copy, {newVersion: newVersion})

    promise.then (result) ->
      if $scope.create
        messages.success('Created ' + result.elementTypeName, "You have created #{result.elementTypeName} #{result.name}.")
      else
        messages.success('Updated ' + result.elementTypeName, "You have updated #{result.elementTypeName} #{result.name}.")
      result
    , (response) ->
      for err in response.data.errors
        $scope.messages.error err.message

]
