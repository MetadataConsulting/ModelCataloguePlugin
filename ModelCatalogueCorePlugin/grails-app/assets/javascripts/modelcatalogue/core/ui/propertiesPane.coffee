angular.module('mc.core.ui.propertiesPane', []).directive 'propertiesPane',  [-> {
    restrict: 'E'
    replace: true
    scope:
      item:         '='
      properties:   '=?'
      title:        '@?'
      valueTitle:   '@?'

    templateUrl: 'modelcatalogue/core/ui/propertiesPane.html'

    controller: ['$scope', ($scope) ->
      if not $scope.properties
        $scope.properties = []
        if $scope.item?.type == 'orderedMap'
          for value in $scope.item.values
            $scope.properties.push {label: value.key, value: value.value}
        else
          for key, value of $scope.item
            $scope.properties.push {label: key, value: value}

      $scope.printObject = (object) ->
        vals = []
        for key, value of object
          if key.indexOf('_')is 0
            continue
          if angular.isFunction(value)
            continue
          vals.push "#{key}: #{value ? ''}"
        vals.join('\n')

      $scope.getEnumerations = (enumeratedType) ->
        return '' if not enumeratedType
        return """<a href="#/catalogue/dataClass/#{enumeratedType.dataClass.id}"><span class="fa fa-fw fa-cubes"></span> #{enumeratedType.dataClass.name}</a>""" if enumeratedType.dataClass
        return enumeratedType.description if not enumeratedType.enumerations
        return enumeratedType.description if not enumeratedType.enumerations.values
        enumerations = []
        enumerations.push "#{enumeration.key}: #{enumeration.value}" for enumeration in enumeratedType.enumerations.values
        enumerations.join('\n')

      $scope.evaluateValue = (value, element) ->
        if angular.isFunction(value) then value(element) else $scope.$eval(value, element)

      $scope.getIcon = (value, element) ->
        result = if angular.isFunction(value) then value(element) else $scope.$eval(value, element)
        return result.getIcon() if angular.isObject(result) and result.getIcon?
        return ''

      $scope.propertyClick = (value, element) ->
        target = if angular.isFunction(value) then value(element) else $scope.$eval(value, element)
        target.show() if angular.isObject(target) and target.show? and angular.isFunction(target.show)

      $scope.displayType = (value, element) ->
        target = if angular.isFunction(value) then value(element) else $scope.$eval(value, element)
        return 'date'         if angular.isDate(target)
        return 'elementArray' if angular.isArray(target) and target.length > 0 and target[0] and target[0].elementType
        return 'array'        if angular.isArray(target)
        return 'element'      if angular.isObject(target) and target.show? and angular.isFunction(target.show)
        return 'text'

      $scope.expandOrCollapse = (element) ->
        if element._expanding
          return
        if element._expanded
          element._expanded = false
          return
        if element._refreshed
          element._expanded = true
          return

        element._expanding = true
        element.refresh().then (detailed) ->
          angular.extend element, detailed
          element._refreshed = true
          element._expanding = false
          element._expanded = true

    ]
  }
]