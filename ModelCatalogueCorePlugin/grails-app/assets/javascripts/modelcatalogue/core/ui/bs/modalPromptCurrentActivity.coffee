angular.module('mc.core.ui.bs.modalPromptCurrentActivity', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
 messagesProvider.setPromptFactory 'current-activity',  [ '$modal', ($modal) ->
   (title) ->
      dialog = $modal.open {
        windowClass: 'messages-modal-prompt'
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body">
            <form role="form">
              <div class="form-group">
                <table class="table table-striped">
                  <tr><th colspan="2">Most Recent Active Users</th></tr>
                  <tr ng-repeat="seen in lastSeen">
                    <th class="col-md-4">{{seen.username}}</th>
                    <td class="col-md-8">{{seen.lastSeen | date:'short'}}</td>
                  </tr>
                </table>
              </div>
            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" ng-click="$close()">OK</button>
        </div>
        '''
        resolve:
          lastSeen: ['$http', 'modelCatalogueApiRoot', 'enhance', ($http, modelCatalogueApiRoot, enhance) ->
            $http.get("#{modelCatalogueApiRoot}/user/lastSeen").then (response) ->
              enhance response.data
          ]
        controller: ['$scope', 'lastSeen', ($scope, lastSeen) ->
          $scope.lastSeen = lastSeen
        ]
      }

      dialog.result
 ]
]