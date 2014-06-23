angular.module('mc.core.ui.bs.modalPromptLogin', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$modal', '$q', 'messages', 'security', ($modal, $q, messages, security) ->
    ->
      deferred = $q.defer()
      dialog = $modal.open {
        windowClass: 'login-modal-prompt'
        template: '''
         <div class="modal-header">
            <h4>Login</h4>
        </div>
        <div class="modal-body">
            <messages-panel messages="messages"></messages-panel>
            <form role="form" ng-submit="login()">
              <div class="form-group">
                <label for="username">Username</label>
                <input type="text" class="form-control" id="username" placeholder="Username" ng-model="user.username">
              </div>
              <div class="form-group">
                <label for="password">Password</label>
                <input type="password" class="form-control" id="password" placeholder="Password" ng-model="user.password">
              </div>
              <div class="checkbox">
                <label>
                  <input type="checkbox" ng-model="user.rememberMe"> Remember Me
                </label>
              </div>
              <button type="submit" class="hide" ng-click="login()"></button>
            </form>
        </div>
        <div class="modal-footer">
            <button type="submit" class="btn btn-success" ng-click="login()" ng-disabled="!user.username || !user.password"><span class="glyphicon glyphicon-ok"></span> Login</button>
            <button class="btn btn-warning" ng-click="$dismiss()">Cancel</button>
        </div>
        '''
        controller: ['$scope', 'messages', 'security', '$modalInstance', '$log',
          ($scope, messages, security, $modalInstance) ->
            $scope.user = {}
            $scope.messages = messages.createNewMessages()

            $scope.login = ->
              security.login($scope.user.username, $scope.user.password, $scope.user.rememberMe).then (success)->
                if success.data.error
                  $scope.messages.error success.data.error
                else if success.data.errors
                  for error in success.data.errors
                    $scope.messages.error error
                else
                  $modalInstance.close success


        ]

      }

      dialog.result.then (result) ->
        deferred.resolve(result)
      , (reason) ->
        deferred.reject(reason)

      deferred.promise
  ]

  messagesProvider.setPromptFactory 'login', factory
]