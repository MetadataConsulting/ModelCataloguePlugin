angular.module('mc.util.ui.bs.contextualMenu', ['mc.util.ui.contextualMenu', 'mc.util.ui.bs.menuItemSingle', 'mc.util.ui.bs.menuItemDropdown']).run [ '$templateCache', ($templateCache) ->
  $templateCache.put 'modelcatalogue/util/ui/contextualMenu.html', '''
      <ul class="nav navbar-nav" ng-class="{'navbar-right' : right}"></ul>
    '''
]