angular.module('mc.core.ui.bs.infiniteTable', ['mc.core.ui.infiniteTable', 'ngSanitize']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/infiniteTable.html', '''
    <div>
      <div class="inf-table-header">
        <table class="inf-table table">
          <thead>
            <tr class="inf-table-header-row">
              <th class="inf-table-header-cell" ng-repeat="column in columns" ng-class="evaluateClasses(column.classes)">
                <a title="Show row actions" ng-if="$first" class="inf-cell-expand" ng-click="triggerHeaderExpanded()" class="btn btn-default"><span class="fa fa-fw" ng-class="{'fa-minus-square-o': $$headerExpanded, 'fa-plus-square-o': !$$headerExpanded}"></span></a>
                <span ng-if="!column.sort">{{column.header}}<span ng-if="!$$headerExpanded &amp;&amp; filters[column.header]" class="text-info" ng-click="triggerHeaderExpanded()"> [{{filters[column.header]}}]</span></span>
                <span ng-if=" column.sort"><a class="inf-table-header-sortable" ng-click="sortBy(column)" >
                  <span class="glyphicon" ng-class="getSortClass(column)"></span>
                  {{column.header}}
                </a>
                <span ng-if="!$$headerExpanded &amp;&amp; filters[column.header]" class="text-info" ng-click="triggerHeaderExpanded()"> [{{filters[column.header]}}]</span>
                </span>
              </th>
            </tr>
            <tr class="actions-row active" ng-if="$$headerExpanded">
                <td class="actions-cell col-md-12" ng-repeat="column in columns" >
                  <input class="form-control" placeholder="Filter {{column.header}}" ng-model="filters[column.header]" >
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
          <tbody ng-if="rows">
             <tr class="inf-table-item-row" ng-repeat-start="row in rows"  ng-class="row.classesForStatus" ng-if="isNotFiltered(row)">
                <td class="inf-table-item-cell" ng-class="cell.classes" ng-switch="row.head.type">
                  <a title="Show row actions" class="inf-cell-expand" ng-click="row.$$expanded = !row.$$expanded" class="btn btn-default"><span class="fa fa-fw" ng-class="{'fa-minus-square-o ': row.$$expanded, 'fa-plus-square-o ': !row.$$expanded}"></span></a>
                  <a    ng-switch-when="link" ng-href="{{row.head.href}}" class="preserve-new-lines">{{row.head.value}}</a>
                  <span ng-switch-when="html" ><span ng-bind-html="row.head.value" class="preserve-new-lines"></span></span>
                  <span ng-switch-when="plain"><span class="preserve-new-lines">{{row.head.value}}</span></span>
                </td>
                <td class="inf-table-item-cell" ng-class="cell.classes" ng-repeat="cell in row.tail" ng-switch="cell.type">
                  <a    ng-switch-when="link" ng-href="{{cell.href}}" class="preserve-new-lines">{{cell.value}}</a>
                  <span ng-switch-when="html" ><span ng-bind-html="cell.value" class="preserve-new-lines"></span></span>
                  <span ng-switch-when="plain"><span class="preserve-new-lines">{{cell.value}}</span></span>
                </td>
             </tr>
             <tr class="actions-row active" ng-repeat-end="" ng-if="row.$$expanded && isNotFiltered(row)">
                <td class="actions-cell col-md-12" colspan="{{columns.length}}">
                  <blockquote ng-if="row.element.description" class="preserve-new-lines" ng-bind-html="row.element.description"></blockquote>
                  <div class="text-right" ng-init="element = row.element">
                    <span class="pull-left text-muted"><em>Actions for {{row.element.getElementTypeName()}}</em></span>
                    <contextual-actions size="sm" no-colors="true" role="item" no-actions="true"></contextual-actions>
                  </div>
                </td>
             </tr>
          </tbody>
          <tfoot>
            <tr class="active" ng-if="loading">
              <td colspan="{{columns.length}}" class="col-md-3">
                <div class="text-center">
                  <span class="fa fa-fw fa-refresh fa-spin"></span>
                  <span class="pull-right text-muted" ng-if="total != 0"><em>{{elements.length}} of {{total}}<span ng-if="isFiltered()"> (unfiltered)</span></em></span>
                  <span class="pull-right text-muted" ng-if="total == 0"><em>Empty</em></span>
                </div>
              </td>
            </tr>
            <tr class="active" ng-if="!loading" ng-click="footerAction.run()" >
              <td colspan="{{columns.length}}" class="col-md-3">
                <div class="text-center">
                  <span class="fa" ng-class="getFooterCentralIconClass()"></span>
                  <span class="pull-right text-muted" ng-if="total != 0"><em>{{elements.length}} of {{total}}<span ng-if="isFiltered()"> (unfiltered)</span></em></span>
                  <span class="pull-right text-muted" ng-if="total == 0"><em>Empty</em></span>
                </div>
              </td>
            </tr>
          </tfoot>
        </table>
      </div>
    </div>
    '''
  ]