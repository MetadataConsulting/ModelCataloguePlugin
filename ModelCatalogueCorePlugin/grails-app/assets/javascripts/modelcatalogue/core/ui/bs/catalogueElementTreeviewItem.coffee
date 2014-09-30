cetiModule = angular.module('mc.core.ui.bs.catalogueElementTreeviewItem', ['mc.core.ui.catalogueElementTreeviewItem', 'mc.core.ui.decoratedList', 'ui.bootstrap'])
cetiModule.config ['$tooltipProvider', ($tooltipProvider) ->
  $tooltipProvider.setTriggers mouseover: 'mouseout'
]
cetiModule.run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/catalogueElementTreeviewItem.html', '''
    <li class="catalogue-element-treeview-item">
      <div class="catalogue-element-treeview-text-content" ng-class="{'active': active}">
        <span class="badge pull-right" ng-if="currentDescend &amp;&amp; numberOfChildren">{{numberOfChildren}}</span>
        <span
            class="catalogue-element-treeview-labels"
            ng-dblclick="element.show()"

          >
          <span ng-if="!element.elementType"><a class="btn btn-link"><span class="glyphicon glyphicon-ban-circle"></span></a> No Data</span>
          <a ng-if="currentDescend &amp;&amp; element.elementType" class="btn btn-link" ng-click="collapseOrExpand(element)">
            <span class="glyphicon glyphicon-folder-close"  ng-if="collapsed &amp;&amp; numberOfChildren &amp;&amp; !loadingChildren"></span>
            <span class="glyphicon glyphicon-folder-open"   ng-if="!collapsed &amp;&amp; numberOfChildren &amp;&amp; !loadingChildren"></span>
            <span ng-class="element.getIcon()"              ng-if="!numberOfChildren"></span>
            <span class="glyphicon glyphicon-refresh"       ng-if="loadingChildren" ></span>
          </a>
          <a ng-if="!currentDescend &amp;&amp; element.elementType" class="btn btn-link" ng-click="select(element)">
            <span ng-class="element.getIcon()"></span>
          </a>
          <span class="catalogue-element-treeview-name" ng-class="{'text-warning': element.status == 'DRAFT', 'text-info': element.status == 'PENDING', 'text-danger': element.status == 'ARCHIVED'}" ng-click="select(element)">{{element.metadata.name || element.metadata.Name || element.name}} <small class="text-muted" ng-show="element.metadata.name || element.metadata.Name &amp;&amp; ((element.metadata.name || element.metadata.Name) != element.name)">{{element.name}}</small></span>
        </span>
      </div>
      <ul ng-if="children" ng-hide="collapsed" class="catalogue-element-treeview-list">
        <catalogue-element-treeview-item element="child" descend="nextDescend" repeat="repeat" ng-repeat="child in children track by $index" root-id="rootId"></catalogue-element-treeview-item>
        <li ng-if="hasMore" class="catalogue-element-treeview-item">
          <span class="catalogue-element-treeview-labels" ng-click="showMore()">
            <a class="btn btn-link"><span class="glyphicon glyphicon-chevron-down"></span></a> <a>Show more</a>
          </span>
        </li>
      </ul>
    </li>
    '''
  ]