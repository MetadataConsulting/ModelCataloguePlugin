angular.module('mc.core.ui.bs.modalOptions', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
 messagesProvider.setPromptFactory 'options', [ '$uibModal', '$q', ($uibModal, $q) ->
   (title, body, args) ->
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
            <button ng-disabled="pending" ng-repeat="option in options" class="btn" ng-class="option.classes" ng-click="select(option.value)"><span ng-if="option.icon" ng-class="option.icon"></span> {{option.label}}</button>
            <button ng-disabled="pending" class="btn btn-warning" ng-click="close(false)"><span class="fa fa-fw fa-ban"></span> Cancel</button>
          </form>
        </div>
        '''

        controller: ['$scope', '$uibModalInstance', ($scope, $uibModalInstance) ->
          $scope.options = args.options
          $scope.pending = false

          $scope.select = (value) ->
            $scope.pending = true
            $q.when(value).then(args.onSelect).then (result)->
              $uibModalInstance.close(result)
            , args.onDismiss

          $scope.close = (reason) ->
            $scope.pending = true
            args.onDismiss(reason) if angular.isFunction(args.onDismiss)
            $uibModalInstance.dismiss(reason)
        ]
      }


      dialog.result
 ]
]
