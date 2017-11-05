angular.module('mc.core.ui.states.mc.resource.xml-editor', ['ui.ace']).config([
  '$stateProvider',
  ($stateProvider) ->
    $stateProvider.state 'mc.resource.xml-editor', {
      url: '/xml-edit-{id:\\d+}?'

      views:
        "":
          templateUrl: '/mc/core/ui/states/xmlEditor.html'
          controller: 'mc.core.ui.states.controllers.XmlEditorCtrl'

      'navbar-left@':
        template: '<contextual-menu role="{{::actionRoleAccess.ROLE_ITEM_ACTION}}"></contextual-menu>'
        controller: 'mc.core.ui.states.controllers.ElementWithDataModelCtrl'

      resolve:
        element: [
          '$stateParams', 'catalogueElementResource', 'lastSelectedElementHolder', 'names',
          ($stateParams, catalogueElementResource, lastSelectedElementHolder, names) ->
            promise = catalogueElementResource($stateParams.resource).get($stateParams.id)
            promise.then (element) -> element.focus()
            return promise
        ]
    }
])
