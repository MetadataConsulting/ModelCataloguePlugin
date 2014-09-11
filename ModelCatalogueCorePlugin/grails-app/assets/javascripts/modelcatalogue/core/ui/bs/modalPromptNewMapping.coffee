angular.module('mc.core.ui.bs.modalPromptNewMapping', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
  messagesProvider.setPromptFactory 'new-mapping', [ '$modal', '$q', 'messages', ($modal, $q, messages) ->
    (title, body, args) ->
      if not args?.element?
        messages.error('Cannot create mapping dialog.', 'The element to be mapped is missing.')
        return $q.reject('Missing element argument!')

      dialog = $modal.open {
        windowClass: 'new-relationship-modal-prompt'
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body">
            <messages-panel messages="messages"></messages-panel>
            <form role="form" ng-submit="createMapping()">
              <div class="form-group">
                <label for="valueDomain" class="">Destination Value Domain</label>
                <input type="text" id="valueDomain" placeholder="Destination Value Domain" ng-model="copy.destination" catalogue-element-picker="''' + args.element.elementType + '''" label="el.name">
              </div>
              <div class="form-group">
                <label for="mapping">Mapping</label>
                <textarea rows="10" ng-model="copy.mapping" placeholder="Mapping" class="form-control" id="mapping"></textarea>
              </div>
            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" ng-click="createMapping()" type="submit"><span class="fa" ng-class="{'fa-superscript' : !update, 'fa-edit': update}"></span> {{update ? 'Update' : 'Create'}} Mapping</button>
            <button class="btn btn-warning" ng-click="$dismiss()">Cancel</button>
        </div>
        '''
        controller: ['$scope', 'messages', '$modalInstance', ($scope, messages, $modalInstance) ->
          $scope.update = args.update
          $scope.messages = messages.createNewMessages()
          $scope.copy = args.mapping ? {}


          $scope.createMapping = ->
            $scope.messages.clearAllMessages()
            if not $scope.copy.destination or not $scope.copy.destination.id
              $scope.messages.error 'Missing Destination Value Domain', 'Please select the destination value domain.'
              return

            if not $scope.copy.mapping
              $scope.messages.error 'Missing Mapping', 'Please specify the mapping'
              return


            args.element.mappings.add($scope.copy.destination.id, {mapping: $scope.copy.mapping}).then (result) ->
              if args.update
                messages.success('Mapping Updated', "You have updated mapping from #{args.element.name} to #{$scope.copy.destination.name}.")
              else
                messages.success('Mapping Created', "You have added new mapping from #{args.element.name} to #{$scope.copy.destination.name}.")
              $modalInstance.close(result)
            , (response) ->
              if response.data.errors
                for err in response.data.errors
                  $scope.messages.error err.message

        ]

      }

      dialog.result
  ]
]