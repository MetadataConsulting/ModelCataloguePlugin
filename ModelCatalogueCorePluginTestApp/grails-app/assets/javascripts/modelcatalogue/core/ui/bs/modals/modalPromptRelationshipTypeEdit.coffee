angular.module('mc.core.ui.bs.modalPromptRelationshipTypeEdit', ['mc.util.messages', 'mc.util.ui.actions']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$uibModal', '$q', 'messages', ($uibModal, $q, messages) ->
    (title, body, args) ->
      if not args?.element? and not args?.create?
        messages.error('Cannot create edit dialog.', 'The element to be edited is missing.')
        return $q.reject('Missing element argument!')

      dialog = $uibModal.open {
        windowClass: 'basic-edit-modal-prompt'
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body">
            <messages-panel messages="messages"></messages-panel>
            <form role="form" ng-submit="saveElement()">
              <div class="form-group">
                <label for="name" class="">Name</label>
                <input type="text" class="form-control" id="name" placeholder="Name" ng-model="copy.name">
              </div>
              <div class="form-group">
                <label for="sourceToDestination" class="">Source to Destination</label>
                <input type="text" class="form-control" id="sourceToDestination" placeholder="Source to Destination" ng-model="copy.sourceToDestination">
              </div>
              <div class="form-group">
                <label for="destinationToSource" class="">Destination to Source</label>
                <input type="text" class="form-control" id="destinationToSource" placeholder="Destination to Source" ng-model="copy.destinationToSource">
              </div>
              <div class="form-group">
                <label for="sourceClass" class="">Source Class</label>
                <input type="text" ng-disabled="!create" class="form-control" id="sourceClass" placeholder="Source Class" ng-model="copy.sourceClass" typeahead="class for class in elementClasses | filter:$viewValue | limitTo:8">
              </div>
              <div class="form-group">
                <label for="destinationClass" class="">Destination Class</label>
                <input type="text" ng-disabled="!create" class="form-control" id="destinationClass" placeholder="Destination Class" ng-model="copy.destinationClass" typeahead="class for class in elementClasses | filter:$viewValue | limitTo:8">
              </div>
              <div class="checkbox">
                <label>
                  <input type="checkbox" ng-model="copy.system"> System
                </label>
              </div>
              <div class="checkbox">
                <label>
                  <input type="checkbox" ng-model="copy.versionSpecific"> Version Specific
                </label>
              </div>
              <div class="checkbox">
                <label>
                  <input type="checkbox" ng-model="copy.bidirectional"> Bidirectional
                </label>
              </div>
              <div class="checkbox">
                <label>
                  <input type="checkbox" ng-model="copy.searchable"> Searchable
                </label>
              </div>
              <div class="form-group">
                <label for="sourceToDestinationDescription">Source to Destination Description</label>
                <textarea rows="10" ng-model="copy.sourceToDestinationDescription" placeholder="Source to Destination Description" class="form-control" id="sourceToDestinationDescription"></textarea>
              </div>
              <div class="form-group">
                <label for="destinationToSourceDescription">Destination to Source Description</label>
                <textarea rows="10" ng-model="copy.destinationToSourceDescription" placeholder="Destination to Source Description" class="form-control" id="destinationToSourceDescription"></textarea>
              </div>
              <div class="form-group">
                <label for="rule" ng-click="ruleCollapsed = !ruleCollapsed">Rule <span class="glyphicon" ng-class="{'glyphicon-collapse-down': ruleCollapsed, 'glyphicon-collapse-up': !ruleCollapsed}"></span></label>
                <textarea uib-collapse="ruleCollapsed" rows="10" ng-model="copy.rule" placeholder="Rule" class="form-control" id="rule"></textarea>
              </div>
              <fake-submit-button/>
            </form>
        </div>
        <div class="modal-footer">
            <contextual-actions role="{{::actionRoleAccess.ROLE_MODAL_ACTION}}"></contextual-actions>
        </div>
        '''

        resolve:
          elementClasses: ['$http', 'modelCatalogueApiRoot', ($http, modelCatalogueApiRoot) ->
            $http.get("#{modelCatalogueApiRoot}/relationshipType/elementClasses").then (response) -> response.data
          ]

        controller: ['$scope', 'messages', '$controller', '$uibModalInstance', 'elementClasses', 'actionRoleAccess', ($scope, messages, $controller, $uibModalInstance, elementClasses, actionRoleAccess) ->
          $scope.actionRoleAccess = actionRoleAccess
          $scope.copy     = angular.copy(args.element ? {})
          $scope.create   = args.create
          $scope.original = args.element ? {}
          $scope.messages = messages.createNewMessages()
          $scope.ruleCollapsed = true
          $scope.elementClasses = elementClasses

          angular.extend(this, $controller('saveAndCreateAnotherCtrlMixin', {$scope: $scope, $uibModalInstance: $uibModalInstance}))

          $scope.hasChanged   = ->
            for prop in ['name', 'sourceToDestination', 'destinationToSource', 'sourceClass', 'destinationClass',
              'system', 'bidirectional', 'searchable', 'rule', 'versionSpecific', 'sourceToDestinationDescription',
              'destinationToSourceDescription']
              return true if !angular.equals($scope.copy[prop], $scope.original[prop])
            return false

          $scope.beforeSave = ->

          $scope.validate = ->
            if not $scope.copy.name
              $scope.messages.error 'Empty Name', 'Please fill the name'
              return false

            if not $scope.copy.sourceToDestination
              $scope.messages.error 'Empty Source to Destination', 'Please fill the Source to Destination'
              return false

            if not $scope.copy.destinationToSource
              $scope.messages.error 'Empty Destination to Source', 'Please fill the Destination to Source'
              return false

            if not $scope.copy.sourceClass
              $scope.messages.error 'Empty Source Class', 'Please fill the Source Class'
              return false

            if not $scope.copy.destinationClass
              $scope.messages.error 'Empty Destination Class', 'Please fill the Destination Class'
              return false
            return true
        ]

      }

      dialog.result
  ]

  messagesProvider.setPromptFactory 'edit-relationshipType', factory

]
