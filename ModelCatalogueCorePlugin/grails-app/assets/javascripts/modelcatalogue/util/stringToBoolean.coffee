angular.module('mc.util.stringToBoolean', []).directive 'stringToBoolean', [() -> {
  require: 'ngModel',
  link: (scope, element, attrs, ngModel) ->
    ngModel.$parsers.push((value) ->
      return '' + value
    )
    ngModel.$formatters.push((value) ->
      return String(value) == "true"
    )
}]
