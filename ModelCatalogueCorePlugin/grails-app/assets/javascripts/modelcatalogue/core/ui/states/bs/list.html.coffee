angular.module('mc.core.ui.states.bs.list.html', []).run(['$templateCache', ($templateCache) ->

  #language=HTML
  $templateCache.put 'modelcatalogue/core/ui/state/list.html', '''
    <div class="row">
      <span class="contextual-actions-right">
        <contextual-actions size="sm" no-colors="true" role="list"></contextual-actions>
      </span>
      <h2><small ng-class="catalogue.getIcon(resource)"></small>&nbsp;<span ng-show="$stateParams.status">{{natural($stateParams.status)}}</span> {{title}}</h2>
      <infinite-list  ng-if="$stateParams.display == 'grid'"  list="list"></infinite-list>
      <infinite-table ng-if="$stateParams.display != 'grid'"  list="list" columns="columns" ></infinite-table>
    </div>
  '''
])