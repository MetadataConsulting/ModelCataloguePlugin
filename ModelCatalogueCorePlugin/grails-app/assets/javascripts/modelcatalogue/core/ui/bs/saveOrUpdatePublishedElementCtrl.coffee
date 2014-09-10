angular.module('mc.core.ui.bs.saveOrUpdatePublishedElementCtrl', []).controller 'saveOrUpdatePublishedElementCtrl', ['$scope', 'messages', '$controller', '$modalInstance', 'args', 'catalogueElementResource', '$q', 'classificationInUse', ($scope, messages, $controller, $modalInstance, args, catalogueElementResource, $q ,classificationInUse) ->
  $scope.$modalInstance = $modalInstance
  $scope.pending        = {classification: null}
  $scope.newEntity      = -> {classifications: $scope.copy?.classifications ? []}
  $scope.copy           = angular.copy(args.element ? $scope.newEntity())
  $scope.original       = args.element ? {}
  $scope.messages       = messages.createNewMessages()
  $scope.create         = args.create

  if args.create and classificationInUse
    $scope.copy.classifications.push classificationInUse

  angular.extend(this, $controller('saveAndCreateAnotherCtrlMixin', {$scope: $scope, $modalInstance: $modalInstance}))

  # required by save and update action
  $scope.hasChanged   = ->
    $scope.copy.name != $scope.original.name or $scope.copy.description != $scope.original.description or $scope.copy.classifications != $scope.original.classifications

  $scope.beforeSave = ->
    promise = $q.when {}

    if angular.isString($scope.pending.classification)
      promise = promise.then -> catalogueElementResource('classification').save({name: $scope.pending.classification}).then (newClassification) ->
        $scope.copy.classifications.push newClassification
        $scope.pending.classification = null

    if angular.isString($scope.copy.valueDomain)
      promise = promise.then -> catalogueElementResource('valueDomain').save({name: $scope.copy.valueDomain}).then (newDomain) ->
        $scope.copy.valueDomain = newDomain

    promise
]
