angular.module('mc.core.ui.bs.simpleObjectEditor', ['mc.core.ui.simpleObjectEditor']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/simpleObjectEditor.html', '''
      <table class="soe-table table">
        <thead>
          <th class="col-md-4">&nbsp;</th>
          <th class="col-md-7">&nbsp;</th>
          <th class="col-md-1">&nbsp;</th>
        </thead>
        <tbody>
          <tr class="soe-table-property-row" ng-repeat="property in editableProperties" ng-class="{'has-error': !isKeyUnique(property.key)}">
            <th class="soe-table-property-key col-md-4"><input type="text" ng-model="property.key" class="form-control" ng-change="keyChanged(property)"></th>
            <td class="soe-table-property-value col-md-7"><input type="text" ng-model="property.value" class="form-control" ng-change="valueChanged(property)"></td>
            <td class="soe-table-property-actions col-md-1">
                <button class="btn btn-link btn-sm" ng-click="addProperty($index, property)"><span
                        class="glyphicon glyphicon-plus"></span></button>
                <button class="btn btn-link btn-sm" ng-class="{disabled: editableProperties.length <= 1}"
                        ng-click="removeProperty($index)"><span class="glyphicon glyphicon-minus"></span>
                </button>
            </td>
          </tr>
        </tbody>
      </table>
    '''
  ]