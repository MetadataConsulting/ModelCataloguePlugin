angular.module('mc.util.ui.bs.contextualActions', ['mc.util.ui.contextualActions', 'mc.util.ui.bs.actionButtonSingle', 'mc.util.ui.bs.actionButtonDropdown']).run [ '$templateCache', ($templateCache) ->
  $templateCache.put 'modelcatalogue/util/ui/contextualActions.html', '''
      <div class="contextual-actions" ng-class="{'btn-group': group, 'btn-toolbar': !group }"></div>
    '''
]