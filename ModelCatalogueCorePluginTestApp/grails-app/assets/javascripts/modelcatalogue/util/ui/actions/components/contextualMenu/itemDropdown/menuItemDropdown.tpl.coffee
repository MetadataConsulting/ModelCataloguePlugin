angular.module('modelcatalogue.util.ui.actions.components.contextualMenu.itemDropdown', ['modelcatalogue.util.ui.actions.components.contextualMenu.itemSingle'])
  .run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/util/ui/menuItemDropdown.html', '''
        <li id="{{::action.id}}-menu-item" class="dropdown" ng-class="::action.$$class" ng-show="action.children.length > 0 || action.show">
              <a id="{{::action.id}}-menu-item-link" class="dropdown-toggle menu-item-link" data-toggle="dropdown"><span ng-show="::action.icon" ng-class="::action.icon" title="{{::action.label}}"></span><span ng-show="::action.icon">&nbsp;</span><span class="action-label" ng-if="::action.label" ng-class="::action.$$class">{{::action.label}} <b class="caret"></b></span></a>
              <ul class="dropdown-menu">
                  <menu-item-single action="::childAction" ng-repeat="childAction in ::action.children track by childAction.id"></menu-item-single>
              </ul>
        </li>
      '''
]
