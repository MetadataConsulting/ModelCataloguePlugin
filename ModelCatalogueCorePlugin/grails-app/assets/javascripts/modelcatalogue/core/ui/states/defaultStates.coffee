angular.module('mc.core.ui.states.defaultStates', ['ui.router'])
.controller('mc.core.ui.states.ShowCtrl', ['$scope', '$stateParams', '$state', '$log', 'element', ($scope, $stateParams, $state, $log, element) ->
    $scope.element  = element
])
.controller('mc.core.ui.states.ListCtrl', ['$scope', '$stateParams', '$state', '$log', 'list', 'names', 'enhance', ($scope, $stateParams, $state, $log, list, names, enhance) ->
    listEnhancer    = enhance.getEnhancer('list')

    $scope.list                     = list
    $scope.title                    = names.getNaturalName($stateParams.resource)
    $scope.resource                 = $stateParams.resource
    $scope.containedElements        = listEnhancer.createEmptyList('org.modelcatalogue.core.DataElement')
    $scope.selectedElement          = if list.size > 0 then list.list[0] else {name: 'No Selection'}
    $scope.containedElementsColumns = [
      {header: 'Name',          value: "relation.name",        classes: 'col-md-6', show: "relation.show()"}
      {header: 'Description',   value: "relation.description", classes: 'col-md-6'}
    ]

    if $scope.resource == 'model'

      for item in list
        item._containedElements_ = listEnhancer.createEmptyList('org.modelcatalogue.core.DataElement')

      $scope.$on 'treeviewElementSelected', (event, element) ->
        unless element._containedElements_?.empty
          element.contains().then (contained)->
            element._containedElements_ = contained
            $scope.containedElements    = contained
        $scope.selectedElement          = element
        $scope.containedElements        = element._containedElements_ ? listEnhancer.createEmptyList('org.modelcatalogue.core.DataElement')
])
.config(['$stateProvider', ($stateProvider) ->

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
          # it's safe to call top level for each controller, only model controller will respond on it
          catalogueElementResource($stateParams.resource).list(offset: (page - 1) * DEFAULT_ITEMS_PER_PAGE, toplevel: true)
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
    <div ng-if="list.total &amp;&amp; resource != 'model'">
      <h2>{{title}} List</h2>
      <decorated-list list="list"></decorated-list>
    </div>
    <div ng-if="resource == 'model'">
      <div class="row" ng-repeat="element in list.list">
        <div class="col-md-12"><h2>{{element.name}}</h2></div>
        <div class="col-md-4">
          <catalogue-element-treeview element="element" descend="'parentOf'"></catalogue-element-treeview>
        </div>
        <div class="col-md-8">
          <h3 ng-show="selectedElement">{{selectedElement.name}} Data Elements</h3>
          <decorated-list list="containedElements" columns="containedElementsColumns"></decorated-list>
        </div>
        <hr/>
      </div>
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