angular.module('mc.core.ui.bs.modalPromptNewExcelImport', ['mc.util.messages', 'mc.core.ui.bs.importCtrl']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$uibModal', ($uibModal) ->
    (title, body, args) ->
      dialog = $uibModal.open {
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
                <label for="modelName" class="">Model Name</label>
                <input type="text" class="form-control" id="modelName" placeholder="Model Name (leave blank to use filename)" ng-model="copy.modelName">
              </div>
              <div class="form-group">
                <label for="excelImportTypeSelect">Select Excel Import Type</label>
                <select name="excelImportTypeSelect"
                        id="excelImportTypeSelect"
                        ng-model="selectedExcelImportType">
                  <option ng-repeat="excelImportType in excelImportTypes" value="{{excelImportType}}">{{excelImportType}}</option>
                </select>
              </div>
              <div class="form-group">
                <label for="asset" class="">Excel File</label>
                <input ng-hide="uploading &amp;&amp; progress" type="file" accept=".xls,.xlsx" class="form-control" id="asset" ngf-model="copy.asset" ngf-select="onFileSelect($files)">
                <uib-progressbar value="progress" ng-show="uploading &amp;&amp; progress">{{progress}} %</uib-progressbar>
              </div>

              <div class="form-group">
                <label for="excelConfigXMLFile">Excel Config XML File</label>
                <input ng-hide="uploading &amp;&amp; progress" type="file" ngf-select ng-model="excelConfigXMLFile" accept=".xml" class="form-control" id="excelConfigXMLFile">
                </input>
              </div>

<!-- no longer use this old way of collecting headers map:
              <div class="form-group">
                      <label ng-click="headersCollapsed = !headersCollapsed" ng-init="headersCollapsed = true">Customize Columns Headers</label>
              </div>
              <div uib-collapse="headersCollapsed">
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
                      <label for="dataTypeClassification">Data Type Data Model</label>
                      <input type="text" class="form-control" id="dataTypeClassification" placeholder="Data Type Data Model" ng-model="headersMap.dataTypeDataModel">
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
                      <label for="parentModelName">Parent Data Class</label>
                      <input type="text" class="form-control" id="parentModelName" placeholder="Parent Data Class" ng-model="headersMap.parentDataClassName">
                    </div>
                    <div class="form-group">
                      <label for="parentModelCode">Parent Data Class Unique Code</label>
                      <input type="text" class="form-control" id="parentModelCode" placeholder="Parent Data Class Unique Code" ng-model="headersMap.parentDataClassCode">
                    </div>
                    <div class="form-group">
                      <label for="containingModelName">Data Class</label>
                      <input type="text" class="form-control" id="containingModelName" placeholder="Data Class" ng-model="headersMap.containingDataClassName">
                    </div>
                    <div class="form-group">
                      <label for="containingModelCode">Data Class Unique Code</label>
                      <input type="text" class="form-control" id="containingModelCode" placeholder="Data Class Unique Code" ng-model="headersMap.containingDataClassCode">
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
                      <label for="classification">Data Model</label>
                      <input type="text" class="form-control" id="classification" placeholder="Data Model" ng-model="headersMap.dataModel">
                    </div>
                    <div class="form-group">
                      <label for="metadata">Metadata</label>
                      <input type="text" class="form-control" id="metadata" placeholder="Metadata" ng-model="headersMap.metadata">
                    </div>
              </div>
-->
              <fake-submit-button/>
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
