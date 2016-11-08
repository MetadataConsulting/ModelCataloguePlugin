angular.module('mc.core.ui.states.bs.list.html', []).run(['$templateCache', ($templateCache) ->

  #language=HTML
  $templateCache.put 'modelcatalogue/core/ui/state/list.html', '''
    <div class="row">
      <div class="col-md-12">
        <h3><small ng-class="catalogue.getIcon(resource)"></small>&nbsp;<span ng-show="$stateParams.status">{{natural($stateParams.status)}}</span> {{title}}</h3>
        <infinite-list  ng-if="$stateParams.display == 'grid'"  list="list"></infinite-list>
        <infinite-table ng-if="$stateParams.display != 'grid'"  list="list" columns="columns" ></infinite-table>
      </div>
    </div>
  '''
])