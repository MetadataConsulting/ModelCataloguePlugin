angular.module('mc.core.ui.bs.modalPromptValueDomainEdit', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$modal', '$q', 'messages', ($modal, $q, messages) ->
    (title, body, args) ->
      if not args?.element? and not args?.create?
        messages.error('Cannot create relationship dialog.', 'The element to be edited is missing.')
        return $q.reject('Missing element argument!')

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
                <label for="conceptualDomain-{{$index}}"> Conceptual Domains</label>
                <div class="tags">
                  <span ng-repeat="conceptualDomain in copy.conceptualDomains">
                      <span class="label label-default">{{conceptualDomain.name}} <a ng-click="removeConceptualDomain($index)" class="remove-tag"><span class="glyphicon glyphicon-remove"></span></a></span>
                  </span>
                </div>
                <input id="conceptualDomain-{{$index}}" placeholder="Conceptual Domain" ng-model="conceptualDomain" catalogue-element-picker="conceptualDomain" label="el.name" typeahead-on-select="addConceptualDomain(conceptualDomain);conceptualDomain = null">
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
                <label for="dataType" class="">Data Type</label>
                <input type="text" id="dataType" placeholder="Data Type" ng-model="copy.dataType" catalogue-element-picker="dataType" label="el.name">
              </div>
              <div class="form-group">
                <label for="unitOfMeasure" class="">Unit of Measure</label>
                <input type="text" id="unitOfMeasure" placeholder="Unit of Measure" ng-model="copy.unitOfMeasure" catalogue-element-picker="measurementUnit" label="el.name + ' (' + el.symbol + ')'">
              </div>
              <div class="checkbox">
                <label>
                  <input type="checkbox" ng-model="copy.multiple"> May contain multiple values (e.g. XSD List)
                </label>
              </div>
              <div class="form-group">
                <label for="rule" ng-click="ruleCollapsed = !ruleCollapsed">Rule <span class="glyphicon" ng-class="{'glyphicon-collapse-down': ruleCollapsed, 'glyphicon-collapse-up': !ruleCollapsed}"></span></label>
                <textarea  collapse="ruleCollapsed" rows="10" ng-model="copy.rule" placeholder="Rule" class="form-control" id="rule"></textarea>
              </div>
            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-success" ng-click="saveElement()" ng-disabled="!hasChanged()"><span class="glyphicon glyphicon-ok"></span> Save</button>
            <button class="btn btn-warning" ng-click="$dismiss()">Cancel</button>
        </div>
        '''
        controller: ['$scope', 'messages', 'names', 'catalogueElementResource', '$modalInstance', ($scope, messages, names, catalogueElementResource, $modalInstance) ->
          $scope.copy     = angular.copy(args.element ? {conceptualDomains: []})
          $scope.original = args.element ? {}
          $scope.messages = messages.createNewMessages()
          $scope.ruleCollapsed = true


          $scope.hasChanged   = ->
            return true if $scope.copy.name != $scope.original.name
            return true if $scope.copy.description != $scope.original.description
            return true if $scope.copy.rule != $scope.original.rule
            return true if $scope.copy.multiple != $scope.original.multiple
            return true if $scope.copy.unitOfMeasure?.id != $scope.original.unitOfMeasure?.id
            return true if $scope.copy.dataType?.id != $scope.original.dataType?.id
            return true if not angular.equals($scope.copy.conceptualDomains, $scope.original.conceptualDomains)
            return false

          $scope.addConceptualDomain = (domain) ->
            for inArray in $scope.copy.conceptualDomains
              return if angular.equals inArray, domain
            $scope.copy.conceptualDomains.push domain

          $scope.removeConceptualDomain = (index) ->
            $scope.copy.conceptualDomains.splice index, 1

          $scope.saveElement = ->
            $scope.messages.clearAllMessages()
            if not $scope.copy.name
              $scope.messages.error 'Empty Name', 'Please fill the name'
              return


            promise = null

            if args?.create
              promise = catalogueElementResource(args.create).save($scope.copy)
            else
              promise = catalogueElementResource($scope.copy.elementType).update($scope.copy)

            promise.then (result) ->
              if args?.create
                messages.success('Created ' + result.elementTypeName, "You have created #{result.elementTypeName} #{result.name}.")
              else
                messages.success('Updated ' + result.elementTypeName, "You have updated #{result.elementTypeName} #{result.name}.")
              $modalInstance.close(result)
            , (response) ->
              for err in response.data.errors
                $scope.messages.error err.message

        ]

      }

      dialog.result
  ]

  messagesProvider.setPromptFactory 'edit-valueDomain', factory
]