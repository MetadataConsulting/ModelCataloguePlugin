angular.module('mc.core.ui.states.bs.csvTransformation.html', []).run(['$templateCache', ($templateCache) ->

  $templateCache.put 'modelcatalogue/core/ui/state/csvTransformation.html', '''
    <div ng-show="element">
      <csv-transformation-view element="element"></csv-transformation-view>
    </div>
  '''

])