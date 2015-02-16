angular.module('mc.core.ui.bs.withClassificationCtrlMixin', ['mc.util.security']).controller 'withClassificationCtrlMixin', ['$scope', 'security',  ($scope, security) ->
  createdMessages = []
  classificationsChanged = (newClassifications) ->
    msg.remove() for msg in createdMessages
    createdMessages = []
    noDrafts = (classification.name for classification in newClassifications when classification.status isnt 'DRAFT')
    if noDrafts.length > 0 and $scope.messages
      createdMessages.push $scope.messages.warning "Draft versions of #{noDrafts.join(', ')} classification#{if noDrafts.length == 1 then '' else 's'} will be used"

  $scope.addToClassifications = (classification = $scope.pending?.classification) ->
    return unless classification
    if classification.status != 'DRAFT'
      for c in $scope.copy.classifications
        if c.name == classification.name and c.status == 'DRAFT'
          $scope.pending.classification = null if $scope.pending
          $scope.messages.info("You have already selected draft version of '#{c.name}'")
          return
    else
      replaceIndex = -1
      for c, index in $scope.copy.classifications
        if c.name == classification.name
          replaceIndex = index
          break
      if replaceIndex >= 0
        $scope.copy.classifications.splice(replaceIndex, 1, classification)
        $scope.pending.classification = null if $scope.pending
        return

    $scope.copy.classifications.push(classification)
    $scope.pending.classification = null if $scope.pending


  $scope.$watchCollection 'copy.classifications', classificationsChanged


  if security.getCurrentUser()?.classifications?.length and not $scope.copy.classifications.length
    for classification in security.getCurrentUser().classifications
      $scope.addToClassifications classification

]
