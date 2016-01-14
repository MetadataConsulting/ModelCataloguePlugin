angular.module('mc.core.ui.bs.modalPromptNewRelationship', ['mc.util.messages', 'mc.core.ui.bs.watchAndAskForImportOrCloneCtrl']).config ['messagesProvider', (messagesProvider)->
  messagesProvider.setPromptFactory 'create-new-relationship', [ '$modal', '$q', 'messages', 'catalogueElementResource', 'enhance', ($modal, $q, messages, catalogueElementResource, enhance) ->
    (title, body, args) ->
      if not args?.element?
        messages.error('Cannot create relationship dialog.', 'The element to be connected to is missing.')
        return $q.reject('Missing element argument!')

      dialog = $modal.open {
        size: 'lg'
        windowClass: 'new-relationship-modal-prompt'
        template: '''
        <div class="modal-body">
            <form role="form" ng-submit="createRelation()">
              <h4>{{element.name}}</h4>
              <messages-panel messages="messages"></messages-panel>
              <div class="form-group">
                <select id="type" ng-model="relationshipTypeInfo" class="form-control" ng-options="rt as rt.value for rt in relationshipTypes" ng-change="updateInfo(relationshipTypeInfo)">
                  <option value="">-- choose relation --</option>
                </select>
                <p class="help-block" ng-if="relationshipTypeInfo.description">{{relationshipTypeInfo.description}}</p>
              </div>
              <div class="panel panel-default" ng-repeat="destination in destinations">
                <div class="panel-heading">
                  <span class="fa fa-remove text-muted with-pointer pull-right" ng-click="removeDestination($index)" ng-if="!$first"></span>
                  <h3 class="panel-title">Destination </h3>
                </div>
                <div class="panel-body">
                  <messages-panel messages="destination.messages"></messages-panel>
                  <div class="form-group">
                    <input id="element" type="text" class="form-control" ng-model="destination.relation" global="'allow'" catalogue-element-picker resource="relationType" typeahead-on-select="destination.updateRelation(destination.relation)" ng-disabled="!relationshipTypeInfo.type">
                  </div>
                  <!--
                  <div class="form-group">
                    <label for="classification" ng-click="destination.classificationExpanded = ! destination.classificationExpanded">Data Model <span class="fa fa-fw" ng-class="{'fa-toggle-up': destination.classificationExpanded, 'fa-toggle-down': !destination.classificationExpanded}"></span></label>
                    <div collapse="!destination.classificationExpanded">
                      <input id="classification" ng-model="destination.classification" catalogue-element-picker="classification" label="el.name" typeahead-on-select="destination.updateClassification(destination.classification)">
                      <p class="help-block">Select a data model only if the relationship applies for given classification only. This usually happens when you are reusing catalogue elements form some standard data model</p>
                    </div>
                  </div>
                  -->
                  <div class="form-group">
                    <label ng-click="destination.metadataExpanded = ! destination.metadataExpanded" class="expand-metadata"">Metadata <span class="fa fa-fw" ng-class="{'fa-toggle-up': destination.metadataExpanded, 'fa-toggle-down': !destination.metadataExpanded}"></label>
                    <div collapse="!destination.metadataExpanded">
                      <p class="help-block metadata-help-block">Metadata specific to this relationship. For example <code>contains</code> and <code>parent of</code> relationship types supports <code>Name</code> metadata as an alias of nested model or data element.</p>
                      <metadata-editor owner="destination.metadataOwner" object="destination.metadata"></metadata-editor>
                    </div>
                  </div>
                </div>
              </div>
              <a class="btn btn-success btn-block" ng-click="addDestination()"><span class="fa fa-fw fa-plus"></span> Add Another</a>
              <fake-submit-button/>
            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" ng-click="createRelation()" type="submit"><span class="glyphicon glyphicon-link"></span> Create Relationship</button>
            <button class="btn btn-warning" ng-click="$dismiss()">Cancel</button>
        </div>
        '''
        controller: ['$scope', 'messages', '$modalInstance', '$controller', '$stateParams', 'catalogueElementResource', ($scope, messages, $modalInstance, $controller, $stateParams, catalogueElementResource) ->

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
              if not destination.relation or angular.isString(destination.relation)
                destination.messages.error 'Missing Destination', 'Please select the destination from the existing elements'
                wasError = true

            return if wasError

            promises = []

            for destination in $scope.destinations
              # this is ignored by binding and handled separately
              destination.relation.metadata = destination.metadata
              destination.relation.__classification = destination.classification
              promises.push args.element["#{$scope.direction}Relationships"].add($scope.relationshipType.name, destination.relation).then (result) ->
                destination.created = true
                messages.success('Relationship Created', "You have added new relationship #{$scope.element.name} #{$scope.relationshipTypeInfo.value} #{destination.relation.name} in the catalogue.")
                result
              , (response) ->
                for err in response.data.errors
                  destination.messages.error err.message
                $q.reject response


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