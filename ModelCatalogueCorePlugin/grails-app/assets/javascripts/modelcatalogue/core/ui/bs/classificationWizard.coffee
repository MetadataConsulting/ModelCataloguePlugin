classificationWizard = angular.module('mc.core.ui.bs.classificationWizard', ['mc.util.messages'])

#http://stackoverflow.com/questions/14833326/how-to-set-focus-on-input-field-in-angularjs
classificationWizard.directive 'focusMe', ['$timeout', '$parse', ($timeout, $parse) -> {
  link: (scope, element, attrs) ->
    scope.$watch $parse(attrs.focusMe), (value) ->
      $timeout (-> element[0].focus()) if value
}]

classificationWizard.config ['messagesProvider', (messagesProvider)->
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
            <button type="button" class="close" ng-click="$dismiss()"><span aria-hidden="true">&times;</span><span class="sr-only">Cancel</span></button>
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
                    <input type="text" class="form-control" id="name" placeholder="Name" ng-model="dataElement.element" focus-me="step=='elements'" catalogue-element-picker="dataElement">
                    <span class="input-group-btn">
                      <button class="btn btn-success" ng-click="push('dataElements', 'dataElement')" ng-disabled="isEmpty(dataElement.element) || isString(dataElement.element)"><span class="glyphicon glyphicon-plus"></span></button>
                    </span>
                  </div>
                </div>
              </form>
            </tab>
          </div>
          <div ng-switch-when="summary" id="summary">
              <div>
              </div>
              <h4 ng-show="classification.name">Creating new classification <strong>{{classification.name}}</strong></h4>
          </div>
        </div>
        '''
        controller: ['$scope', '$state', '$window', 'messages', 'names', 'catalogueElementResource', '$modalInstance', '$timeout', ($scope, $state, $window, messages, names, catalogueElementResource, $modalInstance, $timeout) ->
          $scope.classification = {classifies:{}}
          $scope.dataElement = {}
          $scope.dataElements = []
          $scope.messages = messages.createNewMessages()
          $scope.steps = ['classification', 'elements']
#          'metadata', 'parents', 'children',
          $scope.step = 'classification'
          $scope.finishInProgress = false


          $scope.isEmpty = (object) ->
            return true if not object
            angular.equals object, {}

          $scope.isString = (object) ->
            angular.isString object

          $scope.push = (arrayName, propertyName) ->
            $scope[propertyName].name = $scope[propertyName].element.name
            $scope[arrayName].push $scope[propertyName]

          $scope.openElementInNewWindow = (element) ->
            url = $state.href('mc.resource.show', {resource: names.getPropertyNameFromType(element.elementType), id: element.id})
            $window.open(url,'_blank')
            return

          $scope.finish = () ->
            return if $scope.finishInProgress
            $scope.finishInProgress = true

            $scope.classification.classifies = $scope.dataElements

            $scope.step = 'summary'

            promise = catalogueElementResource('classification').save($scope.classification)

            promise.then (classification) ->
              classification.refresh().then (fresh) ->
                $modalInstance.close(fresh)

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

        ]

      }

      $rootScope.createClassificationWizard.result.finally ->
        $rootScope.createClassificationWizard = undefined
  ]

  messagesProvider.setPromptFactory 'create-classification', factory
]