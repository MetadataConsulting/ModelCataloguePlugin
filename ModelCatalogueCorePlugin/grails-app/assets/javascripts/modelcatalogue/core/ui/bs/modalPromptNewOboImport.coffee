angular.module('mc.core.ui.bs.modalPromptNewOboImport', ['mc.util.messages', 'angularFileUpload']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$modal', ($modal) ->
    (title, body, args) ->
      dialog = $modal.open {
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
                <label for="name" class="">Ontology Name</label>
                <input type="text" class="form-control" id="name" placeholder="Ontology Name (leave blank to use filename)" ng-model="copy.name">
              </div>
              <div class="form-group">
                <label for="idpattern" class="">ID Template (use <code>${id}</code> to insert id)</label>
                <input type="text" class="form-control" id="idpattern" placeholder="ID Template (e.g. http://purl.obolibrary.org/obo/${id.replace(':', '_')})" ng-model="copy.idpattern">
                <p class="help-block">Must be valid URL when evaluated. <a ng-click="copy.idpattern = purlPattern">Use PURL</a></p>
              </div>
              <div class="form-group">
                <label for="asset" class="">File</label>
                <input ng-hide="uploading &amp;&amp; progress" type="file" accept=".obo" class="form-control" id="asset" placeholder="File" ng-model="copy.asset" ng-file-select="onFileSelect($files)">
                <progressbar value="progress" ng-show="uploading &amp;&amp; progress">{{progress}} %</progressbar>
              </div>
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
  messagesProvider.setPromptFactory 'new-obo-import', factory
]