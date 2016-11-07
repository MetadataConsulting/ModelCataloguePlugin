angular.module('mc.core.ui.bs.modalPromptCloneIntoDataModel', ['mc.util.messages', 'mc.util.ui.focusMe']).config ['messagesProvider', (messagesProvider)->
  messagesProvider.setPromptFactory 'clone-into',  [ '$uibModal', ($uibModal) ->
    (title, body, args) ->
      dialog = $uibModal.open {
        windowClass: 'messages-modal-prompt'
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body">
            <form role="form" ng-submit="close(value)">
            <div class="form-group">
                <label for="value">Please, select the element to be cloned</label>
                <input id="value" ng-model="value" class="form-control"  global="true" catalogue-element-picker="catalogueElement" focus-me="true">
            </div>
            <p class="helper-text" ng-if="dependents">
              <strong>{{value.name}}</strong> belongs to data model <strong>{{value.dataModel.name}}</strong> which contains dependencies to other data model<span ng-if="dependents.length > 1">s</span>:
              <strong>{{dependents.join(', ')}}</strong>. If <strong>{{value.name}}</strong> depends on elements from these data models they will be imported
              automatically.
            </p>
            <fake-submit-button/>
            </form>
        </div>
        <div class="modal-footer">
              <button class="btn btn-primary" ng-click="close(value)" ng-disabled="!isElementSelected(value)">OK</button>
            <button class="btn btn-warning" ng-click="$dismiss()">Cancel</button>
        </div>
        '''
        controller: ['$scope', 'catalogue', '$uibModalInstance', ($scope, catalogue, $uibModalInstance) ->
          $scope.value = args.value
          $scope.isElementSelected = (value) ->
            not angular.isString(value) and catalogue.isInstanceOf(value?.elementType ? 'noType', 'catalogueElement')
          $scope.close = (value) ->
            $uibModalInstance.close(value) if $scope.isElementSelected(value)
          $scope.$watch 'value', (newValue) ->
            if $scope.isElementSelected(newValue) and newValue.dataModel and newValue.dataModel.link != args.currentDataModel.link
              newValue.dataModel.execute('dependents').then (dependents) ->
                if dependents?.length > 0
                  $scope.dependents = []
                  angular.forEach dependents, (dataModel) ->
                    $scope.dependents.push(dataModel.name)
                else
                  $scope.dependents = null
        ]
      }

      dialog.result
  ]
]
