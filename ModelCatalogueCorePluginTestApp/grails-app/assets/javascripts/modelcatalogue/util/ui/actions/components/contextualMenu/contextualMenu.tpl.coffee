angular.module('modelcatalogue.util.ui.actions.components.contextualMenu').run [ '$templateCache', ($templateCache) ->
  $templateCache.put 'modelcatalogue/util/ui/contextualMenu.html', '''
      <ul class="nav navbar-nav" ng-class="{'navbar-right' : right}"></ul>
    '''
]
