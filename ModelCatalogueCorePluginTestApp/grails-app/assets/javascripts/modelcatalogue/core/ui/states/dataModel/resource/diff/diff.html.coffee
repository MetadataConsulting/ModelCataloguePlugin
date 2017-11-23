angular.module('modelcatalogue.core.ui.states.dataModel.resource.diff.html', []).run(['$templateCache', ($templateCache) ->

  $templateCache.put 'modelcatalogue/core/ui/state/diff.html', '''
    <h2>Comparison</h2>
    <diff-table elements="elements"></diff-table>
  '''
])
