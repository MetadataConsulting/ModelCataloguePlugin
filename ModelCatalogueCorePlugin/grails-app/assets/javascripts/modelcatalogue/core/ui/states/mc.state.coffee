angular.module('mc.core.ui.states.mc', []).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'mc', {
      abstract: true
      url: '/{dataModelId:[0-9]+|catalogue}'
      templateUrl: 'modelcatalogue/core/ui/layout.html'
      resolve:
        currentDataModel: ['catalogue', '$rootScope', '$stateParams', '$q', 'catalogueElementResource', (catalogue, $rootScope, $stateParams, $q, catalogueElementResource) ->
          if !$stateParams.dataModelId or $stateParams.dataModelId == 'catalogue'
            $rootScope.currentDataModel = undefined
            return undefined
          if $rootScope.currentDataModel and $stateParams.dataModelId == $rootScope.currentDataModel.id.toString
            return $rootScope.currentDataModel

          deferred = $q.defer()

          catalogueElementResource('dataModel').get($stateParams.dataModelId).then (dataModel) ->
            deferred.resolve(dataModel)
            $rootScope.currentDataModel = dataModel

          deferred.promise
        ]
    }

])