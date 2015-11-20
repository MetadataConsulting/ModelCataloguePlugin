angular.module('mc.util.ui.bs.menuItemSingle', ['mc.util.ui.menuItemSingle']).run [ '$templateCache', ($templateCache) ->
  $templateCache.put 'modelcatalogue/util/ui/menuItemSingle.html', '''
        <li id="{{::action.id}}-menu-item" ng-class="::action.$$class"><a id="{{::action.id}}-menu-item-link menu-item-link" ng-click="action.run()"><span ng-show="::action.icon" ng-class="::action.icon" title="{{::action.label}}"></span><span ng-show="::action.icon">&nbsp;</span><span ng-if="::!action.iconOnly"> {{::action.label}}</span></a></li>
    '''
]