angular.module('mc.core.ui.bs.modalPromptValidationRuleEdit', ['mc.util.messages', 'mc.core.ui.bs.withClassificationCtrlMixin']).config (messagesProvider)->
  factory = ($modal, $q, messages) ->
    "ngInject"
    (title, body, args) ->
      if not args?.element? and not args?.create?
        messages.error('Cannot create relationship dialog.', 'The element to be edited is missing.')
        return $q.reject('Missing element argument!')

      dialog = $modal.open {
        windowClass: 'basic-edit-modal-prompt'
        size: 'lg'
        templateUrl: '/mc/core/ui/modals/modalValidationRuleEdit.html'
        controller: ($scope, messages, $controller, $modalInstance) ->
          $scope.title      = args.title
          $scope.pending    = {dataModel: null}
          $scope.newEntity  = -> {dataModels: $scope.copy?.dataModels ? [], dataClasses: $scope.copy?.dataClasses ? [], dataElements: $scope.copy?.dataElements ? []}
          $scope.copy     = angular.copy(args.element ? $scope.newEntity())
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
          angular.extend(this, $controller('saveAndCreateAnotherCtrlMixin', {$scope: $scope, $modalInstance: $modalInstance}))

      }

      dialog.result

  messagesProvider.setPromptFactory 'edit-validationRule', factory
