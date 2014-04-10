angular.module('mc.core.ui.bs.catalogueElementTreeviewItem', ['mc.core.ui.catalogueElementTreeviewItem', 'mc.core.ui.decoratedList', 'ui.bootstrap']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/catalogueElementTreeviewItem.html', '''
    <li class="catalogue-element-treeview-item">
      <span class="catalogue-element-treeview-labels" ng-click="select(element)" ng-class="{'active': active}">
      <span ng-if="!element.elementType"><a class="btn btn-link"><span class="glyphicon glyphicon-ban-circle"></span></a> No Data</span>
      <span class="badge pull-right" ng-if="currentDescend">{{numberOfChildren}}</span>
      <a ng-if="currentDescend &amp;&amp; element.elementType" ng-click="collapseOrExpand()" class="btn btn-link">
        <span class="glyphicon glyphicon-folder-close" ng-if="collapsed"></span>
        <span class="glyphicon glyphicon-folder-open" ng-if="!collapsed"></span>
      </a>
      <a ng-if="!currentDescend &amp;&amp; element.elementType" class="btn btn-link">
        <span class="glyphicon glyphicon-file"></span>
      </a>
      <span class="catalogue-element-treeview-name">{{element.name}}</span>
      <a ng-click="element.show()" class="btn btn-link btn-xs" title="Show" ng-if="element.elementType"><span class="glyphicon glyphicon-link"></span></a>
      </span>
      <ul ng-if="children" ng-hide="collapsed" class="catalogue-element-treeview-list">
        <catalogue-element-treeview-item element="child" descend="nextDescend" ng-repeat="child in children track by $index"></catalogue-element-treeview-item>
        <li ng-if="hasMore" class="catalogue-element-treeview-item">
          <span class="catalogue-element-treeview-labels" ng-click="showMore()">
            <a ng-click="showMore()" class="btn btn-link"><span class="glyphicon glyphicon-chevron-down"></span></a> <a ng-click="showMore()">Show more</a>
          </span>
        </li>
      </ul>
    </li>
    '''
  ]