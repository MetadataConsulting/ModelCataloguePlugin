angular.module('mc.core.ui.bs.modelWizard', ['mc.util.messages', 'mc.util.ui.focusMe']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$modal', '$q', 'messages', '$rootScope', ($modal, $q, messages,$rootScope) ->
    (title, body, args) ->

      # TODO: add add classifications step

      $rootScope.createModelWizard ?= $modal.open {
        windowClass: 'create-model-wizard'
        backdrop: 'static'
        keyboard: false
        size: 'lg'
        resolve:
          args: -> args

        #language=HTML
        template: '''
        <div class="modal-header">
            <button type="button" class="close" ng-click="dismiss()"><span aria-hidden="true">&times;</span><span class="sr-only">Cancel</span></button>
            <h4>Model Wizard</span></h4>
            <ul class="tutorial-steps">
              <li>
                <button id="step-previous" ng-disabled="step == 'model' || step == 'summary'" ng-click="previous()" class="btn btn-default"><span class="glyphicon glyphicon-chevron-left"></span></button>
              </li>
              <li>
                <button id="step-model" ng-disabled="step == 'summary'" ng-click="select('model')" class="btn btn-default" ng-class="{'btn-primary': step == 'model'}">1. Model</button>
              </li>
              <li>
                <button id="step-metadata" ng-disabled="!model.name || step == 'summary'" ng-click="select('metadata')" class="btn btn-default" ng-class="{'btn-primary': step == 'metadata', 'btn-info': step != 'metadata' &amp;&amp; hasMetadata()}">2. Metadata</button>
              </li>
              <li>
                <button id="step-parents" ng-disabled="!model.name || step == 'summary'" ng-click="select('parents')" class="btn btn-default" ng-class="{'btn-primary': step == 'parents', 'btn-info': step != 'parents' &amp;&amp; parents.length > 0}">3. Parents</button>
              </li>
              <li>
                <button id="step-children" ng-disabled="!model.name || step == 'summary'" ng-click="select('children')" class="btn btn-default" ng-class="{'btn-primary': step == 'children', 'btn-info': step != 'children' &amp;&amp; children.length > 0}">4. Children</button>
              </li>
              <li>
                <button id="step-elements" ng-disabled="!model.name || step == 'summary'" ng-click="select('elements')" class="btn btn-default" ng-class="{'btn-primary': step == 'elements', 'btn-info': step != 'elements' &amp;&amp; dataElements.length > 0}">5. Elements</button>
              </li>
              <li>
                <button id="step-classifications" ng-disabled="!model.name || step == 'summary'" ng-click="select('classifications')" class="btn btn-default" ng-class="{'btn-primary': step == 'classifications', 'btn-info': step != 'classifications' &amp;&amp; classifications.length > 0}">6. Classifications</button>
              </li>
              <li>
                <button id="step-next" ng-disabled="!model.name || step == 'classifications' || step == 'summary'" ng-click="next()" class="btn btn-default" ><span class="glyphicon glyphicon-chevron-right"></span></button>
              </li>
              <li>
                <button id="step-finish" ng-disabled="!model.name || !isModelCatalogueIdValid()" ng-click="finish()" class="btn btn-default btn-success"><span class="glyphicon glyphicon-ok"></span></button>
              </li>
            </ul>
        </div>
        <div class="modal-body" ng-switch="step">
          <div ng-switch-when="model" id="model">
              <form role="form" ng-submit="select('metadata')">
                <div class="form-group">
                  <label for="name" class="">Name</label>
                  <div class="input-group">
                    <input type="text" class="form-control" id="name" placeholder="Name (Required)" ng-model="model.name" focus-me="step=='model'" required>
                    <span class="input-group-btn">
                      <a class="btn btn-default" ng-click="prefillFrom()"><span class="fa fa-fw fa-copy"></span></a>
                    </span>
                  </div>
                </div>
                <div class="form-group" ng-class="{ 'has-error': !isModelCatalogueIdValid() }">
                  <label for="modelCatalogueId" class="">Catalogue ID (URL)</label>
                  <input type="text" class="form-control" id="modelCatalogueId" placeholder="e.g. external ID, namespace (leave blank for generated)" ng-model="model.modelCatalogueId">
                </div>
                <div class="form-group">
                  <label for="description" class="">Description</label>
                  <textarea rows="10" ng-model="model.description" placeholder="Description (Optional)" class="form-control" id="description" ng-keydown="navigateOnKey($event, 9, 'metadata')"></textarea>
                </div>
              </form>
          </div>
          <div ng-switch-when="metadata" id="metadata">
              <form ng-submit="select('parents')">
                <div>
                  <h4>Metadata</h4>
                  <simple-object-editor title="Key" value-title="Value" object="metadata"></simple-object-editor>
                </div>
              </form>
          </div>
          <div ng-switch-when="parents" id="parents">
              <br/>
              <form role="form">
                <div class="form-group">
                  <label for="name" class="">Parent Model</label>
                  <elements-as-tags elements="parents"></elements-as-tags>
                  <div class="input-group">
                    <input type="text" class="form-control" id="name" placeholder="Name" ng-model="parent.element" focus-me="step=='parents'" catalogue-element-picker="model" typeahead-on-select="push('parents', 'parent')">
                    <span class="input-group-btn">
                      <button class="btn btn-success" ng-click="push('parents', 'parent')" ng-disabled="isEmpty(parent.element)"><span class="glyphicon glyphicon-plus"></span></button>
                    </span>
                  </div>
                  <p class="help-block">Parent model is source for the hierarchy relationship</p>
                </div>
                <simple-object-editor object="parent.ext" title="Relationship Metadata" hints="['Min Occurs', 'Max Occurs']"></simple-object-editor>
              </form>
          </div>
          <div ng-switch-when="children" id="children">
              <br/>
              <form role="form">
                <div class="form-group">
                  <label for="name" class="">Child Model</label>
                  <elements-as-tags elements="children"></elements-as-tags>
                  <div class="input-group">
                    <input type="text" class="form-control" id="name" placeholder="Name" ng-model="child.element" focus-me="step=='children'" catalogue-element-picker="model" typeahead-on-select="push('children', 'child')">
                    <span class="input-group-btn">
                      <button class="btn btn-success" ng-click="push('children', 'child')" ng-disabled="isEmpty(child.element)"><span class="glyphicon glyphicon-plus"></span></button>
                    </span>
                  </div>
                  <p class="help-block">Child model is destination for the hierarchy relationship</p>
                </div>
                <div ng-click="importChildModelsFromCSV()">
                  <alert type="info">
                    <strong>Hint:</strong> If you have CSV file with sample data you can <a class="alert-link"><span class="fa fa-magic"></span> import child models from CSV file headers</a>.
                  </alert>
                </div>
                <simple-object-editor object="child.ext" title="Relationship Metadata" hints="['Min Occurs', 'Max Occurs']"></simple-object-editor>
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
                  <p class="help-block">Data element is destination for the containment relationship</p>
                </div>
                <div ng-click="importFromCSV()">
                  <alert type="info">
                    <strong>Hint:</strong> If you have CSV file with sample data you can <a class="alert-link"><span class="fa fa-magic"></span> import data elements from CSV file headers</a>.
                  </alert>
                </div>
                <simple-object-editor object="dataElement.ext" title="Relationship Metadata" hints="['Min Occurs', 'Max Occurs']"></simple-object-editor>
              </form>
            </tab>
          </div>
          <div ng-switch-when="classifications" id="classifications">
              <br/>
              <form role="form">
                <div class="form-group">
                  <label for="name" class="">Classifications</label>
                  <elements-as-tags elements="classifications"></elements-as-tags>
                  <div class="input-group">
                    <input type="text" class="form-control" id="name" placeholder="Name" ng-model="classification.element" focus-me="step=='classifications'" catalogue-element-picker="classification"  typeahead-on-select="push('classifications', 'classification')" status="draft">
                    <span class="input-group-btn">
                      <button class="btn btn-success" ng-click="push('classifications', 'classification')" ng-disabled="isEmpty(classification.element)"><span class="glyphicon glyphicon-plus"></span></button>
                    </span>
                  </div>
                </div>
              </form>
            </tab>
          </div>
          <div ng-switch-when="summary" id="summary">
              <h4 ng-show="model.name &amp;&amp; !finished">Creating new model <strong>{{model.name}}</strong></h4>
              <h4 ng-show="model.name &amp;&amp;  finished">Model <strong>{{model.name}} created</strong></h4>
              <progressbar type="{{finished ? 'success' : 'primary'}}" value="pendingActionsCount == 0 ? 100 : Math.round(100 * (totalActions - pendingActionsCount) / totalActions)">{{totalActions - pendingActionsCount}} / {{totalActions}}</progressbar>
          </div>
        </div>
        <div class="modal-footer" ng-if="step == 'summary'">
          <button ng-disabled="!finished" class="btn btn-success" ng-click="reset()"><span class="glyphicon glyphicon-plus"></span> Create Another</button>
          <button ng-disabled="!finished" class="btn btn-default"  ng-click="$close(model)" id="exit-wizard"><span class="glyphicon glyphicon-remove"></span> Close</button>
        </div>
        '''
        controller: ['$scope', '$state', '$window', 'messages', 'names', 'catalogueElementResource', '$modalInstance', '$timeout', 'args', 'delayedQueueExecutor', '$q', '$log', 'enhance', ($scope, $state, $window, messages, names, catalogueElementResource, $modalInstance, $timeout, args, delayedQueueExecutor, $q, $log, enhance) ->
          execAfter50 = delayedQueueExecutor(500)

          orderedMapEnhancer = enhance.getEnhancer('orderedMap')

          $scope.reset = ->
            $scope.args = args
            $scope.model = {classifications: []}
            $scope.metadata = orderedMapEnhancer.emptyOrderedMap()
            $scope.parent = {ext: orderedMapEnhancer.emptyOrderedMap()}
            $scope.parents = []
            $scope.child = {ext: orderedMapEnhancer.emptyOrderedMap()}
            $scope.children = []
            $scope.dataElement = args.dataElement ? {ext: orderedMapEnhancer.emptyOrderedMap()}
            $scope.dataElements = []
            $scope.classification = {}
            $scope.classifications = []
            $scope.messages = messages.createNewMessages()
            $scope.steps = ['model', 'metadata', 'parents', 'children', 'elements', 'classifications']
            $scope.step = 'model'
            $scope.pendingActions = []
            $scope.pendingActionsCount = 0
            $scope.totalActions = 0
            $scope.finishInProgress = false
            $scope.finished = false

            $scope.parentsVisited = false
            $scope.classificationsVisited = false

            if args.parent
              $scope.parents.push {element: args.parent, name: args.parent.name, metadata: orderedMapEnhancer.emptyOrderedMap()}

          $scope.reset()

          $scope.isEmpty = (object) ->
            return true if not object
            angular.equals object, {}

          $scope.isString = (object) ->
            angular.isString object

          $scope.push = (arrayName, propertyName) ->
            value = $scope[propertyName]
            unless value
              $log.warn "no scope value for #{propertyName}", $scope
              return

            if angular.isString value.element
              value.name = value.element
              value.create = true
            else
              value.name = value.element.name
            $scope[arrayName].push value
            $scope[propertyName] = {ext: orderedMapEnhancer.emptyOrderedMap()}

          $scope.openElementInNewWindow = (element) ->
            url = $state.href('mc.resource.show', {resource: names.getPropertyNameFromType(element.elementType), id: element.id})
            $window.open(url,'_blank')
            return

          $scope.finish = () ->
            return if $scope.finishInProgress
            $scope.finishInProgress = true

            $scope.parents.push($scope.parent)                  if angular.isString($scope.parent?.element)
            $scope.children.push($scope.child)                  if angular.isString($scope.child?.element)
            $scope.dataElements.push($scope.dataElement)        if angular.isString($scope.dataElement?.element)
            $scope.classifications.push($scope.classification)  if angular.isString($scope.classification?.element)

            unless $scope.isEmpty($scope.metadata)
              $scope.pendingActions.push (model)->
                model.ext = $scope.metadata
                execAfter50.submit -> catalogueElementResource('model').update(model)

            angular.forEach $scope.parents, (parent) ->
              if angular.isString parent.element
                $scope.pendingActions.push (model) ->
                  catalogueElementResource("model").save({name: parent.element}).then (parentModel) ->
                    parent.element = parentModel
                    model
              $scope.pendingActions.push (model) ->
                parent.element.metadata = parent.ext
                execAfter50.submit -> model.childOf.add parent.element
                model

            angular.forEach $scope.children, (child) ->
              if angular.isString child.element
                $scope.pendingActions.push (model) ->
                  catalogueElementResource("model").save({name: child.element}).then (childModel) ->
                    child.element = childModel
                    model
              $scope.pendingActions.push (model) ->
                child.element.metadata = child.ext
                execAfter50.submit -> model.parentOf.add child.element
                model

            angular.forEach $scope.dataElements, (element) ->
              if angular.isString element.element
                $scope.pendingActions.push (model) ->
                  catalogueElementResource("dataElement").save({name: element.element}).then (newElement) ->
                    element.element = newElement
                    model
              $scope.pendingActions.push (model) ->
                element.element.metadata = element.ext
                execAfter50.submit -> model.contains.add element.element
                model

            angular.forEach $scope.classifications, (classification) ->
              if angular.isString classification.element
                $scope.pendingActions.push (model) ->
                  catalogueElementResource('classification').save({name: classification.element}).then (newClassification) ->
                    model.classifications.push newClassification
                    execAfter50.submit -> catalogueElementResource('model').update(model)
              else
                $scope.pendingActions.push (model) ->
                    model.classifications.push classification.element
                    execAfter50.submit -> catalogueElementResource('model').update(model)

            $scope.totalActions = $scope.pendingActionsCount = $scope.pendingActions.length + 1
            $scope.step = 'summary'

            decreasePendingActionsCount = (model) ->
              $scope.pendingActionsCount--
              # not very effective but otherwise lot of "entity updated by another transactions" occurs
              model.refresh().then (fresh) ->
                $timeout((-> fresh), 200)


            promise = catalogueElementResource('model').save($scope.model).then decreasePendingActionsCount

            for action in $scope.pendingActions
             promise = promise.then(action).then decreasePendingActionsCount, (errorResponse) ->

               if errorResponse.data.errors
                for error in errorResponse.data.errors
                  messages.error(error.message)
               else if errorResponse.data.error
                 messages.error(errorResponse.data.error)
               else
                 messages.error('Unknown expection happened while creating new model. See application logs for details.')

               $scope.finishInProgress = false
               $q.reject errorResponse.data

            promise.then (model) ->
              messages.success "Model #{model.name} created"
              $scope.finished = true
              $scope.model = model

          $scope.select = (step) ->
            $scope.parentsVisited |= step == 'parents'
            $scope.classificationsVisited |= step == 'classifications'
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

          $scope.importFromCSV = ->
            messages.prompt("Import Data Elements", null, {type: 'data-element-suggestions-from-csv'}).then (result) ->
              angular.forEach result, (element) ->
                value = {element : element}
                if angular.isString(value.element)
                  value = {name: value.element, create: true, element: value.element}
                else
                  value.name = value.element.name
                  value.elementType = value.element.elementType
                  value.id = value.element.id
                $scope.dataElements.push value

          $scope.importChildModelsFromCSV = ->
            messages.prompt("Import Child Models", null, {type: 'child-model-suggestions-from-csv'}).then (result) ->
              angular.forEach result, (element) ->
                value = {element : element}
                if angular.isString(value.element)
                  value = {name: value.element, create: true, element: value.element}
                else
                  value.name = value.element.name
                  value.elementType = value.element.elementType
                  value.id = value.element.id
                $scope.children.push value

          $scope.dismiss = (reason) ->
            return $modalInstance.dismiss(reason) if $scope.finished
            if $scope.model.name or $scope.model.description or not $scope.isEmpty($scope.metadata) or $scope.parents.length > 0 or $scope.children.length > 0 or $scope.dataElements.length > 0 or $scope.classifications.length > 0
              messages.confirm("Close Model Wizard", "Do you want to discard all changes?").then ->
                $modalInstance.dismiss(reason)
            else
              $modalInstance.dismiss(reason)

          $scope.prefillFrom = ->
            modelPromise = messages.prompt('Clone Model', 'Please, select from which Model should be the properties cloned', type: 'catalogue-element', resource: 'model')
            modelPromise.then (model) ->
              promises = []
              $scope.model.name         = model.name
              $scope.model.description  = model.description

              $scope.metadata           = angular.copy model.ext

              angular.forEach model.classifications, (classification) ->
                $scope.classifications.push {element: classification, name: classification.name}

              push = (container, property) ->
                (result) ->
                  angular.forEach result.list, (relation) ->
                    $scope[property] = element: relation.relation, ext: relation.ext
                    $scope.push container, property
                  $scope[property] = {ext: orderedMapEnhancer.emptyOrderedMap()}

              promises.push model.parentOf(null, max: 100).then push('children', 'child')
              promises.push model.contains(null, max: 100).then push('dataElements', 'dataElement')

              $q.all promises

          $scope.hasMetadata = ->
            return $scope.metadata.values.length > 0 and $scope.metadata.values[0].key


          $scope.isModelCatalogueIdValid = ->
            return true if not $scope.model.modelCatalogueId
            return new RegExp(/(http|ftp|https):\/\/[\w-]+(\.[\w-]+)+([\w.,@?^=%&:/~+#-]*[\w@?^=%&/~+#-])?/).test($scope.model.modelCatalogueId)
        ]

      }

      $rootScope.createModelWizard.result.finally ->
        $rootScope.createModelWizard = undefined
  ]

  messagesProvider.setPromptFactory 'create-model', factory
]