angular.module('mc.core.ui.simpleObjectEditor', []).directive 'simpleObjectEditor',  [-> {
    restrict: 'E'
    replace: true
    scope:
      object:             '='
      hints:              '=?'
      title:              '@?'
      valueTitle:         '@?'
      keyPlaceholder:     '@?'
      valuePlaceholder:   '@?'
    templateUrl: 'modelcatalogue/core/ui/simpleObjectEditor.html'

    controller: ['$scope', ($scope) ->
      isOrderedMap = (object)->
        object?.type == 'orderedMap'
      
      # default values
      $scope.editableProperties = []
      $scope.canReorder = false

      $scope.lastAddedRow = 0

      $scope.removeProperty = (index) ->
          current = $scope.editableProperties[index]
          unique  = $scope.isKeyUnique(current.key)
          $scope.editableProperties.splice(index, 1)

          if isOrderedMap($scope.object)
            $scope.object.values.splice(index, 1)
          else if unique
            delete $scope.object[current.key]
          else
            for property in $scope.editableProperties
              if property.key == current.key
                $scope.object[property.key] = property.value
          if $scope.editableProperties.length == 0
            $scope.editableProperties.push key: ''

      $scope.addProperty = (index, property = {key: '', value: ''}) ->
        newProperty = angular.copy(property)
        delete newProperty.originalKey
        newIndex = index + 1
        $scope.lastAddedRow = newIndex
        $scope.editableProperties.splice(newIndex, 0, newProperty)
        if isOrderedMap($scope.object)
          $scope.object.values.splice(newIndex, 0, newProperty)

      $scope.moveUp = ($index) ->
        return unless isOrderedMap($scope.object)
        return if $index == 0
        property = $scope.editableProperties[$index]
        $scope.editableProperties.splice($index, 1)
        $scope.editableProperties.splice($index - 1, 0, property)
        value = $scope.object.values[$index]
        $scope.object.values.splice($index, 1)
        $scope.object.values.splice($index - 1, 0, value)

      $scope.moveDown = ($index) ->
        return unless isOrderedMap($scope.object)
        return unless $index < $scope.object.values.length - 1
        $scope.moveUp($index + 1)

      $scope.keyChanged = (property) ->
        if isOrderedMap($scope.object)
          for value, i in $scope.object.values
            if value.key == property.originalKey
              value.key = property.key
          property.originalKey = property.key
          return
        delete $scope.object[property.originalKey]
        return if not property.key
        property.originalKey = property.key
        $scope.object[property.key] = if property.value then property.value else null

      $scope.valueChanged = (property) ->
        if isOrderedMap($scope.object)
          for value, i in $scope.object.values
            if value.key == property.key
              value.value = property.value
          return
        return if not property.key
        $scope.object[property.key] = if property.value then property.value else null

      $scope.isKeyUnique = (key) ->
        return false if not key and $scope.editableProperties.length > 1
        firstFound = false
        for property in $scope.editableProperties
          if property.key == key
            return false if firstFound
            firstFound = true
        return true

      remove = (arr, item) ->
        arr.splice($.inArray(item, arr), 1 )

      onObjectOrHintsChanged = (object, hints) ->
        editableProperties = []
        currentHints       = angular.copy(hints ? [])

        if isOrderedMap(object)
          object.values.push key: '' if object.values .length == 0 and (not hints or hints.length == 0)
          for value in object.values when !(angular.isFunction(value.value) or angular.isObject(value.value))
            editableProperties.push key: value.key, value: value.value, originalKey: value.key
            remove currentHints, value.key
          $scope.canReorder = true
        else
          for key, value of object when key and !(angular.isFunction(value) or angular.isObject(value))
            editableProperties.push key: key, value: value, originalKey: key
            remove currentHints, key
          $scope.canReorder = false

        for hint in currentHints by -1
          editableProperties.unshift key: hint
          if isOrderedMap(object)
            object.values.push key: hint

        editableProperties.push key: '' if editableProperties.length == 0 and (not hints or hints.length == 0)

        $scope.editableProperties = editableProperties


      onObjectOrHintsChanged($scope.object, $scope.hints ? [])

      $scope.sortableOptions = {
        cursor: 'move'
        handle: '.handle'
        update: ($event, $ui) ->
          return unless isOrderedMap($scope.object)

          newIndex = $ui.item.index()
          property = $ui.item.scope().property
          propertyIndex = 0
          for prop, i in $scope.editableProperties
            if prop.$$hashKey == property.$$hashKey
              propertyIndex = i
              break

          $scope.$apply (scope)->
            scope.editableProperties.splice(propertyIndex, 1)
            scope.editableProperties.splice(newIndex, 0, property)

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

    ]

  }
]