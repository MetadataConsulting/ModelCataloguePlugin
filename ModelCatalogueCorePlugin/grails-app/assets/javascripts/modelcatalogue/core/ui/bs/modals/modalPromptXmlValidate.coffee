angular.module('mc.core.ui.bs.modalPromptXmlValidate', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$uibModal', '$q', 'messages', ($uibModal, $q, messages) ->

    (title, body, args) ->
      return $q.reject("Missing asset argument") if not args.asset

      dialog = $uibModal.open {
        windowClass: 'basic-edit-modal-prompt'
        template: '''
         <div class="modal-header">
            <button type="button" class="close" ng-click="$dismiss()"><span aria-hidden="true">&times;</span><span class="sr-only">Cancel</span></button>
            <h4>Validate XML with Schema</h4>
        </div>
        <div class="modal-body">
            <messages-panel messages="messages"></messages-panel>
            <form role="form" ng-submit="validateResult()">
              <div class="form-group">
                <label for="asset" class="">XML File</label>
                <input ng-hide="uploading &amp;&amp; progress" type="file" class="form-control" id="xml" placeholder="XML File" ngf-model="copy.xml" ngf-select="onFileSelect($files)">
                <progressbar value="progress" ng-show="uploading &amp;&amp; progress">{{progress}} %</progressbar>
              </div>
              <fake-submit-button/>
            </form>
        </div>
        <div class="modal-footer">
          <alert type="success" ng-show="result === true">File is VALID</alert>
          <alert type="danger"  ng-show="result !== true &amp;&amp; result">{{result}}</alert>
        </div>
        '''
        controller: ['$scope', 'messages', '$uibModalInstance', 'Upload', 'modelCatalogueApiRoot', 'enhance', ($scope, messages, $uibModalInstance, Upload, modelCatalogueApiRoot, enhance) ->
          $scope.copy     = {}
          $scope.messages = messages.createNewMessages()

          $scope.cancel = ->
            $scope.progress = undefined
            if $scope.upload
              $scope.upload.abort()

            $uibModalInstance.dismiss('Upload Canceled')

          $scope.onFileSelect = ($files) ->
            $scope.copy.file = $files[0]
            $scope.validateResult()

          $scope.validateResult = ->
            $scope.messages.clearAllMessages()
            $scope.uploading = true
            $scope.upload = Upload.upload({
              url: "#{modelCatalogueApiRoot}/asset/#{args.asset.id}/validateXml"
              data: {xml: $scope.copy.file}
            }).progress((evt) ->
              $scope.progress = parseInt(100.0 * evt.loaded / evt.total)
            ).success((result) ->
              result = enhance result
              $scope.uploading = false
              if result.errors
                $scope.result    = ''
                for err in result.errors
                 $scope.result +=  "#{err.message}\n"
              else
                $scope.result = true
            ).error((data) ->
              if data.errors
                $scope.result    = ''
                for err in data.errors
                  $scope.result +=  "#{err.message}\n"
              else
                $scope.result =  "File cannot be validated. Is the asset XML Schema file?"
              $scope.uploading = false
              $scope.progress  = 0
            )

        ]

      }

      dialog.result
  ]

  messagesProvider.setPromptFactory 'validate-xml-by-schema', factory
]
