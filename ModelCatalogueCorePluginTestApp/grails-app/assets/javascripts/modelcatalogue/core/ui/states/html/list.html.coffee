angular.module('modelcatalogue.core.ui.states.html.list.html', [
  'modelcatalogue.core.ui.states.components.infiniteList',
  'modelcatalogue.core.ui.states.components.infiniteTable'
]).run(['$templateCache', ($templateCache) ->

  #language=HTML
  $templateCache.put 'modelcatalogue/core/ui/state/list.html', '''
    <div class="row">
      <div class="col-md-12">
        <h3><small ng-class="catalogue.getIcon(resource)"></small>&nbsp;<span ng-show="$stateParams.status">{{natural($stateParams.status)}}</span> {{title}}</h3>
        <infinite-list  ng-if="$stateParams.display == 'list'"  list="list"></infinite-list>
        <infinite-table ng-if="$stateParams.display != 'list'"  list="list" columns="columns" ></infinite-table>
      </div>
    </div>
  '''
])
