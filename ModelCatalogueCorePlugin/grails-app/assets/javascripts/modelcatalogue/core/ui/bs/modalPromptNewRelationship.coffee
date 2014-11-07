angular.module('mc.core.ui.bs.modalPromptNewRelationship', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
  messagesProvider.setPromptFactory 'create-new-relationship', [ '$modal', '$q', 'messages', 'catalogueElementResource', ($modal, $q, messages, catalogueElementResource) ->
    (title, body, args) ->
      if not args?.element?
        messages.error('Cannot create relationship dialog.', 'The element to be connected to is missing.')
        return $q.reject('Missing element argument!')

      dialog = $modal.open {
        windowClass: 'new-relationship-modal-prompt'
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body">
            <messages-panel messages="messages"></messages-panel>
            <form role="form" ng-submit="createRelation()">
              <table ng-hide="update">
                <tbody>
                  <tr>
                    <td class="col-md-4 small">
                      {{element.name}}
                    </td>
                    <td class="col-md-4">
                      <select id="type" class="form-control input-sm" ng-model="relationshipTypeInfo" ng-options="rt as rt.value for rt in relationshipTypes" ng-change="updateInfo(relationshipTypeInfo)"></select>
                    </td>
                    <td class="col-md-4">
                      <input id="element" type="text" placeholder="... element" class="form-control input-sm" ng-model="relation" catalogue-element-picker resource="relationType" typeahead-on-select="updateRelation(relation)" ng-disabled="!relationshipTypeInfo.type">
                    </td>
                  </tr>
                </tbody>
              </table>
              <p ng-show="update">{{element.name}} {{relationshipTypeInfo.value}} {{relation.name}} <span ng-show="classification"> in classification {{classification.name}}</span></p>
              <div class="form-group">
                <label for="classification" class="small">Classification</label>
                <input id="classification" placeholder="Classification (leave blank for inherited)" ng-model="classification" catalogue-element-picker="classification" label="el.name" class="input-sm" typeahead-on-select="updateClassification(classification)">
              </div>
              <div class="new-relationship-modal-prompt-metadata">
                <simple-object-editor object="metadata" title="Metadata" hints="relationshipTypeInfo.type.metadataHints"></simple-object-editor>
              </div>
            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" ng-click="createRelation()" type="submit"><span class="glyphicon" ng-class="{'glyphicon-link' : !update, 'glyphicon-edit': update}"></span> {{update ? 'Update' : 'Create'}} Relationship</button>
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

          $scope.updateClassification = (classification) ->
            $scope.classification = classification

          $scope.relationType = 'catalogueElement'

          $scope.messages = messages.createNewMessages()

          $scope.metadata = args.metadata ? {}

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

            args.element["#{$scope.direction}Relationships"].add($scope.relationshipType.name, $scope.relation, args.update).then (result) ->
              if args.update
                messages.success('Relationship Updated', "You have updated relationship #{$scope.element.name} #{$scope.relationshipTypeInfo.value} #{$scope.relation.name}.")
              else
                messages.success('Relationship Created', "You have added new relationship #{$scope.element.name} #{$scope.relationshipTypeInfo.value} #{$scope.relation.name} in the catalogue.")
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

          catalogueElementResource('relationshipType').list(max: 100).then(appendToRelationshipTypes)

        ]

      }

      dialog.result
  ]
]