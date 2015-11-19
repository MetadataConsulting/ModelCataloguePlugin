angular.module('mc.core.ui.states.bs.omnisearch.html', []).run(['$templateCache', ($templateCache) ->

  $templateCache.put 'modelcatalogue/core/ui/omnisearch.html', '''
  <form show-if-logged-in class="navbar-form navbar-right navbar-input-group search-form hidden-xs" role="search" autocomplete="off" ng-submit="search()" ng-controller="mc.core.ui.states.controllers.SearchCtrl">
      <a ng-click="clearSelection()" ng-class="{'invisible': !$stateParams.q}" class="clear-selection btn btn-link"><span class="glyphicon glyphicon-remove"></span></a>
      <div class="form-group">
          <input
                 ng-model="searchSelect"
                 type="text"
                 name="search-term"
                 id="search-term"
                 placeholder="Search"
                 typeahead="result.term as result.label for result in getResults($viewValue)"
                 typeahead-on-select="search($item, $model, $label)"
                 typeahead-template-url="modelcatalogue/core/ui/omnisearchItem.html"
                 #{ ###typeahead-wait-ms="300" ### }
                 class="form-control"
                 ng-class="{'expanded': searchSelect}"
          >
      </div>
      <button class="btn btn-default" ng-click="select(searchSelect)"><i class="glyphicon glyphicon-search"></i></button>
  </form>
  '''

  $templateCache.put 'modelcatalogue/core/ui/omnisearchItem.html', '''
    <a>
        <span class="omnisearch-icon" ng-class="match.model.icon"></span>
        <span class="omnisearch-text" ng-if="!match.model.highlight" bind-html-unsafe="match.label" ng-class="{'text-warning': match.model.element.status == 'DRAFT', 'text-danger': match.model.element.status == 'DEPRECATED', 'text-info': match.model.element.status == 'PENDING'}"></span>
        <span class="omnisearch-text" ng-if=" match.model.highlight" bind-html-unsafe="match.label | typeaheadHighlight:query" ng-class="{'text-warning': match.model.element.status == 'DRAFT', 'text-danger': match.model.element.status == 'DEPRECATED', 'text-info': match.model.element.status == 'PENDING'}"></span>
    </a>
  '''

])