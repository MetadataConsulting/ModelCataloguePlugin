angular.module('mc.core.ui.bs.modalPromptNewExcelImport', ['mc.util.messages', 'angularFileUpload']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$modal', ($modal) ->
    (title, body, args) ->
      dialog = $modal.open {
        windowClass: 'basic-edit-modal-prompt'
        backdrop: 'static'
        keyboard: false
        resolve:
          args: -> args
        #language=HTML
        template: """
         <div class="modal-header">
            <h4>#{title}</h4>
        </div>
        <div class="modal-body">
            <messages-panel messages="messages"></messages-panel>
            <form role="form" ng-submit="saveElement()">
              <div class="form-group">
                <label for="name" class="">Name</label>
                <input type="text" class="form-control" id="name" placeholder="Name (leave blank to use filename)" ng-model="copy.name">
              </div>
              <div class="form-group">
                <label for="asset" class="">File</label>
                <input ng-hide="uploading &amp;&amp; progress" type="file" accept=".xls,.xlsx" class="form-control" id="asset" placeholder="File" ng-model="copy.asset" ng-file-select="onFileSelect($files)">
                <progressbar value="progress" ng-show="uploading &amp;&amp; progress">{{progress}} %</progressbar>
              </div>
              <div class="form-group">
                      <label ng-click="headersCollapsed = !headersCollapsed" ng-init="headersCollapsed = true">Customize Columns Headers</label>
              </div>
              <div collapse="headersCollapsed">
                    <div class="form-group">
                      <label for="dataElementCode">Data Element Code</label>
                      <input type="text" class="form-control" id="dataElementCode" placeholder="Data Item Unique Code" ng-model="headersMap.dataElementCode">
                    </div>
                    <div class="form-group">
                      <label for="dataElementName">Data Element Name</label>
                      <input type="text" class="form-control" id="dataElementName" placeholder="Data Item Name" ng-model="headersMap.dataElementName">
                    </div>
                    <div class="form-group">
                      <label for="dataElementDescription">Data Item Description</label>
                      <input type="text" class="form-control" id="dataElementDescription" placeholder="Data Item Description" ng-model="headersMap.dataElementDescription">
                    </div>
                    <div class="form-group">
                      <label for="dataTypeClassification">Data Type Classification</label>
                      <input type="text" class="form-control" id="dataTypeClassification" placeholder="Data Type Classification" ng-model="headersMap.dataTypeClassification">
                    </div>
                    <div class="form-group">
                      <label for="dataTypeName">Data Type</label>
                      <input type="text" class="form-control" id="dataTypeName" placeholder="Data Type" ng-model="headersMap.dataTypeName">
                    </div>
                    <div class="form-group">
                      <label for="dataTypeCode">Data Type Unique Code</label>
                      <input type="text" class="form-control" id="dataTypeCode" placeholder="Data Type Unique Code" ng-model="headersMap.dataTypeCode">
                    </div>
                    <div class="form-group">
                      <label for="valueDomainClassification">Value Domain Classification</label>
                      <input type="text" class="form-control" id="valueDomainClassification" placeholder="Value Domain Classification" ng-model="headersMap.valueDomainClassification">
                    </div>
                    <div class="form-group">
                      <label for="valueDomainName">Value Domain</label>
                      <input type="text" class="form-control" id="valueDomainName" placeholder="Value Domain" ng-model="headersMap.valueDomainName">
                    </div>
                    <div class="form-group">
                      <label for="valueDomainCode">Value Domain Unique Code</label>
                      <input type="text" class="form-control" id="valueDomainCode" placeholder="Value Domain Unique Code" ng-model="headersMap.valueDomainCode">
                    </div>
                    <div class="form-group">
                      <label for="parentModelName">Parent Model</label>
                      <input type="text" class="form-control" id="parentModelName" placeholder="Parent Model" ng-model="headersMap.parentModelName">
                    </div>
                    <div class="form-group">
                      <label for="parentModelCode">Parent Model Unique Code</label>
                      <input type="text" class="form-control" id="parentModelCode" placeholder="Parent Model Unique Code" ng-model="headersMap.parentModelCode">
                    </div>
                    <div class="form-group">
                      <label for="containingModelName">Model</label>
                      <input type="text" class="form-control" id="containingModelName" placeholder="Model" ng-model="headersMap.containingModelName">
                    </div>
                    <div class="form-group">
                      <label for="containingModelCode">Model Unique Code</label>
                      <input type="text" class="form-control" id="containingModelCode" placeholder="Model Unique Code" ng-model="headersMap.containingModelCode">
                    </div>
                    <div class="form-group">
                      <label for="measurementUnitName">Measurement Unit</label>
                      <input type="text" class="form-control" id="measurementUnitName" placeholder="Measurement Unit" ng-model="headersMap.measurementUnitName">
                    </div>
                    <div class="form-group">
                      <label for="measurementSymbol">Measurement Unit Symbol</label>
                      <input type="text" class="form-control" id="measurementSymbol" placeholder="Measurement Unit Symbol" ng-model="headersMap.measurementSymbol">
                    </div>
                    <div class="form-group">
                      <label for="classification">Classification</label>
                      <input type="text" class="form-control" id="classificatio  n" placeholder="Classification" ng-model="headersMap.classification">
                    </div>
                    <div class="form-group">
                      <label for="metadata">Metadata</label>
                      <input type="text" class="form-control" id="metadata" placeholder="Metadata" ng-model="headersMap.metadata">
                    </div>
              </div>
            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-success" ng-click="saveElement()" ng-disabled="!hasChanged() || uploading"><span class="glyphicon glyphicon-ok"></span> Save</button>
            <button class="btn btn-warning" ng-click="cancel()">Cancel</button>
        </div>
        """
        controller: 'importCtrl'

      }

      dialog.result
  ]
  messagesProvider.setPromptFactory 'new-excel-import', factory
]