angular.module('modelcatalogue.core.ui.states.dataModel.resource', [
  'modelcatalogue.core.ui.states.dataModel.resource.diff',
  'modelcatalogue.core.ui.states.dataModel.resource.list',
  'modelcatalogue.core.ui.states.dataModel.resource.list-imported',
  'modelcatalogue.core.ui.states.dataModel.resource.show',
  'modelcatalogue.core.ui.states.dataModel.resource.xmlEditor',

  'modelcatalogue.core.ui.states.dataModel.resource.ElementWithDataModelCtrl'

]).config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'dataModel.resource', {
      abstract: true
      url: '/:resource'
      views:
        "":
          templateUrl: 'modelcatalogue/core/ui/state/parent.html'
    }
])
