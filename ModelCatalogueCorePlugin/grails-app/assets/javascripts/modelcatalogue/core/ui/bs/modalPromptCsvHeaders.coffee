angular.module('mc.core.ui.bs.modalPromptCsvHeaders', ['mc.util.messages', 'angularFileUpload']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$modal', '$q', 'messages', ($modal, $q, messages) ->
    (title, body, args) ->
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
                <label for="separator" class="">Separator</label>
                <input type="text" class="form-control" id="separator" ng-model="separator">
              </div>
              <div class="form-group">
                <label for="asset" class="">CSV File</label>
                <input ng-hide="uploading &amp;&amp; progress" type="file" class="form-control" id="csvFile" placeholder="CSV File" ng-model="csvFile" ng-file-select="onFileSelect($files)">
                <progressbar value="progress" ng-show="uploading &amp;&amp; progress">{{progress}} %</progressbar>
              </div>
            </form>
        </div>
        '''
        controller: ['$scope', 'messages', 'names', 'catalogueElementResource', '$modalInstance', '$upload', 'modelCatalogueApiRoot', 'enhance', ($scope, messages, names, catalogueElementResource, $modalInstance, $upload, modelCatalogueApiRoot, enhance) ->
          $scope.separator = ';'
          $scope.messages = messages.createNewMessages()

          $scope.cancel = ->
            $scope.progress = undefined
            if $scope.upload
              $scope.upload.abort()

            $modalInstance.dismiss('Upload Canceled')

          $scope.onFileSelect = ($files) ->
            $scope.uploading = true
            $scope.upload = $upload.upload({
              params: {separator: $scope.separator}
              url:                "#{modelCatalogueApiRoot}/dataArchitect/elementsFromCSV"
              file:               $files[0]
              fileFormDataName:   'csv'
            }).progress((evt) ->
              $scope.progress = parseInt(100.0 * evt.loaded / evt.total)
            ).success((result) ->
              $scope.uploading = false
              messages.success "Read #{result.length} data elements suggestion from the file headers"
              $modalInstance.close(enhance result)
            ).error(->
              $scope.messages.warning("Cannot process given file")
              $scope.uploading = false
              $scope.progress  = 0
            )

        ]

      }

      dialog.result
  ]

  messagesProvider.setPromptFactory 'data-element-suggestions-from-csv', factory
]