angular.module('mc.core.ui.bs.modalNewVersion', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->

  factory = [ '$uibModal', '$q', 'messages', ($uibModal, $q, messages) ->
    (title, body, args) ->
      if not args?.element?
        messages.error('Cannot create edit dialog.', 'The element to be edited is missing.')
        return $q.reject('Missing element argument!')

      dialog = $uibModal.open {
        template: '''
         <div class="modal-header">
            <h4>''' + (title ? 'New Version of Data Model') + '''</h4>
        </div>
        <div class="modal-body">
            <p>Do you want to create new version of <strong>''' + args.element.name + '''</strong>?</p>
            <p><small>New version of ''' + args.element.name + ''' will be created. The semantic version provided can be changed once more when the data model is finalized. Current semantic version is <strong>''' + args.element.getSemanticVersion() + '''</strong>.</small></p>
            <messages-panel messages="messages"></messages-panel>
            <form role="form" ng-submit="createDraftVersion()">
              <div class="form-group">
                <label for="semanticVersion" class="">Semantic Version</label>
                <input type="text" class="form-control" id="semanticVersion" placeholder="Semantic Version e.g. 1.2.3" ng-model="semanticVersion">
              </div>
              <fake-submit-button/>
            </form>
        </div>
        <div class="modal-footer">
          <contextual-actions role="modal"></contextual-actions>
        </div>
        '''
        controller: ['$rootScope', '$scope', 'messages', '$uibModalInstance', 'catalogueElementResource', ($rootScope, $scope, messages, $uibModalInstance, catalogueElementResource) ->
          $scope.semanticVersion = null
          $scope.messages = messages.createNewMessages()

          $scope.$dismiss = $uibModalInstance.dismiss

          $scope.createDraftVersion = ->
            $scope.pending = true
            args.element.execute('newVersion', 'POST', semanticVersion: $scope.semanticVersion).then (updated) ->
              args.element.updateFrom  updated
              messages.prompt('Draft progress', null, type: 'feedback', id: args.element.id).then ->
                $uibModalInstance.close(updated)
                $scope.pending = false
            , (response) ->
              $scope.pending = false
              $scope.messages.showErrorsFromResponse(response)

        ]

      }

      dialog.result
  ]

  messagesProvider.setPromptFactory 'new-version', factory
]
