angular.module('modelcatalogue.core.ui.states.html.show.html', [
  'modelcatalogue.core.ui.states.components.elementView'
]).run(['$templateCache', ($templateCache) ->


  $templateCache.put 'modelcatalogue/core/ui/state/show.html', '''
    <div ng-if="element">
      <element-view element="element" display-only="displayOnly"></element-view>
    </div>
  '''
])
