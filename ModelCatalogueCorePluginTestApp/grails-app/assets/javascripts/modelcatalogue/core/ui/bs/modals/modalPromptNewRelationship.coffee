angular.module('mc.core.ui.bs.modalPromptNewRelationship', ['mc.util.messages', 'mc.core.ui.bs.watchAndAskForImportOrCloneCtrl']).config (messagesProvider) ->
  messagesProvider.setPromptFactory 'create-new-relationship', ($log, $uibModal, $timeout, $q, messages, catalogueElementResource, enhance, catalogue) ->
    "ngInject"
    (title, body, args) ->
      if not args?.element?
        messages.error('Cannot create relationship dialog.', 'The element to be connected to is missing.')
        return $q.reject('Missing element argument!')

      dialog = $uibModal.open {
        size: 'lg'
        windowClass: 'new-relationship-modal-prompt'
        templateUrl: '/mc/core/ui/modals/modalNewRelationship.html'
        controller: ($scope, messages, $uibModalInstance, $controller, $stateParams, catalogueElementResource) ->
            'ngInject'
            if not args.currentDataModel and $stateParams.dataModelId and $stateParams.dataModelId != 'catalogue'
              catalogueElementResource('dataModel').get($stateParams.dataModelId).then (dataModel) ->
                $scope.currentDataModel = args.currentDataModel = dataModel

            $scope.currentDataModel = args.currentDataModel

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
                $scope.direction = null
                $scope.metadataOwner = null
                $scope.relationType = null

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
              return if $scope.pending
              $scope.pending = true
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

              if wasError
                $scope.pending = false
                return

              promises = []

              THRESHOLD = 300

              angular.forEach $scope.destinations, (destination, i) ->
                promise = $timeout((->destination), i * THRESHOLD)
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
                          return $timeout(( -> destination), THRESHOLD)
                        .catch (response) ->
                          if response.data and response.data.errors
                            for error in response.data.errors
                              destination.messages.error error.message
                          else
                            destination.messages.error "Unexpected error while saving new catalogue element [#{destination.relation}]."
                          return $q.reject response

                promise = promise.then (destination) ->
                  # this is ignored by binding and handled separately
                  destination.relation.metadata = destination.metadata
                  if $scope.currentDataModel and destination.currentModelOnly
                    destination.relation.__dataModel = $scope.currentDataModel
                  args.element["#{$scope.direction}Relationships"].add($scope.relationshipType.name, destination.relation).then (result) ->
                    destination.created = true
                    messages.success('Relationship Created', "You have added new relationship #{$scope.element.name} #{$scope.relationshipTypeInfo.value} #{destination.relation.name} in the catalogue.")
                    result
                  , (response) ->
                    if response.data and response.data.errors
                      for error in response.data.errors
                        destination.messages.error error.message
                    else
                      destination.messages.error "Unexpected error while creating relationship."
                    return $q.reject response

                promises.push promise

              $q.all(promises)
              .then (results) ->
                $uibModalInstance.close(results)
              .finally ->
                $scope.pending = false



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
      }

      dialog.result
