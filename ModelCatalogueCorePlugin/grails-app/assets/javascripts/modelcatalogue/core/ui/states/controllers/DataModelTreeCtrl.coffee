angular.module('mc.core.ui.states.controllers.DataModelTreeCtrl', ['ui.router', 'mc.util.ui']).controller('mc.core.ui.states.controllers.DataModelTreeCtrl', [
  '$scope', 'currentDataModel', 'enhance', '$state' , 'names'
  ($scope ,  currentDataModel ,  enhance ,  $state  ,  names) ->
    listEnhancer = enhance.getEnhancer('list')
    $scope.elementAsList = listEnhancer.createSingletonList(currentDataModel) if currentDataModel

    $scope.onTreeviewSelected = (element) ->
      return if not element

      if element.resource
        $state.go 'mc.resource.list', dataModelId: currentDataModel?.id, resource: element.resource

      if element.elementType and element.id
        type = names.getPropertyNameFromType(element.elementType)
        property = 'properties'

        if type == 'enumeratedValue'
          type = 'enumeratedType'
          property = 'enumerations'
        $state.go 'mc.resource.show.property', dataModelId: currentDataModel?.id, resource: type, id: element.id, property: property

    $scope.$on 'newVersionCreated', (ignored, element) ->
        $state.go '.', {dataModelId: element.getDataModelId(), resource: names.getPropertyNameFromType(element.elementType), id: element.id, property: 'history', page: undefined, q: undefined}
])