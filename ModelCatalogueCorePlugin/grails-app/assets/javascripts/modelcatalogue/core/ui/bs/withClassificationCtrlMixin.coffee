angular.module('mc.core.ui.bs.withClassificationCtrlMixin', ['mc.util.security']).controller 'withClassificationCtrlMixin', ['$scope', 'security',  ($scope, security) ->
  createdMessages = []
  dataModelsChanged = (newDataModels) ->
    newDataModels = newDataModels ? []
    msg.remove() for msg in createdMessages
    createdMessages = []
    noDrafts = (dataModel.name for dataModel in newDataModels when dataModel.status isnt 'DRAFT')
    if noDrafts.length > 0 and $scope.messages
      createdMessages.push $scope.messages.warning "Draft versions of #{noDrafts.join(', ')} dataModel#{if noDrafts.length == 1 then '' else 's'} will be used"

  $scope.hasDataModels = ->
    $scope.pending.dataModel or (angular.isArray($scope.copy.dataModels) && $scope.copy.dataModels.length > 0)

  $scope.addToDataModels = (dataModel = $scope.pending?.dataModel) ->
    return unless dataModel
    $scope.copy.dataModels = $scope.copy.dataModels ? []
    if dataModel.status != 'DRAFT'
      for c in $scope.copy.dataModels
        if c.name == dataModel.name and c.status == 'DRAFT'
          $scope.pending.dataModel = null if $scope.pending
          $scope.messages.info("You have already selected draft version of '#{c.name}'")
          return
    else
      replaceIndex = -1
      for c, index in $scope.copy.dataModels
        if c.name == dataModel.name
          replaceIndex = index
          break
      if replaceIndex >= 0
        $scope.copy.dataModels.splice(replaceIndex, 1, dataModel)
        $scope.pending.dataModel = null if $scope.pending
        return

    $scope.copy.dataModels.push(dataModel)
    $scope.pending.dataModel = null if $scope.pending


  $scope.$watchCollection 'copy.dataModels', dataModelsChanged

  $scope.hideDataModels = ->
    security.getCurrentUser()?.dataModels?.includes?.length == 1

  if security.getCurrentUser()?.dataModels?.includes?.length and not $scope.copy.dataModels.length
    for dataModel in security.getCurrentUser().dataModels.includes
      $scope.addToDataModels dataModel

]
