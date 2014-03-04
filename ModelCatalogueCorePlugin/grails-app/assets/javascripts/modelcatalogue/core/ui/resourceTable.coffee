angular.module('mc.core.ui.resourceTable', ['mc.core.catalogueElementResource']).directive 'resourceTable',  ['catalogueElementResource', (catalogueElementResource) -> {
    restrict: 'A'
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
    controller: ['$scope', '$element', '$attrs', 'catalogueElementResource', ($scope, $element, $attrs, catalogueElementResource) ->
      $element.addClass('resource-table')
      @resource = catalogueElementResource($scope.$eval($attrs.resourceTable))
      @resource.list().then (result) ->
        $scope.resourceList = result

      $scope.columns = if $attrs.columns? then $scope.$eval($attrs.columns) else {name: 'Name', description: 'Description'}
    ]
  }
]