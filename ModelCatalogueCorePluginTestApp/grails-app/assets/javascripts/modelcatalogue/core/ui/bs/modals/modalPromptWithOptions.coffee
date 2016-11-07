angular.module('mc.core.ui.bs.modalPromptWithOptions', ['mc.util.messages', 'mc.util.ui.focusMe']).config ['messagesProvider', (messagesProvider)->
 messagesProvider.setPromptFactory 'with-options',  [ '$uibModal', ($uibModal) ->
   (title, body, args) ->
      dialog = $uibModal.open {
        windowClass: 'messages-modal-prompt'
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body">
            <form role="form">
              <div class="form-group">
                <label for="option">''' + body + '''</label>
                <select class="form-control" focus-me id="option" placeholder="Please select..." ng-model="option" ng-options="key as value for (key , value) in options"/>
              </div>
            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" ng-disabled="!option || selected == option" ng-click="$close(option)">OK</button>
            <button class="btn btn-warning" ng-click="$dismiss()">Cancel</button>
        </div>
        '''

        controller: ['$scope', ($scope) ->
          $scope.selected = args.selected
          $scope.options = args.options
          $scope.option = $scope.selected
        ]
      }

      dialog.result
 ]
]
