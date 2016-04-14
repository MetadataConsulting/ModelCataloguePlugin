angular.module('mc.core.ui.states.controllers.XmlEditorCtrl', ['ui.ace', 'angular.download.service', 'mc.util.xsltTransformer'])
.controller('mc.core.ui.states.controllers.XmlEditorCtrl', [
  '$scope', '$stateParams', '$state', 'element', 'applicationTitle', '$http', 'catalogue', 'security', 'fileDownloadService', 'xsltTransformer',
  ($scope, $stateParams, $state, element, applicationTitle, $http, catalogue, security, fileDownloadService, xsltTransformer) ->

    applicationTitle "Xml Editor for #{element.getLabel()}"
    $scope.element = element

    $http.get("#{element.internalModelCatalogueId}?format=xml").then (resp) ->
      $scope.xml = resp.data

    $http.get("#{security.contextPath}#{catalogue.getDefaultXslt(element.elementType) ? catalogue.getDefaultXslt('catalogueElement')}").then (resp) ->
      $scope.xslt = resp.data

    $scope.download = (name, text, mimeType = 'text/xml;charset=utf-8') ->
      fileDownloadService.setMimeType(mimeType)
      fileDownloadService.downloadFile(name, text)

    transform = (newValues) ->
      xml = newValues[0]
      xslt = newValues[1]

      return unless xml and xslt

      xsltTransformer.transformXml(xml, xslt)
      .then (result) ->
        $scope.xsd = result
      .catch (error) ->
        $scope.xsd = error.message

    $scope.$watchGroup ['xml', 'xslt'], transform
    transform [$scope.xml, $scope.xslt]

])
.config(['actionsProvider', (actionsProvider) ->
  XML_EDITOR_XSLT_ROLE = 'xmleditor-xslt'
  XML_EDITOR_XSD_ROLE = 'xmleditor-xsd'


  actionsProvider.registerActionInRole('load-xslt', XML_EDITOR_XSLT_ROLE, [ '$scope','messages', ($scope, messages) ->

    {
      label: 'Load XSLT'
      icon: 'fa fa-file-code-o'
      action: ->
        messages
        .prompt("Select Stylesheet", "Select stylesheet to be loaded", type: 'catalogue-element', resource: 'asset', contentType: 'text/xsl')
        .then (asset) -> asset.execute('content')
        .then (resp) -> $scope.xslt = resp
    }
  ])

  actionsProvider.registerActionInRole('load-xslt', XML_EDITOR_XSD_ROLE, [ '$scope', ($scope) ->

    {
      label: 'Download Result'
      icon: 'fa fa-download'
      action: ->
        $scope.download('Result.xsd', $scope.xsd)
    }
  ])

])
