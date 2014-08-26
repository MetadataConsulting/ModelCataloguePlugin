angular.module('mc.core.ui.propertiesPane', []).directive 'propertiesPane',  [-> {
    restrict: 'E'
    replace: true
    scope:
      item:         '='
      properties:   '='
      title:        '@?'
      valueTitle:   '@?'

    templateUrl: 'modelcatalogue/core/ui/propertiesPane.html'

    controller: ['$scope', ($scope) ->
      $scope.evaluateValue = (value, element) ->
        result = if angular.isFunction(value) then value(element) else $scope.$eval(value, element)
        return result.name if angular.isObject(result) and result.name?
        result

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
    ]
  }
]