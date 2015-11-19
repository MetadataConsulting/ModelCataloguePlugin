angular.module('mc.core.ui.states.mc.dataArchitect.findRelationsByMetadataKeys', []).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'mc.dataArchitect.findRelationsByMetadataKeys', {
      url: "/findRelationsByMetadataKeys",
      templateUrl: 'modelcatalogue/core/ui/state/parent.html',
      controller: ['$scope','$state','$modal',($scope, $state, $modal)->
        dialog = $modal.open {
          windowClass: 'messages-modal-prompt'
          template: '''
       <div class="modal-header">
          <h4>please enter metadata key</h4>
      </div>
      <div class="modal-body">
          <form role="form">
          <div class="form-group">
              <label for="keyOne">metadata key one</label>
              <input type="text" id="keyOne" ng-model="result.keyOne" class="form-control">
              <label for="keyTwo">metadata key two</label>
              <input type="text" id="keyTwo" ng-model="result.keyTwo" class="form-control">
          </form>
      </div>
      <div class="modal-footer">
          <button class="btn btn-primary" ng-click="$close(result)">OK</button>
          <button class="btn btn-warning" ng-click="$dismiss()">Cancel</button>
      </div>
      '''
        }

        dialog.result.then (result) ->
          $state.go('mc.dataArchitect.showMetadataRelations', {'keyOne':result.keyOne, 'keyTwo':result.keyTwo})
      ]
    }

])