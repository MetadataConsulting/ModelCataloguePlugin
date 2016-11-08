angular.module('mc.core.ui.utils').factory 'dataModelService', () -> {
  anyParentDataModel: (scope) ->
    return scope.currentDataModel if scope.currentDataModel
    return @anyParentDataModel(scope.$parent) if scope.$parent
    return undefined
}
