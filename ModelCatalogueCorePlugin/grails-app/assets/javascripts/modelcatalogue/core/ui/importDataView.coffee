angular.module('mc.core.ui.importDataView', ['angularFileUpload']).directive 'importDataView',  [()-> {
restrict: 'E'
replace: true
templateUrl: 'modelcatalogue/core/ui/importDataView.html'
scope:
  files: '='
controller: ['modelCatalogueApiRoot','$scope', '$upload', (modelCatalogueApiRoot, $scope, $upload) ->
  $scope.onFileSelect = ($files) ->

    #$files: an array of files selected, each file has name, size, and type.
    i = 0

    while i < $files.length
      file = $files[i]
      #upload.php script, node.js route, or servlet url
      # method: 'POST' or 'PUT',
      # headers: {'header-key': 'header-value'},
      # withCredentials: true,
      # or list of files: $files for html5 only
      # set the file formData name ('Content-Desposition'). Default is 'file'

      #fileFormDataName: myFile, //or a list of names for multiple files (html5).
      # customize how data is added to formData. See #40#issuecomment-28612000 for sample code

      #formDataAppender: function(formData, key, val){}
      $scope.upload = $upload.upload(
        url: "#{modelCatalogueApiRoot}/dataArchitect/importData"
        data:
          conceptualDomain: $scope.conceptualDomain
        method: "POST"
        file: file
      ).progress((evt) ->
        console.log "percent: " + parseInt(100.0 * evt.loaded / evt.total)
        return
      ).success((data, status, headers, config) ->

        # file is uploaded successfully
        console.log data
        return
      )
      i++
    return
]
}
]