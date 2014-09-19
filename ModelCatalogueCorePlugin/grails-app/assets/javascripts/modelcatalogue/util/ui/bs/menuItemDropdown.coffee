angular.module('mc.util.ui.bs.menuItemDropdown', ['mc.util.ui.menuItemDropdown', 'mc.util.ui.menuItemSingle']).run [ '$templateCache', ($templateCache) ->
  $templateCache.put 'modelcatalogue/util/ui/menuItemDropdown.html', '''
      <li class="dropdown" ng-class="{'active': action.active}" ng-show="action.children.length > 0">
            <a href="#" class="dropdown-toggle" data-toggle="dropdown"><span ng-show="action.icon" ng-class="action.icon"></span><span ng-show="action.icon">&nbsp;</span>{{action.label}} <b class="caret"></b></a>
            <ul class="dropdown-menu">
                <menu-item-single action="childAction" ng-repeat="childAction in action.children track by $index"></menu-item-single>
            </ul>
      </li>
    '''
]