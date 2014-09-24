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
            <form role="form" ng-submit="saveElement()">
              <div class="form-group">
                <label for="conceptualDomain"> Conceptual Domains</label>
                <elements-as-tags elements="copy.conceptualDomains"></elements-as-tags>
                <input id="conceptualDomain" placeholder="Conceptual Domain" ng-model="pending.conceptualDomain" catalogue-element-picker="conceptualDomain" label="el.name" typeahead-on-select="copy.conceptualDomains.push(pending.conceptualDomain);pending.conceptualDomain = null">
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
                <div collapse="ruleCollapsed" >
                  <textarea rows="10" ng-model="copy.rule" placeholder="Rule" class="form-control" id="rule"></textarea>
                  <p class="help-block">Enter valid <a href="http://www.groovy-lang.org/" target="_blank">Groovy</a> code. Variable <code>x</code> refers to the value validated value and  <code>domain</code> to current value domain. Last row is the result which should be <code>boolean</code> value. For example you can <a ng-click="showRegexExample()"><span class="fa fa-magic"></span> validate using regular expression</a> or <a ng-click="showSetExample()"><span class="fa fa-magic"></span> values in set</a></p>
                </div>
              </div>
            </form>
        </div>
        <div class="modal-footer">
          <contextual-actions></contextual-actions>
        </div>
        '''
        controller: ['$scope', 'messages', '$controller', '$modalInstance', 'catalogueElementResource', ($scope, messages, $controller, $modalInstance, catalogueElementResource) ->
          $scope.newEntity      = -> {conceptualDomains: $scope.copy?.conceptualDomains ? []}
          $scope.copy           = angular.copy(args.element ? $scope.newEntity())
          $scope.original       = args.element ? $scope.newEntity()
          $scope.messages       = messages.createNewMessages()
          $scope.pending        = {}
          $scope.create         = args.create
          $scope.ruleCollapsed  = not $scope.copy.rule

          REGEX_EXAMPLE = """// value is decimal number
x ==~ /\\d+(\\.\\d+)?/
"""
          SET_EXAMPLE = """// value is one of predefined values
x in ['apple', 'banana', 'cherry']
"""

          showExample = (example) ->
            ->
              if $scope.copy.rule and $scope.copy.rule != REGEX_EXAMPLE and $scope.copy.rule != SET_EXAMPLE
                messages.confirm("Replace current rule with example", "Do already have some rule, do you want to replace it with the example?").then ->
                  $scope.copy.rule = example
              else
                $scope.copy.rule = example


          $scope.showRegexExample = showExample(REGEX_EXAMPLE)
          $scope.showSetExample = showExample(SET_EXAMPLE)

          angular.extend(this, $controller('saveAndCreateAnotherCtrlMixin', {$scope: $scope, $modalInstance: $modalInstance}))

          $scope.beforeSave = ->
            promise = $q.when {}

            if $scope.pending.conceptualDomain and angular.isString($scope.pending.conceptualDomain)
              promise = promise.then -> catalogueElementResource('conceptualDomain').save({name: $scope.pending.conceptualDomain}).then (newDomain) ->
                $scope.copy.conceptualDomains.push newDomain
                $scope.pending.conceptualDomain = null

            if $scope.copy.dataType and angular.isString($scope.copy.dataType)
              promise = promise.then -> catalogueElementResource('dataType').save({name: $scope.copy.dataType}).then (newType) ->
                $scope.copy.dataType = newType

            if $scope.copy.unitOfMeasure and angular.isString($scope.copy.unitOfMeasure)
              promise = promise.then -> catalogueElementResource('measurementUnit').save({name: $scope.copy.unitOfMeasure}).then (newUnit) ->
                $scope.copy.unitOfMeasure = newUnit

            promise

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