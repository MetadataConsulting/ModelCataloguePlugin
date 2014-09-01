angular.module('mc.core.ui.bs.modalPromptValueDomainEdit', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
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
            <form role="form">
              <div class="form-group">
                <label for="conceptualDomain"> Conceptual Domains</label>
                <elements-as-tags elements="copy.conceptualDomains"></elements-as-tags>
                <input id="conceptualDomain" placeholder="Conceptual Domain" ng-model="conceptualDomain" catalogue-element-picker="conceptualDomain" label="el.name" typeahead-on-select="copy.conceptualDomains.push(conceptualDomain);conceptualDomain = null">
              </div>
              <div class="form-group">
                <label for="name" class="">Name</label>
                <input type="text" class="form-control" id="name" placeholder="Name" ng-model="copy.name">
              </div>
              <div class="form-group">
                <label for="description" class="">Description</label>
                <textarea rows="10" ng-model="copy.description" placeholder="Description" class="form-control" id="description"></textarea>
              </div>
              <div class="form-group">
                <label for="dataType" class="">Data Type</label>
                <input type="text" id="dataType" placeholder="Data Type" ng-model="copy.dataType" catalogue-element-picker="dataType" label="el.name">
              </div>
              <div class="form-group">
                <label for="unitOfMeasure" class="">Unit of Measure</label>
                <input type="text" id="unitOfMeasure" placeholder="Unit of Measure" ng-model="copy.unitOfMeasure" catalogue-element-picker="measurementUnit" label="el.name + ' (' + el.symbol + ')'">
              </div>
              <div class="checkbox">
                <label>
                  <input type="checkbox" ng-model="copy.multiple"> May contain multiple values (e.g. XSD List)
                </label>
              </div>
              <div class="form-group">
                <label for="rule" ng-click="ruleCollapsed = !ruleCollapsed">Rule <span class="glyphicon" ng-class="{'glyphicon-collapse-down': ruleCollapsed, 'glyphicon-collapse-up': !ruleCollapsed}"></span></label>
                <textarea  collapse="ruleCollapsed" rows="10" ng-model="copy.rule" placeholder="Rule" class="form-control" id="rule"></textarea>
              </div>
            </form>
        </div>
        <div class="modal-footer">
          <contextual-actions></contextual-actions>
        </div>
        '''
        controller: ['$scope', 'messages', '$controller', '$modalInstance', ($scope, messages, $controller, $modalInstance) ->
          $scope.newEntity      = -> {conceptualDomains: []}
          $scope.copy           = angular.copy(args.element ? $scope.newEntity())
          $scope.original       = args.element ? $scope.newEntity()
          $scope.messages       = messages.createNewMessages()
          $scope.create         = args.create
          $scope.ruleCollapsed  = true

          angular.extend(this, $controller('saveAndCreateAnotherCtrlMixin', {$scope: $scope, $modalInstance: $modalInstance}))

          $scope.hasChanged   = ->
            return true if $scope.copy.name != $scope.original.name
            return true if $scope.copy.description != $scope.original.description
            return true if $scope.copy.rule != $scope.original.rule
            return true if $scope.copy.multiple != $scope.original.multiple
            return true if $scope.copy.unitOfMeasure?.id != $scope.original.unitOfMeasure?.id
            return true if $scope.copy.dataType?.id != $scope.original.dataType?.id
            return true if not angular.equals($scope.copy.conceptualDomains, $scope.original.conceptualDomains)
            return false
        ]

      }

      dialog.result
  ]

  messagesProvider.setPromptFactory 'edit-valueDomain', factory
]