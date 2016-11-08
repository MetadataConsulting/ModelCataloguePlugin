angular.module('mc.core.ui.bs.modalPromptWithMultipleOptions', ['mc.util.messages', 'mc.util.ui.focusMe']).config ['messagesProvider', (messagesProvider)->
 messagesProvider.setPromptFactory 'with-multiple-options',  [ '$uibModal', ($uibModal) ->
   (title, body, args) ->
      dialog = $uibModal.open {
        windowClass: 'messages-modal-prompt'
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body">
            <form role="form">
              <p>''' + body + '''</p>
              <div class="checkbox" ng-repeat="option in options">
                <label>
                  <input type="checkbox" ng-checked="selection.indexOf(option) > -1" ng-click="toggleSelection(option)"> {{option.name}}
                </label>
              </div>
            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" ng-disabled="selection.length == 0" ng-click="$close(selection)">OK</button>
            <button class="btn btn-warning" ng-click="$dismiss()">Cancel</button>
        </div>
        '''

        controller: ($scope) ->
          'ngInject'
          $scope.options = args.options
          $scope.selection = []

          $scope.toggleSelection = (option) ->
            idx = $scope.selection.indexOf(option)

            if idx > -1
              # is currently selected
              $scope.selection.splice(idx, 1)
            else
              # is newly selected
             $scope.selection.push(option)

      }

      dialog.result
 ]
]
