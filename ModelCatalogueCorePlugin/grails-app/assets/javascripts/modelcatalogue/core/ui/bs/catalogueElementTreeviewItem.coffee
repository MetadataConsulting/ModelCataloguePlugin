cetiModule = angular.module('mc.core.ui.bs.catalogueElementTreeviewItem', ['mc.core.ui.catalogueElementTreeviewItem', 'ui.bootstrap'])
cetiModule.config ['$tooltipProvider', ($tooltipProvider) ->
  $tooltipProvider.setTriggers mouseover: 'mouseout'
]
cetiModule.run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/catalogueElementTreeviewItem.html', '''
    <li class="catalogue-element-treeview-item">
      <div class="catalogue-element-treeview-text-content" ng-class="{'active': element.$$active, 'archived': element.$$archived}">
        <span class="badge pull-right" ng-if="currentDescend &amp;&amp; element.$$numberOfChildren">{{element.$$numberOfChildren}}</span>
        <span
            class="catalogue-element-treeview-labels"
            ng-dblclick="element.show()"

          >
          <span ng-if="!element.elementType"><a class="btn btn-link"><span class="glyphicon glyphicon-ban-circle"></span></a> No Data</span>
          <a ng-if="currentDescend &amp;&amp; element.elementType" class="btn btn-link" ng-click="collapseOrExpand(element)">
            <span class="glyphicon glyphicon-folder-close"  ng-if="element.$$collapsed &amp;&amp; element.$$numberOfChildren &amp;&amp; !element.$$loadingChildren"></span>
            <span class="glyphicon glyphicon-folder-open"   ng-if="!element.$$collapsed &amp;&amp; element.$$numberOfChildren &amp;&amp; !element.$$loadingChildren"></span>
            <span ng-class="element.getIcon()"              ng-if="!element.$$numberOfChildren"></span>
            <span class="glyphicon glyphicon-refresh"       ng-if="element.$$loadingChildren" ></span>
          </a>
          <a ng-if="!currentDescend &amp;&amp; element.elementType" class="btn btn-link" ng-click="select(element)">
            <span ng-class="element.getIcon()"></span>
          </a>
          <span class="catalogue-element-treeview-name" ng-class="{'text-warning': element.status == 'DRAFT', 'text-info': element.status == 'PENDING', 'text-danger': (element.status == 'DEPRECATED' || element.undone)}" ng-click="select(element)">{{element.$$localName || element.name}}<small class="text-muted" ng-if="element.$$localName"> {{element.name}}</small><small class="text-muted"> {{element.versionNumber}}</small></span>
          <small class="text-muted" ng-if="element.href"> <a class="catalogue-element-treeview-link" ng-href="{{element.href()}}" title="{{element.$$localName || element.name}}"><span class="fa fa-external-link"></span></a></small>
        </span>
      </div>
      <ul ng-if="element.$$children" ng-hide="element.$$collapsed" class="catalogue-element-treeview-list">
        <catalogue-element-treeview-item element="child" descend="nextDescend" repeat="repeat" ng-repeat="child in element.$$children track by $index" root-id="rootId"></catalogue-element-treeview-item>
        <li ng-if="element.$$numberOfChildren > element.$$children.length" class="catalogue-element-treeview-item">
          <span class="catalogue-element-treeview-labels" ng-click="element.$$showMore()">
            <a class="btn btn-link catalogue-element-treeview-show-more"><span class="glyphicon glyphicon-chevron-down"></span></a> <a class="text-muted">Show more</a>
          </span>
        </li>
      </ul>
    </li>
    '''
  ]