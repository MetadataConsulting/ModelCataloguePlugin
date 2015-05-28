angular.module('mc.core.ui.orderedMapEditor', ['mc.core.ui.metadataEditors']).directive 'orderedMapEditor',  [-> {
    restrict: 'E'
    replace: true
    scope:
      object:             '='
      hints:              '=?'
      title:              '@?'
      valueTitle:         '@?'
      keyPlaceholder:     '@?'
      valuePlaceholder:   '@?'
      owner:              '=?' # required for metadata editors
    templateUrl: 'modelcatalogue/core/ui/orderedMapEditor.html'

    controller: ['$scope', 'enhance', '$log', 'metadataEditors', ($scope, enhance, $log, metadataEditors) ->
      isOrderedMap = (object)->
        enhance.isEnhancedBy(object, 'orderedMap')
      
      # default values
      $scope.lastAddedRow = 0
      $scope.availableEditors = []

      $scope.removeProperty = (index) ->
          $scope.object.values.splice(index, 1)
          $scope.object.addPlaceholderIfEmpty()


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

      remove = (arr, item) -> arr.splice($.inArray(item, arr), 1)

      onOwnerChanged = (owner) ->
        if owner
          $scope.availableEditors = metadataEditors.getAvailableEditors(owner)
          $scope.handledKeys = []

          for editor in $scope.availableEditors
            for key in editor.getKeys()
              $scope.handledKeys.push(key)
              unless $scope.object.get(key)?
                $scope.object.values.push(key: key, value: '')

          $scope.object.access('').remove() if $scope.handledKeys.length

        else
          $scope.availableEditors = []
          $scope.handledKeys = []

      onObjectOrHintsChanged = (object, hints) ->
        if object and not isOrderedMap(object)
          $log.error "Object", object, "is not ordered map"
          return

        currentHints = angular.copy(hints ? [])

        object.clearIfOnlyContainsPlaceholder()

        for value in object.values
          remove currentHints, value.key

        for hint in currentHints by -1
            object.values.unshift key: hint

        object.addPlaceholderIfEmpty()

      onObjectOrHintsChanged($scope.object, $scope.hints ? [])

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

          $scope.$apply (scope)->
            value = $scope.object.values[propertyIndex]
            scope.object.values.splice(propertyIndex, 1)
            scope.object.values.splice(newIndex, 0, value)
      }

      $scope.addNewRowOnTab = ($event, index, last)->
        $scope.addProperty(index, {key: '', value: ''}) if $event.keyCode == 9 and last

      $scope.$watch 'object', (newObject) ->
        onObjectOrHintsChanged(newObject, $scope.hints ? [])

      $scope.$watch 'hints', (newHints) ->
        onObjectOrHintsChanged($scope.object, newHints)


      $scope.$watch 'owner', onOwnerChanged
    ]

  }
]