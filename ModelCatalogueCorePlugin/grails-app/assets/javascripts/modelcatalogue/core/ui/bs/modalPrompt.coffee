angular.module('mc.core.ui.bs.modalPrompt', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
 messagesProvider.setDefaultPromptFactory [ '$modal', ($modal) ->
   (title, body, args) ->
      dialog = $modal.open {
        windowClass: 'messages-modal-prompt'
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body" ng-init="value=\'''' + args.value + '''\'">
            <form role="form" ng-submit="$close(value)">
              <div class="form-group">
                <label for="value">''' + body + '''</label>
                <input type="''' + (args?.type ? 'text') + '''" id="value" ng-model="value" class="form-control">
                <fake-submit-button/>
              </div>
            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" ng-click="$close(value)" ng-disabled="! ''' + (!!args?.allowNotSet)  + ''' && !value">OK</button>
            <button class="btn btn-warning" ng-click="$dismiss()">Cancel</button>
        </div>
        '''
      }

      dialog.result
 ]
]
