angular.module('mc.core.ui.bs.modalPromptGenerateSuggestions', ['mc.util.messages', 'mc.util.ui.focusMe']).config ['messagesProvider', (messagesProvider)->
 messagesProvider.setPromptFactory 'generate-suggestions',  [ '$uibModal', ($uibModal) ->
   (title, body) ->
      dialog = $uibModal.open {
        windowClass: 'messages-modal-prompt'
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body">
            <form role="form">
                <div class="form-group">
                  <label for="data-model-1" class="">Data Model 1</label>
                  <elements-as-tags elements="dataModels"></elements-as-tags>
                  <div class="input-group">
                    <input type="text" class="form-control" id="data-model-1" placeholder="Name" ng-model="dataModel1" catalogue-element-picker="dataModel"  typeahead-on-select="push('dataModels', 'dataModel')">
                  </div>
                </div>
            </form>
            <form role="form">
                <div class="form-group">
                  <label for="data-model-2" class="">Data Model 2</label>
                  <elements-as-tags elements="dataModels"></elements-as-tags>
                  <div class="input-group">
                    <input type="text" class="form-control" id="data-model-2" placeholder="Name" ng-model="dataModel2" catalogue-element-picker="dataModel"  typeahead-on-select="push('dataModels', 'dataModel')">
                  </div>
                </div>
            </form>
            <form role="form">
              <div class="form-group">
                <label for="elements">''' + body + '''</label>
                <select class="form-control" ng-options="name for name in suggestionsNames" ng-model="suggestion"/>
              </div>
            </form>

        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" ng-click="close()" ng-disabled="isEmpty(dataModel1)||isEmpty(dataModel2)||isEmpty(suggestion)">OK</button>
            <button class="btn btn-warning" ng-click="$dismiss()">Cancel</button>
        </div>
        '''
        resolve:
          suggestionsNames: ['$http', 'modelCatalogueApiRoot', ($http, modelCatalogueApiRoot) ->
            $http.get("#{modelCatalogueApiRoot}/dataArchitect/suggestionsNames").then (response) -> response.data
          ]
        controller: ['$scope', 'suggestionsNames', '$uibModalInstance', 'rest', 'modelCatalogueApiRoot', 'messages', ($scope, suggestionsNames, $uibModalInstance, rest, modelCatalogueApiRoot, messages) ->
          $scope.suggestionsNames = suggestionsNames
          $scope.close = ->
            rest(method: 'POST', url: "#{modelCatalogueApiRoot}/dataArchitect/generateSuggestions", params: {suggestion: $scope.suggestion, dataModel1: $scope.dataModel1.id, dataModel2: $scope.dataModel2.id}).then ->
              messages.success "Suggestions are generating in the background. Refresh batch list to see the generated results."
              $uibModalInstance.close()
          $scope.isEmpty = (object) ->
            return true if not object
            angular.equals object, {}
        ]
      }

      dialog.result
 ]
]
