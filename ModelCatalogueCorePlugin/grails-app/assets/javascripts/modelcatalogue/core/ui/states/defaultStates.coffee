angular.module('mc.core.ui.states.defaultStates', ['ui.router'])
.controller('mc.core.ui.states.ShowCtrl', ['$scope', '$stateParams', '$log', 'element', ($scope, $stateParams, $log, element) ->
    $scope.element  = element
    $scope.property = $stateParams.property

    @
])
.controller('mc.core.ui.states.ListCtrl', ['$scope', '$stateParams', '$log', 'list', ($scope, $stateParams, $log, list) ->
    $scope.list = list

    @
])
.config(['$stateProvider', ($stateProvider)->

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
    url: '/all'

    templateUrl: 'modelcatalogue/core/ui/state/list.html'

    resolve:
        list: ['$stateParams','catalogueElementResource', ($stateParams,catalogueElementResource) ->
          catalogueElementResource($stateParams.resource).list()
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
  $stateProvider.state 'mc.resource.show.property', {url: '/:property'}

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
      <decorated-list list="list"></decorated-list>
    </div>
  '''

  $templateCache.put 'modelcatalogue/core/ui/state/show.html', '''
    <div ng-show="element">
      <catalogue-element-view element="element" property="property"></catalogue-element-view>
    </div>
  '''
])