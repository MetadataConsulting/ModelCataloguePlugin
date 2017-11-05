angular.module('mc.core.ui.bs.modalPromptEnumeratedTypeEdit', ['mc.util.messages', 'mc.core.ui.bs.withClassificationCtrlMixin', 'mc.core.ui.bs.watchAndAskForImportOrCloneCtrl', 'mc.util.ui.actions']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$uibModal', '$q', 'messages', ($uibModal, $q, messages) ->
    (title, body, args) ->
      if not args?.element? and not args?.create?
        messages.error('Cannot create edit dialog.', 'The element to be edited is missing.')
        return $q.reject('Missing element argument!')

      if not title
        title = "Create Data Type"

      dialog = $uibModal.open {
        windowClass: 'basic-edit-modal-prompt'
        size: 'lg'
        templateUrl: '/mc/core/ui/modals/modalPromptEnumeratedTypeEdit.html'
        controller: ['$scope', 'messages', '$controller', '$uibModalInstance', 'enhance', 'names', 'catalogueElementResource', 'actionRoleAccess', ($scope, messages, $controller, $uibModalInstance, enhance, names, catalogueElementResource, actionRoleAccess) ->
          $scope.actionRoleAccess = actionRoleAccess
          orderedMapEnhancer = enhance.getEnhancer('orderedMap')

          $scope.newEntity = -> {enumerations: orderedMapEnhancer.emptyOrderedMap(true), dataModels: [args.currentDataModel], selectedEnumerations: orderedMapEnhancer.emptyOrderedMap(true)}
          $scope.pending  = {dataModel: null}
          $scope.copy     = angular.copy(args.element ? $scope.newEntity())
          $scope.copy.enumerations = $scope.copy.enumerations ? orderedMapEnhancer.emptyOrderedMap(true)
          $scope.copy.name = args.name ? $scope.copy.name
          $scope.original = args.element ? $scope.newEntity()
          $scope.messages = messages.createNewMessages()
          $scope.ruleCollapsed  = not $scope.copy.rule
          $scope.currentDataModel = args.currentDataModel
          $scope.title = title
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
          angular.extend(this, $controller('saveAndCreateAnotherCtrlMixin', {$scope: $scope, $uibModalInstance: $uibModalInstance}))
          angular.extend(this, $controller('watchAndAskForImportOrCloneCtrl', {$scope: $scope}))

          # measurement unit are bit different - they are not supposed to be cloned at the moment
          # $scope.watchAndAskForImportOrClone('copy.measurementUnit')
          $scope.watchAndAskForImportOrClone('copy.dataClass')

          $scope.hasChanged   = ->
            $scope.copy.name != $scope.original.name\
              or $scope.copy.description != $scope.original.description\
              or $scope.copy.modelCatalogueId != $scope.original.modelCatalogueId

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
