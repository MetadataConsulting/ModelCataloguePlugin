angular.module('mc.core.ui.bs.modalPromptMeasurementUnitEdit', ['mc.util.messages', 'mc.core.ui.bs.withClassificationCtrlMixin']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$modal', '$q', 'messages', ($modal, $q, messages) ->
    (title, body, args) ->
      if not args?.element? and not args?.create?
        messages.error('Cannot create relationship dialog.', 'The element to be edited is missing.')
        return $q.reject('Missing element argument!')

      dialog = $modal.open {
        windowClass: 'basic-edit-modal-prompt'
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body">
            <messages-panel messages="messages"></messages-panel>
            <form role="form" ng-submit="saveElement()">
              <div class="form-group">
                <label for="classification"> Classifications</label>
                <elements-as-tags elements="copy.classifications"></elements-as-tags>
                <input id="classification" placeholder="Classification" ng-model="pending.classification" catalogue-element-picker="classification" label="el.name" typeahead-on-select="addToClassifications()">
              </div>
              <div class="form-group">
                <label for="name" class="">Name</label>
                <input type="text" class="form-control" id="name" placeholder="Name" ng-model="copy.name">
              </div>
              <div class="form-group">
                <label for="symbol" class="">Symbol</label>
                <input type="symbol" class="form-control" id="symbol" placeholder="Symbol" ng-model="copy.symbol">
              </div>
              <div class="form-group">
                <label for="modelCatalogueId" class="">Catalogue ID (URL)</label>
                <input type="text" class="form-control" id="modelCatalogueId" placeholder="e.g. external ID, namespace (leave blank for generated)" ng-model="copy.modelCatalogueId">
              </div>
              <div class="form-group">
                <label for="description" class="">Description</label>
                <textarea rows="10" ng-model="copy.description" placeholder="Description" class="form-control" id="description"></textarea>
              </div>
            </form>
        </div>
        <div class="modal-footer">
          <contextual-actions role="modal"></contextual-actions>
        </div>
        '''
        controller: ['$scope', 'messages', '$controller', '$modalInstance', ($scope, messages, $controller, $modalInstance) ->
          $scope.copy     = angular.copy(args.element ? {classifications: []})
          $scope.original = args.element ? {}
          $scope.messages = messages.createNewMessages()
          $scope.create   = args.create

          $scope.hasChanged   = ->
            $scope.copy.name != $scope.original.name or $scope.copy.description != $scope.original.description or $scope.copy.symbol != $scope.original.symbol or $scope.copy.modelCatalogueId != $scope.original.modelCatalogueId or not angular.equals($scope.original.classifications ? {}, $scope.copy.classifications ? {})

          angular.extend(this, $controller('withClassificationCtrlMixin', {$scope: $scope}))
          angular.extend(this, $controller('saveAndCreateAnotherCtrlMixin', {$scope: $scope, $modalInstance: $modalInstance}))

        ]

      }

      dialog.result
  ]

  messagesProvider.setPromptFactory 'edit-measurementUnit', factory
]