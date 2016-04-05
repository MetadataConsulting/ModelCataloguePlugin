angular.module('mc.core.ui.states.controllers.XMLEditorCtrl', ['ui.ace']).controller('mc.core.ui.states.controllers.XMLEditorCtrl', [
  '$scope', '$stateParams', '$state', 'element', 'applicationTitle', '$http',
  ($scope ,  $stateParams ,  $state ,  element, applicationTitle , $http ) ->

    $scope.element = element

    $scope.$blockScrolling = Infinity
    $scope.Document = "XML"

    transformResponse = (data) ->
      console.log("transformResponse", data)
      $scope.content = data


    if $scope.element
      applicationTitle  "XML Editor for #{element.getLabel()}"
      $http.get("#{$scope.element.internalModelCatalogueId}?format=xsd",responseType:"text", transformResponse:transformResponse).then (resp)->
        console.log resp


])


