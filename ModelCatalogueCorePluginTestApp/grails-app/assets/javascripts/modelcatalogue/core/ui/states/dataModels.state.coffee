angular.module('mc.core.ui.states.dataModels', ['mc.core.ui.states.controllers.PanelsCtrl']).config([
  '$stateProvider',
  ($stateProvider) ->

    $stateProvider.state 'dataModels', {
      url: '/dataModels?type&status&q'

      views:
        "":
          templateUrl: '/mc/core/ui/states/dataModels.html'
          controller: 'mc.core.ui.states.controllers.DataModelsCtrl'

      resolve:
        list: (catalogueElementResource ,  modelCatalogueApiRoot ,  enhance ,  rest ,  $state ,  $q ,  $stateParams ,  security) ->
          "ngInject"
          if $stateParams.type == 'catalogue'
            if $stateParams.q
              return catalogueElementResource('dataModel').search($stateParams.q, status: $stateParams.status ? 'active', minimal: true, max: 25)
            return catalogueElementResource('dataModel').list(status: $stateParams.status ? 'active', max: 25)
          if $stateParams.type == 'elements'
            if $stateParams.q
              return catalogueElementResource('catalogueElement').search($stateParams.q, status: $stateParams.status ? 'active', minimal: true, max: 25)
            return catalogueElementResource('catalogueElement').list(status: $stateParams.status ? 'active', max: 25)
          if $stateParams.q
            return security.requireUser().then (user) ->
              enhance(rest(url: "#{modelCatalogueApiRoot}/user/#{user.id}/outgoing/favourite/search", params: {elementType: 'org.modelcatalogue.core.DataModel', search: $stateParams.q, status: $stateParams.status ? 'active'}))
            ,  ->
              security.requireLogin()
          return security.requireUser().then (user) ->
            # need to use the trick with search as regular fetching of relationship does not support constraining by elementType
            enhance(rest(url: "#{modelCatalogueApiRoot}/user/#{user.id}/outgoing/favourite/search", params: {elementType: 'org.modelcatalogue.core.DataModel', search: '*', status: $stateParams.status ? 'active'}))
          ,  ->
            security.requireLogin()

        dataModelsForPreload: (rest , modelCatalogueApiRoot ) ->
          "ngInject"
          rest(method: 'GET', url: "#{modelCatalogueApiRoot}/dataModel/preload")

      data:
        resource: 'dataModel'
        applicationTitle: 'Data Models'
        createDialogArgs:
          type: 'create-dataModel'
          create: 'dataModel'
    }

])
