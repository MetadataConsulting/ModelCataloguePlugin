angular.module('mc.core.ui.bs.modalPromptRelationshipTypeEdit', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$modal', '$q', 'messages', ($modal, $q, messages) ->
    (title, body, args) ->
      deferred = $q.defer()

      if not args?.element? and not args?.create?
        messages.error('Cannot create edit dialog.', 'The element to be edited is missing.')
        deferred.reject('Missing element argument!')
        return deferred.promise

      dialog = $modal.open {
        windowClass: 'basic-edit-modal-prompt'
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body">
            <messages-panel messages="messages"></messages-panel>
            <form role="form">
              <div class="form-group">
                <label for="name" class="">Name</label>
                <input type="text" class="form-control" id="name" placeholder="Name" ng-model="copy.name">
              </div>
              <div class="form-group">
                <label for="sourceToDestination" class="">Source to Destination</label>
                <input type="text" class="form-control" id="sourceToDestination" placeholder="Source to Destination" ng-model="copy.sourceToDestination">
              </div>
              <div class="form-group">
                <label for="destinationToSource" class="">Destination to Source</label>
                <input type="text" class="form-control" id="destinationToSource" placeholder="Destination to Source" ng-model="copy.destinationToSource">
              </div>
              <div class="form-group">
                <label for="sourceClass" class="">Source Class</label>
                <input type="text" ng-disabled="!create" class="form-control" id="sourceClass" placeholder="Source Class" ng-model="copy.sourceClass" typeahead="class for class in elementClasses | filter:$viewValue | limitTo:8">
              </div>
              <div class="form-group">
                <label for="destinationClass" class="">Destination Class</label>
                <input type="text" ng-disabled="!create" class="form-control" id="destinationClass" placeholder="Destination Class" ng-model="copy.destinationClass" typeahead="class for class in elementClasses | filter:$viewValue | limitTo:8">
              </div>
              <div class="checkbox">
                <label>
                  <input type="checkbox" ng-model="copy.system"> System
                </label>
              </div>
              <div class="checkbox">
                <label>
                  <input type="checkbox" ng-model="copy.bidirectional"> Bidirectional
                </label>
              </div>
              <div class="form-group">
                <label for="metadataHits" class="">Metadata Hints</label>
                <input type="text" class="form-control" id="metadataHits" placeholder="Metadata hints separated by comma" ng-model="copy.metadataHints">
              </div>
              <div class="form-group">
                <label for="rule" ng-click="ruleCollapsed = !ruleCollapsed">Rule <span class="glyphicon" ng-class="{'glyphicon-collapse-down': ruleCollapsed, 'glyphicon-collapse-up': !ruleCollapsed}"></span></label>
                <textarea ng-disabled="!create" collapse="ruleCollapsed" rows="10" ng-model="copy.rule" placeholder="Rule" class="form-control" id="rule"></textarea>
              </div>
            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-success" ng-click="saveElement()" ng-disabled="!hasChanged()"><span class="glyphicon glyphicon-ok"></span> Save</button>
            <button class="btn btn-warning" ng-click="$dismiss()">Cancel</button>
        </div>
        '''

        resolve:
          elementClasses: ['$http', 'modelCatalogueApiRoot', ($http, modelCatalogueApiRoot) ->
            $http.get("#{modelCatalogueApiRoot}/relationshipType/elementClasses").then (response) -> response.data
          ]

        controller: ['$scope', 'messages', 'names', 'catalogueElementResource', '$modalInstance', 'elementClasses', ($scope, messages, names, catalogueElementResource, $modalInstance, elementClasses) ->
          $scope.copy     = angular.copy(args.element ? {})
          $scope.create   = args.create
          $scope.original = args.element ? {}
          $scope.messages = messages.createNewMessages()
          $scope.ruleCollapsed = true
          $scope.elementClasses = elementClasses

          $scope.hasChanged   = ->
            $scope.copy.metadataHints = ($scope.copy.metadataHints ? '').split(/\s*,\s*/) ? [] if angular.isString($scope.copy.metadataHints)
            for prop in ['name', 'sourceToDestination', 'destinationToSource', 'sourceClass', 'destinationClass', 'system', 'bidirectional', 'rule', 'metadataHints']
              return true if !angular.equals($scope.copy[prop], $scope.original[prop])
            return false

          $scope.saveElement = ->
            $scope.copy.metadataHints = $scope.copy.metadataHints.join(',') if angular.isArray($scope.copy.metadataHints)
            $scope.messages.clearAllMessages()
            if not $scope.copy.name
              $scope.messages.error 'Empty Name', 'Please fill the name'
              return

            if not $scope.copy.sourceToDestination
              $scope.messages.error 'Empty Source to Destination', 'Please fill the Source to Destination'
              return

            if not $scope.copy.destinationToSource
              $scope.messages.error 'Empty Destination to Source', 'Please fill the Destination to Source'
              return

            if not $scope.copy.sourceClass
              $scope.messages.error 'Empty Source Class', 'Please fill the Source Class'
              return

            if not $scope.copy.destinationClass
              $scope.messages.error 'Empty Destination Class', 'Please fill the Destination Class'
              return

            promise = null

            if args?.create
              promise = catalogueElementResource(args.create).save($scope.copy)
            else
              promise = catalogueElementResource($scope.copy.elementType).update($scope.copy)

            promise.then (result) ->
              if args?.create
                messages.success('Created Relationship Type', "You have created Relationship Type #{result.name}.")
              else
                messages.success('Updated Relationship Type', "You have updated Relationship Type #{result.name}.")
              $modalInstance.close(result)
            , (response) ->
              for err in response.data.errors
                $scope.messages.error err.message

        ]

      }

      dialog.result.then (result) ->
        deferred.resolve(result)
      , (reason) ->
        deferred.reject(reason)

      deferred.promise
  ]

  messagesProvider.setPromptFactory 'edit-relationshipType', factory

]