angular.module('mc.core.forms.actions').config(function (actionsProvider) {
  "ngInject"

  actionsProvider.registerChildActionInRoles('export', 'export-crf', [actionsProvider.ROLE_LIST_ACTION,
      actionsProvider.ROLE_ITEM_ACTION, actionsProvider.ROLE_NAVIGATION, actionsProvider.ROLE_LIST_HEADER_ACTION],
    function (security, $uibModal, $window, $scope, modelCatalogueApiRoot) {
      "ngInject"
      if (!security.hasRole('CURATOR')) {
        return undefined
      }
      if (!$scope.element || !angular.isFunction($scope.element.isInstanceOf) || !$scope.element.isInstanceOf('dataClass')) {
        return undefined
      }
      var element = $scope.element;

      return {
        position: 5000,
        label: 'Export as Case Report From',
        action: function () {
          $uibModal.open(
            {
              templateUrl: '/mc/core/forms/actions/exportCrf.html',
              controller: function ($scope, $uibModalInstance) {
                "ngInject"
                $scope.assetName = `${element.name} Case Report Form`;

                $scope.preview = function () {
                  var url = URI(`${modelCatalogueApiRoot}/forms/preview/${element.id}`).setQuery({name: $scope.assetName});
                  $window.open(url, '_blank');
                };

                $scope.submit = function () {
                  var url = URI(`${modelCatalogueApiRoot}/forms/generate/${element.id}`).setQuery({name: $scope.assetName});
                  $window.open(url, '_blank');
                  $uibModalInstance.close();
                };
              }
            })
        }
      }
    });
});
