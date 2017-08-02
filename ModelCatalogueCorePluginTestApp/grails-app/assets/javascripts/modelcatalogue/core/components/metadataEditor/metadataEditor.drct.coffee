angular.module('modelcatalogue.core.components.metadataEditor').directive 'metadataEditor',  [-> {
    restrict: 'E'
    replace: true
    scope:
      object:             '='
      title:              '@?'
      valueTitle:         '@?'
      keyPlaceholder:     '@?'
      valuePlaceholder:   '@?'
      owner:              '=?' # required for metadata editors
    templateUrl: '/modelcatalogue/core/components/metadataEditor/metadataEditor.html'

    controller: ['$scope', 'enhance', '$log', 'metadataEditors', '$timeout', ($scope, enhance, $log, metadataEditors, $timeout) ->
      isOrderedMap = (object)->
        enhance.isEnhancedBy(object, 'orderedMap')

      # default values
      $scope.lastAddedRow = 0
      $scope.availableEditors = []
      $scope.selectedEditor = '__ALL__'

      $scope.removeProperty = (index) ->
          $scope.object.values.splice(index, 1)
          $scope.object.addPlaceholderIfEmpty()

      $scope.selectEditor = (editorTitle) ->
        $scope.selectedEditor = editorTitle
        return undefined

      $scope.addProperty = (index, property = {key: '', value: ''}) ->
        newProperty = angular.copy(property)
        delete newProperty.originalKey
        newIndex = index + 1
        $scope.lastAddedRow = newIndex
        $scope.object.values.splice(newIndex, 0, newProperty)

      $scope.isKeyUnique = (key) ->
        return false if not key and $scope.object.values.length > 1
        firstFound = false
        for property in $scope.object.values
          if property.key == key
            return false if firstFound
            firstFound = true
        return true

      onOwnerChanged = (owner) ->
        if owner
          $scope.availableEditors = metadataEditors.getAvailableEditors(owner)
          $scope.handledKeys = []

          for editor in $scope.availableEditors
            for key in editor.getKeys()
              $scope.handledKeys.push(key)

          $scope.object.access('').remove() if $scope.handledKeys.length

        else
          $scope.availableEditors = []
          $scope.handledKeys = []

      onObjectChanged = (object) ->
        if object
          unless isOrderedMap(object)
            $log.error "Object", object, "is not ordered map"
            return

          object.clearIfOnlyContainsPlaceholder()
          object.addPlaceholderIfEmpty()

      onObjectChanged($scope.object)

      $scope.sortableOptions = {
        cursor: 'move'
        handle: '.handle'
        update: ($event, $ui) ->
          return unless isOrderedMap($scope.object)

          newIndex = $ui.item.index()
          property = $ui.item.scope().property
          propertyIndex = 0
          for prop, i in $scope.object.values
            if prop.$$hashKey == property.$$hashKey
              propertyIndex = i
              break

          $timeout ->
            value = $scope.object.values[propertyIndex]
            $scope.object.values.splice(propertyIndex, 1)
            $scope.object.values.splice(newIndex, 0, value)
      }

      $scope.addNewRowOnTab = ($event, index, last)->
        $scope.addProperty(index, {key: '', value: ''}) if $event.keyCode == 9 and last

      $scope.$watch 'object', (newObject) ->
        onObjectChanged(newObject)

      $scope.$watch 'owner', onOwnerChanged
    ]

  }
]
