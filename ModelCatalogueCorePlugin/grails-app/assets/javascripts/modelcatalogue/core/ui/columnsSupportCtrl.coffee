angular.module('mc.core.ui.columnsSupportCtrl', []).controller 'columnsSupportCtrl',  ['$scope', ($scope) ->
   $scope.evaluateClasses = (classes, element) ->
     if angular.isFunction(classes) then classes(element) else classes

   $scope.evaluateValue = (value, element) ->
     (if angular.isFunction(value) then value(element) else $scope.$eval(value, element)) ? ''

   $scope.showItem = (show, element) ->
     show = 'show()' if show == true
     if angular.isFunction(show) then show(element) else $scope.$eval(show, element)

   $scope.showEnabled = (show, element) ->
     return 'link' if show == true && angular.isFunction(element?.href)
     return show?

   $scope.classesForStatus = (element) ->
     status = element?.status ? element?.relation?.status
     {'warning': status == 'DRAFT', 'info': status == 'PENDING' , 'danger': status == 'ARCHIVED' }

]