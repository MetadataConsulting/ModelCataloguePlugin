angular.module('mc.core.ui.bs.modalPromptEditRelationship', ['mc.util.messages']).config (messagesProvider) ->
  messagesProvider.setPromptFactory 'update-relationship', ($uibModal, $q, messages, catalogueElementResource, enhance, $stateParams) ->
    "ngInject"
    (title, body, args) ->
      if not args?.element?
        messages.error('Cannot create relationship dialog.', 'The element to be connected to is missing.')
        return $q.reject('Missing element argument!')

      dialog = $uibModal.open {
        windowClass: 'new-relationship-modal-prompt'
        size: 'lg'
        templateUrl: '/mc/core/ui/modals/modalEditRelationship.html'
        controller: ($scope, messages, $uibModalInstance, catalogue) ->
          'ngInject'
          $scope.relationshipTypes    = []
          $scope.relationshipTypeInfo = null
          $scope.relation             = args.relation
          $scope.classification       = args.classification
          $scope.update               = args.update


          if not args.currentDataModel and $stateParams.dataModelId and $stateParams.dataModelId != 'catalogue'
            catalogueElementResource('dataModel').get($stateParams.dataModelId).then (dataModel) ->
              $scope.currentDataModel = args.currentDataModel = dataModel
              $scope.currentModelOnly = $scope.currentDataModel?.id == args.classification?.id

          $scope.currentDataModel = args.currentDataModel
          $scope.currentModelOnly = $scope.currentDataModel?.id == args.classification?.id

          $scope.updateModelOnly = (currentModelOnly) ->
            if currentModelOnly
              $scope.classification = $scope.currentDataModel
            else
              $scope.classification = null

          $scope.updateInfo = (info) ->
            if info
              $scope.relationshipTypeInfo = info
              $scope.relationshipType = info.type
              $scope.direction = info.direction
              if info.type.rule?.indexOf("isSameClass()") >= 0
                if catalogue.isInstanceOf($scope.element.elementType, 'dataType')
                  $scope.relationType = 'org.modelcatalogue.core.DataType'
                else
                  $scope.relationType = $scope.element.elementType
              else
                $scope.relationType = $scope.relationshipType["#{info.relation}Class"]
            else
              $scope.relationshipTypeInfo = null
              $scope.relationshipType = null
              $scope.relationType = null
              $scope.direction = null

          appendToRelationshipTypes = (result) ->
            for type in result.list
              outgoing = if args.element.isInstanceOf(type.sourceClass) then {type: type, value: type.sourceToDestination, relation: 'destination',  direction: 'outgoing'}
              incoming = if args.element.isInstanceOf(type.destinationClass) and type.sourceToDestination != type.destinationToSource then {type: type, value: type.destinationToSource, relation: 'source',       direction: 'incoming'}
              $scope.relationshipTypes.push outgoing if outgoing
              $scope.relationshipTypes.push incoming if incoming

              if args.relationshipTypeName and args.direction and args.relationshipTypeName == type.name
                $scope.updateInfo(if args.direction == 'sourceToDestination' then outgoing else incoming)

            if result.next.size > 0
              result.next().then appendToRelationshipTypes

          $scope.element    = args.element

          $scope.updateRelation = (relation) ->
            $scope.relation = relation

            $scope.metadataOwner =
              type: $scope.relationshipTypeInfo?.type
              element: $scope.element
              relation: if relation then relation else {elementType: $scope.relationType}
              direction: if $scope.direction == 'outgoing' then 'sourceToDestination' else 'destinationToSource'
              ext: $scope.metadata
              elementType: 'relationship'

          $scope.updateClassification = (classification) ->
            $scope.classification = classification

          $scope.relationType = 'catalogueElement'

          $scope.messages = messages.createNewMessages()

          $scope.metadata = args.metadata ? enhance.getEnhancer('orderedMap').emptyOrderedMap()

          $scope.createRelation = ->
            $scope.messages.clearAllMessages()
            if not $scope.relationshipType
              $scope.messages.error 'Missing Relationship Type', 'Please select the relationship type'
              return

            if not $scope.direction
              $scope.messages.error 'Missing Direction', 'Please select the direction'
              return

            if not $scope.relation or angular.isString($scope.relation)
              $scope.messages.error 'Missing Relation', 'Please select the relation from the existing elements'
              return

            # this is ignored by binding and handled separately
            $scope.relation.metadata = $scope.metadata
            $scope.relation.__classification = $scope.classification
            $scope.relation.__oldClassification = args.classification

            args.element["#{$scope.direction}Relationships"].add($scope.relationshipType.name, $scope.relation, args.update).then (result) ->
              messages.success('Relationship Updated', "You have updated relationship #{$scope.element.name} #{$scope.relationshipTypeInfo.value} #{$scope.relation.name}.")
              $uibModalInstance.close(result)
            , (response) ->
              for err in response.data.errors
                $scope.messages.error err.message


          if args.relation and args.direction and args.relationshipType
            info = { type: args.relationshipType}

            if args.direction == 'sourceToDestination'
              info.relation = 'destination'
              info.direction = 'outgoing'
              info.value = args.relationshipType.sourceToDestination
            else
              info.relation = 'source'
              info.direction = 'incoming'
              info.value = args.relationshipType.destinationToSource

            $scope.updateInfo info

          catalogueElementResource('relationshipType').list(max: 100).then(appendToRelationshipTypes).then ->
            $scope.updateRelation($scope.relation)

      }

      dialog.result
