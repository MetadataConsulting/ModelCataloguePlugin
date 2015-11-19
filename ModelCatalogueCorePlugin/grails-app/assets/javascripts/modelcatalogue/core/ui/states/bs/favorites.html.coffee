angular.module('mc.core.ui.states.bs.favorites.html', []).run(['$templateCache', ($templateCache) ->

  #language=HTML
  $templateCache.put 'modelcatalogue/core/ui/state/favorites.html', '''
    <div class="row">
      <span class="contextual-actions-right">
        <contextual-actions size="sm" no-colors="true" role="list"></contextual-actions>
      </span>
      <h2><small class="fa fa-fw fa-star text-muted"></small>Favourites</h2>
      <infinite-table list="list" columns="columns" transform="$element.relation"></infinite-table>
    </div>
  '''
])