angular.module('mc.core.ui.bs.modalPromptValidateValue', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$uibModal', '$q', 'messages', ($uibModal, $q, messages) ->
    (title, body, args) ->

      dialog = $uibModal.open {
        windowClass: 'basic-edit-modal-prompt'
        template: '''
         <div class="modal-header">
            <button type="button" class="close" ng-click="$dismiss()"><span aria-hidden="true">&times;</span><span class="sr-only">Cancel</span></button>
            <h4>Validate Value<span ng-show="args.domain"> by <strong>{{args.domain.name}}</strong> domain</span></h4>
        </div>
        <div class="modal-body">
            <messages-panel messages="messages"></messages-panel>
            <form role="form" ng-submit="validate()">
              <div class="form-group" ng-hide="args.domain">
                <label for="type">Data Type</label>
                <input type="text" id="type" placeholder="Data Type" ng-model="definition.domain" catalogue-element-picker="dataType" label="el.name" typeahead-on-select="validate()">
              </div>
              <div class="form-group">
                <label for="value">Value</label>
                <input type="text" class="form-control" id="value" placeholder="Value" ng-model="definition.value" ng-change="validate()">
              </div>
              <fake-submit-button/>
            </form>
        </div>
        <div class="modal-footer">
          <alert type="success" ng-show="result === true">VALID</alert>
          <alert type="danger"  ng-show="result === false">INVALID</alert>
          <alert type="warning" ng-hide="result === false || result === true">{{result ? result : defaultResult}}</alert>
        </div>
        '''
        controller: [ '$scope', 'rest', 'modelCatalogueApiRoot', ($scope, rest, modelCatalogueApiRoot) ->
          $scope.args = args

          $scope.definition =
            domain: args.domain ? args.domainHint
            value: args.value

          $scope.messages = messages.createNewMessages()

          $scope.defaultResult = 'Please enter value.'

          $scope.validate = ->
            if $scope.definition.domain and not angular.isString($scope.definition.domain) and $scope.definition.value
              rest(method: 'GET', url: "#{modelCatalogueApiRoot}#{$scope.definition.domain.link}/validateValue", params: {value: $scope.definition.value}).then (result) ->
                $scope.result = result.result
              , ->
                $scope.result = $scope.defaultResult
                $scope.messages.error "Error happened during the conversion. See application log for details"
            else
              $scope.result = $scope.defaultResult

          $scope.validate()
        ]

      }

      dialog.result
  ]

  messagesProvider.setPromptFactory 'validate-value-by-domain', factory
]
