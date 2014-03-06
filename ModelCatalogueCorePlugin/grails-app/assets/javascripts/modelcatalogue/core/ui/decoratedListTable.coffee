angular.module('mc.core.ui.decoratedListTable', []).directive 'decoratedListTable',  [-> {
    restrict: 'E'
    replace: true
    scope:
      list: '='
      columns: '=?'

    template: '''
      <table class="dl-table">
        <thead>
          <tr class="dl-table-header-row">
            <th class="dl-table-header-cell" ng-repeat="column in columns">{{column.header}}</th>
          </tr>
        </thead>
        <tbody>
            <tr class="dl-table-item-row" ng-repeat="element in list.list">
              <td class="dl-table-item-cell" ng-repeat="column in columns">{{_valueOf(element, column.value)}}</td>
            </tr>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="{{columns.length}}">
              <a class="dl-table-prev" ng-click="_previous()">Previous</a>
              <a class="dl-table-next" ng-click="_next()">Next</a>
            </td>
          </tr>
        </tfoot>
      </table>
    '''

    controller: ['$scope', '$element', ($scope, $element) ->
      updateControls = (list) ->
        if list.next.size == 0
          $element.find('a.dl-table-next').addClass('disabled')
        else
          $element.find('a.dl-table-next').removeClass('disabled')

        if list.previous.size == 0
          $element.find('a.dl-table-prev').addClass('disabled')
        else
          $element.find('a.dl-table-prev').removeClass('disabled')


      nextOrPrev = (nextOrPrevFn) ->
        return if nextOrPrevFn.size == 0
        $scope.loading = true
        nextOrPrevFn().then (result) ->
          $scope.loading = false
          $scope.list = result

      $scope.columns ?= [
        {header: 'Name', value: 'name'}
        {header: 'Descripton', value: 'description'}
      ]

      $scope._valueOf = (element, value) ->
        return value(element) if angular.isFunction(value)
        element[value]

      $scope._previous  = -> nextOrPrev($scope.list.previous)
      $scope._next      = -> nextOrPrev($scope.list.next)

      $scope.$watch 'loading', (newVal, ignored) ->
        if newVal
          $element.find('a.dl-table-prev,a.dl-table-next').addClass('disabled')

      $scope.$watch 'list', (newVal, ignored) ->
        updateControls(newVal)

      updateControls($scope.list)

    ]
  }
]