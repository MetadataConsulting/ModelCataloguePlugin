angular.module('modelcatalogue.core.sections.detailSections.forms.actions').config (actionsProvider, actionClass) ->
    "ngInject"
    Action = actionClass
    actionsProvider.registerChildAction 'export', 'export-crf',
      (security, $uibModal, $window, $scope, modelCatalogueApiRoot) ->
        "ngInject"
        return undefined unless security.hasRole('CURATOR')
        return undefined unless $scope.element?.isInstanceOf?('dataClass')
        element = $scope.element;

        Action.createStandardAction(
          position: 5000
          label: 'Export as Case Report From'
          icon: null
          type: null
          action: ->
            $uibModal.open({
              templateUrl: '/modelcatalogue/core/sections/detailSections/forms/actions/exportCrf.html',
              controller: ($scope, $uibModalInstance) ->
                "ngInject"
                $scope.assetName = "#{element.name} Case Report Form"

                $scope.preview = ->
                  url = URI("#{modelCatalogueApiRoot}/forms/preview/#{element.id}").setQuery({name: $scope.assetName})
                  $window.open(url, '_blank')

                $scope.submit = ->
                  url = URI("#{modelCatalogueApiRoot}/forms/generate/#{element.id}").setQuery({name: $scope.assetName})
                  $window.open(url, '_blank')
                  $uibModalInstance.close()
            })
        )

