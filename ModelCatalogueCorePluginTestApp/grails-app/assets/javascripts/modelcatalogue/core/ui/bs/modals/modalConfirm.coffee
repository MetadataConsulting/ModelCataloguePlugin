angular.module('mc.core.ui.bs.modalConfirm', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
 messagesProvider.setConfirmFactory [ '$uibModal', ($uibModal) ->
   (title, body) ->
      dialog = $uibModal.open {
        windowClass: 'messages-modal-confirm'
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body">
            ''' + body + '''
        </div>
        <div class="modal-footer">
          <form role="form">
            <button class="btn btn-primary" ng-click="$close(true)">OK</button>
            <button class="btn btn-warning" ng-click="$dismiss(false)">Cancel</button>
          </form>
        </div>
        '''
      }


      dialog.result
 ]
]
