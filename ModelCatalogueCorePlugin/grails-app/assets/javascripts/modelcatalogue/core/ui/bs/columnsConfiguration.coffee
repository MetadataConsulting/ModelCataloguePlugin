angular.module('mc.core.ui.bs.columnsConfiguration', ['mc.core.ui.columnsConfiguration']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/columnsConfiguration.html', '''
        <table class="table">
            <tr ng-show="columns">
                <th>
                    Header
                </th>
                <th>
                    Value
                </th>
                <th>
                    Class
                </th>
                <th>
                    Show
                </th>
                <th>
                    &nbsp;
                </th>
            </tr>
            <tr ng-repeat="column in columns">
                <td><input class="form-control" ng-model="column.header"/></td>
                <td><input class="form-control" ng-model="column.value"/></td>
                <td><input class="form-control" ng-model="column.classes"/></td>
                <td><input class="form-control" ng-model="column.show"/></td>
                <td>
                    <button class="btn btn-success btn-sm" ng-click="addColumn($index, column)"><span
                            class="glyphicon glyphicon-plus"></span> Add</button>
                    <button class="btn btn-danger btn-sm" ng-class="{disabled: columns.length <= 1}"
                            ng-click="removeColumn($index)"><span class="glyphicon glyphicon-minus"></span> Remove
                    </button>
                </td>
            </tr>
        </table>
    '''
  ]