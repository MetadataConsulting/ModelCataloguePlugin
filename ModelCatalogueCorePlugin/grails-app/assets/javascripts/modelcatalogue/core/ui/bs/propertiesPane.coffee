angular.module('mc.core.ui.bs.propertiesPane', ['mc.core.ui.propertiesPane', 'ngSanitize']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/propertiesPane.html', '''
      <table class="pp-table table">
        <thead>
          <th class="col-md-4">{{title}}</th>
          <th class="col-md-6">{{valueTitle}}</th>
        </thead>
        <tbody>
          <tr class="pp-table-property-row" ng-repeat="property in properties" ng-switch="displayType(property.value, item)" ng-init="theValue = evaluateValue(property.value, item)">
            <th class="pp-table-property-label col-md-4">{{property.label}}</th>
            <td class="pp-table-property-value col-md-8 pp-table-property-element-value" ng-switch-when="element"><a ng-click="propertyClick(property.value, item)">{{theValue}}</a></td>
            <td class="pp-table-property-value col-md-8 pp-table-property-element-value" ng-switch-when="date">{{theValue | date:'short'}}</td>
            <td class="pp-table-property-value col-md-8 pp-table-property-element-value" ng-switch-when="elementArray"><span ng-repeat="element in theValue"><a ng-click="element.show()">{{element.name}}</a><span ng-hide="$last">, </span></span></td>
            <td class="pp-table-property-value col-md-8 pp-table-property-element-value" ng-switch-when="array"><span ng-repeat="element in theValue">{{element}}<span ng-hide="$last">, </span></span></td>
            <td class="pp-table-property-value col-md-8 pp-table-property-element-value" ng-switch-default ng-bind-html="theValue == null ? '' : ('' + theValue) | linky:'_blank'"></td>
          </tr>
        </tbody>
      </table>
    '''
  ]