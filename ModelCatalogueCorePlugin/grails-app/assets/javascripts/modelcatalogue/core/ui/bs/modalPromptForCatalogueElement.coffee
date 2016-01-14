angular.module('mc.core.ui.bs.modalPromptForCatalogueElement', ['mc.util.messages', 'mc.util.ui.focusMe']).config ['messagesProvider', (messagesProvider)->
  messagesProvider.setPromptFactory 'catalogue-element',  [ '$modal', ($modal) ->
    (title, body, args) ->
      dialog = $modal.open {
        windowClass: 'messages-modal-prompt'
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body">
            <form role="form" ng-submit="close(value)">
            <div class="form-group">
                <label for="value">''' + body + '''</label>
                <input id="value" ng-model="value" class="form-control" status="''' + (args.status ? '') + '''" global="''' + (args.global ? false) + '''" catalogue-element-picker="''' + (args.resource ? 'catalogueElement') + '''" focus-me="true">
            </div>
            <fake-submit-button/>
            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" ng-click="close(value)" ng-disabled="!isElementSelected(value)">OK</button>
            <button class="btn btn-warning" ng-click="$dismiss()">Cancel</button>
        </div>
        '''
        controller: ['$scope', 'catalogue', '$modalInstance', ($scope, catalogue, $modalInstance) ->
          $scope.isElementSelected = (value) ->
            catalogue.isInstanceOf(value?.elementType ? 'noType', 'catalogueElement')

          $scope.close = (value) ->
            $modalInstance.close(value) if $scope.isElementSelected(value)

        ]
      }

      dialog.result
  ]
]