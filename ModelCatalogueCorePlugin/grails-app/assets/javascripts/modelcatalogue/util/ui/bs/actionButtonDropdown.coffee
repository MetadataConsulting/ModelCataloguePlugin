angular.module('mc.util.ui.bs.actionButtonDropdown', ['mc.util.ui.actionButtonDropdown']).run [ '$templateCache', ($templateCache) ->
  $templateCache.put 'modelcatalogue/util/ui/actionButtonDropdown.html', '''
      <div class="btn-group" dropdown>
          <button ng-if="!action.abstract" ng-disabled="action.disabled" title="{{action.label}}" type="button" class="btn" ng-class="'btn-' + action.type + ' btn-' + size" ng-click="action.run()"><span ng-show="action.icon" class="glyphicon" ng-class="'glyphicon-' + action.icon"></span><span ng-hide="iconOnly"> {{action.label}}</span></button>
          <button ng-if="!action.abstract" ng-disabled="action.disabled" title="{{action.label}}" type="button" class="btn dropdown-toggle" ng-class="'btn-' + action.type + ' btn-' + size">
            <span class="caret"></span>
            <span class="sr-only">Split button!</span>
          </button>
          <button ng-if=" action.abstract" ng-disabled="action.disabled" type="button" class="btn dropdown-toggle" ng-class="'btn-' + action.type + ' btn-' + size">
            <span ng-show="action.icon" class="glyphicon" ng-class="'glyphicon-' + action.icon"></span></span><span ng-hide="iconOnly"> {{action.label}}</span> <span class="caret"></span>
          </button>

          <ul class="dropdown-menu" role="menu">
            <li ng-repeat="childAction in action.children"><a ng-click="childAction.run()" ng-class="{'disabled': childAction.disabled}"><span ng-show="childAction.icon" class="glyphicon" ng-class="'glyphicon-' + childAction.icon"></span></span><span ng-hide="iconOnly"> {{action.label}}</span></a></li>
            <!--<li class="divider"></li>-->
          </ul>
      </div>
    '''
]