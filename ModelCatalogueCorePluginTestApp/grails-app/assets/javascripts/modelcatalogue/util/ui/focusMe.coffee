#http://stackoverflow.com/questions/14833326/how-to-set-focus-on-input-field-in-angularjs
angular.module('mc.util.ui.focusMe', []).directive 'focusMe', ['$timeout', '$parse', ($timeout, $parse) -> {
link: (scope, element, attrs) ->
  scope.$watch $parse(attrs.focusMe), (value) ->
    $timeout (-> element[0].focus()) if value
}]