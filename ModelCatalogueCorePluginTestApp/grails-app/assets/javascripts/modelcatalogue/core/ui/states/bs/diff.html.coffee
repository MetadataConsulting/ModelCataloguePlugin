angular.module('mc.core.ui.states.bs.diff.html', []).run(['$templateCache', ($templateCache) ->

  $templateCache.put 'modelcatalogue/core/ui/state/diff.html', '''
    <h2>Comparison</h2>
    <diff-table elements="elements"></diff-table>
  '''
])