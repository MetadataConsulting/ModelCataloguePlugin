angular.module('mc.util.ui.bs.menuItemSingle', ['mc.util.ui.menuItemSingle']).run [ '$templateCache', ($templateCache) ->
  $templateCache.put 'modelcatalogue/util/ui/menuItemSingle.html', '''
        <li id="{{action.id}}-menu-item-link" ng-class="{'active': action.active, 'disabled': action.disabled}"><a id="{{action.id}}-menu-item-link" ng-click="action.run()"><span ng-show="action.icon" ng-class="action.icon"></span><span ng-show="action.icon">&nbsp;</span> {{action.label}}</a></li>
    '''
]