angular.module('mc.core.ui.decoratedListTable', ['mc.core.ui.decoratedList']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/decoratedList.html', '''
      <table class="dl-table table">
        <thead>
          <tr class="dl-table-header-row" ng-switch="hasSelection()">
            <th class="dl-table-select-all-cell col-md-1" ng-switch-when="true">
              <input type="checkbox" ng-change="updateSelectAll(allSelected)" ng-model="allSelected">
            </th>
            <th class="dl-table-header-cell" ng-repeat="column in columns" ng-class="evaluateClasses(column.classes)">{{column.header}}</th>
          </tr>
        </thead>
        <tbody>
             <tr class="dl-table-item-row" ng-repeat="element in list.list" ng-switch="hasSelection()">
              <td class="dl-table-select-item-cell" ng-switch-when="true">
                <input type="checkbox" ng-change="updateSelection()" ng-model="element._selected">
              </td>
              <td class="dl-table-item-cell" ng-class="evaluateClasses(column.classes, evaluateValue(column.value, element), element)" ng-repeat="column in columns">{{evaluateValue(column.value, element)}}</td>
            </tr>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="{{hasSelection() ? columns.length + 1 : columns.length}}">
              <ul class="pager">
                <li class="previous dl-table-prev" ng-class="{disabled: !hasPrevious() || loading}"><a ng-click="previous()">Previous</a></li>
                <li class="next dl-table-next" ng-class="{disabled: !hasNext() || loading}"><a ng-click="next()">Next</a></li>
              </ul>
            </td>
          </tr>
        </tfoot>
      </table>
    '''
  ]