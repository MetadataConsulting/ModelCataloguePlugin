angular.module('mc.core.ui.bs.classificationWizard', ['mc.util.messages', 'mc.util.ui.focusMe']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$modal', '$q', 'messages', '$rootScope', ($modal, $q, messages,$rootScope) ->
    (title, body, args) ->

      $rootScope.createClassificationWizard ?= $modal.open {
        windowClass: 'create-classification-wizard'
        backdrop: 'static'
        keyboard: false
        resolve:
          args: -> args

        #language=HTML
        template: '''
        <div class="modal-header">
            <button type="button" class="close" ng-click="dismiss()"><span aria-hidden="true">&times;</span><span class="sr-only">Cancel</span></button>
            <h4>Classification Wizard</h4>
            <ul class="tutorial-steps">
              <li>
                <button id="step-previous" ng-disabled="step == 'classification'" ng-click="previous()" class="btn btn-default"><span class="glyphicon glyphicon-chevron-left"></span></button>
              </li>
              <li>
                <button id="step-classification" ng-click="select('classification')" class="btn btn-default" ng-class="{'btn-primary': step == 'classification'}">1. Classification</button>
              </li>
              <li>
                <button id="step-elements" ng-disabled="!classification.name" ng-click="select('elements')" class="btn btn-default" ng-class="{'btn-primary': step == 'elements'}">2. Elements</button>
              </li>
              <li>
                <button id="step-next" ng-disabled="!classification.name || step == 'elements'" ng-click="next()" class="btn btn-default" ><span class="glyphicon glyphicon-chevron-right"></span></button>
              </li>
              <li>
                <button id="step-finish" ng-disabled="!classification.name" ng-click="finish()" class="btn btn-default btn-success"><span class="glyphicon glyphicon-ok"></span></button>
              </li>
            </ul>
        </div>
        <div class="modal-body" ng-switch="step">
          <div ng-switch-when="classification" id="classification">
              <form role="form" ng-submit="select('elements')">
                <div class="form-group">
                  <label for="name" class="">Name</label>
                  <input type="text" class="form-control" id="name" placeholder="Name (Required)" ng-model="classification.name" focus-me="step=='classification'" required>
                </div>
                <div class="form-group">
                  <label for="description" class="">Description</label>
                  <textarea rows="10" ng-model="classification.description" placeholder="Description (Optional)" class="form-control" id="description" ng-keydown="navigateOnKey($event, 9, 'elements')"></textarea>
                </div>
              </form>
          </div>
          <div ng-switch-when="elements" id="elements">
              <br/>
              <form role="form">
                <div class="form-group">
                  <label for="name" class="">Data Element</label>
                  <elements-as-tags elements="dataElements"></elements-as-tags>
                  <div class="input-group">
                    <input type="text" class="form-control" id="name" placeholder="Name" ng-model="dataElement.element" focus-me="step=='elements'" catalogue-element-picker="dataElement"  typeahead-on-select="push('dataElements', 'dataElement')">
                    <span class="input-group-btn">
                      <button class="btn btn-success" ng-click="push('dataElements', 'dataElement')" ng-disabled="isEmpty(dataElement.element)"><span class="glyphicon glyphicon-plus"></span></button>
                    </span>
                  </div>
                </div>
              </form>
              <div  ng-click="importFromCSV()">
                <alert type="'info'">
                  <strong>Hint:</strong> If you have CSV file with sample data  <a class="alert-link"><span class="fa fa-magic"></span> import data elements from CSV file headers</a>.
                </alert>
              </div>
          </div>
          <div ng-switch-when="summary" id="summary">
              <h4 ng-show="classification.name &amp;&amp;  finished">Classification <strong>{{classification.name}} created</strong></h4>
          </div>
        </div>
        <div class="modal-footer" ng-if="step == 'summary'">
          <button ng-disabled="!finished" class="btn btn-success" ng-click="reset()"><span class="glyphicon glyphicon-plus"></span> Create Another</button>
          <button ng-disabled="!finished" class="btn btn-default"  ng-click="$dismiss()"><span class="glyphicon glyphicon-remove"></span> Close</button>
        </div>
        '''
        controller: ['$scope', '$state', '$window', 'messages', 'names', 'catalogueElementResource', '$q', '$modalInstance', ($scope, $state, $window, messages, names, catalogueElementResource, $q, $modalInstance) ->
          $scope.reset = ->
            $scope.classification = {classifies:{}}
            $scope.dataElement = {}
            $scope.dataElements = []
            $scope.messages = messages.createNewMessages()
            $scope.steps = ['classification', 'elements']
            $scope.step = 'classification'
            $scope.finishInProgress = false
            $scope.finished = false

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
            return

          $scope.finish = () ->
            return if $scope.finishInProgress
            $scope.finishInProgress = true

            $scope.step = 'summary'

            promise = $q.when {}

            angular.forEach $scope.dataElements, (element, i)->
              unless element.element and element.name
                promise = promise.then ->
                  catalogueElementResource('dataElement').save({name: element.name}).then (newElement) ->
                    $scope.dataElements[i] = newElement
              else
                $scope.dataElements[i] = element.element

            promise = promise.then ->
              $scope.classification.classifies = $scope.dataElements
              catalogueElementResource('classification').save($scope.classification)

            promise = promise.then (classification) ->
                messages.success "Classification #{classification.name} created"
                $scope.finished = true
                classification.show()

          $scope.select = (step) ->
            return if step != 'classification' and not $scope.classification.name
            $scope.step = step
            return

          $scope.next = ->
            return if not $scope.classification.name
            for step, i in $scope.steps
              if step == $scope.step and i < $scope.steps.length - 1
                nextStep = $scope.steps[i + 1]
                if nextStep == 'summary'
                  $scope.finish()
                else
                  $scope.step = nextStep
                  break

          $scope.previous = ->
            return if not $scope.classification.name
            for step, i in $scope.steps
              if step == $scope.step and i != 0
                $scope.step = $scope.steps[i - 1]
                break

          $scope.navigateOnKey = ($event, key, step) ->
            $scope.select(step) if $event.keyCode == key

          $scope.select('classification')

          $scope.dismiss = (reason) ->
            return $modalInstance.dismiss(reason) if $scope.finished
            if $scope.classification.name or $scope.dataElements.length > 0
              messages.confirm("Close Classification Wizard", "Do you want to discard all changes?").then ->
                $modalInstance.dismiss(reason)
            else
              $modalInstance.dismiss(reason)

          $scope.importFromCSV = ->
            messages.prompt("Import Data Elements", null, {type: 'data-element-suggestions-from-csv'}).then (result) ->
              angular.forEach result, (element) ->
                value = {element : element}
                if angular.isString(value.element)
                  value = {name: value.element, create: true}
                else
                  value.name = value.element.name
                  value.elementType = value.element.elementType
                  value.id = value.element.id
                $scope.dataElements.push value

        ]

      }

      $rootScope.createClassificationWizard.result.finally ->
        $rootScope.createClassificationWizard = undefined
  ]

  messagesProvider.setPromptFactory 'create-classification', factory
]