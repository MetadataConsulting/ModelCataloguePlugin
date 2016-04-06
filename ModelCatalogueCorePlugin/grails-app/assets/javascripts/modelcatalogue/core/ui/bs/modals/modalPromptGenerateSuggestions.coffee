angular.module('mc.core.ui.bs.modalPromptGenerateSuggestions', ['mc.util.messages', 'mc.util.ui.focusMe']).config ['messagesProvider', (messagesProvider)->
 messagesProvider.setPromptFactory 'generate-suggestions',  [ '$modal', ($modal) ->
   (title, body) ->
      dialog = $modal.open {
        windowClass: 'messages-modal-prompt'
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body">
            <form role="form">
              <div class="form-group">
                <label for="elements">''' + body + '''</label>
                <select class="form-control" ng-options="name for name in suggestionsNames" ng-model="suggestion"/>
              </div>
            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" ng-click="close()">OK</button>
            <button class="btn btn-warning" ng-click="$dismiss()">Cancel</button>
        </div>
        '''
        resolve:
          suggestionsNames: ['$http', 'modelCatalogueApiRoot', ($http, modelCatalogueApiRoot) ->
            $http.get("#{modelCatalogueApiRoot}/dataArchitect/suggestionsNames").then (response) -> response.data
          ]
        controller: ['$scope', 'suggestionsNames', '$modalInstance', 'rest', 'modelCatalogueApiRoot', 'messages', ($scope, suggestionsNames, $modalInstance, rest, modelCatalogueApiRoot, messages) ->
          $scope.suggestionsNames = suggestionsNames
          $scope.close = ->
            rest(method: 'POST', url: "#{modelCatalogueApiRoot}/dataArchitect/generateSuggestions", params: {suggestion: $scope.suggestion}).then ->
              messages.success "Suggestions are generating in the background. Refresh batch list to see the generated results."
              $modalInstance.close()
        ]
      }

      dialog.result
 ]
]