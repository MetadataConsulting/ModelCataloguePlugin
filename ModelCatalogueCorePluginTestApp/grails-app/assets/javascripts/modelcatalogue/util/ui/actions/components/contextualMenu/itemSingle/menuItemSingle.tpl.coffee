angular.module('modelcatalogue.util.ui.actions.components.contextualMenu.itemSingle').run [ '$templateCache', ($templateCache) ->
  $templateCache.put 'modelcatalogue/util/ui/menuItemSingle.html', '''
        <li id="{{::action.id}}-menu-item" ng-class="::action.$$class"><a id="{{::action.id}}-menu-item-link" class="menu-item-link" ng-click="action.run()"><span ng-if="::action.icon" ng-class="::action.icon" title="{{::action.label}}"></span><span ng-if="::(action.icon &amp;&amp; action.label)" class="action-label-space">&nbsp;</span><span ng-if="::action.label" ng-class="::action.$$class" class="action-label">{{::action.label}}</span></a></li>
    '''
]
