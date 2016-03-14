angular.module('mc.core.ui.bs.modalPromptEditRelationship', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
  messagesProvider.setPromptFactory 'update-relationship', [ '$modal', '$q', 'messages', 'catalogueElementResource', 'enhance', ($modal, $q, messages, catalogueElementResource, enhance) ->
    (title, body, args) ->
      if not args?.element?
        messages.error('Cannot create relationship dialog.', 'The element to be connected to is missing.')
        return $q.reject('Missing element argument!')

      dialog = $modal.open {
        windowClass: 'new-relationship-modal-prompt'
        size: 'lg'
        template: '''
        <div class="modal-body">
            <div class="panel-body">
              <form role="form" ng-submit="createRelation()">
                <h4>{{element.name}} <code>{{relationshipTypeInfo.value}}</code> {{relation.name}} <span ng-show="classification"> in classification {{classification.name}}</h4>
                <messages-panel messages="messages"></messages-panel>
                <div class="form-group">
                  <label for="classification">Data Model</label>
                  <input id="classification" ng-model="classification" catalogue-element-picker="dataModel" label="el.name" typeahead-on-select="updateClassification(classification)">
                  <p class="help-block">Select a data model only if the relationship applies for given classification only. This usually happens when you are reusing catalogue elements form some standard data model</p>
                </div>
                <div class="form-group">
                  <label>Metadata</label>
                  <p class="help-block">Metadata specific to this relationship. For example <code>contains</code> and <code>parent of</code> relationship types supports <code>Name</code> metadata as an alias of nested model or data element.</p>
                  <metadata-editor object="metadata" owner="metadataOwner"></metadata-editor>
                </div>
                <fake-submit-button/>
              </form>
            </div>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" ng-click="createRelation()" type="submit"><span class="glyphicon glyphicon-edit"></span> Update Relationship</button>
            <button class="btn btn-warning" ng-click="$dismiss()">Cancel</button>
        </div>
        '''
        controller: ['$scope', 'messages', '$modalInstance', ($scope, messages, $modalInstance) ->
          $scope.relationshipTypes    = []
          $scope.relationshipTypeInfo = null
          $scope.relation             = args.relation
          $scope.classification       = args.classification
          $scope.update               = args.update

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
              $modalInstance.close(result)
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

        ]

      }

      dialog.result
  ]
]