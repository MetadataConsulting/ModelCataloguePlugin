angular.module('mc.core.ui.bs.modalPromptLogin', ['mc.util.messages', 'ngCookies']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$uibModal', 'messages', 'security', ($uibModal, messages, security) ->
    ->
      dialog = $uibModal.open {
        windowClass: 'login-modal-prompt'
        template: '''
         <div class="modal-header">
            <h4>Login</h4>
        </div>
        <div class="modal-body">
            <messages-panel messages="messages"></messages-panel>
            <div ng-if="providers.length">
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
                <p class="help-block" ng-if="canResetPassword"><a ng-href="{{forgotPasswordLink}}">Forgot Password?</a></p>
              </div>
              <div class="checkbox">
                <label>
                  <input type="checkbox" ng-model="user.rememberMe"> Remember Me
                </label>
              </div>
              <fake-submit-button/>
            </form>
        </div>
        <div class="modal-footer">
            <button type="submit" class="btn btn-success" ng-click="login()" ng-disabled="!user.username || !user.password"><span class="glyphicon glyphicon-ok"></span> Login</button>
            <button class="btn btn-warning" ng-click="$dismiss()">Cancel</button>
        </div>
        '''
        controller: ['$scope', '$cookies', 'messages', 'security', '$uibModalInstance', 'names', '$window', '$interval', '$rootScope',
          ($scope, $cookies, messages, security, $uibModalInstance, names, $window, $interval, $rootScope) ->
            $scope.user = {rememberMe: $cookies.mc_remember_me == "true"}
            $scope.messages = messages.createNewMessages()
            $scope.providers = security.oauthProviders
            $scope.names = names
            $scope.contextPath = security.contextPath
            $scope.forgotPasswordLink = "#{security.contextPath}/register/forgotPassword"
            $scope.canResetPassword = security.canResetPassword

            onSuccess = (success) ->
              if success.data.error
                $scope.messages.error success.data.error
              else if success.data.errors
                for error in success.data.errors
                  $scope.messages.error error
              else
                $cookies.mc_remember_me = $scope.user.rememberMe
                $uibModalInstance.close success

            onFailure = (failure) ->
              if failure.data.error
                $scope.messages.error failure.data.error
              else if failure.data.errors
                for error in failure.data.errors
                  $scope.messages.error error

            $scope.login = ->
              security.login($scope.user.username, $scope.user.password, $scope.user.rememberMe).then(onSuccess, onFailure)

            $scope.loginExternal = (provider) ->
              url = security.contextPath  + '/oauth/' + provider + '/authenticate'
              externalLoginWindow = $window.open(url, 'mc_external_login', 'menubar=no,status=no,titlebar=no,toolbar=no')

              helperPromise = null


              closeWindowWhenLoggedIn = ->
                if externalLoginWindow.closed
                  $uibModalInstance.dismiss()
                  $interval.cancel(helperPromise)
                  $uibModalInstance.dismiss()
                  return
                try # mute cross origin errors when redirected login page
                  if $window.location.host is externalLoginWindow.location.host and (externalLoginWindow.location.pathname == security.contextPath or externalLoginWindow.location.pathname == "#{security.contextPath}/")
                    $interval.cancel(helperPromise)
                    externalLoginWindow.close()
                    security.requireUser().then (success)->
                      $rootScope.$broadcast 'userLoggedIn', success
                      $uibModalInstance.close success

              helperPromise = $interval closeWindowWhenLoggedIn, 100

        ]

      }

      dialog.result
  ]

  messagesProvider.setPromptFactory 'login', factory
]
