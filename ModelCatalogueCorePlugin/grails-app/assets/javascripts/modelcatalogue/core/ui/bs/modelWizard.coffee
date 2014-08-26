angular.module('mc.core.ui.bs.modelWizard', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$modal', '$q', 'messages', '$rootScope', ($modal, $q, messages,$rootScope) ->
    (title, body, args) ->

      $rootScope.createModelWizard ?= $modal.open {
        windowClass: 'create-model-wizard'
        backdrop: 'static'
        resolve:
          args: -> args

        #language=HTML
        template: '''
        <div class="modal-header">
            <button type="button" class="close" ng-click="$dismiss()"><span aria-hidden="true">&times;</span><span class="sr-only">Cancel</span></button>
            <h4>Model Wizard</h4>
            <ul class="tutorial-steps">
              <li>
                <button ng-click="previous()" class="btn btn-default"><span class="glyphicon glyphicon-chevron-left"></span></button>
              </li>
              <li>
                <button ng-click="select('model')" class="btn btn-default" ng-class="{'btn-primary': step == 'model'}">1. Model</button>
              </li>
              <li>
                <button ng-disabled="!model.name" ng-click="select('metadata')" class="btn btn-default" ng-class="{'btn-primary': step == 'metadata'}">2. Metadata</button>
              </li>
              <li>
                <button ng-disabled="!model.name" ng-click="select('parents')" class="btn btn-default" ng-class="{'btn-primary': step == 'parents'}">3. Parents</button>
              </li>
              <li>
                <button ng-disabled="!model.name" ng-click="select('children')" class="btn btn-default" ng-class="{'btn-primary': step == 'children'}">4. Children</button>
              </li>
              <li>
                <button ng-disabled="!model.name" ng-click="select('elements')" class="btn btn-default" ng-class="{'btn-primary': step == 'elements'}">5. Elements</button>
              </li>
              <li>
                <button ng-disabled="!model.name" ng-click="finish()" class="btn btn-default btn-success"><span class="glyphicon glyphicon-ok"></span></button>
              </li>
              <li>
                <button ng-disabled="!model.name" ng-click="next()" class="btn btn-default" ><span class="glyphicon glyphicon-chevron-right"></span></button>
              </li>
            </ul>
        </div>
        <div class="modal-body" ng-switch="step">
          <div ng-switch-when="model">
              <messages-panel messages="messages"></messages-panel>
              <form role="form">
                <div class="form-group">
                  <label for="name" class="">Name</label>
                  <input type="text" class="form-control" id="name" placeholder="Name" ng-model="model.name">
                </div>
                <div class="form-group">
                  <label for="description" class="">Description</label>
                  <textarea rows="10" ng-model="model.description" placeholder="Description" class="form-control" id="description"></textarea>
                </div>
              </form>
          </div>
          <div ng-switch-when="metadata">
              <messages-panel messages="messages"></messages-panel>
              <simple-object-editor title="Key" value-title="Value" object="metadata"></simple-object-editor>
          </div>
          <div ng-switch-when="parents">
              <br/>
              <messages-panel messages="messages"></messages-panel>
              <form role="form">
                <div class="form-group">
                  <label for="name" class="">Parent Model</label>
                  <input type="text" class="form-control" id="name" placeholder="Name" ng-model="parent.element" catalogue-element-picker="model">
                  <p class="help-block">Parent model is source for the hierarchy relationship</p>
                </div>
                <simple-object-editor object="parent.ext" title="Relationship Metadata"></simple-object-editor>
                <button class="btn btn-sm btn-success" ng-click="push('parents', 'parent')" ng-disabled="isEmpty(parent.element) || isString(parent.element)"><span class="glyphicon glyphicon-plus"></span> Add Parent</button>
              </form>
              <hr/>
              <div class="panel panel-default" ng-repeat="r in parents">
                <div class="panel-heading">
                  <h3 class="panel-title"><a ng-click="openElementInNewWindow(r.element)">{{r.element.name}}</a></h3>
                </div>
                <div class="panel-body">
                  <blockquote ng-show="r.element.description" class="preserve-new-lines">{{r.element.description}}</blockquote>
                  <blockquote ng-show="!r.element.description" class="preserve-new-lines"><em>No description</em></blockquote>
                  <h5 ng-hide="isEmpty(r.ext)">Realtionship Metadata</h5>
                  <table ng-hide="isEmpty(r.ext)" class="table table-condensed">
                    <tbody>
                      <tr ng-repeat="(key, value) in r.ext">
                        <td class="col-md-6">{{key}}</td>
                        <td class="col-md-6">{{value}}</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
          </div>
          <div ng-switch-when="children">
              <br/>
              <messages-panel messages="messages"></messages-panel>
              <form role="form">
                <div class="form-group">
                  <label for="name" class="">Child Model</label>
                  <input type="text" class="form-control" id="name" placeholder="Name" ng-model="child.element" catalogue-element-picker="model">
                  <p class="help-block">Child model is destination for the hierarchy relationship</p>
                </div>
                <simple-object-editor object="child.ext" title="Relationship Metadata"></simple-object-editor>
                <button class="btn btn-sm btn-success" ng-click="push('children', 'child')" ng-disabled="isEmpty(child.element) || isString(child.element)"><span class="glyphicon glyphicon-plus"></span> Add Child</button>
              </form>
              <hr/>
              <div class="panel panel-default" ng-repeat="r in children">
                <div class="panel-heading">
                  <h3 class="panel-title"><a ng-click="openElementInNewWindow(r.element)">{{r.element.name}}</a></h3>
                </div>
                <div class="panel-body">
                  <blockquote ng-show="r.element.description" class="preserve-new-lines">{{r.element.description}}</blockquote>
                  <blockquote ng-show="!r.element.description" class="preserve-new-lines"><em>No description</em></blockquote>
                  <h5 ng-hide="isEmpty(r.ext)">Relationship Metadata</h5>
                  <table ng-hide="isEmpty(r.ext)" class="table table-condensed">
                    <tbody>
                      <tr ng-repeat="(key, value) in r.ext">
                        <td class="col-md-6">{{key}}</td>
                        <td class="col-md-6">{{value}}</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
          </div>
          <div ng-switch-when="elements">
              <br/>
              <messages-panel messages="messages"></messages-panel>
              <form role="form">
                <div class="form-group">
                  <label for="name" class="">Data Element</label>
                  <input type="text" class="form-control" id="name" placeholder="Name" ng-model="dataElement.element" catalogue-element-picker="dataElement">
                  <p class="help-block">Data element is destination for the containment relationship</p>
                </div>
                <simple-object-editor object="dataElement.ext" title="Relationship Metadata" hints="['Source Min Occurs', 'Source Max Occurs', 'Destination Min Occurs', 'Destination Max Occurs']"></simple-object-editor>
                <button class="btn btn-sm btn-success" ng-click="push('dataElements', 'dataElement')" ng-disabled="isEmpty(dataElement.element) || isString(dataElement.element)"><span class="glyphicon glyphicon-plus"></span> Add Data Element</button>
              </form>
              <hr/>
              <div class="panel panel-default" ng-repeat="r in dataElements">
                <div class="panel-heading">
                  <h3 class="panel-title"><a ng-click="openElementInNewWindow(r.element)">{{r.element.name}}</a></h3>
                </div>
                <div class="panel-body">
                  <blockquote ng-show="r.element.description" class="preserve-new-lines">{{r.element.description}}</blockquote>
                  <blockquote ng-show="!r.element.description" class="preserve-new-lines"><em>No description</em></blockquote>
                  <h5 ng-hide="isEmpty(r.ext)">Relationship Metadata</h5>
                  <table ng-hide="isEmpty(r.ext)" class="table table-condensed">
                    <tbody>
                      <tr ng-repeat="(key, value) in r.ext">
                        <td class="col-md-6">{{key}}</td>
                        <td class="col-md-6">{{value}}</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </tab>
          </div>
          <div ng-switch-when="summary">
              <messages-panel messages="messages"></messages-panel>
              <div>
              </div>
              <h4 ng-show="model.name">Crating new model <strong>{{model.name}}</strong></h4>
              <progressbar class="progress-striped active" value="pendingActionsCount == 0 ? 100 : Math.round(100 * (totalActions - pendingActionsCount) / totalActions)">{{totalActions - pendingActionsCount}} / {{totalActions}}({{pendingActionsCount == 0 ? 100 : Math.round(100 * (totalActions - pendingActionsCount) / totalActions)}})</progressbar>
          </div>
        </div>
        '''
        controller: ['$scope', '$state', '$window', 'messages', 'names', 'catalogueElementResource', '$modalInstance', '$timeout', ($scope, $state, $window, messages, names, catalogueElementResource, $modalInstance, $timeout) ->
          $scope.model = {}
          $scope.metadata = {}
          $scope.parent = {ext: {}}
          $scope.parents = []
          $scope.child = {ext: {}}
          $scope.children = []
          $scope.dataElement = {ext: {}}
          $scope.dataElements = []
          $scope.messages = messages.createNewMessages()
          $scope.steps = ['model', 'metadata', 'parents', 'children', 'elements', 'summary']
          $scope.step = 'model'
          $scope.pendingActions = []
          $scope.pendingActionsCount = 0
          $scope.totalActions = 0
          $scope.finishInProgress = false


          $scope.isEmpty = (object) ->
            return true if not object
            angular.equals object, {}

          $scope.isString = (object) ->
            angular.isString object

          $scope.push = (arrayName, propertyName) ->
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
              model.refresh().then (fresh) ->
                $modalInstance.close(fresh)

          $scope.select = (step) ->
            $scope.step = step

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

        ]

      }

      $rootScope.createModelWizard.result.finally ->
        $rootScope.createModelWizard = undefined
  ]

  messagesProvider.setPromptFactory 'create-model-wizard', factory
]