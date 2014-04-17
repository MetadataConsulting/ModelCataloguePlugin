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
              <table>
                <tbody>
                  <tr>
                    <td class="col-md-4 small">
                      {{element.name}}
                    </td>
                    <td class="col-md-4">
                      <input id="type" type="text" placeholder="relates to ..." class="form-control input-sm" ng-model="relationshipTypeInfo" typeahead="rt as rt.value for rt in getFiteredRelationshipTypes($viewValue)" typeahead-on-select="updateRelationshipType(relationshipTypeInfo)">
                    </td>
                    <td class="col-md-4">
                      <input id="type" type="text" placeholder="... element" class="form-control input-sm" ng-model="relation" catalogue-element-picker resource="relationType" typeahead-on-select="updateRelation(relation)" ng-disabled="!relationshipTypeInfo.type">
                    </td>
                  </tr>
                </tbody>
              </table>
            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" ng-click="createRelation()"><span class="glyphicon glyphicon-link"></span> Create Relationship</button>
            <button class="btn btn-warning" ng-click="$dismiss()">Cancel</button>
        </div>
        '''
        controller: ['$scope', 'messages', '$modalInstance', ($scope, messages, $modalInstance) ->
          deferredRelationshipTypes = $q.defer()

          relationshipTypes = []

          appendToRelationshipTypes = (result) ->
            for type in result.list
              relationshipTypes.push {type: type, direction: 'sourceToDestination', value: type.sourceToDestination, relation: 'destination', direction: 'outgoing'} if args.element.isInstanceOf(type.sourceClass)
              relationshipTypes.push {type: type, direction: 'destinationToSource', value: type.destinationToSource, relation: 'source', direction: 'incoming'} if args.element.isInstanceOf(type.destinationClass)

            if result.next.size > 0
              result.next().then appendToRelationshipTypes
            else
              deferredRelationshipTypes.resolve(relationshipTypes)

          catalogueElementResource('relationshipType').list(max: 100).then appendToRelationshipTypes

          $scope.relationshipTypes = deferredRelationshipTypes.promise

          $scope.getFiteredRelationshipTypes = (query) ->
            deferredResult = $q.defer()
            filtered = []
            $scope.relationshipTypes.then (types) ->
              for type in types when type.value.indexOf(query) > -1
                filtered.push type
              deferredResult.resolve filtered
            deferredResult.promise


          $scope.element    = args.element

          $scope.updateRelationshipType = (info) ->
            $scope.relationshipTypeInfo = info
            $scope.relationshipType = info.type
            $scope.relationType = $scope.relationshipType["#{info.relation}Class"]
            $scope.direction = info.direction

          $scope.updateRelation = (relation) ->
            $scope.relation = relation

          $scope.relationType = 'catalogueElement'

          $scope.messages = messages.createNewMessages()

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


            args.element["#{$scope.direction}Relationships"].add($scope.relationshipType.name, $scope.relation).then (result) ->
              messages.success('Relationship Created', "You have added new relationship #{$scope.element.name} #{$scope.relationshipTypeInfo.value} #{$scope.relation.name} in the catalogue.")
              $modalInstance.close(result)
            , (response) ->
              for err in response.data.errors
                $scope.messages.error err.message

        ]

      }



      dialog.result.then (result) ->
        deferred.resolve(result)
      , (reason) ->
        deferred.reject(reason)

      deferred.promise
  ]
]

# <div class="form-group">
# <label for="type">Relationship Type</label>
# <input id="type" type="text"  class="form-control" ng-model="relationshipType" catalogue-element-picker="relationshipType" hide-element-type="true" typeahead-on-select="updateDirections(relationshipType)">
# <label for="direction" ng-show="directions">Direction</label>
# <select id="direction" ng-show="directions" ng-model="direction" ng-options="d.label for d in directions" class="form-control" ng-change="updateRelationPicker()"></select>
# <label for="relation" ng-show="direction">{{direction.relationLabel}}</label>
# </div>