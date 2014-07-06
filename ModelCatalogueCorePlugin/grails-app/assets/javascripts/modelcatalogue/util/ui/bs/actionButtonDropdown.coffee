angular.module('mc.util.ui.bs.actionButtonDropdown', ['mc.util.ui.actionButtonDropdown']).run [ '$templateCache', ($templateCache) ->
  $templateCache.put 'modelcatalogue/util/ui/actionButtonDropdown.html', '''
      <span class="btn-group" dropdown>
          <button ng-if="!action.abstract" ng-disabled="action.disabled || action.children.length == 0" title="{{action.label}}" type="button" class="btn" ng-class="'btn-' + (noColors ? 'default' : action.type) + ' btn-' + size" ng-click="action.run()"><span ng-show="action.icon" class="glyphicon" ng-class="'glyphicon-' + action.icon"></span><span ng-hide="iconOnly"> {{action.label}}</span></button>
          <button ng-if="!action.abstract" ng-disabled="action.disabled || action.children.length == 0" title="{{action.label}}" type="button" class="btn dropdown-toggle" ng-class="'btn-' + (noColors ? 'default' : action.type) + ' btn-' + size">
            <span class="caret"></span>
            <span class="sr-only">Split button!</span>
          </button>
          <button ng-if=" action.abstract" ng-disabled="action.disabled || action.children.length == 0" type="button" class="btn dropdown-toggle" ng-class="'btn-' + (noColors ? 'default' : action.type) + ' btn-' + size">
            <span ng-show="action.icon" class="glyphicon" ng-class="'glyphicon-' + action.icon"></span></span><span ng-hide="iconOnly"> {{action.label}}</span> <span class="caret"></span>
          </button>

          <ul class="dropdown-menu" role="menu">
            <li ng-repeat="childAction in action.children track by $index" ng-class="{'dropdown-header': childAction.heading, 'active': childAction.active}">
              <a    ng-hide="childAction.heading" ng-click="childAction.run()" ng-class="{'disabled': childAction.disabled}"><span ng-show="childAction.icon" class="glyphicon" ng-class="'glyphicon-' + childAction.icon"></span></span> {{childAction.label}}</a>
              <span ng-show="childAction.heading">{{childAction.label}}</span>
            </li>
          </ul>
      </span>
    '''
]