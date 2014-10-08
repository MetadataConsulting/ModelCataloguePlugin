angular.module('mc.core.ui.bs.infiniteTable', ['mc.core.ui.infiniteTable', 'ngSanitize']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/infiniteTable.html', '''
    <div>
      <div class="inf-table-header">
        <table class="inf-table table">
          <thead>
            <tr class="inf-table-header-row">
              <th class="inf-table-header-cell" ng-repeat="column in columns" ng-class="evaluateClasses(column.classes)">
                <a title="Show row actions" ng-show="$first" class="inf-cell-expand" ng-click="triggerHeaderExpanded()" class="btn btn-default"><span class="fa fa-fw" ng-class="{'fa-minus-square-o ': $$headerExpanded, 'fa-plus-square-o ': !$$headerExpanded}"></span></a>
                <span ng-if="!column.sort">{{column.header}}<span ng-show="!$$headerExpanded &amp;&amp; filters[column.header]" class="text-info" ng-click="triggerHeaderExpanded()"> [{{filters[column.header]}}]</span></span>
                <span ng-if=" column.sort"><a class="inf-table-header-sortable" ng-click="sortBy(column)" >
                  <span class="glyphicon" ng-class="getSortClass(column)"></span>
                  {{column.header}}
                </a>
                <span ng-show="!$$headerExpanded &amp;&amp; filters[column.header]" class="text-info" ng-click="triggerHeaderExpanded()"> [{{filters[column.header]}}]</span>
                </span>
              </th>
            </tr>
            <tr class="actions-row active" ng-show="$$headerExpanded">
                <td class="actions-cell col-md-12" ng-repeat="column in columns" >
                  <input class="form-control" placeholder="Filter {{column.header}}" ng-model="filters[column.header]">
                </td>
            </tr>
            <tr class="actions-row active" ng-show="$$headerExpanded">
                <td class="actions-cell col-md-12" colspan="{{columns.length}}">
                  <div class=" text-right">
                    <contextual-actions size="sm" no-colors="true" role="header" no-actions="true"></contextual-actions>
                  </div>
                </td>
            </tr>
          </thead>
        </table>
      </div>
      <div class="inf-table-spacer">
      </div>
      <div class="inf-table-body">
        <table class="inf-table table">
          <tbody>
             <tr class="inf-table-item-row" ng-repeat-start="element in elements"  ng-class="classesForStatus(element)" ng-if="isNotFiltered(element)" >
                <td class="inf-table-item-cell" ng-class="evaluateClasses(column.classes, evaluateValue(column.value, element), element)" ng-repeat="column in columns">
                  <a title="Show row actions" ng-show="$first" class="inf-cell-expand" ng-click="element.$$expanded = !element.$$expanded" class="btn btn-default"><span class="fa fa-fw" ng-class="{'fa-minus-square-o ': element.$$expanded, 'fa-plus-square-o ': !element.$$expanded}"></span></a>
                  <a ng-if="column.href" ng-href="{{evaluateValue(column.href, element)}}" class="preserve-new-lines">{{evaluateValue(column.value, element)}}</a>
                  <span ng-if="!column.href" ><span ng-bind-html="evaluateValue(column.value, element)" class="preserve-new-lines"></span></span>
                </td>
             </tr>
             <tr class="actions-row active" ng-repeat-end="" ng-if="element.$$expanded && isNotFiltered(element)">
                <td class="actions-cell col-md-12" colspan="{{columns.length}}">
                  <blockquote ng-show="element.description" class="preserve-new-lines" ng-bind-html="element.description"></blockquote>
                  <div class=" text-right">
                    <span class="pull-left text-muted"><em>Actions for {{element.getElementTypeName()}}</em></span>
                    <contextual-actions size="sm" no-colors="true" role="item" no-actions="true"></contextual-actions>
                  </div>
                </td>
             </tr>
          </tbody>
          <tfoot>
            <tr class="active" ng-click="loading ? '' : footerAction.run()">
              <td colspan="{{columns.length}}" class="col-md-3">
                <div class="text-center">
                  <span class="fa" ng-class="getFooterCentralIconClass()"></span>
                  <span class="pull-right text-muted" ng-show="total != 0"><em>{{elements.length}} of {{total}}<span ng-if="isFiltered()"> (unfiltered)</span></em></span>
                  <span class="pull-right text-muted" ng-show="total == 0"><em>Empty</em></span>
                </div>
              </td>
            </tr>
          </tfoot>
        </table>
      </div>
    </div>
    '''
  ]