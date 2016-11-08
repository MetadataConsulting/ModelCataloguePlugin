angular.module('mc.core.ui.bs.modalPromptForCatalogueElements', ['mc.util.messages', 'mc.util.ui.focusMe']).config ['messagesProvider', (messagesProvider)->
 messagesProvider.setPromptFactory 'catalogue-elements',  [ '$uibModal', ($uibModal) ->
   (title, body, args) ->
      dialog = $uibModal.open {
        windowClass: 'messages-modal-prompt'
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body">
            <form role="form">
              <div class="form-group">
                <label for="elements">''' + body + '''</label>
                <elements-as-tags elements="copy.elements"></elements-as-tags>
                <input id="elements" placeholder="Name or Catalogue ID" ng-model="pending.element" status="''' + (args.status ? '') + '''" catalogue-element-picker="''' + (args.resource ? 'catalogueElement') + '''" focus-me="true" label="el.name" typeahead-on-select="copy.elements.push(pending.element);pending.element = null">
              </div>
            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" ng-click="$close(copy.elements)">OK</button>
            <button class="btn btn-warning" ng-click="$dismiss()">Cancel</button>
        </div>
        '''

        controller: ['$scope', ($scope) ->
          $scope.copy =
            elements: args.elements ? []
        ]
      }

      dialog.result
 ]
]
