angular.module('mc.core.ui.bs.modalPromptEnumeratedTypeEdit', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$modal', '$q', 'messages', ($modal, $q, messages) ->
    (title, body, args) ->
      if not args?.element? and not args?.create?
        messages.error('Cannot create edit dialog.', 'The element to be edited is missing.')
        return $q.reject('Missing element argument!')

      dialog = $modal.open {
        windowClass: 'basic-edit-modal-prompt'
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body">
            <messages-panel messages="messages"></messages-panel>
            <form role="form">
              <div class="form-group">
                <label for="name" class="">Name</label>
                <input type="text" class="form-control" id="name" placeholder="Name" ng-model="copy.name">
              </div>
              <div class="form-group">
                <label for="description" class="">Description</label>
                <textarea rows="10" ng-model="copy.description" placeholder="Description" class="form-control" id="description"></textarea>
              </div>
            </form>
            <simple-object-editor object="copy.enumerations" title="Enumerations" key-placeholder="Value" value-placeholder="Description"></simple-object-editor>
        </div>
        <div class="modal-footer">
          <contextual-actions></contextual-actions>
        </div>
        '''
        controller: ['$scope', 'messages', '$controller', '$modalInstance', ($scope, messages, $controller, $modalInstance) ->
          $scope.newEntity = -> {enumerations: {}}
          $scope.copy     = angular.copy(args.element ? $scope.newEntity())
          $scope.original = args.element ? $scope.newEntity()
          $scope.messages = messages.createNewMessages()
          $scope.create   = args.create

          angular.extend(this, $controller('saveAndCreateAnotherCtrlMixin', {$scope: $scope, $modalInstance: $modalInstance}))

          $scope.hasChanged   = ->
            $scope.copy.name != $scope.original.name or $scope.copy.description != $scope.original.description



        ]

      }

      dialog.result
  ]

  messagesProvider.setPromptFactory 'edit-enumeratedType', factory
]