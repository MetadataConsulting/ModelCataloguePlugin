angular.module('mc.core.ui.bs.modalPromptNewRareDiseaseImport', ['mc.util.messages', 'ngFileUpload']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$uibModal', ($uibModal) ->
    (title, body, args) ->
      dialog = $uibModal.open {
        windowClass: 'basic-edit-modal-prompt'
        backdrop: 'static'
        keyboard: false
        resolve:
          title: -> title
          args: -> args
        templateUrl: '/mc/core/ui/modals/modalRareDiseaseImport.html'
        controller: ($uibModalInstance, $scope, Upload, catalogue, enhance, messages, modelCatalogueApiRoot) ->
          'ngInject'
          $scope.title = title
          $scope.messages = messages.createNewMessages()

          $scope.cancel = ->
            $scope.progress = undefined
            if $scope.uploadObject
              $scope.uploadObject.abort()

            $uibModalInstance.dismiss('Upload Canceled')

          $scope.onFileSelect = ($files) ->
            $scope.file = $files[0]

          $scope.canUpload = ->
            !$scope.uploading and $scope.file and $scope.dataModel and $scope.hpoDataModel and $scope.testDataModel

          $scope.upload = ->
            wasError = false
            $scope.messages.clearAllMessages()
            if (!angular.isObject($scope.dataModel))
              $scope.messages.error "Data Model #{$scope.dataModel} doesn't exist.", "Please select existing Data Model."
              wasError = true
            if (!angular.isObject($scope.hpoDataModel))
              $scope.messages.error "Data Model #{$scope.hpoDataModel} doesn't exist.", "Please select existing Data Model."
              wasError = true
            if (!angular.isObject($scope.testDataModel))
              $scope.messages.error "Data Model #{$scope.testDataModel} doesn't exist.", "Please select existing Data Model."
              wasError = true
            if (wasError)
              return

            $scope.uploading = true

            $scope.upload = Upload.upload({
              url: "#{modelCatalogueApiRoot}/genomics/imports/upload"
              params: {
                dataModelId: $scope.dataModel.modelCatalogueId
                hpoDataModelId: $scope.hpoDataModel.modelCatalogueId
                testDataModelId: $scope.testDataModel.modelCatalogueId
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
                $uibModalInstance.close(result)
                if catalogue.isInstanceOf result.elementType, 'asset'
                  result.show()
            ).error((data) ->
              for err in data.errors
                $scope.messages.error err.message
              $scope.uploading = false
              $scope.progress  = 0
              $uibModalInstance.close()
            )
      }

      dialog.result
  ]
  messagesProvider.setPromptFactory 'new-rare-disease-csv-import', factory
]
