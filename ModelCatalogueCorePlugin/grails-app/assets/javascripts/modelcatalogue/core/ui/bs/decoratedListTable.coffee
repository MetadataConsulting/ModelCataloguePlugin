angular.module('mc.core.ui.bs.decoratedListTable', ['mc.core.ui.decoratedList']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/decoratedList.html', '''
      <table class="dl-table table table-hover">
        <thead>
          <tr class="dl-table-header-row" ng-switch="hasSelection()">
            <th class="dl-table-select-all-cell col-md-1" ng-switch-when="true">
              <input type="checkbox" ng-change="updateSelectAll(allSelected)" ng-model="allSelected">
            </th>
            <th class="dl-table-header-cell" ng-repeat="column in columns" ng-class="evaluateClasses(column.classes)">{{column.header}}</th>
          </tr>
        </thead>
        <tbody>
             <tr class="dl-table-item-row" ng-repeat="element in list.list" ng-switch="hasSelection()" ng-click="itemClick(element)">
              <td class="dl-table-select-item-cell" ng-switch-when="true">
                <input type="checkbox" ng-change="updateSelection()" ng-model="element._selected">
              </td>
              <td class="dl-table-item-cell" ng-class="evaluateClasses(column.classes, evaluateValue(column.value, element), element)" ng-repeat="column in columns">{{evaluateValue(column.value, element)}}</td>
            </tr>
        </tbody>
        <tfoot>
          <tr ng-hide="list.total <= list.page">
            <td colspan="{{hasSelection() ? columns.length + 1 : columns.length}}" class="text-center">
              <ul class="pagination">
                <li class="previous dl-table-prev" ng-class="{disabled: !hasPrevious()}"><a ng-click="previous()">Previous</a></li>
                <li ng-show="hasMorePrevPages"><a ng-click="goto(pages[0] - 1)" class>...</a></li>
                <li ng-repeat="page in pages"  ng-class="{active: page == list.currentPage}"><a ng-click="goto(page)">{{page}}</a></li>
                <li ng-show="hasMoreNextPages"><a ng-click="goto(pages[pages.length - 1] + 1)" class>...</a></li>
                <li class="next dl-table-next" ng-class="{disabled: !hasNext()}"><a ng-click="next()">Next</a></li>
              </ul>
            </td>
          </tr>
        </tfoot>
      </table>
    '''
  ]