angular.module('mc.core.ui.states.mc.resource.show', ['mc.core.ui.states.controllers']).config(($stateProvider) ->

    $stateProvider.state 'mc.resource.show', {
      url: '/{id:\\d+}'

      views:
        "":
          templateUrl: 'modelcatalogue/core/ui/state/show.html'
          controller: 'mc.core.ui.states.controllers.ShowCtrl'

        'navbar-left@':
          template: '<contextual-menu role="item"></contextual-menu>'
          controller: 'mc.core.ui.states.controllers.ElementWithDataModelCtrl'

      resolve:
        element: ($stateParams , catalogueElementResource ,  lastSelectedElementHolder ,  $rootScope ,  $http ,  names, $log) ->
          lastSelectedElement = lastSelectedElementHolder.element
          if lastSelectedElement \
            and "#{lastSelectedElement.id}" == "#{$stateParams.id}" \
            and $stateParams.resource == names.getPropertyNameFromType(lastSelectedElement.elementType)
              return lastSelectedElement.refreshIfMinimal() # lastSelectedElement might be minimal, so get the full element

          catalogueElementResource($stateParams.resource).get($stateParams.id).then (result) ->
            if angular.isFunction(result.focus)
              result.focus()
            else
              $log.error 'Element does not have method focus:', result
            return result
    }

)
