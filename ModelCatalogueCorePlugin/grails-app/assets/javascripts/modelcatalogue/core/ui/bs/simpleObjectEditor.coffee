angular.module('mc.core.ui.bs.simpleObjectEditor', ['mc.core.ui.simpleObjectEditor']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/simpleObjectEditor.html', '''
      <table class="soe-table table">
        <thead>
          <th class="col-md-4 soe-table-property-key">{{title}}</th>
          <th class="col-md-7 soe-table-property-value ">{{valueTitle}}</th>
          <th class="col-md-1 soe-table-property-actions">&nbsp;</th>
        </thead>
        <tbody>
          <tr class="soe-table-property-row" ng-repeat="property in editableProperties" ng-class="{'has-error': !isKeyUnique(property.key)}">
            <th class="soe-table-property-key col-md-4"><input type="text" ng-model="property.key" class="form-control" ng-change="keyChanged(property)" placeholder="Key"></th>
            <td class="soe-table-property-value col-md-7"><input type="text" ng-model="property.value" class="form-control" ng-change="valueChanged(property)" data-for-property="{{property.key}}" placeholder="null"></td>
            <td class="soe-table-property-actions col-md-1">
                <a class="btn btn-link btn-sm" ng-click="addProperty($index, property)"><span
                        class="glyphicon glyphicon-plus"></span></a>
                <a class="btn btn-link btn-sm"
                        ng-click="removeProperty($index)"><span class="glyphicon glyphicon-minus"></span>
                </a>
            </td>
          </tr>
        </tbody>
      </table>
    '''
  ]