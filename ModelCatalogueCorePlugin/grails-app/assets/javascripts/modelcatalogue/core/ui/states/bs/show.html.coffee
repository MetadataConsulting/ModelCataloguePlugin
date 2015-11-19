angular.module('mc.core.ui.states.bs.show.html', []).run(['$templateCache', ($templateCache) ->


  $templateCache.put 'modelcatalogue/core/ui/state/show.html', '''
    <div ng-if="element">
      <catalogue-element-view element="element"></catalogue-element-view>
    </div>
  '''
])