angular.module('mc.core.ui.bs.propertiesPane', ['mc.core.ui.propertiesPane', 'ngSanitize']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/propertiesPane.html', '''
      <table class="pp-table table">
        <thead>
          <th class="col-md-4">{{title}}</th>
          <th class="col-md-6">{{valueTitle}}</th>
        </thead>
        <tbody>
          <tr class="pp-table-property-row" ng-repeat="property in properties" ng-switch="canShow(property.value, item)">
            <th class="pp-table-property-label col-md-4">{{property.label}}</th>
            <td class="pp-table-property-value col-md-8 pp-table-property-element-value" ng-switch-when="true"><a ng-click="propertyClick(property.value, item)">{{evaluateValue(property.value, item)}}</a></td>
            <td class="pp-table-property-value col-md-8" ng-switch-when="false" ng-bind-html="'' + evaluateValue(property.value, item) | linky:'_blank'"></td>
          </tr>
        </tbody>
      </table>
    '''
  ]