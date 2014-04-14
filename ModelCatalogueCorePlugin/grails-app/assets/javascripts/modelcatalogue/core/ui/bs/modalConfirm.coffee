angular.module('mc.core.ui.bs.modalConfirm', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
 messagesProvider.setConfirmFactory [ '$modal', '$q', ($modal, $q) ->
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


      deferred = $q.defer()
      dialog.result.then () ->
        deferred.resolve(true)
      , () ->
        deferred.resolve(false)

      deferred.promise
 ]
]