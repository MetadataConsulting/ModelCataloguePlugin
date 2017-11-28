angular.module('modelcatalogue.core.ui.states.catalogue.actions.show.batch.html', ['modelcatalogue.core.ui.states.catalogue.actions.show.components.batchView']).run(['$templateCache', ($templateCache) ->

  $templateCache.put 'modelcatalogue/core/ui/state/batch.html', '''
    <div ng-show="element">
      <batch-view batch="element"></batch-view>
    </div>
  '''

])
