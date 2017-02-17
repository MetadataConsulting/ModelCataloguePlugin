angular.module('mc.core.ui.infiniteList').directive 'infiniteList', -> {
  restrict: 'E'
  replace: true
  scope:
    list: '='
    noActions: '=?'
    itemHref: '&?'
    transform: '&?'

  templateUrl: (el, attr) ->
    if attr.type == 'short' then '/mc/core/ui/infinite-list/infinitePanelsShort.html' else '/mc/core/ui/infinite-list/infinitePanels.html'

  controller: ($scope, $animate, $window, $controller, $element, $attrs, detailSections) ->
    'ngInject'
    sectionsCache = {}

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
      return [] unless element

      sections = sectionsCache[element.link]

      return sections if sections
      return sectionsCache[element.link] = detailSections.getAvailableViews(element)
}
