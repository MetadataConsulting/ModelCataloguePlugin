angular.module('mc.core.ui.states.defaultStates', ['ui.router'])
.controller('mc.core.ui.states.ShowCtrl', ['$scope', '$stateParams', '$state', '$log', 'element', ($scope, $stateParams, $state, $log, element) ->
    $scope.element  = element
])
.controller('mc.core.ui.states.ListCtrl', ['$scope', '$stateParams', '$state', '$log', 'list', 'names', ($scope, $stateParams, $state, $log, list, names) ->
    $scope.list = list
    $scope.title = names.getNaturalName($stateParams.resource)
])
.config(['$stateProvider', ($stateProvider)->

  DEFAULT_ITEMS_PER_PAGE = 10

  $stateProvider.state 'mc', {
    abstract: true
    url: '/catalogue'
    templateUrl: 'modelcatalogue/core/ui/state/parent.html'
  }
  $stateProvider.state 'mc.resource', {
    abstract: true
    url: '/:resource'
    templateUrl: 'modelcatalogue/core/ui/state/parent.html'
  }
  $stateProvider.state 'mc.resource.list', {
    url: '/all?page'

    templateUrl: 'modelcatalogue/core/ui/state/list.html'

    resolve:
        list: ['$stateParams','catalogueElementResource', ($stateParams,catalogueElementResource) ->
          page = parseInt($stateParams.page ? 1, 10)
          catalogueElementResource($stateParams.resource).list(offset: (page - 1) * DEFAULT_ITEMS_PER_PAGE)
        ]

    controller: 'mc.core.ui.states.ListCtrl'
  }
  $stateProvider.state 'mc.resource.show', {
    url: '/{id:\\d+}'

    templateUrl: 'modelcatalogue/core/ui/state/show.html'

    resolve:
        element: ['$stateParams','catalogueElementResource', ($stateParams, catalogueElementResource) ->
          catalogueElementResource($stateParams.resource).get($stateParams.id)
        ]

    controller: 'mc.core.ui.states.ShowCtrl'
  }
  $stateProvider.state 'mc.resource.show.property', {url: '/:property?page'}

])
.run(['$rootScope', '$state', '$stateParams', ($rootScope, $state, $stateParams) ->
    # It's very handy to add references to $state and $stateParams to the $rootScope
    # so that you can access them from any scope within your applications.For example,
    # <li ui-sref-active="active }"> will set the <li> // to active whenever
    # 'contacts.list' or one of its decendents is active.
    $rootScope.$state = $state
    $rootScope.$stateParams = $stateParams
])
.run(['$templateCache', ($templateCache) ->

  $templateCache.put 'modelcatalogue/core/ui/state/parent.html', '''
    <ui-view></ui-view>
  '''

  $templateCache.put 'modelcatalogue/core/ui/state/list.html', '''
    <div ng-show="list.total">
      <h2>{{title}} List</h2>
      <decorated-list list="list"></decorated-list>
    </div>
  '''

  $templateCache.put 'modelcatalogue/core/ui/state/show.html', '''
    <div ng-show="element">
      <catalogue-element-view element="element"></catalogue-element-view>
    </div>
  '''
])
# debug states
#.run(['$rootScope', '$log', ($rootScope, $log) ->
#  $rootScope.$on '$stateChangeSuccess', (event, toState, toParams, fromState, fromParams) ->
#    $log.info "$stateChangeSuccess", toState, toParams, fromState, fromParams
#])