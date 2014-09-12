angular.module('mc.core.ui.bs.modalPromptDataElementEdit', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$modal', '$q', 'messages', ($modal, $q, messages) ->
    (title, body, args) ->

      if not args?.element? and not args?.create?
        messages.error('Cannot create edit dialog.', 'The element to be edited is missing.')
        return $q.reject('Missing element argument!')

      dialog = $modal.open {
        windowClass: 'basic-edit-modal-prompt'
        resolve:
          args: -> args
          classificationInUse: ['$stateParams', 'catalogueElementResource',  ($stateParams, catalogueElementResource)->
            return undefined if not $stateParams.classification
            catalogueElementResource('classification').get($stateParams.classification)
          ]
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body">
            <messages-panel messages="messages"></messages-panel>
            <form role="form" ng-submit="saveElement()">
              <div class="form-group">
                <label for="classification"> Classifications</label>
                <elements-as-tags elements="copy.classifications"></elements-as-tags>
                <input id="classification-{{$index}}" placeholder="Classification" ng-model="pending.classification" catalogue-element-picker="classification" label="el.name" typeahead-on-select="copy.classifications.push(pending.classification);pending.classification = null">
              </div>
              <div class="form-group">
                <label for="name" class="">Name</label>
                <input type="text" class="form-control" id="name" placeholder="Name" ng-model="copy.name">
              </div>
              <div class="form-group">
                <label for="description" class="">Description</label>
                <textarea rows="10" ng-model="copy.description" placeholder="Description" class="form-control" id="description"></textarea>
              </div>
              <div class="form-group">
                <label for="valueDomain" class="">Value Domain</label>
                <input type="text" id="valueDomain" placeholder="Value Domain" ng-model="copy.valueDomain" catalogue-element-picker="valueDomain" label="el.name">
              </div>
            </form>
        </div>
        <div class="modal-footer">
            <contextual-actions></contextual-actions>
        </div>
        '''
        controller: 'saveOrUpdatePublishedElementCtrl'

      }

      dialog.result
  ]

  messagesProvider.setPromptFactory 'edit-dataElement', factory
]