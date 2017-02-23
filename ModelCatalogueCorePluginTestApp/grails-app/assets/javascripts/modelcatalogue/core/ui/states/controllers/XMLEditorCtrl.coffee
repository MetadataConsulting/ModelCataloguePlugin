angular.module('mc.core.ui.states.controllers.XmlEditorCtrl', ['ui.ace', 'ngFileSaver', 'mc.util.xsltTransformer'])
.controller('mc.core.ui.states.controllers.XmlEditorCtrl', [
  '$log', '$scope', '$stateParams', '$state', '$timeout', 'element', 'applicationTitle', '$http', 'catalogue', 'security', 'FileSaver', 'Blob', 'xsltTransformer',
  ($log, $scope, $stateParams, $state, $timeout, element, applicationTitle, $http, catalogue, security, FileSaver, Blob, xsltTransformer) ->

    applicationTitle "Xml Editor for #{element.getLabel()}"
    $scope.element = element
    $scope.transformationInProgress = false

    transform = (newValues) ->
      xml = newValues[0]
      xslt = newValues[1]

      return unless xml and xslt
      $log.debug("transformation started", {xml: xml, xslt: xslt})

      $timeout( ->
        $scope.transformationInProgress = true
      , 500
      )
      xsltTransformer.transformXml(xml, xslt)
      .then (result) ->
        $scope.xsd = result
        $log.debug("transformation succeeded with result", {xsd: result})
      .catch (error) ->
        $scope.xsd = error.message
        $log.debug("transformation failed with error", error)
      .finally () ->
        $timeout( ->
          $scope.transformationInProgress = false
        , 500
        )

    $http.get("#{element.internalModelCatalogueId}?format=xml&full=true&repetitive=true").then (resp) ->
      $scope.xml = resp.data

    $http.get("#{security.contextPath}#{catalogue.getDefaultXslt(element.elementType) ? catalogue.getDefaultXslt('catalogueElement')}").then (resp) ->
      $scope.xslt = resp.data

    $scope.download = (name, text, mimeType = 'text/xml;charset=utf-8') ->
      FileSaver.saveAs(new Blob([text], type: mimeType), name)

    $scope.$watchGroup ['xml', 'xslt'], transform

])
.config(['actionsProvider', (actionsProvider) ->
  XML_EDITOR_XSLT_ROLE = 'xmleditor-xslt'
  XML_EDITOR_XSD_ROLE = 'xmleditor-xsd'

  actionsProvider.registerActionInRole('load-xslt', XML_EDITOR_XSLT_ROLE, [
    '$log', '$scope','messages', ($log, $scope, messages) ->
      {
        label: 'Load XSLT'
        icon: 'fa fa-file-code-o'
        action: ->
          messages
          .prompt("Select Stylesheet", "Select stylesheet to be loaded", type: 'catalogue-element', resource: 'asset', contentType: 'text/xsl')
          .then (asset) ->
            $scope.xsltAsset = asset
            $log.debug("loaded asset", asset)
            return asset.execute('content')
          .then (resp) ->
            $scope.xslt = resp
      }
  ])

  actionsProvider.registerActionInRole('save-xslt', XML_EDITOR_XSLT_ROLE, [
    '$log', '$scope','messages', 'Upload', 'modelCatalogueApiRoot', 'enhance', ($log, $scope, messages, Upload, modelCatalogueApiRoot, enhance) ->
      {
        label: 'Save Stylesheet'
        icon: 'fa fa-save'
        action: ->
          messages
          .prompt("Save Stylesheet", "Asset name", type: 'catalogue-element', resource: 'asset', contentType: 'text/xsl', allowString: true, value: $scope.xsltAsset)
          .then (asset) ->
            if angular.isString(asset)
              name = asset
            else
              id = asset.id
              name = asset.name

            $scope.upload = Upload.upload({
              url: "#{modelCatalogueApiRoot}/asset/upload"
              params: {id: id, name: name, dataModel: $scope.currentDataModel?.id, filename: "#{name}.xsl"}
              data: {asset: new Blob([$scope.xslt], {type: 'text/xsl'})}
            }).progress((evt) ->
              $scope.progress = parseInt(100.0 * evt.loaded / evt.total)
            ).success((result) ->
              $scope.xsltAsset = enhance(result)
              $log.debug("asset saved", result)
              messages.success "Asset was successfully saved."
            ).catch( (reason) ->
              $log.error("asset was not saved", reason)
              messages.error "Could not save asset. #{reason}"
            )
      }
  ])

  actionsProvider.registerActionInRole('save-xsd', XML_EDITOR_XSD_ROLE, [
    '$log', '$scope','messages', 'Upload', 'modelCatalogueApiRoot', 'enhance', ($log, $scope, messages, Upload, modelCatalogueApiRoot, enhance) ->
      {
        label: 'Save Schema'
        icon: 'fa fa-save'
        action: ->
          messages
          .prompt("Save Schema", "Asset name", type: 'catalogue-element', resource: 'asset', contentType: 'text/xml', allowString: true, value: $scope.xsdAsset)
          .then (asset) ->
            if angular.isString(asset)
              name = asset
            else
              id = asset.id
              name = asset.name

            $scope.upload = Upload.upload({
              url: "#{modelCatalogueApiRoot}/asset/upload"
              params: {id: id, name: name, dataModel: $scope.currentDataModel?.id, filename: "#{name}.xsd"}
              data: {asset: new Blob([$scope.xsd], {type: 'text/xml'})}
            }).progress((evt) ->
              $scope.progress = parseInt(100.0 * evt.loaded / evt.total)
            ).success((result) ->
              $scope.xsdAsset = enhance(result)
              $log.debug("asset saved", result)
              messages.success "Asset was successfuly saved."
            ).catch( (reason) ->
              $log.error("asset was not saved", reason)
              messages.error "Could not save asset. #{reason}"
            )
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
