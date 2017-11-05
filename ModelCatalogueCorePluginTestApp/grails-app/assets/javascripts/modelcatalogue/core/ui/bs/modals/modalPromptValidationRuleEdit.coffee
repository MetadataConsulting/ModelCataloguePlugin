angular.module('mc.core.ui.bs.modalPromptValidationRuleEdit', ['mc.util.messages', 'mc.core.ui.bs.withClassificationCtrlMixin', 'mc.util.ui.actions']).config (messagesProvider)->
  factory = ($uibModal, $q, messages) ->
    "ngInject"
    (title, body, args) ->
      if not args?.element? and not args?.create?
        messages.error('Cannot create relationship dialog.', 'The element to be edited is missing.')
        return $q.reject('Missing element argument!')

      dialog = $uibModal.open {
        windowClass: 'basic-edit-modal-prompt'
        size: 'lg'
        templateUrl: '/mc/core/ui/modals/modalValidationRuleEdit.html'
        controller: ($scope, messages, $controller, $uibModalInstance, actionRoleAccess) ->
          'ngInject'
          $scope.actionRoleAccess = actionRoleAccess
          $scope.title      = args.title
          $scope.pending    = {dataModel: null}
          $scope.newEntity  = -> {dataModels: $scope.copy?.dataModels ? [], dataClasses: $scope.copy?.dataClasses ? [], dataElements: $scope.copy?.dataElements ? []}
          $scope.copy     = angular.copy(args.element ? $scope.newEntity())
          $scope.copy.name = args.name ? $scope.copy.name
          $scope.original = args.element ? {}
          $scope.messages = messages.createNewMessages()
          $scope.create   = args.create
          $scope.currentDataModel = args.currentDataModel

          $scope.hasChanged   = ->
            [
              'name'
              'description'
              'modelCatalogueId'
              'component'
              'ruleFocus'
              'trigger'
              'rule'
              'errorCondition'
              'issueRecord'
              'notification'
              'notificationTarget'
            ].some (name) -> $scope.copy[name] != $scope.original[name]

          angular.extend(this, $controller('withClassificationCtrlMixin', {$scope: $scope}))
          angular.extend(this, $controller('saveAndCreateAnotherCtrlMixin', {$scope: $scope, $uibModalInstance: $uibModalInstance}))

      }

      dialog.result

  messagesProvider.setPromptFactory 'edit-validationRule', factory
