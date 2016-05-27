angular.module('mc.core.ui.bs.modalPromptDatModelPolicyEdit', ['mc.util.messages']).config (messagesProvider)->
  factory =  ($modal, $q, messages) ->
    "ngInject"
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
                <label for="name" class="">Name</label>
                <input type="text" class="form-control" id="name" placeholder="Name" ng-model="copy.name">
              </div>
              <div class="form-group">
                <label for="policyText">Policy Text</label>
                <textarea rows="10" ng-model="copy.policyText" placeholder="Policy Text" class="form-control" id="policyText"></textarea>
              </div>
              <fake-submit-button/>
            </form>
        </div>
        <div class="modal-footer">
            <contextual-actions role="modal"></contextual-actions>
        </div>
        '''

        controller: ($scope, messages, $controller, $modalInstance) ->
          $scope.copy     = angular.copy(args.element ? {})
          $scope.create   = args.create
          $scope.original = args.element ? {}
          $scope.messages = messages.createNewMessages()

          angular.extend(this, $controller('saveAndCreateAnotherCtrlMixin', {$scope: $scope, $modalInstance: $modalInstance}))

          $scope.hasChanged   = ->
            for prop in ['name', 'policyText']
              return true if !angular.equals($scope.copy[prop], $scope.original[prop])
            return false

          $scope.beforeSave = ->

          $scope.validate = ->
            if not $scope.copy.name
              $scope.messages.error 'Empty Name', 'Please fill the name'
              return false

            if not $scope.copy.policyText
              $scope.messages.error 'Empty Policy Text', 'Please fill the Policy Text'
              return false
            return true

      }

      dialog.result

  messagesProvider.setPromptFactory 'edit-dataModelPolicy', factory
