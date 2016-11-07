angular.module('mc.core.ui.general').directive 'image', () ->
  'ngInject'

  replace: true
  restrict: 'A'
  template: '<img ng-src="{{getImageSrc(image)}}" />'
  scope:
    image: '='
  controller: ($scope, security) ->
    $scope.getImageSrc = (relativePath) ->
      lastIndex = security.contextPath.lastIndexOf('/')

      # context path already ends with slash
      if lastIndex != -1 and lastIndex + 1 == security.contextPath.length
        "#{security.contextPath}assets#{relativePath}"
      # context path doesn't end with slash
      else
        "#{security.contextPath}/assets#{relativePath}"
