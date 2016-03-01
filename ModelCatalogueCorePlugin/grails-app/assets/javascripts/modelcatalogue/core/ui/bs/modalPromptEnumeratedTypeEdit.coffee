angular.module('mc.core.ui.bs.modalPromptEnumeratedTypeEdit', ['mc.util.messages', 'mc.core.ui.bs.withClassificationCtrlMixin', 'mc.core.ui.bs.watchAndAskForImportOrCloneCtrl']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$modal', '$q', 'messages', ($modal, $q, messages) ->
    (title, body, args) ->
      if not args?.element? and not args?.create?
        messages.error('Cannot create edit dialog.', 'The element to be edited is missing.')
        return $q.reject('Missing element argument!')

      dialog = $modal.open {
        windowClass: 'basic-edit-modal-prompt'
        size: 'lg'
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body">
            <messages-panel messages="messages"></messages-panel>
            <form role="form" ng-submit="saveElement()">
              <div class="form-group" ng-if="!hideDataModels()">
                <label for="dataModel"> Data Models</label>
                <elements-as-tags elements="copy.dataModels"></elements-as-tags>
                <input id="dataModel" placeholder="Data Model" ng-model="pending.dataModel" catalogue-element-picker="dataModel" label="el.name" typeahead-on-select="addToDataModels()">
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
              <div class="form-group">
                  <label for="rule" ng-click="ruleCollapsed = !ruleCollapsed">Rule <span class="glyphicon" ng-class="{'glyphicon-collapse-down': ruleCollapsed, 'glyphicon-collapse-up': !ruleCollapsed}"></span></label>
                  <div collapse="ruleCollapsed" >
                    <textarea rows="10" ng-model="copy.rule" placeholder="Rule" class="form-control" id="rule"></textarea>
                    <p class="help-block">Enter valid <a href="http://www.groovy-lang.org/" target="_blank">Groovy</a> code. Variable <code>x</code> refers to the value validated value and  <code>dataType</code> to current data type. Last row is the result which should be <code>boolean</code> value. For example you can <a ng-click="showRegexExample()"><span class="fa fa-magic"></span> validate using regular expression</a> or <a ng-click="showSetExample()"><span class="fa fa-magic"></span> values in set</a></p>
                  </div>
                </div>
              <label class="radio-inline">
                <input type="radio" ng-model="subtype" name="subtype" id="pickSimpleType" value="dataType"> Simple
              </label>
              <label class="radio-inline">
                <input ng-model="subtype" type="radio"  name="subtype" id="pickEnumeratedType" value="enumeratedType"> Enumerated
              </label>
              <label class="radio-inline">
                <input ng-model="subtype" type="radio" name="subtype" id="pickPrimitiveType" value="primitiveType"> Primitive
              </label>
              <label class="radio-inline">
                <input ng-model="subtype" type="radio" name="subtype" id="pickReferenceType" value="referenceType"> Reference
              </label>
              <label class="radio-inline">
                <input ng-model="subtype" type="radio" name="subtype" id="pickReferenceType" value="subset"> Subset
              </label>
              <div collapse="subtype != 'enumeratedType'">
                <ordered-map-editor object="copy.enumerations" title="Enumerations" key-placeholder="Value" value-placeholder="Description"></ordered-map-editor>
              </div>
              <div collapse="subtype != 'referenceType'">
                <div class="form-group">
                  <label for="dataClass" class="">Data Class</label>
                  <input type="text" id="dataClass" placeholder="Data Class" ng-model="copy.dataClass" global="'allow'" catalogue-element-picker="dataClass" label="el.name">
                </div>
              </div>
              <div collapse="subtype != 'primitiveType'">
                <div class="form-group">
                  <label for="measurementUnit" class="">Measurement Unit</label>
                  <input type="text" id="measurementUnit" placeholder="Measurement Unit" ng-model="copy.measurementUnit" catalogue-element-picker="measurementUnit" label="el.name">
                </div>
              </div>
              <div collapse="subtype != 'subset'">
                <div class="form-group">
                  <label for="baseEnumeration" class="">Enumerated Type Base</label>
                  <input type="text" id="baseEnumeration" placeholder="Enumerated Type Base" ng-model="copy.baseEnumeration" catalogue-element-picker="enumeratedType" label="el.name">
                </div>
                <div class="form-group" ng-if="copy.baseEnumeration.enumerations">
                  <label for="subset">Subset</label>
                    <div class="alert alert-warning" ng-if="copy.selectedEnumerations.values.length == 0">If no enumeration value selected, all values will be used</div>
                    <div class="checkbox" ng-repeat="value in copy.baseEnumeration.enumerations.values">
                      <label>
                        <input type="checkbox" value="{{value}}" ng-checked="copy.selectedEnumerations.values.indexOf(value) > -1" ng-click="toggleSelection(copy.selectedEnumerations.values, value)">
                        {{value.key}}: {{value.value}}
                      </label>
                    </div>
                </div>
              </div>
              <fake-submit-button />
            </form>
        </div>
        <div class="modal-footer">
          <contextual-actions role="modal"></contextual-actions>
        </div>
        '''
        controller: ['$scope', 'messages', '$controller', '$modalInstance', 'enhance', 'names', 'catalogueElementResource', ($scope, messages, $controller, $modalInstance, enhance, names, catalogueElementResource) ->
          orderedMapEnhancer = enhance.getEnhancer('orderedMap')

          $scope.newEntity = -> {enumerations: orderedMapEnhancer.emptyOrderedMap(true), dataModels: []}
          $scope.pending  = {dataModel: null}
          $scope.copy     = angular.copy(args.element ? $scope.newEntity())
          $scope.copy.enumerations = $scope.copy.enumerations ? orderedMapEnhancer.emptyOrderedMap(true)
          $scope.original = args.element ? $scope.newEntity()
          $scope.messages = messages.createNewMessages()
          $scope.ruleCollapsed  = not $scope.copy.rule
          $scope.currentDataModel = args.currentDataModel
          $scope.create   = args.create
          if args.create
            $scope.subtype = args.create
          else if args.element and args.element.elementType
            $scope.original.subtype = $scope.subtype = names.getPropertyNameFromType(args.element.elementType)
          else
            $scope.subtype = 'dataType'

          if $scope.create
            $scope.$watch 'subtype', (subtype) ->

                $scope.create = if subtype == 'subset' then 'enumeratedType' else subtype

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

          angular.extend(this, $controller('withClassificationCtrlMixin', {$scope: $scope}))
          angular.extend(this, $controller('saveAndCreateAnotherCtrlMixin', {$scope: $scope, $modalInstance: $modalInstance}))
          angular.extend(this, $controller('watchAndAskForImportOrCloneCtrl', {$scope: $scope}))

          # measurement unit are bit different - they are not supposed to be cloned at the moment
          # $scope.watchAndAskForImportOrClone('copy.measurementUnit')
          $scope.watchAndAskForImportOrClone('copy.dataClass')

          $scope.hasChanged   = ->
            $scope.copy.name != $scope.original.name or $scope.copy.description != $scope.original.description or $scope.copy.modelCatalogueId != $scope.original.modelCatalogueId or not angular.equals($scope.original.enumerations ? {}, $scope.copy.enumerations ? {}) or not angular.equals($scope.original.dataModels ? {}, $scope.copy.dataModels ? {})

          $scope.beforeSave = ->
            # TODO verify this works as expected - you should be able to edit this even for subtype
            if $scope.original.subtype and $scope.original.subtype isnt $scope.subtype
              $scope.copy.newType = if $scope.subtype == 'subset' then 'enumeratedType' else $scope.subtype

            promise = $q.when {}

            if $scope.pending.dataModel and angular.isString($scope.pending.dataModel)
               promise = promise.then -> catalogueElementResource('dataModel').save({name: $scope.pending.dataModel}).then (newDataModel) ->
                 $scope.copy.dataModels = $scope.copy.dataModels ? []
                 $scope.copy.dataModels.push newDataModel
                 $scope.pending.dataModel = null



            if $scope.subtype is 'referenceType'
              if $scope.copy.dataClass and angular.isString($scope.copy.dataClass)
                 promise = promise.then -> catalogueElementResource('dataClass').save({name: $scope.copy.dataClass, dataModels: $scope.copy.dataModels}).then (newClass) ->
                   $scope.copy.dataClass = newClass
            else
              $scope.copy.dataClass = undefined

            if $scope.subtype is 'primitiveType'
              if $scope.copy.measurementUnit and angular.isString($scope.copy.measurementUnit)
                 promise = promise.then -> catalogueElementResource('measurementUnit').save({name: $scope.copy.measurementUnit, dataModels: $scope.copy.dataModels}).then (newUnit) ->
                   $scope.copy.measurementUnit = newUnit
            else
              $scope.copy.measurementUnit = undefined

            promise

          $scope.$watch 'subtype', ->
            if $scope.original.subtype
              if $scope.original.subtype isnt $scope.subtype
                $scope.messages.warning "You have changed the subtype of the data element. New version will be created!"
              else
                $scope.messages.clearAllMessages()

          $scope.copy.selectedEnumerations = orderedMapEnhancer.emptyOrderedMap(true)

          $scope.toggleSelection = (array, selection) ->
            idx = array.indexOf(selection)
            if idx > -1
              array.splice(idx, 1)
            else
              array.push(selection)

          originalValidate = $scope.validate

          $scope.validate = ->
            result = originalValidate()

            return unless result

            if $scope.subtype == 'subset' and not $scope.copy.baseEnumeration
              $scope.messages.error "Empty base enumeration", "You need to select the base enumeration for a subset!"
              return false

            return true

        ]

      }

      dialog.result
  ]

  messagesProvider.setPromptFactory 'edit-dataType', factory
  messagesProvider.setPromptFactory 'edit-enumeratedType', factory
  messagesProvider.setPromptFactory 'edit-referenceType', factory
  messagesProvider.setPromptFactory 'edit-primitiveType', factory
]
