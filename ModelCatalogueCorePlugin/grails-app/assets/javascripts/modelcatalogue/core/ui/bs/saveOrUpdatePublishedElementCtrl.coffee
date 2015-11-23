angular.module('mc.core.ui.bs.saveOrUpdatePublishedElementCtrl', ['mc.core.ui.bs.withClassificationCtrlMixin']).controller 'saveOrUpdatePublishedElementCtrl', ['$scope', 'messages', '$controller', '$modalInstance', 'args', 'catalogueElementResource', '$q', ($scope, messages, $controller, $modalInstance, args, catalogueElementResource, $q) ->
  $scope.$modalInstance = $modalInstance
  $scope.pending        = {dataModel: null}
  $scope.newEntity      = -> {dataModels: $scope.copy?.dataModels ? []}
  $scope.copy           = angular.copy(args.element ? $scope.newEntity())
  $scope.original       = args.element ? {}
  $scope.messages       = messages.createNewMessages()
  $scope.create         = args.create
  $scope.currentDataModel = args.currentDataModel

  angular.extend(this, $controller('withClassificationCtrlMixin', {$scope: $scope}))
  angular.extend(this, $controller('saveAndCreateAnotherCtrlMixin', {$scope: $scope, $modalInstance: $modalInstance}))

  # required by save and update action
  $scope.hasChanged   = ->
    $scope.copy.name != $scope.original.name or $scope.copy.description != $scope.original.description or $scope.copy.classifications != $scope.original.classifications

  $scope.beforeSave = ->
    promise = $q.when {}

    if $scope.pending.dataModel and angular.isString($scope.pending.dataModel)
      promise = promise.then -> catalogueElementResource('dataModel').save({name: $scope.pending.dataModel}).then (newDataModel) ->
        $scope.copy.dataModels = $scope.copy.dataModels ? []
        $scope.copy.dataModels.push newDataModel
        $scope.pending.dataModel = null

    if $scope.copy.dataType and angular.isString($scope.copy.dataType)
      promise = promise.then -> catalogueElementResource('dataType').save({name: $scope.copy.dataType, dataModels: $scope.copy.dataModels}).then (newType) ->
        $scope.copy.dataType = newType

    promise
]
