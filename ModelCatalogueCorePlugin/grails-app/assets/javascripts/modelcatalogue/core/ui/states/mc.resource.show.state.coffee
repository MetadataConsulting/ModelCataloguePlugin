angular.module('mc.core.ui.states.mc.resource.show', ['mc.core.ui.states.controllers']).config(['$stateProvider', ($stateProvider) ->

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
        element: [
          '$stateParams','catalogueElementResource', 'lastSelectedElementHolder', '$rootScope', '$http', 'names',
          ($stateParams , catalogueElementResource ,  lastSelectedElementHolder ,  $rootScope ,  $http ,  names) ->
            if lastSelectedElementHolder.element \
              and "#{lastSelectedElementHolder.element.id}" == "#{$stateParams.id}" \
              and $stateParams.resource == names.getPropertyNameFromType(lastSelectedElementHolder.element.elementType)
                return lastSelectedElementHolder.element

            catalogueElementResource($stateParams.resource).get($stateParams.id).then (result) ->
              result.focus()
              return result
        ]
    }

])
