angular.module('modelcatalogue.core.ui.states.dataModel.resource.show', ['modelcatalogue.core.ui.states.controllers',
'modelcatalogue.core.ui.states.dataModel.resource.show.property']).config(($stateProvider) ->

    $stateProvider.state 'dataModel.resource.show', {
      url: '/{id:\\d+}'

      views:
        "":
          templateUrl: 'modelcatalogue/core/ui/state/show.html'
          controller: 'modelcatalogue.core.ui.states.controllers.ShowCtrl'

        'navbar-left@':
          template: '<contextual-menu role="{{::actionRoleAccess.ROLE_ITEM_ACTION}}"></contextual-menu>'
          controller: 'modelcatalogue.core.ui.states.dataModel.resource.ElementWithDataModelCtrl'

      resolve:
        element: ($stateParams , catalogueElementResource ,  lastSelectedElementHolder ,  $rootScope ,  $http ,  names, $log) ->
          if lastSelectedElementHolder.element \
            and "#{lastSelectedElementHolder.element.id}" == "#{$stateParams.id}" \
            and $stateParams.resource == names.getPropertyNameFromType(lastSelectedElementHolder.element.elementType)
              return lastSelectedElementHolder.element

          catalogueElementResource($stateParams.resource).get($stateParams.id).then (result) ->
            if angular.isFunction(result.focus)
              result.focus()
            else
              $log.error 'Element does not have method focus:', result
            return result
    }

)
