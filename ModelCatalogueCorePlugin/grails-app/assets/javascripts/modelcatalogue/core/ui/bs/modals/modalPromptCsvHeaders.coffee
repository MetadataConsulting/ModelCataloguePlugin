angular.module('mc.core.ui.bs.modalPromptCsvHeaders', ['mc.util.messages', 'ngFileUpload']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$uibModal', 'messages', ($uibModal, messages) ->
    (title) ->
      dialog = $uibModal.open {
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
                <input ng-hide="uploading &amp;&amp; progress" type="file" class="form-control" id="csvFile" placeholder="CSV File" ngf-model="csvFile" ngf-select="onFileSelect($files)">
                <progressbar value="progress" ng-show="uploading &amp;&amp; progress"><span ng-hide="progress == 100">{{progress}} %</span><span ng-show="progress == 100">Upload finished. Elements are being matched. This may take a while.</span></progressbar>
              </div>
            </form>
        </div>
        '''
        controller: ['$scope', 'messages', 'names', 'catalogueElementResource', '$uibModalInstance', 'Upload', 'modelCatalogueApiRoot', 'enhance', ($scope, messages, names, catalogueElementResource, $uibModalInstance, Upload, modelCatalogueApiRoot, enhance) ->
          $scope.separator = ';'
          $scope.messages = messages.createNewMessages()

          $scope.cancel = ->
            $scope.progress = undefined
            if $scope.upload
              $scope.upload.abort()

            $uibModalInstance.dismiss('Upload Canceled')

          $scope.onFileSelect = ($files) ->

            if(title=="Import Child Data Classes") then url ="dataArchitect/modelsFromCSV" else url = "dataArchitect/elementsFromCSV"

            $scope.uploading = true
            $scope.upload = Upload.upload({
              url: "#{modelCatalogueApiRoot}/#{url}"
              params: {separator: $scope.separator}
              data: {csv: $files[0]}
            }).progress((evt) ->
              $scope.progress = parseInt(100.0 * evt.loaded / evt.total)
            ).success((result) ->
              $scope.uploading = false
              messages.success "Read #{result.length} data elements suggestion from the file headers"
              $uibModalInstance.close(enhance result)
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
  messagesProvider.setPromptFactory 'child-model-suggestions-from-csv', factory
]
