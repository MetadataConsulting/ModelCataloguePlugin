angular.module('mc.core.ui.states.mc', ['mc.core.ui.states.bs.mc.html', 'mc.core.ui.states.controllers.DataModelTreeCtrl'])
.config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'mc', {
      abstract: true
      url: '/{dataModelId:[0-9]+|catalogue}'
      views:
        "":
          templateUrl: 'modelcatalogue/core/ui/state/mc.html'
          controller: 'mc.core.ui.states.controllers.DataModelTreeCtrl'
        'navbar-left':
          template: '<contextual-menu></contextual-menu>'
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