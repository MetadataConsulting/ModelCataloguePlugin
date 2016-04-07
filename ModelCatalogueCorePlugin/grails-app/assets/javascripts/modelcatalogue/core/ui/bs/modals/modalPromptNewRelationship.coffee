angular.module('mc.core.ui.bs.modalPromptNewRelationship', ['mc.util.messages', 'mc.core.ui.bs.watchAndAskForImportOrCloneCtrl']).config ['messagesProvider', (messagesProvider)->
  messagesProvider.setPromptFactory 'create-new-relationship', [ '$log', '$modal', '$q', 'messages', 'catalogueElementResource', 'enhance', ($log, $modal, $q, messages, catalogueElementResource, enhance) ->
    (title, body, args) ->
      if not args?.element?
        messages.error('Cannot create relationship dialog.', 'The element to be connected to is missing.')
        return $q.reject('Missing element argument!')

      dialog = $modal.open {
        size: 'lg'
        windowClass: 'new-relationship-modal-prompt'
        templateUrl: '/mc/core/ui/modals/modalNewRelationship.html'
        controller: ['$scope', 'messages', '$modalInstance', '$controller', '$stateParams', 'catalogueElementResource',
          ($scope, messages, $modalInstance, $controller, $stateParams, catalogueElementResource) ->

            if not args.currentDataModel and $stateParams.dataModelId and $stateParams.dataModelId != 'catalogue'
              catalogueElementResource('dataModel').get($stateParams.dataModelId).then (dataModel) ->
                args.currentDataModel = dataModel

            angular.extend(this, $controller('watchAndAskForImportOrCloneCtrl', {$scope: $scope}))

            $scope.relationshipTypes = []
            $scope.relationshipTypeInfo = null
            $scope.relationType = 'catalogueElement'
            $scope.element = args.element
            $scope.messages = messages.createNewMessages()
            $scope.destinations = []

            $scope.updateInfo = (info) ->
              if info
                $scope.relationshipTypeInfo = info
                $scope.relationshipType = info.type
                $scope.relationType = $scope.relationshipType["#{info.relation}Class"]
                $scope.direction = info.direction

              else
                $scope.relationshipTypeInfo = null
                $scope.relationshipType = null
                $scope.relationType = null
                $scope.direction = null
                $scope.metadataOwner = null

              for destination in $scope.destinations
                destination.updateRelation(destination.relation)

            $scope.addDestination = ->
              destination = messages: messages.createNewMessages(), metadata: enhance.getEnhancer('orderedMap').emptyOrderedMap()
              destination.updateRelation = (relation) ->

                doUpdateRelation = (relation) =>
                  @metadataOwner =
                    type: $scope.relationshipTypeInfo?.type
                    element: $scope.element
                    relation: if relation then relation else {elementType: $scope.relationType}
                    direction: if $scope.direction == 'outgoing' then 'sourceToDestination' else 'destinationToSource'
                    ext:
                      type: 'orderedMap'
                    elementType: 'relationship'

                  @relation = relation

                if $scope.relationshipType?.versionSpecific
                  $scope.cloneOrImport(relation, args.currentDataModel).then doUpdateRelation
                else
                  doUpdateRelation(relation)

              destination.updateClassification = (classification) -> @classification = classification

              # init metadata editors
              destination.updateRelation(null)

              $scope.destinations.push destination

            $scope.removeDestination = ($index) ->
              $scope.destinations.splice $index, 1


            $scope.createRelation = ->
              wasError = false
              $scope.messages.clearAllMessages()
              if not $scope.relationshipType
                $scope.messages.error 'Missing Relationship Type', 'Please select the relationship type'
                wasError = true

              for destination in $scope.destinations
                $scope.messages.clearAllMessages()
                if not destination.relation
                  destination.messages.error 'Missing Destination', 'Please select the destination from the existing elements or type new destination name.'
                  wasError = true

              return if wasError

              promises = []

              angular.forEach $scope.destinations, (destination) ->
                promise = $q.when destination
                # create new model catalog if requested
                if angular.isString(destination.relation)
                  # create new catalog element
                  promise = promise.then (destination)->
                    # check if relationType is unknown
                    if ($scope.relationType == 'org.modelcatalogue.core.CatalogueElement')
                      destination.messages.error "cannot create new catalog element [#{destination.relation}] as the " +
                          "type is unknown [#{$scope.relationType}]"
                      return $q.reject false
                    else
                      $log.info "creating new catalog element (#{$scope.relationType}) #{destination.relation}"
                      return catalogueElementResource($scope.relationType).save({name: destination.relation, dataModel: args.currentDataModel})
                        .then (catalogueElement) ->
                          destination.relation = catalogueElement
                          return destination
                        .catch (response) ->
                          destination.messages.error "Unexpected error while saving new catalogue element [#{destination.relation}]."
                          return $q.reject response

                promise = promise.then (destination) ->
                  # this is ignored by binding and handled separately
                  destination.relation.metadata = destination.metadata
                  destination.relation.__classification = destination.classification
                  args.element["#{$scope.direction}Relationships"].add($scope.relationshipType.name, destination.relation).then (result) ->
                    destination.created = true
                    messages.success('Relationship Created', "You have added new relationship #{$scope.element.name} #{$scope.relationshipTypeInfo.value} #{destination.relation.name} in the catalogue.")
                    result
                  , (response) ->
                    destination.messages.error "Unexpected error while creating relationship."
                    $q.reject response

                promises.push promise

              $q.all(promises).then (results) ->
                $modalInstance.close(results)



            appendToRelationshipTypes = (result) ->
              for type in result.list
                outgoing = if args.element.isInstanceOf(type.sourceClass) then {type: type, value: type.sourceToDestination, relation: 'destination',  direction: 'outgoing', description: type.sourceToDestinationDescription}
                incoming = if args.element.isInstanceOf(type.destinationClass) and type.sourceToDestination != type.destinationToSource then {type: type, value: type.destinationToSource, relation: 'source', direction: 'incoming', description: type.destinationToSourceDescription}
                $scope.relationshipTypes.push outgoing if outgoing
                $scope.relationshipTypes.push incoming if incoming

                if args.relationshipTypeName and args.direction and args.relationshipTypeName == type.name
                  $scope.updateInfo(if args.direction == 'sourceToDestination' then outgoing else incoming)

              if result.next.size > 0
                result.next().then appendToRelationshipTypes

            $scope.addDestination()

            catalogueElementResource('relationshipType').list(max: 100).then(appendToRelationshipTypes)
        ]
      }

      dialog.result
  ]
]
