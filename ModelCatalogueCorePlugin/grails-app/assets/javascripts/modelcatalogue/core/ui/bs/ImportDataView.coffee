angular.module('mc.core.ui.bs.importDataView', ['mc.core.ui.importDataView']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/importDataView.html', '''
    <div role="form">
      <div class="form-group">
        <label for="conceptualDomain">Conceptual Domain</label>
        <input class="form-control" id="conceptualDomainName" type="text" ng-model="conceptualDomainName">
      </div>
      <div  ng-show="conceptualDomainName">
        <div class="form-group">
          <input type="file" ng-file-select="onFileSelect($files)" >
          <input type="file" ng-file-select="onFileSelect($files)" multiple>
        </div>
        <div style="height: 250px;width:250px; background: #e0e0e0;" ng-file-drop="onFileSelect($files)" ng-file-drag-over-class="optional-css-class"
              ng-show="dropSupported">drop files here</div>
        <div ng-file-drop-available="dropSupported=true"
              ng-show="!dropSupported">HTML5 Drop File is not supported!</div>
        <button class="btn btn-default" ng-click="upload.abort()">Cancel Upload</button>
      </div>
  </div>
    '''
  ]