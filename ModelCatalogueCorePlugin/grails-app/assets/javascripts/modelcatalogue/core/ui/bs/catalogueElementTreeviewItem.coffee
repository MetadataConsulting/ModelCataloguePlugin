angular.module('mc.core.ui.bs.catalogueElementTreeviewItem', ['mc.core.ui.catalogueElementTreeviewItem', 'mc.core.ui.decoratedList', 'ui.bootstrap']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/catalogueElementTreeviewItem.html', '''
    <li class="catalogue-element-treeview-item">
      <div class="catalogue-element-treeview-text-content" ng-class="{'active': active}">
        <span class="badge pull-right" ng-if="currentDescend">{{numberOfChildren}}</span>
        <span class="catalogue-element-treeview-labels" ng-dblclick="element.show()">
          <span ng-if="!element.elementType"><a class="btn btn-link"><span class="glyphicon glyphicon-ban-circle"></span></a> No Data</span>
          <a ng-if="currentDescend &amp;&amp; element.elementType" ng-click="collapseOrExpand()" class="btn btn-link">
            <span class="glyphicon glyphicon-folder-close" ng-if="collapsed &amp;&amp; !loadingChildren"></span>
            <span class="glyphicon glyphicon-folder-open" ng-if="!collapsed &amp;&amp; !loadingChildren"></span>
            <span class="glyphicon glyphicon-refresh" ng-if="loadingChildren"></span>
          </a>
          <a ng-if="!currentDescend &amp;&amp; element.elementType" class="btn btn-link">
            <span class="glyphicon glyphicon-file"></span>
          </a>
          <span class="catalogue-element-treeview-name" ng-click="select(element)">{{element.name}}</span>
          <a ng-click="element.show()" class="btn btn-link btn-xs" title="Show" ng-if="element.elementType"><span class="glyphicon glyphicon-link"></span></a>
        </span>
      </div>
      <ul ng-if="children" ng-hide="collapsed" class="catalogue-element-treeview-list">
        <catalogue-element-treeview-item element="child" descend="nextDescend" repeat="repeat" ng-repeat="child in children track by $index" root-id="rootId"></catalogue-element-treeview-item>
        <li ng-if="hasMore" class="catalogue-element-treeview-item">
          <span class="catalogue-element-treeview-labels" ng-click="showMore()">
            <a ng-click="showMore()" class="btn btn-link"><span class="glyphicon glyphicon-chevron-down"></span></a> <a ng-click="showMore()">Show more</a>
          </span>
        </li>
      </ul>
    </li>
    '''
  ]