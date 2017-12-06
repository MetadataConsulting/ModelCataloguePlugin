angular.module('mc.core.ui.bs.modalPromptNewXsdImport', ['mc.util.messages', 'mc.core.ui.bs.importCtrl']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$uibModal', ($uibModal) ->
    (title, body, args) ->
      dialog = $uibModal.open {
        windowClass: 'basic-edit-modal-prompt'
        backdrop: 'static'
        keyboard: false
        resolve:
          args: -> args
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body">
            <messages-panel messages="messages"></messages-panel>
            <form role="form" ng-submit="saveElement()">
              <div class="form-group">
                <label for="conceptualDomain" class="">Conceptual Domain</label>
                <input type="text" class="form-control" id="conceptualDomain" ng-model="copy.conceptualDomain">
              </div>
              <div class="form-group">
                <label for="modelName" class="">Model Name</label>
                <input type="text" class="form-control" id="modelName" placeholder="Model Name (leave blank to use filename)" ng-model="copy.modelName">
              </div>
              <div class="form-group">
                <label for="asset" class="">File</label>
                <input ng-hide="uploading &amp;&amp; progress" type="file" accept=".xsd,.xml" class="form-control" id="asset" placeholder="File" ngf-model="copy.asset" ngf-select="onFileSelect($files)">
                <uib-progressbar value="progress" ng-show="uploading &amp;&amp; progress">{{progress}} %</uib-progressbar>
              </div>
              <div class="checkbox">
                <label>
                  <input type="checkbox" ng-model="copy.createModelsForElements"> Create models for elements with the same name
                </label>
              </div>
              <fake-submit-button/>
            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-success" ng-click="saveElement()" ng-disabled="!hasChanged() || uploading"><span class="glyphicon glyphicon-ok"></span> Save</button>
            <button class="btn btn-warning" ng-click="cancel()">Cancel</button>
        </div>
        '''
        controller: 'importCtrl'

      }

      dialog.result
  ]
  messagesProvider.setPromptFactory 'new-xsd-import', factory
]
