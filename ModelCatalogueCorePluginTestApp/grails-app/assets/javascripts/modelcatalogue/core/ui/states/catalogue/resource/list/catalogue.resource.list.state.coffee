angular.module('modelcatalogue.core.ui.states.catalogue.resource.list', ['modelcatalogue.core.ui.states.controllers.ListCtrl']).config([
  '$stateProvider', 'catalogueProvider',
  ($stateProvider ,  catalogueProvider) ->

    DEFAULT_ITEMS_PER_PAGE = 10

    $stateProvider.state 'catalogue.resource.list', {
      url: '/all?page&order&sort&status&q&max&classification&display'
      views:
        "":
          templateUrl: 'modelcatalogue/core/ui/state/list.html'
          controller: 'modelcatalogue.core.ui.states.controllers.ListCtrl'

        'navbar-left@':
          template: '<contextual-menu role="{{::actionRoleAccess.ROLE_LIST_ACTION}}"></contextual-menu>'
          controller: 'modelcatalogue.core.ui.states.controllers.ListCtrl'


      resolve:
        list: ['$stateParams', 'catalogueElementResource', ($stateParams, catalogueElementResource) ->
          page = parseInt($stateParams.page ? 1, 10)
          page = 1 if isNaN(page)
          # it's safe to call top level for each controller, only data class controller will respond on it

          defaultSorts = catalogueProvider.getDefaultSort($stateParams.resource) ? {sort: 'name', order: 'asc'}

          params = offset: (page - 1) * DEFAULT_ITEMS_PER_PAGE, toplevel: true, system: true
          params.order = $stateParams.order ? defaultSorts.order
          params.sort = $stateParams.sort ? defaultSorts.sort
          params.status = $stateParams.status
          params.max = $stateParams.max ? 10
          params.classification = $stateParams.classification ? undefined

          if $stateParams.dataModelId and $stateParams.dataModelId != 'catalogue'
            params.dataModel = $stateParams.dataModelId

          if $stateParams.q
            return catalogueElementResource($stateParams.resource).search($stateParams.q, params)

          catalogueElementResource($stateParams.resource).list(params)
        ]

    }

])
