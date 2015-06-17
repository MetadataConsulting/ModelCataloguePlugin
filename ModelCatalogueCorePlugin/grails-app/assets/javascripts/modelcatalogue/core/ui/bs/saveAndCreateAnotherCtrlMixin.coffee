angular.module('mc.core.ui.bs.saveAndCreateAnotherCtrlMixin', []).controller 'saveAndCreateAnotherCtrlMixin', ['$scope', '$modalInstance', 'messages', 'catalogueElementResource', '$q',  ($scope, $modalInstance, messages, catalogueElementResource, $q) ->
  $scope.newEntity ?= -> {}

  if $scope.create
      $scope.saveAndCreateAnother ?= ->
        $scope.save().then ->
          $scope.copy = $scope.newEntity() ? {}

  $scope.saveElement ?= (newVersion) ->
     closeModal = (result)->
       $modalInstance.close(result)
     promise = $scope.save(newVersion)
     promise.then closeModal

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

    promise = $q.when $scope.beforeSave()

    return unless $scope.validate()


    if $scope.create
      promise = promise.then -> catalogueElementResource($scope.create).save($scope.copy)
    else
      promise = promise.then -> catalogueElementResource($scope.copy.elementType).update($scope.copy, {newVersion: newVersion})

    promise.then (result) ->
      if $scope.create
        messages.success('Created ' + result.getElementTypeName(), "You have created #{result.getElementTypeName()} #{result.name}.")
      else
        messages.success('Updated ' + result.getElementTypeName(), "You have updated #{result.getElementTypeName()} #{result.name}.")
      result
    , (response) ->
      if response?.data?.errors
        for err in response.data.errors
          $scope.messages.error err.message
      $q.reject response

]
