angular.module('mc.core.ui.states.controllers.DataModelTreeCtrl', ['ui.router', 'mc.util.ui']).controller('mc.core.ui.states.controllers.DataModelTreeCtrl', [
  '$scope', 'currentDataModel', 'enhance', '$state' , 'names'
  ($scope ,  currentDataModel ,  enhance ,  $state  ,  names) ->
    $scope.currentDataModel = currentDataModel
    listEnhancer = enhance.getEnhancer('list')
    $scope.elementAsList = listEnhancer.createSingletonList(currentDataModel) if currentDataModel

    $scope.onTreeviewSelected = (element) ->
      return if not element

      if element.resource
        if element.getDataModelId() == currentDataModel?.id
          $state.go 'mc.resource.list', dataModelId: currentDataModel?.id, resource: element.resource
        else
          $state.go 'mc.resource.list-imported', dataModelId: currentDataModel?.id, otherDataModelId: element.getDataModelId(), resource: element.resource

      if element.elementType and element.id
        type = names.getPropertyNameFromType(element.elementType)
        property = 'properties'
        id = element.id
        dataModelId = currentDataModel?.id
        resource = type

        if type == 'enumeratedValue'
          resource = 'enumeratedType'
          property = 'enumerations'
        if type == 'versions'
          resource = 'dataModel'
          property = 'history'
          id = currentDataModel?.id
        if type == 'relationships'
          property = element.property
          id = element.element.id
          resource = names.getPropertyNameFromType(element.element.elementType)

        $state.go 'mc.resource.show.property', dataModelId: dataModelId, resource: resource, id: id, property: property

    $scope.$on 'newVersionCreated', (ignored, element) ->
        $state.go '.', {dataModelId: element.getDataModelId(), resource: names.getPropertyNameFromType(element.elementType), id: element.id, property: 'history', page: undefined, q: undefined}
])