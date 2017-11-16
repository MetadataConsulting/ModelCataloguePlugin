angular.module('modelcatalogue.core.ui.states.dataModel.resource.xmlEditor', ['ui.ace',
'modelcatalogue.core.ui.states.dataModel.resource.xmlEditor.xmlEditorCtrl']).config([
  '$stateProvider',
  ($stateProvider) ->
    $stateProvider.state 'mc.resource.xml-editor', {
      url: '/xml-edit-{id:\\d+}?'

      views:
        "":
          templateUrl: '/modelcatalogue/core/ui/states/dataModel/resource/xmlEditor/xmlEditor.html'
          controller: 'modelcatalogue.core.ui.states.dataModel.resource.xmlEditor.xmlEditorCtrl'

      'navbar-left@':
        template: '<contextual-menu role="{{::actionRoleAccess.ROLE_ITEM_ACTION}}"></contextual-menu>'
        controller: 'modelcatalogue.core.ui.states.dataModel.resource.ElementWithDataModelCtrl'

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
