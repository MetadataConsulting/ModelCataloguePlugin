angular.module('mc.core.ui.decoratedListTable', []).directive 'decoratedListTable',  [-> {
    restrict: 'E'
    replace: true
    scope:
      list: '='
      columns: '=?'

    template: '''
      <table class="dl-table table">
        <thead>
          <tr class="dl-table-header-row">
            <th class="dl-table-header-cell" ng-repeat="column in columns" ng-class="evaluateClasses(column.classes)">{{column.header}}</th>
          </tr>
        </thead>
        <tbody>
            <tr class="dl-table-item-row" ng-repeat="element in list.list">
              <td ng-init="value = evaluateValue(column.value, element)" class="dl-table-item-cell" ng-class="evaluateClasses(column.classes, value, element)" ng-repeat="column in columns">{{value}}</td>
            </tr>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="{{columns.length}}">
              <ul class="pager">
                <li class="previous dl-table-prev" ng-class="{disabled: !hasPrevious() || loading}"><a ng-click="previous()">Previous</a></li>
                <li class="next dl-table-next" ng-class="{disabled: !hasNext() || loading}"><a ng-click="next()">Next</a></li>
              </ul>
            </td>
          </tr>
        </tfoot>
      </table>
    '''

    controller: ['$scope', ($scope) ->
      nextOrPrev = (nextOrPrevFn) ->
        return if nextOrPrevFn.size == 0
        $scope.loading = true
        nextOrPrevFn().then (result) ->
          $scope.loading = false
          $scope.list = result

      hasNextOrPrev = (nextOrPrevFn) -> nextOrPrevFn.size? and nextOrPrevFn.size != 0

      $scope.previous       = -> nextOrPrev($scope.list.previous)
      $scope.next           = -> nextOrPrev($scope.list.next)
      $scope.hasPrevious    = -> hasNextOrPrev($scope.list.previous)
      $scope.hasNext        = -> hasNextOrPrev($scope.list.next)

      $scope.columns ?= [
        {header: 'Name', value: 'name', classes: 'col-md-4'}
        {header: 'Descripton', value: 'description', classes: 'col-md-8'}
      ]

      $scope.list ?=
        list: []
        next: {size: 0}
        previous: {size: 0}

      $scope.evaluateClasses = (classes, element) ->
        if angular.isFunction(classes) then classes(element) else classes

      $scope.evaluateValue = (value, element) ->
        if angular.isFunction(value) then value(element) else element[value]

    ]
  }
]