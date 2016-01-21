angular.module('mc.core.ui.bs.modalFinalize', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->

  factory = [ '$modal', '$q', 'messages', ($modal, $q, messages) ->
    (title, body, args) ->
      if not args?.element?
        messages.error('Cannot create edit dialog.', 'The element to be edited is missing.')
        return $q.reject('Missing element argument!')

      dialog = $modal.open {
        template: '''
         <div class="modal-header">
            <h4>''' + (title ? 'Finalize Data Model') + '''</h4>
        </div>
        <div class="modal-body">
            <p>Do you want to finalize data model <strong>''' + args.element.name + '''</strong>?</p>
            <p><small>Every item declared inside data model will be finalized as well. The data model will be published under given semantic version which cannot be changed later.</small></p>
            <messages-panel messages="messages"></messages-panel>
            <form role="form" ng-submit="finalizeElement()">
              <div class="form-group">
                <label for="semanticVersion" class="">Semantic Version</label>
                <input type="text" class="form-control" id="semanticVersion" placeholder="Semantic Version e.g. 1.2.3" ng-model="semanticVersion">
              </div>
              <div class="form-group">
                <label for="revisionNotes" class="">Revision Notes</label>
                <textarea rows="10" class="form-control" id="revisionNotes" placeholder="Please, describe the changes in current version" ng-model="revisionNotes"></textarea>
              </div>
              <fake-submit-button/>
            </form>
        </div>
        <div class="modal-footer">
          <contextual-actions role="modal"></contextual-actions>
        </div>
        '''
        controller: ['$rootScope', '$scope', 'messages', '$modalInstance', 'enhance', 'rest', 'modelCatalogueApiRoot', ($rootScope, $scope, messages, $modalInstance, enhance, rest, modelCatalogueApiRoot) ->
          $scope.semanticVersion = args.element.semanticVersion
          $scope.revisionNotes = args.element.revisionNotes
          $scope.messages = messages.createNewMessages()

          $scope.$dismiss = $modalInstance.dismiss
          $scope.working = false

          $scope.finalizeElement = ->
            $scope.working = true
            enhance(rest(url: "#{modelCatalogueApiRoot}#{args.element.link}/finalize", method: 'POST', params: {semanticVersion: $scope.semanticVersion, revisionNotes: $scope.revisionNotes})).then (finalized) ->
              args.element.updateFrom finalized
              $rootScope.$broadcast 'catalogueElementUpdated', finalized
              $rootScope.$broadcast 'catalogueElementFinalized', finalized
              $rootScope.$broadcast 'redrawContextualActions'
              $modalInstance.close(finalized)
              $scope.working = false
            , (response) ->
              $scope.messages.showErrorsFromResponse(response)
              $scope.working = false


        ]

      }

      dialog.result
  ]

  messagesProvider.setPromptFactory 'finalize', factory
]