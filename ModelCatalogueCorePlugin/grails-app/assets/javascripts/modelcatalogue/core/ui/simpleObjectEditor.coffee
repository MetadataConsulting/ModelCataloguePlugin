angular.module('mc.core.ui.simpleObjectEditor', []).directive 'simpleObjectEditor',  [-> {
    restrict: 'E'
    replace: true
    scope:
      object:             '='
      hints:              '=?'
    templateUrl: 'modelcatalogue/core/ui/simpleObjectEditor.html'

    controller: ['$scope', '$filter', ($scope, $filter) ->
      # default values
      $scope.editableProperties = []

      $scope.removeProperty = (index) ->
        return if $scope.editableProperties.length <= 1
        current = $scope.editableProperties[index]
        unique  = $scope.isKeyUnique(current.key)
        $scope.editableProperties.splice(index, 1)
        if unique
          $scope.object[current.key] = undefined
        else
          for property in $scope.editableProperties
            if property.key == current.key
              $scope.object[property.key] = property.value

      $scope.addProperty = (index, property = {key: 'Key', value: 'Value'}) ->
        newProperty = angular.copy(property)
        newProperty.originalKey = undefined
        $scope.editableProperties.splice(index + 1, 0, newProperty)

      $scope.keyChanged = (property) ->
        $scope.object[property.originalKey] = undefined
        property.originalKey = property.key
        $scope.object[property.key] = property.value

      $scope.valueChanged = (property) ->
        $scope.object[property.key] = if property.value then property.value else null

      $scope.isKeyUnique = (key) ->
        return false if not key
        firstFound = false
        for property in $scope.editableProperties
          if property.key == key
            return false if firstFound
            firstFound = true
        return true

      remove = (arr, item) ->
        for i in arr.length by -1
          if arr[i] == item
            arr.splice(i, 1)

      onObjectOrHintsChanged = (object, hints) ->
        editableProperties = []
        currentHints       = angular.copy(hints ? [])

        for key, value of object when !(angular.isFunction(value) or angular.isObject(value))
          editableProperties.push key: key, value: value, originalKey: key
          remove currentHints, key

        for hint in currentHints
          editableProperties.push key: hint

        editableProperties.push key: '', value: '' unless editableProperties

        $scope.editableProperties = $filter('orderBy')(editableProperties, 'key')


      onObjectOrHintsChanged($scope.object, $scope.hints ? [])


      $scope.$watch 'object', (newObject) ->
        onObjectOrHintsChanged(newObject, $scope.hints ? [])

      $scope.$watch 'hints', (newHints) ->
        onObjectOrHintsChanged($scope.object, newHints)

    ]

  }
]