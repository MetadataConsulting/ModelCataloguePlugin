angular.module('mc.core.ui.bs.modalAlert', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
 messagesProvider.setPromptFactory 'alert', [ '$uibModal', ($uibModal) ->
   (title, body, args) ->
      dialog = $uibModal.open {
        size: args.size
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
          </form>
        </div>
        '''
      }


      dialog.result
 ]
]
