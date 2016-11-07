angular.module('mc.core.ui.bs.modalPromptConvert', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$uibModal', '$q', 'messages', ($uibModal, $q, messages) ->
    (title, body, args) ->

      dialog = $uibModal.open {
        windowClass: 'basic-edit-modal-prompt'
        template: '''
         <div class="modal-header">
            <button type="button" class="close" ng-click="$dismiss()"><span aria-hidden="true">&times;</span><span class="sr-only">Cancel</span></button>
            <h4>Convert Value<span ng-show="args.source"> from <strong>{{args.source.name}}</strong></span><span ng-show="args.destination"> to <strong>{{args.destination.name}}</strong></span></h4>
        </div>
        <div class="modal-body">
            <messages-panel messages="messages"></messages-panel>
            <form role="form" ng-submit="convert()">
              <div class="form-group" ng-hide="args.source">
                <label for="source">From</label>
                <input type="text" id="source" placeholder="Source Data Type" ng-model="definition.source" catalogue-element-picker="dataType" label="el.name" typeahead-on-select="convert()">
              </div>
              <div class="form-group" ng-hide="args.destination">
                <label for="destination">To</label>
                <select class="form-control" ng-model="definition.destination" ng-change="convert()" ng-options="dest as dest.name for dest in destinations" ng-disabled="destinations.length < 2"></select>
              </div>
              <div class="form-group">
                <label for="value">Value</label>
                <input type="text" class="form-control" id="value" placeholder="Value" ng-model="definition.value" ng-change="convert()">
              </div>
              <fake-submit-button/>
            </form>
        </div>
        <div class="modal-footer">
          <pre>{{result ? result : defaultResult}}</pre>
        </div>
        '''
        controller: [ '$scope', 'rest', 'modelCatalogueApiRoot', ($scope, rest, modelCatalogueApiRoot) ->
          $scope.args = args

          $scope.definition =
            source: args.source ? args.sourceHint
            destination: args.destination
            value: args.value

          $scope.messages = messages.createNewMessages()

          $scope.defaultResult = 'Please enter value.'

          $scope.destinations = []

          appendToDestinations = (result) ->
            for mapping in result.list
              $scope.destinations.push mapping.destination

            if result.next.size > 0
              result.next().then appendToDestinations
            else if $scope.destinations.length > 0
              $scope.definition.destination = $scope.destinations[0]
              $scope.convert()


          onSourceSelected = (source) ->
            $scope.destinations = []
            return if not source or not source.mappings
            source.mappings().then appendToDestinations

          $scope.$watch 'definition.source', onSourceSelected

          $scope.convert = ->
            if $scope.definition.source and not angular.isString($scope.definition.source) and $scope.definition.destination and not angular.isString($scope.definition.destination) and $scope.definition.value
              rest(method: 'GET', url: "#{modelCatalogueApiRoot}#{$scope.definition.source.link}/convert/#{$scope.definition.destination.id}", params: {value: $scope.definition.value}).then (result) ->
                $scope.result = "#{result.result}"
              , (response) ->
                $scope.result = $scope.defaultResult
                if response.status == 404
                  $scope.messages.error "No mapping found for given source and destination"
                else
                  $scope.messages.error "Error happened during the conversion. See application log for details"
            else
              $scope.result = $scope.defaultResult

          $scope.convert()
        ]

      }

      dialog.result
  ]

  messagesProvider.setPromptFactory 'convert-with-value-domain', factory
]
