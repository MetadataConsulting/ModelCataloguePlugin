module = angular.module('mc.core.ui.bs.modalSearchForActions', ['mc.util.messages', 'mc.util.ui.focusMe'])
module.config ['messagesProvider', (messagesProvider)->
  messagesProvider.setPromptFactory 'search-action',  [ '$uibModal', 'names',  ($uibModal) ->
    ->
      dialog = $uibModal.open {
        size: 'lg'
        template: """
        <div class="modal-body" ng-keydown="keydown($event)">
            <div class="search-lg">
              <div class="input-group input-group-lg">
                <span class="input-group-addon"><span class="fa fa-fw fa-flash"></span></span>
                <input id="value" class="form-control" ng-model="query" placeholder="Search for Actions and Views" ng-model-options="{debounce: 500}" focus-me="true" autofocus='true'>
                <div class="input-group-addon with-pointer" ng-click='$dismiss()'><span class='fa fa-fw fa-close'></span></div>
              </div>
            </div>
            <div ng-if="actions.length == 0 &amp;&amp; !loading">
              <div class="leave-10-before"></div>
              <div uib-alert class="alert alert-warning">No Results</div>
            </div>
            <div>
              <div class class="list-group">
                <div class="leave-10-before"></div>
                <a ng-repeat="action in actions | limitTo:10" class="list-group-item with-pointer item-found" ng-class="{'active': $index == selected}" ng-click="performAction(action)">
                    <h4 class="list-group-item-heading"><span class="fa fa-fw" ng-class="getIcon(action)"></span> {{action.label}}</h4>
                </a>
                <a class="list-group-item disabled" ng-if="actions.length > 10">
                  <div class="text-center"><span class="fa fa-search"></span> Start typing to show more actions...</div>
                </a>
              </ul>
            </div>
        </div>
        """

        controller: ['$scope', 'actions', 'actionRoleAccess', '$uibModalInstance', '$filter', ($scope, actions, actionRoleAccess, $uibModalInstance, $filter) ->
          $scope.selected = -1
          $scope.actions = actions.getActions($scope, actionRoleAccess.ROLE_GLOBAL_ACTION)

          $scope.getIcon = (action) ->
            action.icon ? 'fa-flash'

          $scope.performAction = (action) ->
            $uibModalInstance.close(action.run())

          queryFilter = (value) ->
            return true if not $scope.query
            return value.label?.toLowerCase().indexOf($scope.query.toLowerCase()) > -1

          $scope.$watch 'query', ->
            $scope.actions = $filter('filter')(actions.getActions($scope, actionRoleAccess.ROLE_GLOBAL_ACTION), queryFilter)



          ARROW_DOWN = 40
          ARROW_UP   = 38
          ENTER      = 13

          $scope.keydown = ($event) ->
            if $event.keyCode == ARROW_UP
              $scope.selected = Math.max($scope.selected - 1, 0)
            else if $event.keyCode == ARROW_DOWN
              if $scope.selected < $scope.actions.length - 1
                $scope.selected = $scope.selected + 1
            else if $event.keyCode == ENTER and $scope.selected >= 0
              $scope.performAction $scope.actions[$scope.selected]

            $scope.$evalAsync ->
              return unless $
              element = angular.element('.list-group-item.with-pointer.item-found.active')
              if element.length
                $('.modal').scrollTop(element[0].offsetTop - 100);

        ]
      }

      dialog.result
  ]
]
