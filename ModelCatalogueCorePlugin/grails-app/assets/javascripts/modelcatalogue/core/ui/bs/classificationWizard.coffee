angular.module('mc.core.ui.bs.dataModelWIzard', ['mc.util.messages', 'mc.util.ui.focusMe']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$modal', '$q', 'messages', '$rootScope', ($modal, $q, messages,$rootScope) ->
    (title, body, args) ->

      $rootScope.createDataModelWizard ?= $modal.open {
        windowClass: 'create-classification-wizard'
        backdrop: 'static'
        keyboard: false
        resolve:
          args: -> args

        #language=HTML
        template: '''
        <div class="modal-header">
            <button type="button" class="close" id="exit-wizard" ng-click="dismiss()"><span aria-hidden="true">&times;</span><span class="sr-only">Cancel</span></button>
            <h4>Data Model Wizard</h4>
            <ul class="tutorial-steps">
              <li>
                <button id="step-previous" ng-disabled="step == 'classification'" ng-click="previous()" class="btn btn-default"><span class="glyphicon glyphicon-chevron-left"></span></button>
              </li>
              <li>
                <button id="step-classification" ng-click="select('classification')" class="btn btn-default" ng-class="{'btn-primary': step == 'classification'}">1. Data Model</button>
              </li>
              <li>
                <button id="step-imports" ng-disabled="!nameUnique" ng-click="select('imports')" class="btn btn-default" ng-class="{'btn-primary': step == 'imports'}">2. Imports</button>
              </li>
              <li>
                <button id="step-next" ng-disabled="!nameUnique || step == 'imports'" ng-click="next()" class="btn btn-default" ><span class="glyphicon glyphicon-chevron-right"></span></button>
              </li>
              <li>
                <button id="step-finish" ng-disabled="!nameUnique" ng-click="finish()" class="btn btn-default btn-success"><span class="glyphicon glyphicon-ok"></span></button>
              </li>
            </ul>
        </div>
        <div class="modal-body" ng-switch="step">
          <messages-panel messages="messages"></messages-panel>
          <div ng-switch-when="classification" id="classification">
              <form role="form" ng-submit="select('imports')">
                <div class="form-group">
                  <label for="name" class="">Name</label>
                  <input type="text" class="form-control" id="name" placeholder="Name (Required)" ng-model="classification.name" focus-me="step=='classification'" required ng-model-options="{debounce: 500}">
                  <p class="help-block">Please, keep the name short. It will be dislayed next to the declared elements' names.</p>
                </div>
                <div class="form-group">
                  <label for="semanticVersion" class="">Semantic Version</label>
                  <input type="text" class="form-control" id="semanticVersion" placeholder="Semantic Version (e.g. 1.0.0)" ng-model="classification.semanticVersion" >
                  <p class="help-block">You will be able to change semantic version when the data model will be published</p>
                </div>
                <div class="form-group">
                  <label for="name" class="">Catalogue ID (URL)</label>
                  <input type="text" class="form-control" id="modelCatalogueId" placeholder="e.g. external ID, namespace (leave blank for generated)" ng-model="classification.modelCatalogueId">
                </div>
                <div class="form-group">
                  <label for="description" class="">Description</label>
                  <textarea rows="10" ng-model="classification.description" placeholder="Description (Optional)" class="form-control" id="description" ng-keydown="navigateOnKey($event, 9, 'imports')"></textarea>
                </div>
              </form>
          </div>
          <div ng-switch-when="imports" id="imports">
              <br/>
              <form role="form">
                <div class="form-group">
                  <label for="name" class="">Imported Data Models</label>
                  <elements-as-tags elements="imports"></elements-as-tags>
                  <div class="input-group">
                    <input type="text" class="form-control" id="name" placeholder="Name" ng-model="import.element" focus-me="step=='imports'" catalogue-element-picker="dataModel" status="finalized" typeahead-on-select="push('imports', 'import')">
                    <span class="input-group-btn">
                      <button class="btn btn-success" ng-click="push('imports', 'import')" ng-disabled="isEmpty(import.element)"><span class="glyphicon glyphicon-plus"></span></button>
                    </span>
                  </div>
                  <p class="help-block">To reuse data classes, data types and measurement units from you need to import them first. You can import only finalized data models.</p>
                </div>
              </form>
          </div>
          <div ng-switch-when="summary" id="summary">
              <h4 ng-show="classification.name &amp;&amp;  finished">Data Model <strong>{{classification.name}} created</strong></h4>
          </div>
        </div>
        <div class="modal-footer" ng-if="step == 'summary'">
          <button ng-disabled="!finished" class="btn btn-success" ng-click="reset()"><span class="glyphicon glyphicon-plus"></span> Create Another</button>
          <button ng-disabled="!finished" class="btn btn-default"  ng-click="$dismiss()"><span class="glyphicon glyphicon-remove"></span> Close</button>
        </div>
        '''
        controller: ['$scope', '$state', '$window', 'messages', 'names', 'catalogueElementResource', '$q', '$modalInstance', 'catalogue', '$rootScope', 'delayedQueueExecutor', ($scope, $state, $window, messages, names, catalogueElementResource, $q, $modalInstance, catalogue, $rootScope, delayedQueueExecutor) ->
          execAfter50 = delayedQueueExecutor(500)

          $scope.reset = ->
            $scope.classification = { classifies:{}, semanticVersion: '0.0.1' }
            $scope.import = {}
            $scope.imports = []
            $scope.messages = messages.createNewMessages()
            $scope.steps = ['classification', 'imports']
            $scope.step = 'classification'
            $scope.finishInProgress = false
            $scope.finished = false
            $scope.nameUnique = false

          $scope.reset()

          $scope.isEmpty = (object) ->
            return true if not object
            angular.equals object, {}

          $scope.isString = (object) ->
            angular.isString object

          $scope.push = (arrayName, propertyName) ->
            value = $scope[propertyName]

            if angular.isString(value.element)
              value = {name: value.element, create: true}
            else
              value.name = value.element.name

            $scope[arrayName].push value
            $scope[propertyName] = {}

          $scope.openElementInNewWindow = (element) ->
            url = $state.href('mc.resource.show', {resource: names.getPropertyNameFromType(element.elementType), id: element.id})
            $window.open(url,'_blank')


          isNameUnique = (name) ->
            deferred = $q.defer()

            unless name
              deferred.resolve false
              return deferred.promise

            checkForUniqueness = (list) ->
              if list.total == 0
                deferred.resolve true
                return

              for classification in list.list
                if classification.name == name
                  deferred.resolve false
                  $scope.messages.error "Name is not unique"
                  return
              if list.next.size != 0
                list.next.then checkForUniqueness
              else
                deferred.resolve true
            catalogueElementResource('dataModel').search(name).then checkForUniqueness
            deferred.promise

          $scope.finish = ->
            return if $scope.finishInProgress
            $scope.finishInProgress = true

            isNameUnique($scope.classification.name).then (unique) ->
              return if not unique
              $scope.step = 'summary'

              promise = $q.when {}

              promise = promise.then ->
                catalogueElementResource('dataModel').save($scope.classification)

              angular.forEach $scope.imports, (element) ->
                promise = promise.then (classification) ->
                  execAfter50.submit ->
                    classification.imports.add element.element
                    classification


              promise = promise.then (classification) ->
                messages.success "Data Model #{classification.name} created"
                $state.go 'mc.resource.show', {dataModelId: classification.id, resource: 'dataModel', id: classification.id}
                $scope.finished = true


          $scope.$watch 'classification.name', (name) ->
            $scope.nameUnique = false
            $scope.messages.clearAllMessages()

            isNameUnique(name).then (unique) ->
              $scope.nameUnique = unique

          $scope.select = (step) ->
            return if step != 'classification' and not $scope.classification.name
            $scope.step = step

          $scope.next = ->
            return undefined if not $scope.classification.name
            for step, i in $scope.steps
              if step == $scope.step and i < $scope.steps.length - 1
                nextStep = $scope.steps[i + 1]
                if nextStep == 'summary'
                  $scope.finish()
                else
                  $scope.step = nextStep
                  break

          $scope.previous = ->
            return undefined if not $scope.classification.name
            for step, i in $scope.steps
              if step == $scope.step and i != 0
                $scope.step = $scope.steps[i - 1]
                break

          $scope.navigateOnKey = ($event, key, step) ->
            $scope.select(step) if $event.keyCode == key

          $scope.select('classification')

          $scope.dismiss = (reason) ->
            return $modalInstance.dismiss(reason) if $scope.finished
            if $scope.classification.name or $scope.imports.length > 0
              messages.confirm("Close Data Model Wizard", "Do you want to discard all changes?").then ->
                $modalInstance.dismiss(reason)
            else
              $modalInstance.dismiss(reason)
        ]

      }

      $rootScope.createDataModelWizard.result.finally ->
        $rootScope.createDataModelWizard = undefined
  ]

  messagesProvider.setPromptFactory 'create-classification', factory
  messagesProvider.setPromptFactory 'create-dataModel', factory
]