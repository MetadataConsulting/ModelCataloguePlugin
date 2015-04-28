angular.module('mc.core.ui.bs.modalPromptEnumeratedTypeEdit', ['mc.util.messages', 'mc.core.ui.bs.withClassificationCtrlMixin']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$modal', '$q', 'messages', ($modal, $q, messages) ->
    (title, body, args) ->
      if not args?.element? and not args?.create?
        messages.error('Cannot create edit dialog.', 'The element to be edited is missing.')
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
                <label for="modelCatalogueId" class="">Catalogue ID (URL)</label>
                <input type="text" class="form-control" id="modelCatalogueId" placeholder="e.g. external ID, namespace (leave blank for generated)" ng-model="copy.modelCatalogueId">
              </div>
              <div class="form-group">
                <label for="description" class="">Description</label>
                <textarea rows="10" ng-model="copy.description" placeholder="Description" class="form-control" id="description"></textarea>
              </div>
            </form>
            <div class="checkbox">
              <label>
                <input type="checkbox" ng-model="settings.enumerated" ng-disabled="!create" id="enumerated"> Enumerated
              </label>
            </div>
            <div collapse="!settings.enumerated"><ordered-map-editor object="copy.enumerations" title="Enumerations" key-placeholder="Value" value-placeholder="Description"></ordered-map-editor></div>
        </div>
        <div class="modal-footer">
          <contextual-actions role="modal"></contextual-actions>
        </div>
        '''
        controller: ['$scope', 'messages', '$controller', '$modalInstance', 'enhance', ($scope, messages, $controller, $modalInstance, enhance) ->
          orderedMapEnhancer = enhance.getEnhancer('orderedMap')

          $scope.newEntity = -> {enumerations: orderedMapEnhancer.emptyOrderedMap(), classifications: []}
          $scope.copy     = angular.copy(args.element ? $scope.newEntity())
          $scope.original = args.element ? $scope.newEntity()
          $scope.messages = messages.createNewMessages()
          $scope.create   = args.create

          $scope.settings = {enumerated: args.create == 'enumeratedType' || args?.element?.isInstanceOf('enumeratedType')}

          if $scope.create
            $scope.$watch 'settings.enumerated', (enumerated) ->
              if enumerated
                $scope.create = 'enumeratedType'
              else
                $scope.create = 'dataType'

          angular.extend(this, $controller('withClassificationCtrlMixin', {$scope: $scope}))
          angular.extend(this, $controller('saveAndCreateAnotherCtrlMixin', {$scope: $scope, $modalInstance: $modalInstance}))

          $scope.hasChanged   = ->
            $scope.copy.name != $scope.original.name or $scope.copy.description != $scope.original.description or $scope.copy.modelCatalogueId != $scope.original.modelCatalogueId or not angular.equals($scope.original.enumerations ? {}, $scope.copy.enumerations ? {}) or not angular.equals($scope.original.classifications ? {}, $scope.copy.classifications ? {})



        ]

      }

      dialog.result
  ]

  messagesProvider.setPromptFactory 'edit-dataType', factory
  messagesProvider.setPromptFactory 'edit-enumeratedType', factory
]