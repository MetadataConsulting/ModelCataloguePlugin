angular.module('mc.core.ui.resourceTable', ['mc.core.catalogueElementResource']).directive 'resourceTable',  ['catalogueElementResource', (catalogueElementResource) -> {
    restrict:   'A'
    template:   '<table><tr ng-repeat="element in resourceList.list"><td>{{element.name}}</td></tr></table>'
    controller: ['$scope', '$element', '$attrs', 'catalogueElementResource', ($scope, $element, $attrs, catalogueElementResource) ->
      @resource = catalogueElementResource($attrs.resourceTable)
      @resource.list().then (result) ->
        $scope.resourceList = result
    ]
  }
]