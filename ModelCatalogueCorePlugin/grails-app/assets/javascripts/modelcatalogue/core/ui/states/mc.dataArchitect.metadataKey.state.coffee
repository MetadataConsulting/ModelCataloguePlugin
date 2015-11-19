angular.module('mc.core.ui.states.mc.dataArchitect.metadataKey', ['mc.core.ui.states.controllers']).config(['$stateProvider', ($stateProvider) ->


    $stateProvider.state 'mc.dataArchitect.metadataKey', {
      url: "/metadataKeyCheck",
      templateUrl: 'modelcatalogue/core/ui/state/parent.html'
      controller: ['$state','$modal',($state, $modal)->
        dialog = $modal.open {
          windowClass: 'messages-modal-prompt'
          template: '''
         <div class="modal-header">
            <h4>please enter metadata key</h4>
        </div>
        <div class="modal-body">
            <form role="form" ng-submit="$close(value)">
            <div class="form-group">
                <label for="value">metadata key</label>
                <input type="text" id="value" ng-model="value" class="form-control">
            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" ng-click="$close(value)">OK</button>
            <button class="btn btn-warning" ng-click="$dismiss()">Cancel</button>
        </div>
        '''
        }
        dialog.result.then (result) ->
          $state.go('mc.dataArchitect.metadataKeyCheck', {'metadata':result})

      ]
    }

])