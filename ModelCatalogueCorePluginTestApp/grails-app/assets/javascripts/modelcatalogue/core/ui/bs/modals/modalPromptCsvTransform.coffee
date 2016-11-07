angular.module('mc.core.ui.bs.modalPromptCsvTransform', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$uibModal', '$q', 'messages', ($uibModal, $q, messages) ->

    (title, body, args) ->
      return $q.reject("Missing element argument") if not args.element

      dialog = $uibModal.open {
        windowClass: 'basic-edit-modal-prompt'
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body">
            <messages-panel messages="messages"></messages-panel>
              <div class="form-group">
                <label for="separator" class="">Separator</label>
                <input type="text" name="separator" class="form-control" id="separator" ng-model="separator">
              </div>
            <form action="{{link}}" method="POST" enctype="multipart/form-data" target="helper-frame" id="upload-worker">
              <div class="form-group hide">
                <label for="separator" class="">Separator</label>
                <input type="text" name="separator" class="form-control" id="separator" ng-model="separator">
              </div>
              <div class="form-group">
                <label for="csv" class="">CSV File</label>
                <input type="file" name="csv" class="form-control" id="csv" placeholder="CSV File" ng-model="csv" ngf-select="onFileSelect($files)">
              </div>
            </form>
        </div>
        <div class="modal-footer">
                <button type="submit" class="btn btn-primary" ng-click="submit()" ng-disabled="!fileSelected">Transform</button>
                <button type="button" class="btn btn-warning" ng-click="$dismiss()">Cancel</button>
        </div>
        '''
        controller: ['$scope', 'messages', 'names', 'catalogueElementResource', '$uibModalInstance', 'modelCatalogueApiRoot', ($scope, messages, names, catalogueElementResource, $uibModalInstance, modelCatalogueApiRoot) ->
          if angular.element('#helper-frame').length == 0
            angular.element(document.body).append('<iframe width="0" height="0" name="helper-frame" id="helper-frame" class="hide"></iframe>')

          $scope.separator = ';'
          $scope.messages = messages.createNewMessages()
          $scope.fileSelected = false

          $scope.link = "#{modelCatalogueApiRoot}#{args.element.link}/transform"

          $scope.onFileSelect = ($files) ->
            if $files.length > 0
              $scope.fileSelected = true
            else
              $scope.fileSelected = false

          $scope.submit = ->
            if not $scope.fileSelected
              $scope.messages.error "Please specify the file to be converted"
              return

            angular.element('#upload-worker').submit()

            messages.success("Your file is being transformed.", "This may take very long time for large data sets. Please don't exit the catalogue before finished.")

            $uibModalInstance.close()
        ]

      }

      dialog.result
  ]

  messagesProvider.setPromptFactory 'transform-csv-file', factory
]
