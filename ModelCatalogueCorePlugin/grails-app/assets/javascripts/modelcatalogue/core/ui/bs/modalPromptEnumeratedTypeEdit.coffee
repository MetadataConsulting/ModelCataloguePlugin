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
            </form>

            <label class="radio-inline">
              <input ng-disabled="!create" type="radio" ng-model="subtype" name="subtype" id="pickSimpleType" value="dataType"> Simple
            </label>
            <label class="radio-inline">
              <input ng-disabled="!create" ng-model="subtype" type="radio"  name="subtype" id="pickEnumeratedType" value="enumeratedType"> Enumerated
            </label>
            <label class="radio-inline">
              <input ng-disabled="!create" ng-model="subtype" type="radio" name="subtype" id="pickPrimitiveType" value="primitiveType"> Primitive
            </label>
            <label class="radio-inline">
              <input ng-disabled="!create" ng-model="subtype" type="radio" name="subtype" id="pickReferenceType" value="referenceType"> Reference
            </label>
            <div collapse="subtype != 'enumeratedType'">
              <ordered-map-editor object="copy.enumerations" title="Enumerations" key-placeholder="Value" value-placeholder="Description"></ordered-map-editor>
            </div>
            <div collapse="subtype != 'referenceType'">
              <div class="form-group">
                <label for="dataClass" class="">Data Class</label>
                <input type="text" id="dataClass" placeholder="Data Class" ng-model="copy.dataClass" catalogue-element-picker="dataClass" label="el.name">
              </div>
            </div>
            <div collapse="subtype != 'primitiveType'">
              <div class="form-group">
                <label for="measurementUnit" class="">Measurement Unit</label>
                <input type="text" id="measurementUnit" placeholder="Measurement Unit" ng-model="copy.measurementUnit" catalogue-element-picker="measurementUnit" label="el.name">
              </div>
            </div>
        </div>
        <div class="modal-footer">
          <contextual-actions role="modal"></contextual-actions>
        </div>
        '''
        controller: ['$scope', 'messages', '$controller', '$modalInstance', 'enhance', 'names', 'catalogueElementResource', ($scope, messages, $controller, $modalInstance, enhance, names, catalogueElementResource) ->
          orderedMapEnhancer = enhance.getEnhancer('orderedMap')

          $scope.newEntity = -> {enumerations: orderedMapEnhancer.emptyOrderedMap(), dataModels: []}
          $scope.copy     = angular.copy(args.element ? $scope.newEntity())
          $scope.copy.enumerations = $scope.copy.enumerations ? orderedMapEnhancer.emptyOrderedMap()
          $scope.original = args.element ? $scope.newEntity()
          $scope.messages = messages.createNewMessages()
          $scope.create   = args.create
          if args.create
            $scope.subtype = args.create
          else if args.element and args.element.elementType
            $scope.subtype = names.getPropertyNameFromType(args.element.elementType)
          else
            $scope.subtype = 'dataType'

          if $scope.create
            $scope.$watch 'subtype', (subtype) ->
                $scope.create = subtype

          angular.extend(this, $controller('withClassificationCtrlMixin', {$scope: $scope}))
          angular.extend(this, $controller('saveAndCreateAnotherCtrlMixin', {$scope: $scope, $modalInstance: $modalInstance}))

          $scope.hasChanged   = ->
            $scope.copy.name != $scope.original.name or $scope.copy.description != $scope.original.description or $scope.copy.modelCatalogueId != $scope.original.modelCatalogueId or not angular.equals($scope.original.enumerations ? {}, $scope.copy.enumerations ? {}) or not angular.equals($scope.original.dataModels ? {}, $scope.copy.dataModels ? {})

          $scope.beforeSave = ->
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

        ]

      }

      dialog.result
  ]

  messagesProvider.setPromptFactory 'edit-dataType', factory
  messagesProvider.setPromptFactory 'edit-enumeratedType', factory
  messagesProvider.setPromptFactory 'edit-referenceType', factory
]