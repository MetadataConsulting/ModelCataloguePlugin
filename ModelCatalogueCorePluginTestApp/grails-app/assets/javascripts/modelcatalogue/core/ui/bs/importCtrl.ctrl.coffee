angular.module('mc.core.ui.bs.importCtrl', ['mc.util.messages', 'ngFileUpload']).controller 'importCtrl', ['$scope', 'messages', 'names', 'modelCatalogueDataArchitect', '$uibModalInstance', 'Upload', 'modelCatalogueApiRoot', 'enhance', 'catalogue', 'args', ($scope, messages, names, modelCatalogueDataArchitect, $uibModalInstance, Upload, modelCatalogueApiRoot, enhance, catalogue, args) ->
    $scope.copy     = angular.copy(args.element ? {})
    $scope.original = args.element ? {}
    $scope.messages = messages.createNewMessages()

    $scope.hasChanged   = ->
      $scope.copy.file or $scope.copy.name != $scope.original.name or $scope.copy.description != $scope.original.description

    $scope.cancel = ->
      $scope.progress = undefined
      if $scope.upload
        $scope.upload.abort()

      $uibModalInstance.dismiss('Upload Canceled')

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


      $scope.uploading = true
      # TODO: rename conceptualDomain to classification and let user pick from existing classification
      $scope.upload = Upload.upload({
        url: "#{modelCatalogueApiRoot}/dataArchitect/imports/upload"
        params: {
          id: $scope.copy.id,
          name: $scope.copy.name,
          conceptualDomain: $scope.copy.conceptualDomain,
          createModelsForElements: $scope.copy.createModelsForElements,
          idpattern: $scope.copy.idpattern
        }
        data: {
          file: $scope.copy.file
          headersMap: $scope.headersMap
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

    $scope.purlPattern = "http://purl.obolibrary.org/obo/${id.replace('%3A', '_')}"
]

