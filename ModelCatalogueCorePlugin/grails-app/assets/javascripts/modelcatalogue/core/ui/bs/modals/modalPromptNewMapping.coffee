angular.module('mc.core.ui.bs.modalPromptNewMapping', ['mc.util.messages']).config ['messagesProvider', 'catalogueProvider', (messagesProvider, catalogueProvider)->
  messagesProvider.setPromptFactory 'new-mapping', [ '$uibModal', '$q', 'messages', ($uibModal, $q, messages) ->
    (title, body, args) ->
      if not args?.element?
        messages.error('Cannot create mapping dialog.', 'The element to be mapped is missing.')
        return $q.reject('Missing element argument!')

      elementType = args.element.elementType

      elementType = 'dataType' if catalogueProvider.isInstanceOf(elementType, 'dataType')


      dialog = $uibModal.open {
        windowClass: 'new-relationship-modal-prompt'
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body">
            <messages-panel messages="messages"></messages-panel>
            <form role="form" ng-submit="createMapping()">
              <div class="form-group">
                <label for="dataType" class="">Destination Data Type</label>
                <input type="text" id="dataType" placeholder="Destination Data Type" ng-model="copy.destination" catalogue-element-picker="''' + elementType + '''" label="el.name">
              </div>
              <div class="form-group">
                <label for="mapping">Mapping</label>
                <textarea rows="10" ng-model="copy.mapping" placeholder="Mapping" class="form-control" id="mapping"></textarea>
                <p class="help-block">Enter valid <a href="http://www.groovy-lang.org/" target="_blank">Groovy</a> code. Variable <code>x</code> refers to the value from current domain. Last row is the result. For example you can <a ng-click="showMapExample()"><span class="fa fa-magic"></span> map string values</a> or <a ng-click="showConvertExample()"><span class="fa fa-magic"></span> convert numeric values</a></p>
              </div>
              <fake-submit-button/>
            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" ng-click="createMapping()" type="submit"><span class="fa" ng-class="{'fa-superscript' : !update, 'fa-edit': update}"></span> {{update ? 'Update' : 'Create'}} Mapping</button>
            <button class="btn btn-warning" ng-click="$dismiss()">Cancel</button>
        </div>
        '''
        controller: ['$scope', 'messages', '$uibModalInstance', ($scope, messages, $uibModalInstance) ->
          $scope.update = args.update
          $scope.messages = messages.createNewMessages()
          $scope.copy = if args.mapping then angular.copy(args.mapping) else {}

          MAP_EXAMPLE = """// letters to fruit
[
  'a': 'apple',
  'b': 'banana',
  'c': 'cherry'
][x]"""
          CONVERSION_EXAMPLE = """// compute Fahrenheit to Celsius
(x as Double) * 9 / 5 + 32
"""
          showExample = (example) ->
            ->
              if $scope.copy.mapping and $scope.copy.mapping != MAP_EXAMPLE and $scope.copy.mapping != CONVERSION_EXAMPLE
                messages.confirm("Replace current mapping with example", "Do already have some mapping, do you want to replace it with the example?").then ->
                  $scope.copy.mapping = example
              else
                $scope.copy.mapping = example


          $scope.showMapExample = showExample(MAP_EXAMPLE)
          $scope.showConvertExample = showExample(CONVERSION_EXAMPLE)



          $scope.createMapping = ->
            $scope.messages.clearAllMessages()
            if not $scope.copy.destination or not $scope.copy.destination.id
              $scope.messages.error 'Missing Destination Data Type', 'Please select the destination data type.'
              return

            if not $scope.copy.mapping
              $scope.messages.error 'Missing Mapping', 'Please specify the mapping'
              return


            args.element.mappings.add($scope.copy.destination.id, {mapping: $scope.copy.mapping}, args.update).then (result) ->
              if args.update
                messages.success('Mapping Updated', "You have updated mapping from #{args.element.name} to #{$scope.copy.destination.name}.")
              else
                messages.success('Mapping Created', "You have added new mapping from #{args.element.name} to #{$scope.copy.destination.name}.")
              $uibModalInstance.close(result)
            , (response) ->
              if response.data.errors
                for err in response.data.errors
                  $scope.messages.error err.message

        ]

      }

      dialog.result
  ]
]
