angular.module('mc.core.ui.resourceTable', ['mc.core.catalogueElementResource']).directive 'resourceTable',  ['catalogueElementResource', (catalogueElementResource) -> {
    restrict: 'A'
    scope:
      # it makes no sense to change the resource name
      resourceName: '@resourceTable'
      # but you may want to change the columns
      columns: '=columns'
    template: '''
      <thead>
        <tr class="resource-table-header-row">
          <th class="resource-table-header-cell" ng-repeat="(header, property) in columns">{{header}}</th>
        </tr>
      </thead>
      <tbody>
          <tr class="resource-table-item-row" ng-repeat="element in resourceList.list">
            <td class="resource-table-item-cell" ng-repeat="(header, property) in columns">{{element[property]}}</td>
          </tr>
      </tbody>
    '''
    controller: ['$scope', '$element', 'catalogueElementResource', ($scope, $element, catalogueElementResource) ->
      $element.addClass('resource-table')
      @resource = catalogueElementResource($scope.resourceName)
      @resource.list().then (result) ->
        $scope.resourceList = result

      $scope.columns = if $scope.columns? then $scope.columns else {name: 'Name', description: 'Description'}
    ]
  }
]