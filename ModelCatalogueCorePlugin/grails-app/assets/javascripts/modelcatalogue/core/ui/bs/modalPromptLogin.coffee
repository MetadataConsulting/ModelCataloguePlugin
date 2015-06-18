angular.module('mc.core.ui.bs.modalPromptLogin', ['mc.util.messages', 'ngCookies']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$modal', 'messages', 'security', ($modal, messages, security) ->
    ->
      dialog = $modal.open {
        windowClass: 'login-modal-prompt'
        template: '''
         <div class="modal-header">
            <h4>Login</h4>
        </div>
        <div class="modal-body">
            <messages-panel messages="messages"></messages-panel>
            <div ng-if="providers &amp;&amp; contextPath">
              <a ng-repeat="provider in providers" ng-click="loginExternal(provider)" class="btn btn-primary btn-block"><span class="fa fa-fw" ng-class="'fa-' + provider"></span> Login with {{names.getNaturalName(provider)}}</a>
              <hr/>
            </div>
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
        controller: ['$scope', '$cookies', 'messages', 'security', '$modalInstance', 'names', '$window', '$interval', '$rootScope',
          ($scope, $cookies, messages, security, $modalInstance, names, $window, $interval, $rootScope) ->
            $scope.user = {rememberMe: $cookies.mc_remember_me == "true"}
            $scope.messages = messages.createNewMessages()
            $scope.providers = security.oauthProviders
            $scope.names = names
            $scope.contextPath = security.contextPath
            $scope.login = ->
              security.login($scope.user.username, $scope.user.password, $scope.user.rememberMe).then (success)->
                if success.data.error
                  $scope.messages.error success.data.error
                else if success.data.errors
                  for error in success.data.errors
                    $scope.messages.error error
                else
                  $cookies.mc_remember_me = $scope.user.rememberMe
                  $modalInstance.close success

            $scope.loginExternal = (provider) ->
              url = security.contextPath  + '/oauth/' + provider + '/authenticate'
              externalLoginWindow = $window.open(url, 'mc_external_login', 'menubar=no,status=no,titlebar=no,toolbar=no')

              helperPromise = null


              closeWindowWhenLoggedIn = ->
                if externalLoginWindow.closed
                  $modalInstance.dismiss()
                  $interval.cancel(helperPromise)
                  $modalInstance.dismiss()
                  return
                try # mute cross origin errors when redirected login page
                  if $window.location.host is externalLoginWindow.location.host and (externalLoginWindow.location.pathname == security.contextPath or externalLoginWindow.location.pathname == "#{security.contextPath}/")
                    $interval.cancel(helperPromise)
                    externalLoginWindow.close()
                    security.requireUser().then (success)->
                      $rootScope.$broadcast 'userLoggedIn', success
                      $modalInstance.close success

              helperPromise = $interval closeWindowWhenLoggedIn, 100

        ]

      }

      dialog.result
  ]

  messagesProvider.setPromptFactory 'login', factory
]