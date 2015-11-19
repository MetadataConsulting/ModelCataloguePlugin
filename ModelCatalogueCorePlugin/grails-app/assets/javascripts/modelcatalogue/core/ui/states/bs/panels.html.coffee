
angular.module('mc.core.ui.states.bs.panels.html', []).run(['$templateCache', ($templateCache) ->

  #language=HTML
  $templateCache.put 'modelcatalogue/core/ui/state/panels.html', '''
    <div class="row">
      <div class="col-md-12">
        <infinite-list heading="title" on-create-requested="createElement()" list="list" no-actions="true"></infinite-list>
      </div>
    </div>
  '''
])