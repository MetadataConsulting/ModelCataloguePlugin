angular.module('mc.core.ui.bs.diffTable', ['mc.core.ui.diffTable']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/diffTable.html', '''
      <div class="diff-table">
        <table class="table diff-table">
          <thead>
            <tr>
              <th class="col-md-2">Property</th>
              <th ng-repeat="element in elements" class="col-md-5"><a ng-href="{{element.href()}}"><catalogue-element-icon type="element.elementType"></catalogue-element-icon>{{element.getLabel()}}</a></th>
            </tr>
          </thead>
          <tbody>
            <tr ng-repeat="row in rows">
              <th class="col-md-2" ng-class="{'warning': !row.noChange || row.hasLoaders, 'success': row.noChange &amp;&amp; !row.hasLoaders}">{{row.name}} <span class="fa fa-fw fa-plus-square-o" ng-show="row.hasLoaders && !row.loading" ng-click="row.expand()"></span><span class="fa fa-fw fa-refresh" ng-show="row.hasLoaders && row.loading"></span></th>
              <td ng-repeat="value in row.values track by $index" class="col-md-5" ng-class="{'hide-del': row.values[0] == ' ' &amp;&amp; value, 'hide-ins': value == ' '  &amp;&amp; row.values[0], 'warning': !row.noChanges[$index] || row.hasLoaders, 'success': row.noChanges[$index] &amp;&amp; !row.hasLoaders}">
                <div ng-if=" $first" class="preserve-new-lines"><span ng-if="!row.hrefs[$index]">{{value}}</span><a ng-if="row.hrefs[$index]" ng-href="{{row.hrefs[$index]}}">{{value}}</a></div>
                <div ng-if="!$first">
                  <div ng-if="!row.hrefs[$index] &amp;&amp; !row.multiline" processing-diff left-obj="row.values[0]" right-obj="value" class="preserve-new-lines"></div>
                  <div ng-if="!row.hrefs[$index] &amp;&amp;  row.multiline"       line-diff left-obj="row.values[0]" right-obj="value" class="preserve-new-lines"></div>
                  <a   ng-if=" row.hrefs[$index] &amp;&amp; !row.multiline" processing-diff left-obj="row.values[0]" right-obj="value" class="preserve-new-lines" ng-href="{{row.hrefs[$index]}}" ></a>
                  <a   ng-if=" row.hrefs[$index] &amp;&amp;  row.multiline"       line-diff left-obj="row.values[0]" right-obj="value" class="preserve-new-lines" ng-href="{{row.hrefs[$index]}}" ></a>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    '''
  ]