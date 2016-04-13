angular.module('mc.core.ui.states.controllers.XmlEditorCtrl', ['ui.ace']).controller('mc.core.ui.states.controllers.XmlEditorCtrl', [
  '$scope', '$stateParams', '$state', 'element', 'applicationTitle', '$http', 'catalogue', 'security'
  ($scope, $stateParams, $state, element, applicationTitle, $http, catalogue, security) ->
    $scope.element = element
    $scope.$blockScrolling = Infinity
    $scope.Document = "XML"

    if $scope.element
      applicationTitle "Xml Editor for #{element.getLabel()}"
      $http.get("#{$scope.element.internalModelCatalogueId}?format=xml").then (resp)->
        $scope.content = resp.data

      $http.get("#{security.contextPath}#{catalogue.getDefaultXslt($scope.element.elementType) ? catalogue.getDefaultXslt('catalogueElement')}").then (resp)->
        $scope.xslt = resp.data
])
