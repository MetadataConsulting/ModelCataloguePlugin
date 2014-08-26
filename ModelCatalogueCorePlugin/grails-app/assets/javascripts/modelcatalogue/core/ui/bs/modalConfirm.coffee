angular.module('mc.core.ui.bs.modalConfirm', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
 messagesProvider.setConfirmFactory [ '$modal', ($modal) ->
   (title, body) ->
      dialog = $modal.open {
        windowClass: 'messages-modal-confirm'
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body">
            ''' + body + '''
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" ng-click="$close(true)">OK</button>
            <button class="btn btn-warning" ng-click="$dismiss(false)">Cancel</button>
        </div>
        '''
      }


      dialog.result
 ]
]