angular.module('mc.core.ui.columnsSupportCtrl', ['mc.core.ui.catalogueElementProperties']).controller 'columnsSupportCtrl',  ['$scope', 'catalogueElementProperties', ($scope, catalogueElementProperties) ->
   $scope.evaluateClasses = (classes, element) ->
     if angular.isFunction(classes) then classes(element, catalogueElementProperties) else classes

   $scope.evaluateValue = (value, element) ->
     '' + ((if angular.isFunction(value) then value(element, catalogueElementProperties) else $scope.$eval(value, element)) ? '')

   $scope.showItem = (show, element) ->
     show = 'show()' if show == true
     if angular.isFunction(show) then show(element, catalogueElementProperties) else $scope.$eval(show, element)

   $scope.showEnabled = (show, element) ->
     return 'link' if show == true && angular.isFunction(element?.href)
     return show?

   $scope.classesForStatus = (element) ->
     status = element?.status ? element?.relation?.status
     {'warning': status == 'DRAFT', 'info': status == 'PENDING' , 'danger': status == 'DEPRECATED' }

]