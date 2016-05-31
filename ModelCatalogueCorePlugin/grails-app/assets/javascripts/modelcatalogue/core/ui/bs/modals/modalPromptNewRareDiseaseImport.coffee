angular.module('mc.core.ui.bs.modalPromptNewRareDiseaseImport', ['mc.util.messages', 'ngFileUpload']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$modal', ($modal) ->
    (title, body, args) ->
      dialog = $modal.open {
        windowClass: 'basic-edit-modal-prompt'
        backdrop: 'static'
        keyboard: false
        resolve:
          title: -> title
          args: -> args
        templateUrl: '/mc/core/ui/modals/modalRareDiseaseImport.html'
        controller: ($modalInstance, $scope, Upload, catalogue, enhance, messages, modelCatalogueApiRoot) ->
          $scope.title = title
          $scope.messages = messages.createNewMessages()

          $scope.cancel = ->
            $scope.progress = undefined
            if $scope.uploadObject
              $scope.uploadObject.abort()

            $modalInstance.dismiss('Upload Canceled')

          $scope.onFileSelect = ($files) ->
            $scope.file = $files[0]

          $scope.canUpload = ->
            !$scope.uploading and $scope.file and $scope.dataModel and $scope.hpoDataModel and $scope.testDataModel

          toDataModelName = (catalogueElement) ->
            if (typeof catalogueElement is 'string')
              return catalogueElement
            else
              return catalogueElement.name

          $scope.upload = ->
            $scope.messages.clearAllMessages()
            $scope.uploading = true

            $scope.upload = Upload.upload({
              url: "#{modelCatalogueApiRoot}/genomics/imports/upload"
              params: {
                dataModelName: toDataModelName($scope.dataModel)
                hpoDataModelName: toDataModelName($scope.hpoDataModel)
                testDataModelName: toDataModelName($scope.hpoDataModel)
              }
              data: {
                file: $scope.file
              }
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
                $scope.messages.error err.message
              $scope.uploading = false
              $scope.progress  = 0
              $modalInstance.close()
            )
      }

      dialog.result
  ]
  messagesProvider.setPromptFactory 'new-rare-disease-csv-import', factory
]
