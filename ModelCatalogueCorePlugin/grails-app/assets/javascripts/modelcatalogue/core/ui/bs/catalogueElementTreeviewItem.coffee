cetiModule = angular.module('mc.core.ui.bs.catalogueElementTreeviewItem', ['mc.core.ui.catalogueElementTreeviewItem', 'ui.bootstrap'])
cetiModule.config ['$tooltipProvider', ($tooltipProvider) ->
  $tooltipProvider.setTriggers mouseover: 'mouseout'
]
cetiModule.run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/catalogueElementTreeviewItem.html', '''
    <li class="catalogue-element-treeview-item">
      <div class="catalogue-element-treeview-text-content" ng-class="{'active': element.$$active, 'archived': element.$$archived}">
        <span class="badge pull-right" ng-if="currentDescend &amp;&amp; element.$$numberOfChildren">{{element.$$numberOfChildren}}</span>
        <span class="catalogue-element-treeview-labels">
          <span ng-if="!element.elementType"><a class="catalogue-element-treeview-icon btn btn-link"><span class="fa fa-fw fa-ban"></span></a> No Data</span>
          <a ng-if="currentDescend &amp;&amp; element.elementType" class="catalogue-element-treeview-icon  btn btn-link" ng-click="select(element)">
            <span class="fa fa-fw fa-folder"  ng-if="element.$$collapsed &amp;&amp; element.$$numberOfChildren &amp;&amp; !element.$$loadingChildren"></span>
            <span class="fa fa-fw fa-folder-open"   ng-if="!element.$$collapsed &amp;&amp; element.$$numberOfChildren &amp;&amp; !element.$$loadingChildren"></span>
            <span ng-class="element.getIcon()"              ng-if="!element.$$numberOfChildren"></span>
            <span class="fa fa-fw fa-refresh"       ng-if="element.$$loadingChildren" ></span>
          </a>
          <a ng-if="!currentDescend &amp;&amp; element.elementType" class="catalogue-element-treeview-icon btn btn-link">
            <span ng-class="element.getIcon()"></span>
          </a>
          <span class="catalogue-element-treeview-name" ng-class="{'text-warning': element.status == 'DRAFT', 'text-info': element.status == 'PENDING', 'text-danger': (element.status == 'DEPRECATED' || element.undone)}" ng-click="select(element)">{{element.$$localName || element.name}}<small class="text-muted" ng-if="element.$$localName"> {{element.name}}</small><small class="text-muted"> <span ng-if="element.latestVersionId" class="catalogue-element-treeview-version-number">{{element.latestVersionId}}.{{element.versionNumber}}</span></small></span>
          <small class="text-muted" ng-if="element.href &amp;&amp; element.href()"> <a class="catalogue-element-treeview-link" ng-href="{{element.href()}}" title="{{element.$$localName || element.name}}" target="_blank"><span class="fa fa-external-link"></span></a></small>
        </span>
      </div>
      <ul ng-if="element.$$children" ng-hide="element.$$collapsed" class="catalogue-element-treeview-list">
        <catalogue-element-treeview-item element="child" descend="nextDescend" repeat="repeat" treeview="treeview" ng-repeat="child in element.$$children track by $index"></catalogue-element-treeview-item>
        <li ng-if="element.$$numberOfChildren > element.$$children.length" class="catalogue-element-treeview-item">
          <span class="catalogue-element-treeview-labels" ng-click="element.$$showMore()">
            <a class="catalogue-element-treeview-icon btn btn-link catalogue-element-treeview-show-more"><span class="fa fa-fw fa-chevron-down"></span></a> <a class="text-muted">Show more</a>
          </span>
        </li>
      </ul>
    </li>
    '''
  ]