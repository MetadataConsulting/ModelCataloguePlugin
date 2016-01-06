angular.module('mc.core.ui.bs.modalOptions', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
 messagesProvider.setPromptFactory 'options', [ '$modal', ($modal) ->
   (title, body, args) ->
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
          <form role="form">
            <button ng-repeat="option in options" class="btn" ng-class="option.classes" ng-click="$close(option.value)"><span ng-if="option.icon" ng-class="option.icon"></span> {{option.label}}</button>
            <button class="btn btn-warning" ng-click="$dismiss(false)"><span class="fa fa-fw fa-ban"></span> Cancel</button>
          </form>
        </div>
        '''

        controller: ['$scope', ($scope) ->
          $scope.options = args.options
        ]
      }


      dialog.result
 ]
]