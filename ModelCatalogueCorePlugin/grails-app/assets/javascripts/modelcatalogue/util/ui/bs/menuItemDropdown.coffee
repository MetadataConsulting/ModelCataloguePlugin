angular.module('mc.util.ui.bs.menuItemDropdown', ['mc.util.ui.menuItemDropdown', 'mc.util.ui.menuItemSingle']).run [ '$templateCache', ($templateCache) ->
  $templateCache.put 'modelcatalogue/util/ui/menuItemDropdown.html', '''
      <li id="{{::action.id}}-menu-item" class="dropdown" ng-class="{'active': action.active}" ng-show="action.children.length > 0 || action.show">
            <a id="{{::action.id}}-menu-item-link menu-item-link" class="dropdown-toggle" data-toggle="dropdown"><span ng-show="::action.icon" ng-class="::action.icon" title="{{::action.label}}"></span><span ng-show="::action.icon">&nbsp;</span> <span ng-if="::!action.iconOnly">{{::action.label}} <b class="caret"></b></span></a>
            <ul class="dropdown-menu">
                <menu-item-single action="childAction" ng-repeat="childAction in ::action.children track by $index"></menu-item-single>
            </ul>
      </li>
    '''
]