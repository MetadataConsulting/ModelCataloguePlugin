angular.module('mc.core.ui.bs.modalPromptForCatalogueElement', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
 messagesProvider.setPromptFactory 'catalogue-element',  [ '$modal', ($modal) ->
   (title, body, args) ->
      dialog = $modal.open {
        windowClass: 'messages-modal-prompt'
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body">
            <form role="form" ng-submit="$close(value)">
            <div class="form-group">
                <label for="value">''' + body + '''</label>
                <input id="value" ng-model="value" class="form-control" catalogue-element-picker="''' + (args.resource ? 'catalogueElement') + '''">
            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" ng-click="$close(value)">OK</button>
            <button class="btn btn-warning" ng-click="$dismiss()">Cancel</button>
        </div>
        '''
      }

      dialog.result
 ]
]