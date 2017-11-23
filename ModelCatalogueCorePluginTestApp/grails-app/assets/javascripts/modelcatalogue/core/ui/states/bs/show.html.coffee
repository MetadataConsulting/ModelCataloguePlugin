angular.module('mc.core.ui.states.bs.show.html', [
  'modelcatalogue.core.ui.states.components.catalogueElementView'
]).run(['$templateCache', ($templateCache) ->


  $templateCache.put 'modelcatalogue/core/ui/state/show.html', '''
    <div ng-if="element">
      <catalogue-element-view element="element" display-only="displayOnly"></catalogue-element-view>
    </div>
  '''
])
