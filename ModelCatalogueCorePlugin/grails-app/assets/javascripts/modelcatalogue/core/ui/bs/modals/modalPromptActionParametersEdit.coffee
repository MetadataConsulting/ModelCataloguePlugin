angular.module('mc.core.ui.bs.modalPromptActionParametersEdit', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$uibModal', '$q', 'messages', ($uibModal, $q, messages) ->
    (title, body, args) ->
      if not args?.action? and not args?.create?
        messages.error('Cannot create edit dialog.', 'The action to be edited is missing.')
        return $q.reject('Missing action argument!')

      dialog = $uibModal.open {
        windowClass: 'new-relationship-modal-prompt'
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body">
            <messages-panel messages="messages"></messages-panel>
            <blockquote class="action-description">{{action.description}}</blockquote>
            <form role="form" ng-submit="updateParameters()">
              <div class="new-relationship-modal-prompt-metadata">
                <simple-object-editor object="parameters" title="Parameters"></simple-object-editor>
              </div>
              <fake-submit-button/>
            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" id="update-parameters" ng-click="updateParameters()"><span class="glyphicon glyphicon-save"></span> Update Parameters</button>
            <button class="btn btn-warning" ng-click="$dismiss()">Cancel</button>
        </div>
        '''
        controller: ['$scope', 'messages', '$uibModalInstance', ($scope, messages, $uibModalInstance) ->
          $scope.action = args.action
          $scope.parameters = args.action.parameters
          $scope.messages = messages.createNewMessages()

          $scope.updateParameters = ->
            $scope.messages.clearAllMessages()

            args.action.parameters = $scope.parameters

            args.action.updateParameters().then (result) ->
              messages.success('Action Parameters Updated', "You have updated parameters for #{args.action.naturalName}")
              $uibModalInstance.close(result)
            , (response) ->
              for err in response.data.errors
                $scope.messages.error err.message
        ]

      }


      dialog.result
  ]

  messagesProvider.setPromptFactory 'update-action-parameters', factory

]
