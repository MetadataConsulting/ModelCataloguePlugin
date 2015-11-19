angular.module('mc.core.ui.states.bs.diff.html', []).run(['$templateCache', ($templateCache) ->

  $templateCache.put 'modelcatalogue/core/ui/state/diff.html', '''
    <span class="contextual-actions-right">
      <contextual-actions size="sm" no-colors="true" role="item"></contextual-actions>
    </span>
    <h2>Comparison</h2>
    <diff-table elements="elements"></diff-table>
  '''
])