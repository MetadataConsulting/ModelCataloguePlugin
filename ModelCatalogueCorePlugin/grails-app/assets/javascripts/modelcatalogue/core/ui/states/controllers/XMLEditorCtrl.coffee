angular.module('mc.core.ui.states.controllers.XmlEditorCtrl', ['ui.ace', 'angular.download.service', 'mc.util.xsltTransformer'])
.controller('mc.core.ui.states.controllers.XmlEditorCtrl', [
  '$scope', '$stateParams', '$state', 'element', 'applicationTitle', '$http', 'catalogue', 'security', 'fileDownloadService', 'xsltTransformer', '$q',
  ($scope, $stateParams, $state, element, applicationTitle, $http, catalogue, security, fileDownloadService, xsltTransformer, $q) ->

    applicationTitle "Xml Editor for #{element.getLabel()}"

    xmlPromise = $http.get("#{element.internalModelCatalogueId}?format=xml").then (resp) ->
      $scope.xml = resp.data

    xsltPromise = $http.get("#{security.contextPath}#{catalogue.getDefaultXslt(element.elementType) ? catalogue.getDefaultXslt('catalogueElement')}").then (resp) ->
      $scope.xslt = resp.data

    $scope.download = (name, text, mimeType = 'text/xml;charset=utf-8') ->
      fileDownloadService.setMimeType(mimeType)
      fileDownloadService.downloadFile(name, text)

    $scope.$watchGroup ['xml', 'xslt'], (newValues) ->
      xml = newValues[0]
      xslt = newValues[1]

      if xml and xslt
        xsltTransformer.transformXml(xml, xslt)
        .then (result) ->
          $scope.xsd = result
        .catch (error) ->
          $scope.xsd = error.message

])
