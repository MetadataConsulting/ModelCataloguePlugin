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
            <simple-object-editor object="copy.enumerations" title="Enumerations"></simple-object-editor>
        </div>
        <div class="modal-footer">
          <contextual-actions></contextual-actions>
        </div>
        '''
        controller: ['$scope', 'messages', 'names', 'catalogueElementResource', '$modalInstance', ($scope, messages, names, catalogueElementResource, $modalInstance) ->
          $scope.copy     = angular.copy(args.element ? {enumerations: {}})
          $scope.original = args.element ? {enumerations: {}}
          $scope.messages = messages.createNewMessages()

          $scope.hasChanged   = ->
            $scope.copy.name != $scope.original.name or $scope.copy.description != $scope.original.description

          $scope.saveElement = ->
            $scope.messages.clearAllMessages()
            if not $scope.copy.name
              $scope.messages.error 'Empty Name', 'Please fill the name'
              return


            promise = null

            if args?.create
              promise = catalogueElementResource(args.create).save($scope.copy)
            else
              promise = catalogueElementResource($scope.copy.elementType).update($scope.copy)

            promise.then (result) ->
              if args?.create
                messages.success('Created ' + result.elementTypeName, "You have created #{result.elementTypeName} #{result.name}.")
              else
                messages.success('Updated ' + result.elementTypeName, "You have updated #{result.elementTypeName} #{result.name}.")
              $modalInstance.close(result)
            , (response) ->
              for err in response.data.errors
                $scope.messages.error err.message

        ]

      }

      dialog.result
  ]

  messagesProvider.setPromptFactory 'edit-enumeratedType', factory
]