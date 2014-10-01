angular.module('mc.core.ui.infiniteList', ['mc.core.ui.infiniteListCtrl', 'ngAnimate']).directive 'infiniteList',  [-> {
    restrict: 'E'
    replace: true
    scope:
      list: '='

    templateUrl: 'modelcatalogue/core/ui/infinitePanels.html'

    controller: ['$scope', '$animate', '$window', '$controller', ($scope, $animate, $window, $controller) ->
      angular.extend(this, $controller('infiniteListCtrl', {$scope: $scope}))

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

    ]
  }
]