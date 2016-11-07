angular.module('mc.core.ui.bs.modalAbout', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
 messagesProvider.setPromptFactory 'about-dialog', [ '$uibModal', ($uibModal) ->
   (title, body) ->
      dialog = $uibModal.open {
        windowClass: 'messages-modal-confirm'
        template: '''
         <div class="modal-header">
            <h4>Model Catalogue Version</h4>
        </div>
        <div class="modal-body">
          Version: <span ng-include="'/info/version.html'"></span>
        </div>
        <div class="modal-footer">
          <form role="form">
            <button class="btn btn-primary" ng-click="$close(true)">Hide</button>
          </form>
        </div>
        '''
      }


      dialog.result
 ]
]
