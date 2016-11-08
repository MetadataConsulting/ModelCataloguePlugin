angular.module('mc.util.stringToNumber', []).directive 'stringToNumber', [() -> {
  require: 'ngModel',
  link: (scope, element, attrs, ngModel) ->
    ngModel.$parsers.push((value) ->
      return '' + value
    )
    ngModel.$formatters.push((value) ->
      return parseFloat(value, 10)
    )
}]
