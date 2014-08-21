angular.module('mc.core.ui.bs.modalPromptNewRelationship', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
  messagesProvider.setPromptFactory 'new-relationship', [ '$modal', '$q', 'messages', 'catalogueElementResource', ($modal, $q, messages, catalogueElementResource) ->
    (title, body, args) ->
      deferred = $q.defer()

      if not args?.element?
        messages.error('Cannot create relationship dialog.', 'The element to be connected to is missing.')
        deferred.reject('Missing element argument!')
        return deferred.promise

      dialog = $modal.open {
        windowClass: 'new-relationship-modal-prompt'
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body">
            <messages-panel messages="messages"></messages-panel>
            <form role="form">
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
              <p ng-show="update">{{element.name}} {{relationshipTypeInfo.value}} {{relation.name}}</p>
              <div class="new-relationship-modal-prompt-metadata">
                <simple-object-editor object="metadata" title="Metadata" hints="relationshipTypeInfo.type.metadataHints"></simple-object-editor>
              </div>
            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" ng-click="createRelation()"><span class="glyphicon" ng-class="{'glyphicon-link' : !update, 'glyphicon-edit': update}"></span> {{update ? 'Update' : 'Create'}} Relationship</button>
            <button class="btn btn-warning" ng-click="$dismiss()">Cancel</button>
        </div>
        '''
        controller: ['$scope', 'messages', '$modalInstance', ($scope, messages, $modalInstance) ->
          $scope.relationshipTypes    = []
          $scope.relationshipTypeInfo = null
          $scope.relation             = args.relation
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
              $scope.relationshipTypes.push {type: type, value: type.sourceToDestination, relation: 'destination',  direction: 'outgoing'} if args.element.isInstanceOf(type.sourceClass)
              $scope.relationshipTypes.push {type: type, value: type.destinationToSource, relation: 'source',       direction: 'incoming'} if args.element.isInstanceOf(type.destinationClass) and type.sourceToDestination != type.destinationToSource

            if result.next.size > 0
              result.next().then appendToRelationshipTypes

          $scope.element    = args.element

          $scope.updateRelation = (relation) ->
            $scope.relation = relation

          $scope.relationType = 'catalogueElement'

          $scope.messages = messages.createNewMessages()

          $scope.metadata = {}

          $scope.createRelation = ->
            $scope.messages.clearAllMessages()
            if not $scope.relationshipType
              $scope.messages.error 'Missing Relationship Type', 'Please select the relationship type'
              return

            if not $scope.direction
              $scope.messages.error 'Missing Direction', 'Please select the direction'
              return

            if not $scope.relation
              $scope.messages.error 'Missing Relation', 'Please select the relation'
              return

            # this is ignored by binding and handled separately
            $scope.relation.metadata = $scope.metadata

            args.element["#{$scope.direction}Relationships"].add($scope.relationshipType.name, $scope.relation).then (result) ->
              if args.update
                messages.success('Relationship Updated', "You have updated relationship #{$scope.element.name} #{$scope.relationshipTypeInfo.value} #{$scope.relation.name}.")
              else
                messages.success('Relationship Created', "You have added new relationship #{$scope.element.name} #{$scope.relationshipTypeInfo.value} #{$scope.relation.name} in the catalogue.")
              $modalInstance.close(result)
            , (response) ->
              for err in response.data.errors
                $scope.messages.error err.message


          if args.relation and args.direction and args.relationshipType
            info = { type: args.relationshipType }

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



      dialog.result.then (result) ->
        deferred.resolve(result)
      , (reason) ->
        deferred.reject(reason)

      deferred.promise
  ]
]