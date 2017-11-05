angular.module('mc.core.ui.bs.modalNewVersion', ['mc.util.messages', 'mc.util.ui.actions']).config ['messagesProvider', (messagesProvider)->

  factory = [ '$uibModal', '$q', 'messages', ($uibModal, $q, messages) ->
    (title, body, args) ->
      if not args?.element?
        messages.error('Cannot create edit dialog.', 'The element to be edited is missing.')
        return $q.reject('Missing element argument!')

      dialog = $uibModal.open {
        template: '''
         <div class="modal-header">
            <h4>''' + (title ? 'New Version of Data Model') + '''</h4>
        </div>
        <div class="modal-body">
            <p>Do you want to create new version of <strong>''' + args.element.name + '''</strong>?</p>
            <p><small>New version of ''' + args.element.name + ''' will be created. The semantic version provided can be changed once more when the data model is finalized. Current semantic version is <strong>''' + args.element.getSemanticVersion() + '''</strong>.</small></p>
            <messages-panel messages="messages"></messages-panel>
            <form role="form" ng-submit="createDraftVersion()">
              <div class="form-group">
                <label for="semanticVersion" class="">Semantic Version</label>
                <input type="text" class="form-control" id="semanticVersion" placeholder="Semantic Version e.g. 1.2.3" ng-model="semanticVersion">
              </div>
              <label ng-if="dependents.length">Prefer drafts for following dependent data models:</label>
              <div class="checkbox" ng-repeat="dependent in dependents track by dependent.id">
                <label>
                  <input type="checkbox" ng-model="preferDrafts[dependent.latestVersionId]">
                  {{dependent.name}}
                </label>
              </div>

              <fake-submit-button/>
            </form>
        </div>
        <div class="modal-footer">
          <contextual-actions role="{{::actionRoleAccess.ROLE_MODAL_ACTION}}"></contextual-actions>
        </div>
        '''

        resolve:
          dependents: ->
            args.element.execute('dependents')

        controller: ($rootScope, $scope, messages, $uibModalInstance, dependents, actionRoleAccess) ->
          'ngInject'
          $scope.actionRoleAccess = actionRoleAccess
          collectDraftPreference = (preferDrafts) ->
            result = []
            angular.forEach preferDrafts, (value, key) ->
              result.push key if value
            result.join(',')

          $scope.semanticVersion = null
          $scope.messages = messages.createNewMessages()

          $scope.dependents = dependents
          $scope.preferDrafts = {}

          angular.forEach $scope.dependents, (dependent) ->
            $scope.preferDrafts[dependent.latestVersionId] = true

          $scope.$dismiss = $uibModalInstance.dismiss

          $scope.createDraftVersion = ->
            $scope.pending = true
            args.element.execute('newVersion', 'POST', semanticVersion: $scope.semanticVersion, preferDrafts: collectDraftPreference($scope.preferDrafts)).then (updated) ->
              args.element.updateFrom  updated
              messages.prompt('Draft progress', null, type: 'feedback', id: args.element.id).then ->
                $uibModalInstance.close(updated)
                $scope.pending = false
            , (response) ->
              $scope.pending = false
              $scope.messages.showErrorsFromResponse(response)

      }

      dialog.result
  ]

  messagesProvider.setPromptFactory 'new-version', factory
]
