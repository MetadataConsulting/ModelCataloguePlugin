angular.module('mc.core.ui.bs.modalPromptNewRelationship', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
 messagesProvider.setPromptFactory 'new-relationship', [ '$modal', '$q', 'messages', ($modal, $q, messages) ->
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
            <div class="form-group">
                <label for="type">Relationship Type</label>
                <input id="type" type="text"  class="form-control" ng-model="relationshipType" catalogue-element-picker="relationshipType" hide-element-type="true" typeahead-on-select="updateDirections(relationshipType)">
                <label for="direction" ng-show="directions">Direction</label>
                <select id="direction" ng-show="directions" ng-model="direction" ng-options="d.label for d in directions" class="form-control" ng-change="updateRelationPicker()"></select>
                <label for="relation" ng-show="direction">{{direction.relationLabel}}</label>
                <input id="relation" ng-show="direction" type="text" class="form-control" ng-model="relation" catalogue-element-picker resource="relationType" typeahead-on-select="updateRelation(relation)">
            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" ng-click="createRelation()"><span class="glyphicon glyphicon-link"></span> Create Relationship</button>
            <button class="btn btn-warning" ng-click="$dismiss()">Cancel</button>
        </div>
        '''
       controller: ['$scope', 'messages', '$modalInstance', ($scope, messages, $modalInstance) ->
          $scope.direction        = null
          $scope.relationshipType = null
          $scope.relation         = null

          $scope.updateDirections = (newVal) ->
            $scope.directions = [
              {value: 'incoming', label: newVal.destinationToSource, relation: 'source', relationLabel: 'Source'}
              {value: 'outgoing', label: newVal.sourceToDestination, relation: 'destination', relationLabel: 'Destination'}
            ]
            $scope.direction = $scope.directions[0]
            $scope.relationshipType = newVal
            $scope.updateRelationPicker()

          $scope.updateRelationPicker = ->
            $scope.relationType = $scope.relationshipType["#{$scope.direction.relation}Class"]

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


            args.element["#{$scope.direction.value}Relationships"].add($scope.relationshipType.name, $scope.relation).then (result) ->
              messages.success('Relationship Created')
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