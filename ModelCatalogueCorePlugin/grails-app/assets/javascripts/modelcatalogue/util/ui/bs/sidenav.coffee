angular.module('mc.util.ui.bs.sidenav', ['mc.util.ui.sidenav', 'ui.bootstrap']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/util/ui/sidenav.html', '''
      <div class="sidenav">
            <div class="list-group">
              <a id="{{action.id}}" ng-click="action.run()" class="list-group-item" ng-class="{ 'active': action.active}" ng-repeat="action in actions" title="{{action.label}}">
                <span class="fa-2x fa-fw" ng-class="action.icon"></span>
              </a>
            </div>
      </div>
    '''
  ]