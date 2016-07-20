angular.module('mc.core.ui.utils').directive 'orderedMapEditor',  [-> {
    restrict: 'E'
    replace: true
    scope:
      object:             '='
      canDeprecate:       '@?'
      title:              '@?'
      valueTitle:         '@?'
      keyPlaceholder:     '@?'
      valuePlaceholder:   '@?'
    templateUrl: '/mc/core/ui/utils/orderedMapEditor.html'

    controller: ['$scope', 'enhance', '$log', 'messages', '$timeout',  ($scope, enhance, $log, messages, $timeout) ->
      isOrderedMap = (object)->
        enhance.isEnhancedBy(object, 'orderedMap')

      # default values
      $scope.lastAddedRow = 0

      $scope.removeProperty = (index) ->
          $scope.object.values.splice(index, 1)
          $scope.object.addPlaceholderIfEmpty()


      $scope.addProperty = (index, property = {key: '', value: ''}) ->
        newProperty = angular.copy(property)
        delete newProperty.originalKey
        newIndex = index + 1
        $scope.lastAddedRow = newIndex
        $scope.object.values.splice(newIndex, 0, newProperty)

      $scope.setDeprecated = (property, deprecated) ->
        if ($scope.canDeprecate)
          property.deprecated = deprecated
        else
          $log.error("cannot deprecate property as it is not allowed for this orderMapEditor", property)

      $scope.isKeyUnique = (key) ->
        return false if not key and $scope.object.values.length > 1
        firstFound = false
        for property in $scope.object.values
          if property.key == key
            return false if firstFound
            firstFound = true
        return true

      onObjectChanged = (object) ->
        if object
          if not isOrderedMap(object)
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

      $scope.importExcel = (importText) ->
        unless importText?.length > 0
          messages.error "No excel content to be imported."
          return importText

        $log.debug("excel import with content #{importText}")
        updatedValues = 0
        createdValues = 0
        for line in importText.split(/\r\n|\n\r|\n|\r/g)
          tokens = line.split("\t")
          if tokens.length > 0
            value = ''
            if tokens.length > 1
              value = tokens[1]
            key = tokens[0]
            if $scope.object.get(key)
              updatedValues++
            else
              createdValues++
            newObject = {}
            newObject[key] = value
            $scope.object.updateFrom(newObject, true)

        # remove empty key - present when new map is created
        if (createdValues > 0 || updatedValues > 0)
          $scope.object.remove('')

        # success message
        messages.info "Import was successful, #{createdValues} enumerations was created and #{updatedValues} enumerations was updated."
        # clear import text
        return ''

      $scope.pasteExcel = (event) ->
        if event.clipboardData? && event.clipboardData.getData? # Standard
          data = event.clipboardData.getData "text/plain"
        else if event.originalEvent? && event.originalEvent.clipboardData? && event.originalEvent.clipboardData.getData? # jQuery
          data = event.originalEvent.clipboardData.getData "text/plain"
        else if window.clipboardData? && window.clipboardData.getData? # Internet Explorer
          data = window.clipboardData.getData "Text"

        # do the import
        $scope.importExcel(data)

        # do not trigger the default paste event
        event.preventDefault()

      $scope.$watch 'object', (newObject) ->
        onObjectChanged(newObject)
    ]
  }
]
