angular.module('mc.core.ui.states.controllers.XmlEditorCtrl', ['ui.ace', 'angular.download.service'])
.controller('mc.core.ui.states.controllers.XmlEditorCtrl', [
  '$scope', '$stateParams', '$state', 'element', 'applicationTitle', '$http', 'catalogue', 'security', 'fileDownloadService'
  ($scope, $stateParams, $state, element, applicationTitle, $http, catalogue, security, fileDownloadService) ->
    applicationTitle "Xml Editor for #{element.getLabel()}"

    $http.get("#{element.internalModelCatalogueId}?format=xml").then (resp)->
      $scope.xml = resp.data

    $http.get("#{security.contextPath}#{catalogue.getDefaultXslt(element.elementType) ? catalogue.getDefaultXslt('catalogueElement')}").then (resp)->
      $scope.xslt = resp.data

    $scope.xsd = "<catalogue><dataClass></dataClass></catalogue>"

    $scope.download = (name, text, mimeType = 'text/xml;charset=utf-8') ->
      fileDownloadService.setMimeType(mimeType)
      fileDownloadService.downloadFile(name, text)
])
