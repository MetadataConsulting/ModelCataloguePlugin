angular.module('modelcatalogue.core.ui.states.dataModel.csvTransformations.show.template', [
  'modelcatalogue.core.ui.states.dataModel.csvTransformations.show.components.csvTransformationView']).run(['$templateCache', ($templateCache) ->

  $templateCache.put 'modelcatalogue/core/ui/state/csvTransformation.html', '''
    <div ng-show="element">
      <csv-transformation-view element="element"></csv-transformation-view>
    </div>
  '''

])
