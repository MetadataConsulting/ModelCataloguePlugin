angular.module('mc.core.ui.bs.modalPromptDataElementEdit', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$uibModal', '$q', 'messages', ($uibModal, $q, messages) ->
    (title, body, args) ->

      if not args?.element? and not args?.create?
        messages.error('Cannot create edit dialog.', 'The element to be edited is missing.')
        return $q.reject('Missing element argument!')

      if not title
        title = "Create Data Element"

      dialog = $uibModal.open {
        windowClass: 'basic-edit-modal-prompt'
        size: 'lg'
        resolve:
          args: -> args
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body">
            <messages-panel messages="messages"></messages-panel>
            <form role="form" ng-submit="saveElement()">
              <div class="form-group" ng-if="!hideDataModels()">
                <label for="dataModel"> Data Models</label>
                <elements-as-tags elements="copy.dataModels"></elements-as-tags>
                <input id="dataModel-{{$index}}" placeholder="Data Model" ng-model="pending.dataModel" catalogue-element-picker="dataModel" label="el.name" typeahead-on-select="addToDataModels()">
              </div>
              <div class="form-group">
                <label for="name" class="">Name</label>
                <input type="text" class="form-control" id="name" placeholder="Name" ng-model="copy.name">
              </div>
              <div class="form-group">
                <label for="modelCatalogueId" class="">Catalogue ID</label>
                <input type="text" class="form-control" id="modelCatalogueId" placeholder="e.g. external ID, namespace (leave blank for generated)" ng-model="copy.modelCatalogueId">
              </div>
              <div class="form-group">
                <label for="description" class="">Description</label>
                <textarea rows="10" ng-model="copy.description" placeholder="Description" class="form-control" id="description"></textarea>
              </div>
              <div class="form-group">
                <label for="dataType" class="">Data Type</label>
                <input type="text" id="dataType" placeholder="Data Type" ng-model="copy.dataType" catalogue-element-picker="dataType" global="'allow'" label="el.name">
                <p class="help-block">
                  Creates new data type when no one is selected from advanced search (new data type name must be provided).
                </p>
              </div>
              <fake-submit-button/>
            </form>
        </div>
        <div class="modal-footer">
            <contextual-actions role="{{::actionRoleAccess.ROLE_MODAL_ACTION}}"></contextual-actions>
        </div>
        '''
        controller: 'saveOrUpdatePublishedElementCtrl'

      }

      dialog.result
  ]

  messagesProvider.setPromptFactory 'edit-dataElement', factory
]
