angular.module('mc.core.ui.bs.modalPromptDatModelPolicyEdit', ['mc.util.messages', 'mc.util.ui.actions']).config (messagesProvider)->
  factory =  ($uibModal, $q, messages) ->
    "ngInject"
    (title, body, args) ->
      if not args?.element? and not args?.create?
        messages.error('Cannot create edit dialog.', 'The element to be edited is missing.')
        return $q.reject('Missing element argument!')

      dialog = $uibModal.open {
        windowClass: 'basic-edit-modal-prompt'
        size: 'lg'
        template: '''
         <div class="modal-header">
            <h4>''' + (title ? 'Create Data Policy') + '''</h4>
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
                <p class="help-block">Enter valid <a href="http://www.groovy-lang.org/" target="_blank">Groovy</a> code with <a target="_blank" href="https://github.com/MetadataConsulting/ModelCataloguePlugin/blob/2.x/docs/development/recipes/policies.md">Data Model Policy DSL definition</a>. For example you can <a ng-click="requireDataType()"><span class="fa fa-magic"></span> require all data elements to have data type specified</a>, <a ng-click="uniqueName()"><span class="fa fa-magic"></span> all data elements' names to be unique</a> or <a ng-click="regexpName()"><span class="fa fa-magic"></span> all data elements' names to match regular expression</a></p>
              </div>
              <fake-submit-button/>
            </form>
        </div>
        <div class="modal-footer">
            <contextual-actions role="{{::actionRoleAccess.ROLE_MODAL_ACTION}}"></contextual-actions>
        </div>
        '''

        controller: ($scope, messages, $controller, $uibModalInstance, actionRoleAccess) ->
          'ngInject'
          $scope.actionRoleAccess = actionRoleAccess
          $scope.copy     = angular.copy(args.element ? {})
          $scope.create   = args.create
          $scope.original = args.element ? {}
          $scope.messages = messages.createNewMessages()

          angular.extend(this, $controller('saveAndCreateAnotherCtrlMixin', {$scope: $scope, $uibModalInstance: $uibModalInstance}))

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

          REQUIRED_EXAMPLE = """check dataElement property 'dataType' is 'required' otherwise 'Data type is missing for {2}'"""
          UNIQUE_EXAMPLE = """check dataElement property 'name' is 'unique' otherwise 'Data element\'s name is not unique for {2}'"""
          REGEX_EXAMPLE = """check dataElement property 'name' apply regex: '[^_ -]+' otherwise 'Name of {2} contains illegal characters ("_", "-" or " ")'"""

          showExample = (example) ->
            ->
              if $scope.copy.policyText and $scope.copy.policyText != REGEX_EXAMPLE and $scope.copy.policyText != UNIQUE_EXAMPLE and $scope.copy.policyText != REQUIRED_EXAMPLE
                messages.confirm("Replace current rules with example", "Do already have some rules, do you want to replace them with the example?").then ->
                  $scope.copy.policyText = example
              else
                $scope.copy.policyText = example


          $scope.requireDataType = showExample(REQUIRED_EXAMPLE)
          $scope.uniqueName = showExample(UNIQUE_EXAMPLE)
          $scope.regexpName = showExample(REGEX_EXAMPLE)

      }

      dialog.result

  messagesProvider.setPromptFactory 'edit-dataModelPolicy', factory
