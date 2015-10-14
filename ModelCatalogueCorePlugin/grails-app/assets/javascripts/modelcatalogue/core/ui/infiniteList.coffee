angular.module('mc.core.ui.infiniteList', ['mc.core.ui.infiniteListCtrl', 'ngAnimate']).directive 'infiniteList',  [-> {
    restrict: 'E'
    replace: true
    scope:
      list: '='
      noActions: '=?'
      itemHref: '&?'
      onCreateRequested: '&?'
      heading: '=?'

    templateUrl: 'modelcatalogue/core/ui/infinitePanels.html'

    controller: ['$scope', '$animate', '$window', '$controller', '$element', '$attrs', ($scope, $animate, $window, $controller, $element, $attrs) ->
      unless $attrs.transform
        $scope.transform = (args) -> args.$element
      angular.extend(this, $controller('infiniteListCtrl', {$scope: $scope, $element: $element}))

      unless $attrs.onCreateRequested
        $scope.onCreateRequested = undefined

      unless $attrs.itemHref
        $scope.itemHref = (obj) -> obj.$element.href()

      $scope.requestCreate = ->
        $scope.onCreateRequested($list: $scope.list)

      $scope.nameFilter = ''
      $scope.href = (element) ->
        $scope.itemHref($element: element)
      $scope.isNotFiltered = (element) ->
        return true unless $scope.nameFilter
        return false if element.name?.toLowerCase().indexOf($scope.nameFilter?.toLowerCase()) == -1
        return true


      $scope.$watch 'nameFilter', (newFilter)->
        $scope.$emit "infiniteList:filtered", newFilter
    ]
  }
]