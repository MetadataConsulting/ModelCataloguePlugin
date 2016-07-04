angular.module('mc.core.ui.elementsAsTags', ['mc.util.names']).directive('elementsAsTags',  function() {
  return  {
    restrict: 'E',
    replace: true,
    scope: {
      elements: '='
    },
    templateUrl: '/mc/core/ui/elementsAsTags.html',

    controller: function($scope, names, $state, $window) {
      $scope.openElementInNewWindow =  function(element) {
        if (!element || !element.element || !angular.isFunction(element.element.href)) {
          return undefined;
        }
        $window.open(element.element.href(),'_blank')
      };

      $scope.getStatus = function(element) {
        if (!element || !element.element || !element.element.status) {
          return 'FINALIZED';
        }
        return element.element.status
      };

      $scope.removeItem = function(index){
        $scope.elements.splice(index, 1);
      };

      $scope.isString = angular.isString
    }
  };
});
