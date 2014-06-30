angular.module('mc.util.ui.bs.contextualActions', ['mc.util.ui.contextualActions', 'mc.util.ui.bs.actionButton']).run [ '$templateCache', ($templateCache) ->
  $templateCache.put 'modelcatalogue/util/ui/contextualActions.html', '''
      <span class="contextual-actions">
        <action-button action="action" ng-repeat="action in actions">
      </span>
    '''
]