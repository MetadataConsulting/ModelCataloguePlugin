angular.module('mc.core.ui.bs.modalPromptAssetEdit', ['mc.util.messages', 'angularFileUpload']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$modal', '$q', 'messages', ($modal, $q, messages) ->
    (title, body, args) ->
      if not args?.element? and not args?.create?
        messages.error('Cannot create edit dialog.', 'The element to be edited is missing.')
        return $q.reject('Missing element argument!')

      dialog = $modal.open {
        windowClass: 'basic-edit-modal-prompt'
        backdrop: 'static'
        keyboard: false
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body">
            <messages-panel messages="messages"></messages-panel>
            <form role="form">
              <div class="form-group">
                <label for="name" class="">Name</label>
                <input type="text" class="form-control" id="name" placeholder="Name (leave blank to use filename)" ng-model="copy.name">
              </div>
              <div class="form-group">
                <label for="asset" class="">File</label>
                <input ng-hide="uploading &amp;&amp; progress" type="file" class="form-control" id="asset" placeholder="File" ng-model="copy.asset" ng-file-select="onFileSelect($files)">
                <progressbar value="progress" ng-show="uploading &amp;&amp; progress">{{progress}} %</progressbar>
              </div>
              <div class="form-group">
                <label for="description" class="">Description</label>
                <textarea rows="10" ng-model="copy.description" placeholder="Description" class="form-control" id="description"></textarea>
              </div>
            </form>
        </div>
        <div class="modal-footer">
          <contextual-actions></contextual-actions>
        </div>
        '''
        controller: ['$scope', 'messages', 'names', 'catalogueElementResource', '$modalInstance', '$upload', 'modelCatalogueApiRoot', 'enhance', ($scope, messages, names, catalogueElementResource, $modalInstance, $upload, modelCatalogueApiRoot, enhance) ->
          $scope.copy     = angular.copy(args.element ? {})
          $scope.original = args.element ? {}
          $scope.messages = messages.createNewMessages()
          $scope.create   = args.create

          $scope.hasChanged   = ->
            $scope.copy.file or $scope.copy.name != $scope.original.name or $scope.copy.description != $scope.original.description

          $scope.cancel = ->
            $scope.progress = undefined
            if $scope.upload
              $scope.upload.abort()

            $modalInstance.dismiss('Upload Canceled')

          $scope.onFileSelect = ($files) ->
            if not args?.create
              $scope.messages.clearAllMessages()
              $scope.messages.info 'By changing the file you\'re creating new version automatically'
            $scope.copy.file = $files[0]
            if $scope.copy.name == $scope.copy.originalFileName or $scope.nameFromFile
              $scope.nameFromFile = true
              $scope.copy.name = $scope.copy.file.name

          $scope.saveElement = (newVersion) ->
            $scope.messages.clearAllMessages()
            if not $scope.copy.name and not $scope.copy.file
              $scope.messages.error 'Empty Name', 'Please fill the name'
              return


            if $scope.copy.file
              $scope.uploading = true
              $scope.upload = $upload.upload({
                params: {id: $scope.copy.id, name: $scope.copy.name, description: $scope.copy.description }
                url:                "#{modelCatalogueApiRoot}/asset/upload"
                file:               $scope.copy.file
                fileFormDataName:   'asset'
              }).progress((evt) ->
                $scope.progress = parseInt(100.0 * evt.loaded / evt.total)
              ).success((result) ->
                $scope.uploading = false
                if result.errors
                  for err in result.errors
                    $scope.messages.error err.message
                else
                  if args?.create
                    messages.success('Created ' + result.getElementTypeName(), "You have created #{result.getElementTypeName()} #{result.name}.")
                  else
                    messages.success('Updated ' + result.getElementTypeName(), "You have updated #{result.getElementTypeName()} #{result.name}.")
                  $modalInstance.close(enhance result)
              ).error((data) ->
                for err in data.errors
                  $scope.messages.error err.message
                $scope.uploading = false
                $scope.progress  = 0
              )
            else
              promise = null

              if args?.create
                promise = catalogueElementResource(args.create).save($scope.copy)
              else
                promise = catalogueElementResource($scope.copy.elementType).update($scope.copy, {newVersion: newVersion})

              promise.then (result) ->
                if args?.create
                  messages.success('Created ' + result.getElementTypeName(), "You have created #{result.getElementTypeName()} #{result.name}.")
                else
                  messages.success('Updated ' + result.getElementTypeName(), "You have updated #{result.getElementTypeName()} #{result.name}.")
                $modalInstance.close(enhance result)
              , (response) ->
                for err in response.data.errors
                  $scope.messages.error err.message

        ]

      }

      dialog.result
  ]

  messagesProvider.setPromptFactory 'edit-asset', factory
]