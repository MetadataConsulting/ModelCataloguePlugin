angular.module('mc.core.ui.bs.catalogueElementTreeview', ['mc.core.ui.catalogueElementTreeview', 'ui.bootstrap']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/catalogueElementTreeview.html', '''
    <div class="catalogue-element-treeview-list-container">
      <ul class="catalogue-element-treeview-list-root catalogue-element-treeview-list" ng-switch="mode">
          <span ng-if="!element.elementType &amp;&amp; !list.total"><a class="btn btn-link"><span class="glyphicon glyphicon-ban-circle"></span></a> No Data</span>
          <catalogue-element-treeview-item element="item" descend="descend" root-id="id" repeat="repeat" ng-repeat="item in list.$$children"></catalogue-element-treeview-item>
          <li ng-switch-when="list" ng-if="list.total > list.$$children.length" class="catalogue-element-treeview-item">
            <span class="catalogue-element-treeview-labels" ng-click="showMore()">
              <a class="btn btn-link"><span class="glyphicon glyphicon-chevron-down"></span></a> <a>Show more</a>
            </span>
          </li>
          <catalogue-element-treeview-item element="element" descend="descend" root-id="id" repeat="repeat" ng-switch-when="element"></catalogue-element-treeview-item>
      </ul>
    </div>
    '''
  ]