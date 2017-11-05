angular.module('mc.core.ui.states.mc.resource.show', ['mc.core.ui.states.controllers']).config(($stateProvider) ->

    $stateProvider.state 'mc.resource.show', {
      url: '/{id:\\d+}'

      views:
        "":
          templateUrl: 'modelcatalogue/core/ui/state/show.html'
          controller: 'mc.core.ui.states.controllers.ShowCtrl'

        'navbar-left@':
          template: '<contextual-menu role="{{::actionRoleAccess.ROLE_ITEM_ACTION}}"></contextual-menu>'
          controller: 'mc.core.ui.states.controllers.ElementWithDataModelCtrl'

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
