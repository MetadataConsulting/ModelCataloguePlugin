angular.module('mc.core.ui.states.mc.resource.list', ['mc.core.ui.states.controllers.ListCtrl']).config([
  '$stateProvider', 'catalogueProvider',
  ($stateProvider ,  catalogueProvider) ->

    DEFAULT_ITEMS_PER_PAGE = 10

    $stateProvider.state 'mc.resource.list', {
      url: '/all?page&order&sort&status&q&max&classification&display'

      views:
        "":
          templateUrl: 'modelcatalogue/core/ui/state/list.html'
          controller: 'mc.core.ui.states.controllers.ListCtrl'

        'navbar-left@':
          template: '<contextual-menu role="list"></contextual-menu>'
          controller: 'mc.core.ui.states.controllers.ListCtrl'


      resolve:
        list: ['$stateParams', 'catalogueElementResource', ($stateParams, catalogueElementResource) ->
          page = parseInt($stateParams.page ? 1, 10)
          page = 1 if isNaN(page)
          # it's safe to call top level for each controller, only data class controller will respond on it

          defaultSorts = catalogueProvider.getDefaultSort($stateParams.resource) ? {sort: 'name', order: 'asc'}

          params = offset: (page - 1) * DEFAULT_ITEMS_PER_PAGE, toplevel: true, system: true
          params.order = $stateParams.order ? defaultSorts.order
          params.sort = $stateParams.sort ? defaultSorts.sort
          params.status = $stateParams.status ? (if $stateParams.resource is 'dataModel' then 'active' else 'finalized')
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