###
  Not used anywhere
###
angular.module('mc.core.ui.states.bs.panels.html', [
  'modelcatalogue.core.ui.states.components.infiniteList'
]).run(['$templateCache', ($templateCache) ->

  #language=HTML
  $templateCache.put 'modelcatalogue/core/ui/state/panels.html', '''
    <div class="row leave-10-before">
      <div class="col-md-12">
        <infinite-list heading="title" on-create-requested="createElement()" on-search="search($term)" list="list" no-actions="true"></infinite-list>
      </div>
    </div>
  '''
])
