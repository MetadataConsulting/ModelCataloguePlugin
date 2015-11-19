angular.module('mc.core.ui.states.bs.dataImport.html', []).run(['$templateCache', ($templateCache) ->

  $templateCache.put 'modelcatalogue/core/ui/state/dataImport.html', '''
    <div ng-show="element">
      <import-view element="element"></import-view>
    </div>
  '''
])