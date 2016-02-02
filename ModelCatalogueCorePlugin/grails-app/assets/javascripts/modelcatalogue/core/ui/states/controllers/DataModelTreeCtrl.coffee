angular.module('mc.core.ui.states.controllers.DataModelTreeCtrl', ['ui.router', 'mc.util.ui']).controller('mc.core.ui.states.controllers.DataModelTreeCtrl', [
  '$scope', 'currentDataModel', 'lastSelectedElementHolder', 'enhance', '$state' , 'names'
  ($scope ,  currentDataModel ,  lastSelectedElementHolder ,  enhance ,  $state  ,  names) ->
    $scope.currentDataModel = currentDataModel
    listEnhancer = enhance.getEnhancer('list')
    $scope.elementAsList = listEnhancer.createSingletonList(currentDataModel) if currentDataModel

    $scope.onTreeviewSelected = (element) ->
      return if not element

      lastSelectedElementHolder.element = element

      if element.resource
        if element.getDataModelId() == currentDataModel?.id
          if element.resource == 'asset'
            $state.go 'mc.resource.list', dataModelId: currentDataModel?.id, resource: element.resource, status: 'active'
            return

          $state.go 'mc.resource.list', dataModelId: currentDataModel?.id, resource: element.resource
          return

        $state.go 'mc.resource.list-imported', dataModelId: currentDataModel?.id, otherDataModelId: element.getDataModelId(), resource: element.resource
        return

      if element.elementType and element.id
        type = names.getPropertyNameFromType(element.elementType)
        params =
          dataModelId: currentDataModel?.id
          resource: type
          id: element.id
          property: 'properties'
          focused: undefined

        if type == 'enumeratedValue'
          params.resource = 'enumeratedType'
          params.property = 'enumerations'
          params.focused = true
        if type == 'versions'
          params.resource = 'dataModel'
          params.property = 'history'
          params.id = currentDataModel?.id
          params.focused = true
        if type == 'relationships'
          params.property = element.property
          params.id = element.element.id
          params.resource = names.getPropertyNameFromType(element.element.elementType)
          params.focused = true

        if type == 'dataModel'
          params.property = 'activity'

        if type == 'asset'
          params.property = 'history'

        $state.go 'mc.resource.show.property', params

    $scope.$on 'newVersionCreated', (ignored, element) ->
        $state.go '.', {dataModelId: element.getDataModelId(), resource: names.getPropertyNameFromType(element.elementType), id: element.id, property: 'activity', page: undefined, q: undefined}
])