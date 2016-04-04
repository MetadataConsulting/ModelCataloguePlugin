angular.module('mc.core.ui.states.mc.resource.xmleditor', ['ui.ace']).config([
  '$stateProvider',
  ($stateProvider ) ->


    $stateProvider.state 'mc.resource.xmleditor', {
      url: '/xmledit-{id:\\d+}?'

      views:
        "":
          templateUrl: '/mc/core/ui/states/xmlEditor.html'
          controller: 'mc.core.ui.states.controllers.XMLEditorCtrl'

      'navbar-left@':
        template: '<contextual-menu role="item"></contextual-menu>'
        controller: 'mc.core.ui.states.controllers.ElementWithDataModelCtrl'


      resolve:
        element: [
          '$stateParams','catalogueElementResource', 'lastSelectedElementHolder', 'names',
          ($stateParams , catalogueElementResource ,  lastSelectedElementHolder ,  names) ->

            catalogueElementResource($stateParams.resource).get($stateParams.id)
        ]

    }

])
