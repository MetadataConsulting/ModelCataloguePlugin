angular.module('mc.core.ui.bs.csvTransformationView', ['mc.core.ui.csvTransformationView',  'mc.core.ui.propertiesPane', 'mc.core.ui.simpleObjectEditor', 'ui.bootstrap', 'ngSanitize']).run [ '$templateCache', ($templateCache) ->

  $templateCache.put 'modelcatalogue/core/ui/csvTransformationView.html', '''
    <div>
      <h3 class="ce-name"><small ng-class="element.getIcon()" title="{{element.getElementTypeName()}}"></small> {{element.name}} <small><span class="label" ng-show="element.status" ng-class="{'label-warning': element.status == 'DRAFT', 'label-info': element.status == 'PENDING', 'label-primary': element.status == 'FINALIZED', 'label-danger': element.status == 'DEPRECATED'}">{{element.status}}</span></small></h3>
      <blockquote class="ce-description" ng-show="element.description" ng-bind-html="'' + element.description | linky:'_blank'"></blockquote>
      <table class="table">
        <thead>
          <tr>
            <th>Source Data Element</th>
            <th>Destination Data Element</th>
            <th colspan="2">Header</th>
          </tr>
        </thead>
        <tbody>
          <tr ng-repeat="column in columns track by $index">
            <td class="col-md-4">
              <input ng-model="column.source" catalogue-element-picker="dataElement" placeholder="Source" focus-me="lastAddedRow == $index &amp;&amp; $index != 0">
            </td>
            <td class="col-md-4">
              <input class="form-control" ng-model="column.destination" catalogue-element-picker="dataElement" placeholder="Destination (leave blank if same)" ng-disabled="!hasSource(column)">
            </td>
            <td class="col-md-3">
              <input class="form-control" ng-model="column.header" placeholder="Header (leave blank if same)" ng-keydown="addNewRowOnTab($event, $index, $last)" ng-disabled="isEmpty(column)">
            </td>
            <td class="col-md-1">
                <a class="btn btn-link btn-sm" ng-click="addColumn($index, column)" ng-class="{'disabled': isEmpty(column)}"><span class="glyphicon glyphicon-plus"></span></a>
                <a class="btn btn-link btn-sm" ng-click="removeColumn($index)" ng-class="{'disabled': $index == 0}"><span class="glyphicon glyphicon-minus"></span></a>
            </td>
          </tr>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="4" class="text-center">
              <div class="form-control-static">
                <button class="btn btn-primary" ng-disabled="!hasChanged() || updating" ng-click="update()">Update</button>
                <button class="btn btn-default" ng-disabled="!hasChanged() || updating" ng-click="reset()">Reset</button>
              </div>
            </td>
          </tr>

        </tfoot>
      </table>
    </div>
    '''
]
