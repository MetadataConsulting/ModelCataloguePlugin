angular.module('mc.util.ui.bs.actionButton', ['mc.util.ui.actionButton']).run [ '$templateCache', ($templateCache) ->
  $templateCache.put 'modelcatalogue/util/ui/actionButton.html', '''
      <span class="action-wrapper">
        <button ng-if="!action.children" ng-click="action.run()" class="btn" ng-class="'btn-' + action.type" ng-disabled="action.disabled"><span ng-show="action.icon" class="glyphicon" ng-class="'glyphicon-' + action.icon"></span> {{action.label}}</button>
        <div    ng-if=" action.children" class="btn-group" dropdown>
            <button ng-if="!action.abstract" ng-disabled="action.disabled" type="button" class="btn" ng-class="'btn-' + action.type" ng-click="action.run()"><span ng-show="action.icon" class="glyphicon" ng-class="'glyphicon-' + action.icon"></span> {{action.label}}</button>
            <button ng-if="!action.abstract" ng-disabled="action.disabled" type="button" class="btn dropdown-toggle" ng-class="'btn-' + action.type">
              <span class="caret"></span>
              <span class="sr-only">Split button!</span>
            </button>
            <button ng-if=" action.abstract" ng-disabled="action.disabled" type="button" class="btn dropdown-toggle" ng-class="'btn-' + action.type">
              <span ng-show="action.icon" class="glyphicon" ng-class="'glyphicon-' + action.icon"></span> {{action.label}} <span class="caret"></span>
            </button>

            <ul class="dropdown-menu" role="menu">
              <li ng-repeat="childAction in action.children"><a ng-click="childAction.run()" ng-class="{'disabled': childAction.disabled}"><span ng-show="childAction.icon" class="glyphicon" ng-class="'glyphicon-' + childAction.icon"></span> {{childAction.label}}</a></li>
              <!--<li class="divider"></li>-->
            </ul>
        </div>
      </span>
    '''
]