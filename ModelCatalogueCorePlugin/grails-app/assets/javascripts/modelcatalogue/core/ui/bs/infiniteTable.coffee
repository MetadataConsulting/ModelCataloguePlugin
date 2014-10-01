angular.module('mc.core.ui.bs.infiniteTable', ['mc.core.ui.infiniteTable', 'ngSanitize']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/infiniteTable.html', '''
    <div>
      <div ng-show="total > 0"  class="inf-table-header">
        <table class="inf-table table">
          <thead>
            <tr class="inf-table-header-row">
              <th class="inf-table-header-cell" ng-repeat="column in columns" ng-class="evaluateClasses(column.classes)">
                <span ng-if="!column.sort">{{column.header}}</span>
                <a class="inf-table-header-sortable" ng-click="sortBy(column)" ng-if="column.sort">
                  <span class="glyphicon" ng-class="getSortClass(column)"></span>
                  {{column.header}}
                </a>
              </th>
            </tr>
          </thead>
        </table>
      </div>
      <div class="inf-table-body">
        <table ng-show="total > 0" class="inf-table table" infinite-scroll="loadMore()" infinite-scroll-disabled="loading" infinite-scroll-distance="1">
          <tbody>
             <tr class="inf-table-item-row" ng-repeat="element in elements"  ng-class="classesForStatus(element)">
                <td class="inf-table-item-cell" ng-class="evaluateClasses(column.classes, evaluateValue(column.value, element), element)" ng-repeat="column in columns" ng-init="value = evaluateValue(column.value, element); href = evaluateValue(column.href, element)">
                  <span ng-if="$last">
                      <contextual-actions size="sm" no-colors="true" icon-only="true"></contextual-actions>
                  </span>
                  <a ng-if="href" ng-href="{{href}}" class="preserve-new-lines">{{value}}</a>
                  <span ng-if="!href" ><span ng-bind-html="value" class="preserve-new-lines"></span></span>
                </td>
             </tr>
          </tbody>
          <tfoot ng-show="loading">
            <tr class="text-center active">
              <td class="" colspan="{{columns.length}}" class="col-md-12">
                <span class="fa fa-refresh fa-spin"></span>
              </td>
            </tr>
          </tfoot>
        </table>
      </div>
      <div ng-show="total == 0">
        <div class="alert alert-warning">Empty</div>
      </div>
    </div>
    '''
  ]