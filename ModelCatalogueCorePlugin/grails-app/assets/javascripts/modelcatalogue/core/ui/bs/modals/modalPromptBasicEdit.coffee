angular.module('mc.core.ui.bs.modalPromptBasicEdit', ['mc.util.messages', 'mc.core.ui.bs.saveAndCreateAnotherCtrlMixin']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$uibModal', '$q', 'messages', ($uibModal, $q, messages) ->
    (title, body, args) ->
      if not args?.element? and not args?.create?
        messages.error('Cannot create edit dialog.', 'The element to be edited is missing.')
        return $q.reject('Missing element argument!')

      dialog = $uibModal.open {
        windowClass: 'basic-edit-modal-prompt'
        size: 'lg'
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body">
            <messages-panel messages="messages"></messages-panel>
            <form role="form" ng-submit="saveElement()">
              <div class="form-group">
                <label for="name" class="">Name</label>
                <input type="text" class="form-control" id="name" placeholder="Name" ng-model="copy.name">
              </div>
                <div class="form-group">
                  <label for="modelCatalogueId" class="">Catalogue ID (URL)</label>
                  <input type="text" class="form-control" id="modelCatalogueId" placeholder="e.g. external ID, namespace (leave blank for generated)" ng-model="copy.modelCatalogueId">
                </div>
              <div class="form-group">
                <label for="description" class="">Description</label>
                <textarea rows="10" ng-model="copy.description" placeholder="Description" class="form-control" id="description"></textarea>
              </div>
              <h4>Metadata</h4>
              <metadata-editor object="copy.ext" title="Key" value-title="Value" owner="element"></metadata-editor>
              <fake-submit-button/>
            </form>
        </div>
        <div class="modal-footer">
          <contextual-actions role="modal"></contextual-actions>
        </div>
        '''
        controller: ['$scope', 'messages', 'names', '$uibModalInstance', '$controller', ($scope, messages, names, $uibModalInstance, $controller) ->
          $scope.copy     = angular.copy(args.element ? {})
          $scope.original = args.element ? {}
          $scope.messages = messages.createNewMessages()
          $scope.create   = args.create

          angular.extend(this, $controller('saveAndCreateAnotherCtrlMixin', {$scope: $scope, $uibModalInstance: $uibModalInstance}))

          $scope.hasChanged   = ->
            $scope.copy.name != $scope.original.name or $scope.copy.description != $scope.original.description or $scope.copy.modelCatalogueId != $scope.original.modelCatalogueId

        ]

      }

      dialog.result
  ]

  messagesProvider.setPromptFactory 'edit-classification', factory
  messagesProvider.setPromptFactory 'edit-dataModel', factory
  messagesProvider.setPromptFactory 'edit-batch', factory
  messagesProvider.setPromptFactory 'edit-csvTransformation', factory
]
