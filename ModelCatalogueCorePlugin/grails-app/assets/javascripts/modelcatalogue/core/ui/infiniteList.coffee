angular.module('mc.core.ui.infiniteList', ['mc.core.listEnhancer', 'mc.core.ui.columns', 'ngAnimate']).directive 'infiniteList',  [-> {
    restrict: 'E'
    replace: true
    scope:
      list: '='

    templateUrl: 'modelcatalogue/core/ui/infinitePanels.html'

    controller: ['$scope', '$animate', '$window', ($scope, $animate, $window) ->
      $scope.loading  = false
      $scope.elements = $scope.list.list
      $scope.next     = $scope.list.next

      $scope.extendOrCollapse = ($event)->
        panelContainer = angular.element(angular.element($event.currentTarget).closest('.panel').parent())
        if not panelContainer.hasClass('expanded')
          $animate.removeClass(panelContainer, 'col-md-4')
          $animate.removeClass(panelContainer, 'col-sm-6')
          $animate.removeClass(panelContainer, 'col-lg-4')
          $animate.addClass(panelContainer, 'col-md-12')
          $animate.addClass(panelContainer, 'col-sm-12')
          $animate.addClass(panelContainer, 'col-lg-12')
          $animate.addClass(panelContainer, 'expanded')
        else
          $animate.removeClass(panelContainer, 'col-md-12')
          $animate.removeClass(panelContainer, 'col-sm-12')
          $animate.removeClass(panelContainer, 'col-lg-12')
          $animate.removeClass(panelContainer, 'expanded')
          $animate.addClass(panelContainer, 'col-md-4')
          $animate.addClass(panelContainer, 'col-sm-6')
          $animate.addClass(panelContainer, 'col-lg-4')

        $window.scrollTo 0, panelContainer.offset().top - 70

      $scope.loadMore = ->
        if $scope.list.total > $scope.elements.length
          $scope.loading = true
          $scope.next().then (result) ->
            for element in result.list
              $scope.elements.push element
            $scope.next = result.next
            $scope.loading = false

    ]
  }
]