angular.module('mc.core.ui.bs.decoratedListTable', ['mc.core.ui.decoratedList', 'ngSanitize']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/decoratedList.html', '''
    <div>
      <table ng-show="list.total>0" class="dl-table table">
        <thead>
          <tr class="dl-table-header-row" ng-switch="hasSelection()">
            <th class="dl-table-select-all-cell col-md-1" ng-switch-when="true">
              <input type="checkbox" ng-change="updateSelectAll(allSelected)" ng-model="allSelected">
            </th>
            <th class="dl-table-header-cell" ng-repeat="column in columns" ng-class="evaluateClasses(column.classes)">
              <span ng-if="!column.sort">{{column.header}}</span>
              <a class="dl-table-header-sortable" ng-click="sortBy(column)" ng-if="column.sort">
                <span class="glyphicon" ng-class="getSortClass(column)"></span>
                {{column.header}}
              </a>
            </th>
            <th ng-if="actions" ng-class="getActionsClass()">
              &nbsp;
            </th>
          </tr>
        </thead>
        <tbody>
             <tr class="dl-table-item-row" ng-repeat="element in list.list" ng-switch="hasSelection()" ng-class="{'warning': element.status == 'DRAFT' || element.relation.status == 'DRAFT', 'info': element.status == 'PENDING' || element.relation.status == 'PENDING', 'danger': element.status == 'ARCHIVED' || element.relation.status == 'ARCHIVED'}">
              <td class="dl-table-select-item-cell" ng-switch-when="true">
                <input type="checkbox" ng-change="updateSelection()" ng-model="element._selected">
              </td>
              <td class="dl-table-item-cell" ng-class="evaluateClasses(column.classes, evaluateValue(column.value, element), element)" ng-repeat="column in columns" ng-switch="showEnabled(column.show, element)"><a ng-href="{{element.href()}}" ng-switch-when="link">{{evaluateValue(column.value, element)}}</a><a ng-click="showItem(column.show, element)" ng-switch-when="true">{{evaluateValue(column.value, element)}}</a><span ng-switch-when="false"><span ng-bind-html="evaluateValue(column.value, element) == null ? '' : '' + evaluateValue(column.value, element)"></span></span></td>
              <td class="dl-table-actions-item-cell" ng-if="actions">
                <div class="btn-group btn-group-xs">
                  <a ng-repeat="action in actions" class="btn btn-xs" ng-class="getActionClass(action)" ng-click="performAction(action.action, element, list)"><span ng-if="action.icon" class="glyphicon" ng-class="'glyphicon-' + action.icon"></span><span ng-if="action.icon &amp;&amp; action.title">&nbsp;&nbsp;</span>{{action.title}}</a>
                </div>
              </td>
            </tr>
        </tbody>
        <tfoot>
          <tr ng-hide="!list.total || list.total <= list.page">
            <td colspan="{{getColumnsCount()}}" class="text-center">
                <ul class="pagination">
                  <li class="previous dl-table-prev" ng-class="{disabled: !hasPrevious()}"><a ng-click="previous()">Previous</a></li>
                  <li ng-show="hasMorePrevPages"><a ng-click="goto(pages[0] - 1)" class>...</a></li>
                  <li ng-repeat="page in pages"  ng-class="{active: page == list.currentPage}"><a ng-click="goto(page)">{{page}}</a></li>
                  <li ng-show="hasMoreNextPages"><a ng-click="goto(pages[pages.length - 1] + 1)" class>...</a></li>
                  <li class="next dl-table-next" ng-class="{disabled: !hasNext()}"><a ng-click="next()">Next</a></li>
                </ul>
                <ul class="pagination pull-right">
                  <li ng-repeat="theMax in [10, 50, 100]"  ng-class="{active: list.page == theMax}"><a ng-click="setMax(theMax)">{{theMax}}</a></li>
                </ul>
            </td>
          </tr>
        </tfoot>
      </table>
      <div ng-show="list.total==0">
        <div class="alert alert-warning">Empty</div>
      </div>
    </div>
    '''
  ]