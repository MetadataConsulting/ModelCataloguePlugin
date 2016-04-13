angular.module('mc.core.ui.states.controllers.XmlEditorCtrl', ['ui.ace', 'angular.download.service', 'mc.util.Saxon', 'mc.util.vkBeautify'])
.controller('mc.core.ui.states.controllers.XmlEditorCtrl', [
  '$scope', '$stateParams', '$state', 'element', 'applicationTitle', '$http', 'catalogue', 'security', 'fileDownloadService', 'SaxonPromise', '$q', 'vkBeautify'
  ($scope, $stateParams, $state, element, applicationTitle, $http, catalogue, security, fileDownloadService, SaxonPromise, $q, vkBeautify) ->

    applicationTitle "Xml Editor for #{element.getLabel()}"

    xmlPromise = $http.get("#{element.internalModelCatalogueId}?format=xml").then (resp)->
      $scope.xml = resp.data

    xsltPromise = $http.get("#{security.contextPath}#{catalogue.getDefaultXslt(element.elementType) ? catalogue.getDefaultXslt('catalogueElement')}").then (resp)->
      $scope.xslt = resp.data

    $scope.download = (name, text, mimeType = 'text/xml;charset=utf-8') ->
      fileDownloadService.setMimeType(mimeType)
      fileDownloadService.downloadFile(name, text)

    $q.all(xmlPromise, xsltPromise).then ->
      SaxonPromise.then (Saxon) ->
        processor = Saxon.newXSLT20Processor(Saxon.parseXML($scope.xslt))
        document = processor.transformToDocument(Saxon.parseXML($scope.xml))
        $scope.xsd = vkbeautify.xml(Saxon.serializeXML(document), 2)

])
