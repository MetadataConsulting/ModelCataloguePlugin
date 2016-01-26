angular.module('mc.core.ui.infiniteList', ['mc.core.ui.infiniteListCtrl', 'ngAnimate']).directive 'infiniteList',  [-> {
    restrict: 'E'
    replace: true
    scope:
      list: '='
      noActions: '=?'
      itemHref: '&?'
      transform: '&?'

    templateUrl: 'modelcatalogue/core/ui/infinitePanels.html'

    controller: ['$scope', '$animate', '$window', '$controller', '$element', '$attrs', 'detailSections', ($scope, $animate, $window, $controller, $element, $attrs, detailSections) ->

      unless $attrs.transform
        $scope.transform = (args) -> args.$element

      angular.extend(this, $controller('infiniteListCtrl', {$scope: $scope, $element: $element}))

      unless $attrs.itemHref
        $scope.itemHref = (obj) -> obj.$element.href()

      $scope.href = (element) ->
        $scope.itemHref($element: element)

      $scope.getElement = (item) ->
        $scope.transform($element: item)


      $scope.getDetailSections = (element) ->
        detailSections.getAvailableViews(element)

    ]
  }
]