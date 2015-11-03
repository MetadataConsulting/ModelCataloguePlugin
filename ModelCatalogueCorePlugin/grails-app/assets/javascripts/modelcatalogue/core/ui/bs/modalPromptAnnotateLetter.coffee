angular.module('mc.core.ui.bs.modalPromptAnnotateLetter', ['mc.util.messages', 'angularFileUpload']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$modal', ($modal) ->
    (title, body, args) ->
      dialog = $modal.open {
        windowClass: 'basic-edit-modal-prompt'
        backdrop: 'static'
        keyboard: false
        resolve:
          args: -> args
        template: '''
         <div class="modal-header">
            <h4>Annotate Letter</h4>
        </div>
        <div class="modal-body">
            <messages-panel messages="messages"></messages-panel>
            <form role="form" ng-submit="saveElement()">
              <div class="form-group">
                <label for="classifications"> Classifications</label>
                <elements-as-tags elements="copy.classifications"></elements-as-tags>
                <input id="classifications" placeholder="Classification" ng-model="pending.classification" catalogue-element-picker="classification" label="el.name" typeahead-on-select="addClassification()">
              </div>
              <div class="form-group">
                <label for="name" class="">Annotated File Name</label>
                <input type="text" class="form-control" id="name" placeholder="Annotated File Name (leave blank to use filename)" ng-model="copy.name">
              </div>
              <div class="form-group">
                <label for="asset" class="">File</label>
                <input ng-hide="uploading &amp;&amp; progress" type="file" accept="text/plain" class="form-control" id="asset" placeholder="File" ng-model="copy.asset" ng-file-select="onFileSelect($files)">
                <progressbar value="progress" ng-show="uploading &amp;&amp; progress">{{progress}} %</progressbar>
              </div>
            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-success" ng-click="saveElement()" ng-disabled="uploading"><span class="glyphicon glyphicon-ok"></span> Save</button>
            <button class="btn btn-warning" ng-click="cancel()">Cancel</button>
        </div>
        '''
        controller: ['$scope', 'messages', 'names', 'modelCatalogueDataArchitect', '$modalInstance', '$upload', 'modelCatalogueApiRoot', 'enhance', 'catalogue', 'args', ($scope, messages, names, modelCatalogueDataArchitect, $modalInstance, $upload, modelCatalogueApiRoot, enhance, catalogue, args) ->
           $scope.copy =
             classifications = []
           $scope.messages = messages.createNewMessages()

           $scope.addClassification = ->
             $scope.copy = {classifications: []} unless $scope.copy
             $scope.copy.classifications = [] unless $scope.copy.classifications
             $scope.copy.classifications.push($scope.pending.classification)
             delete $scope.pending.classification

           $scope.cancel = ->
             $scope.progress = undefined
             if $scope.upload
               $scope.upload.abort()

             $modalInstance.dismiss('Upload Canceled')

           $scope.onFileSelect = ($files) ->
             $scope.copy.file = $files[0]
             if $scope.copy.name == $scope.copy.originalFileName or $scope.nameFromFile
               $scope.nameFromFile = true
               $scope.copy.name = $scope.copy.file.name

           $scope.saveElement = ->
             $scope.messages.clearAllMessages()
             if not $scope.copy.name and not $scope.copy.file
               $scope.messages.error 'Empty Name', 'Please fill the name'
               return

             if $scope.copy.classifications.length == 0
               $scope.messages.error 'Empty Classifications', 'Please select at least one classification'
               return


             $scope.uploading = true
             $scope.upload = $upload.upload({
               params: {name: $scope.copy.name, classifications: (c.id for c in $scope.copy.classifications).join(',')}
               url:                "#{modelCatalogueApiRoot}/dataArchitect/imports/annotate"
               file:               $scope.copy.file
               fileFormDataName:   'file'
             }).progress((evt) ->
               $scope.progress = parseInt(100.0 * evt.loaded / evt.total)
             ).success((result) ->
               result = enhance result
               $scope.uploading = false
               if result.errors
                 for err in result.errors
                   $scope.messages.error err.message
               else
                 messages.success('Created ' + result.getElementTypeName(), "You have created #{result.getElementTypeName()} #{result.name}.")
                 $modalInstance.close(result)
                 if catalogue.isInstanceOf result.elementType, 'asset'
                   result.show()
             ).error((data) ->
               for err in data.errors
                 if angular.isString(err)
                   $scope.messages.error err
                 else if err.message
                  $scope.messages.error err.message
               $scope.uploading = false
               $scope.progress  = 0
               $modalInstance.close()
             )
        ]

      }

      dialog.result
  ]
  messagesProvider.setPromptFactory 'annotate-letter', factory
]