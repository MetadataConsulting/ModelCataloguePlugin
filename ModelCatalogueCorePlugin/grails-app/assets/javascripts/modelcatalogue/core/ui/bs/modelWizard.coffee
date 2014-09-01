modelWizard = angular.module('mc.core.ui.bs.modelWizard', ['mc.util.messages'])

#http://stackoverflow.com/questions/14833326/how-to-set-focus-on-input-field-in-angularjs
modelWizard.directive 'focusMe', ['$timeout', '$parse', ($timeout, $parse) -> {
  link: (scope, element, attrs) ->
    scope.$watch $parse(attrs.focusMe), (value) ->
      $timeout (-> element[0].focus()) if value
}]

modelWizard.config ['messagesProvider', (messagesProvider)->
  factory = [ '$modal', '$q', 'messages', '$rootScope', ($modal, $q, messages,$rootScope) ->
    (title, body, args) ->

      $rootScope.createModelWizard ?= $modal.open {
        windowClass: 'create-model-wizard'
        backdrop: 'static'
        keyboard: false
        resolve:
          args: -> args

        #language=HTML
        template: '''
        <div class="modal-header">
            <button type="button" class="close" ng-click="$dismiss()"><span aria-hidden="true">&times;</span><span class="sr-only">Cancel</span></button>
            <h4>Model Wizard</h4>
            <ul class="tutorial-steps">
              <li>
                <button id="step-previous" ng-disabled="step == 'model' || step == 'summary'" ng-click="previous()" class="btn btn-default"><span class="glyphicon glyphicon-chevron-left"></span></button>
              </li>
              <li>
                <button id="step-model" ng-disabled="step == 'summary'" ng-click="select('model')" class="btn btn-default" ng-class="{'btn-primary': step == 'model'}">1. Model</button>
              </li>
              <li>
                <button id="step-metadata" ng-disabled="!model.name || step == 'summary'" ng-click="select('metadata')" class="btn btn-default" ng-class="{'btn-primary': step == 'metadata'}">2. Metadata</button>
              </li>
              <li>
                <button id="step-parents" ng-disabled="!model.name || step == 'summary'" ng-click="select('parents')" class="btn btn-default" ng-class="{'btn-primary': step == 'parents'}">3. Parents</button>
              </li>
              <li>
                <button id="step-children" ng-disabled="!model.name || step == 'summary'" ng-click="select('children')" class="btn btn-default" ng-class="{'btn-primary': step == 'children'}">4. Children</button>
              </li>
              <li>
                <button id="step-elements" ng-disabled="!model.name || step == 'summary'" ng-click="select('elements')" class="btn btn-default" ng-class="{'btn-primary': step == 'elements'}">5. Elements</button>
              </li>
              <li>
                <button id="step-next" ng-disabled="!model.name || step == 'elements' || step == 'summary'" ng-click="next()" class="btn btn-default" ><span class="glyphicon glyphicon-chevron-right"></span></button>
              </li>
              <li>
                <button id="step-finish" ng-disabled="!model.name" ng-click="finish()" class="btn btn-default btn-success"><span class="glyphicon glyphicon-ok"></span></button>
              </li>
            </ul>
        </div>
        <div class="modal-body" ng-switch="step">
          <div ng-switch-when="model" id="model">
              <form role="form" ng-submit="select('metadata')">
                <div class="form-group">
                  <label for="name" class="">Name</label>
                  <input type="text" class="form-control" id="name" placeholder="Name (Required)" ng-model="model.name" focus-me="step=='model'" required>
                </div>
                <div class="form-group">
                  <label for="description" class="">Description</label>
                  <textarea rows="10" ng-model="model.description" placeholder="Description (Optional)" class="form-control" id="description" ng-keydown="navigateOnKey($event, 9, 'metadata')"></textarea>
                </div>
              </form>
          </div>
          <div ng-switch-when="metadata" id="metadata">
              <form ng-submit="select('parents')">
                <simple-object-editor title="Key" value-title="Value" object="metadata"></simple-object-editor>
              </form>
          </div>
          <div ng-switch-when="parents" id="parents">
              <br/>
              <form role="form">
                <div class="form-group">
                  <label for="name" class="">Parent Model</label>
                  <elements-as-tags elements="parents"></elements-as-tags>
                  <div class="input-group">
                    <input type="text" class="form-control" id="name" placeholder="Name" ng-model="parent.element" focus-me="step=='parents'" catalogue-element-picker="model">
                    <span class="input-group-btn">
                      <button class="btn btn-success" ng-click="push('parents', 'parent')" ng-disabled="isEmpty(parent.element) || isString(parent.element)"><span class="glyphicon glyphicon-plus"></span></button>
                    </span>
                  </div>
                  <p class="help-block">Parent model is source for the hierarchy relationship</p>
                </div>
                <simple-object-editor object="parent.ext" title="Relationship Metadata"></simple-object-editor>
              </form>
          </div>
          <div ng-switch-when="children" id="children">
              <br/>
              <form role="form">
                <div class="form-group">
                  <label for="name" class="">Child Model</label>
                  <elements-as-tags elements="children"></elements-as-tags>
                  <div class="input-group">
                    <input type="text" class="form-control" id="name" placeholder="Name" ng-model="child.element" focus-me="step=='children'" catalogue-element-picker="model">
                    <span class="input-group-btn">
                      <button class="btn btn-success" ng-click="push('children', 'child')" ng-disabled="isEmpty(child.element) || isString(child.element)"><span class="glyphicon glyphicon-plus"></span></button>
                    </span>
                  </div>
                  <p class="help-block">Child model is destination for the hierarchy relationship</p>
                </div>
                <simple-object-editor object="child.ext" title="Relationship Metadata"></simple-object-editor>
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
                  <p class="help-block">Data element is destination for the containment relationship</p>
                </div>
                <simple-object-editor object="dataElement.ext" title="Relationship Metadata" hints="['Source Min Occurs', 'Source Max Occurs', 'Destination Min Occurs', 'Destination Max Occurs']"></simple-object-editor>
              </form>
            </tab>
          </div>
          <div ng-switch-when="summary" id="summary">
              <h4 ng-show="model.name &amp;&amp; !finished">Crating new model <strong>{{model.name}}</strong></h4>
              <h4 ng-show="model.name &amp;&amp;  finished">Model <strong>{{model.name}} created</strong></h4>
              <progressbar type="{{finished ? 'success' : 'primary'}}" value="pendingActionsCount == 0 ? 100 : Math.round(100 * (totalActions - pendingActionsCount) / totalActions)">{{totalActions - pendingActionsCount}} / {{totalActions}}</progressbar>
          </div>
        </div>
        <div class="modal-footer" ng-if="step == 'summary'">
          <button ng-disabled="!finished" class="btn btn-success" ng-click="reset()"><span class="glyphicon glyphicon-plus"></span> Create Another</button>
          <button ng-disabled="!finished" class="btn btn-default"  ng-click="$dismiss()"><span class="glyphicon glyphicon-remove"></span> Close</button>
        </div>
        '''
        controller: ['$scope', '$state', '$window', 'messages', 'names', 'catalogueElementResource', '$modalInstance', '$timeout', ($scope, $state, $window, messages, names, catalogueElementResource, $modalInstance, $timeout) ->
          $scope.reset = ->
            $scope.model = {}
            $scope.metadata = {}
            $scope.parent = {ext: {}}
            $scope.parents = []
            $scope.child = {ext: {}}
            $scope.children = []
            $scope.dataElement = {ext: {}}
            $scope.dataElements = []
            $scope.messages = messages.createNewMessages()
            $scope.steps = ['model', 'metadata', 'parents', 'children', 'elements']
            $scope.step = 'model'
            $scope.pendingActions = []
            $scope.pendingActionsCount = 0
            $scope.totalActions = 0
            $scope.finishInProgress = false
            $scope.finished = false

          $scope.reset()


          $scope.isEmpty = (object) ->
            return true if not object
            angular.equals object, {}

          $scope.isString = (object) ->
            angular.isString object

          $scope.push = (arrayName, propertyName) ->
            $scope[propertyName].name = $scope[propertyName].element.name
            $scope[arrayName].push $scope[propertyName]
            $scope[propertyName] = {ext: {}}

          $scope.openElementInNewWindow = (element) ->
            url = $state.href('mc.resource.show', {resource: names.getPropertyNameFromType(element.elementType), id: element.id})
            $window.open(url,'_blank')
            return

          $scope.finish = () ->
            return if $scope.finishInProgress
            $scope.finishInProgress = true

            unless $scope.isEmpty($scope.metadata)
              $scope.pendingActions.push (model)->
                model.ext = $scope.metadata
                catalogueElementResource('model').update(model)

            angular.forEach $scope.parents, (parent) ->
              $scope.pendingActions.push (model) ->
                parent.element.metadata = parent.ext
                model.childOf.add parent.element
                model

            angular.forEach $scope.children, (child) ->
              $scope.pendingActions.push (model) ->
                child.element.metadata = child.ext
                model.parentOf.add child.element
                model

            angular.forEach $scope.dataElements, (element) ->
              $scope.pendingActions.push (model) ->
                element.element.metadata = element.ext
                model.contains.add element.element
                model

            $scope.totalActions = $scope.pendingActionsCount = $scope.pendingActions.length + 1
            $scope.step = 'summary'

            decreasePendingActionsCount = (model) ->
              $scope.pendingActionsCount--
              # not very effective but otherwise lot of "entity updated by another transactions" occurs
              model.refresh().then (fresh) ->
                $timeout((-> fresh), 200)


            promise = catalogueElementResource('model').save($scope.model).then decreasePendingActionsCount

            for action in $scope.pendingActions
             promise = promise.then(action).then decreasePendingActionsCount

            promise.then (model) ->
              messages.success "Model #{model.name} created"
              $scope.finished = true
              model.show()

          $scope.select = (step) ->
            return if step != 'model' and not $scope.model.name
            $scope.step = step
            return

          $scope.next = ->
            return if not $scope.model.name
            for step, i in $scope.steps
              if step == $scope.step and i < $scope.steps.length - 1
                nextStep = $scope.steps[i + 1]
                if nextStep == 'summary'
                  $scope.finish()
                else
                  $scope.step = nextStep
                  break

          $scope.previous = ->
            return if not $scope.model.name
            for step, i in $scope.steps
              if step == $scope.step and i != 0
                $scope.step = $scope.steps[i - 1]
                break

          $scope.navigateOnKey = ($event, key, step) ->
            $scope.select(step) if $event.keyCode == key

          $scope.select('model')

        ]

      }

      $rootScope.createModelWizard.result.finally ->
        $rootScope.createModelWizard = undefined
  ]

  messagesProvider.setPromptFactory 'create-model', factory
]