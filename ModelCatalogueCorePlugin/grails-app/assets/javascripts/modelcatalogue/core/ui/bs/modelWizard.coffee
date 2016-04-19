angular.module('mc.core.ui.bs.modelWizard', ['mc.util.messages', 'mc.util.ui.focusMe', 'mc.core.ui.bs.watchAndAskForImportOrCloneCtrl']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$modal', '$q', 'messages', '$rootScope', ($modal, $q, messages,$rootScope) ->
    (title, body, args) ->

      # TODO: add add dataModels step

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
            <h4>Data Class Wizard</span></h4>
            <ul class="tutorial-steps">
              <li>
                <button id="step-previous" ng-disabled="step == 'dataClass' || step == 'summary'" ng-click="previous()" class="btn btn-default"><span class="glyphicon glyphicon-chevron-left"></span></button>
              </li>
              <li>
                <button id="step-dataClass" ng-disabled="step == 'summary'" ng-click="select('dataClass')" class="btn btn-default" ng-class="{'btn-primary': step == 'dataClass'}">1. Data Class*</button>
              </li>
              <li>
                <button id="step-metadata" ng-disabled="!dataClass.name || step == 'summary'" ng-click="select('metadata')" class="btn btn-default" ng-class="{'btn-primary': step == 'metadata', 'btn-info': step != 'metadata' &amp;&amp; hasMetadata()}">2. Metadata</button>
              </li>
              <li>
                <button id="step-parents" ng-disabled="!dataClass.name || step == 'summary'" ng-click="select('parents')" class="btn btn-default" ng-class="{'btn-primary': step == 'parents', 'btn-info': step != 'parents' &amp;&amp; parents.length > 0}">3. Parents</button>
              </li>
              <li>
                <button id="step-children" ng-disabled="!dataClass.name || step == 'summary'" ng-click="select('children')" class="btn btn-default" ng-class="{'btn-primary': step == 'children', 'btn-info': step != 'children' &amp;&amp; children.length > 0}">4. Children</button>
              </li>
              <li>
                <button id="step-elements" ng-disabled="!dataClass.name || step == 'summary'" ng-click="select('elements')" class="btn btn-default" ng-class="{'btn-primary': step == 'elements', 'btn-info': step != 'elements' &amp;&amp; dataElements.length > 0}">5. Elements</button>
              </li>
              <li ng-if="dataModels.length == 0">
                <button id="step-dataModels" ng-disabled="!dataClass.name || step == 'summary'" ng-click="select('dataModels')" class="btn btn-default" ng-class="{'btn-primary': step == 'dataModels', 'btn-info': step != 'dataModels' &amp;&amp; dataModels.length > 0}">6. Data Models*</button>
              </li>
              <li>
                <button id="step-next" ng-disabled="!dataClass.name || step == 'dataModels' || step == 'summary'" ng-click="next()" class="btn btn-default" ><span class="glyphicon glyphicon-chevron-right"></span></button>
              </li>
              <li>
                <button id="step-finish" ng-disabled="!dataClass.name || !isModelCatalogueIdValid() || (dataModels.length == 0 &amp;&amp; !dataModel.element)" ng-click="finish()" class="btn btn-default btn-success"><span class="glyphicon glyphicon-ok"></span></button>
              </li>
            </ul>
        </div>
        <div class="modal-body" ng-switch="step">
          <div ng-switch-when="dataClass" id="dataClass">
              <form role="form" ng-submit="select('metadata')">
                <div class="form-group">
                  <label for="name" class="">Name</label>
                  <div class="input-group">
                    <input type="text" class="form-control" id="name" placeholder="Name (Required)" ng-model="dataClass.name" focus-me="step=='dataClass'" required>
                    <span class="input-group-btn">
                      <a class="btn btn-default" ng-click="prefillFrom()"><span class="fa fa-fw fa-copy"></span></a>
                    </span>
                  </div>
                </div>
                <div class="form-group" ng-class="{ 'has-error': !isModelCatalogueIdValid() }">
                  <label for="modelCatalogueId" class="">Catalogue ID (URL)</label>
                  <input type="text" class="form-control" id="modelCatalogueId" placeholder="e.g. external ID, namespace (leave blank for generated)" ng-model="dataClass.modelCatalogueId">
                </div>
                <div class="form-group">
                  <label for="description" class="">Description</label>
                  <textarea rows="10" ng-model="dataClass.description" placeholder="Description (Optional)" class="form-control" id="description" ng-keydown="navigateOnKey($event, 9, 'metadata')"></textarea>
                </div>
                <fake-submit-button/>
              </form>
          </div>
          <div ng-switch-when="metadata" id="metadata">
              <form ng-submit="select('parents')">
                <div>
                  <h4>Metadata</h4>
                  <metadata-editor title="Key" value-title="Value" object="metadata" owner="owners.dataClass"></metadata-editor>
                </div>
                <fake-submit-button/>
              </form>
          </div>
          <div ng-switch-when="parents" id="parents">
              <br/>
              <form role="form">
                <div class="form-group">
                  <label for="name" class="">Parent Data Class</label>
                  <elements-as-tags elements="parents"></elements-as-tags>
                  <div class="input-group">
                    <input type="text" class="form-control" id="name" placeholder="Name" ng-model="parent.element" focus-me="step=='parents'" catalogue-element-picker="dataClass" status='draft' typeahead-on-select="push('parents', 'parent')">
                    <span class="input-group-btn">
                      <button class="btn btn-success" ng-click="push('parents', 'parent')" ng-disabled="isEmpty(parent.element)"><span class="glyphicon glyphicon-plus"></span></button>
                    </span>
                  </div>
                  <p class="help-block">Parent data class is source for the hierarchy relationship</p>
                </div>
                <metadata-editor object="parent.ext" title="Relationship Metadata" owner="owners.parents"></metadata-editor>
              </form>
          </div>
          <div ng-switch-when="children" id="children">
              <br/>
              <form role="form">
                <div class="form-group">
                  <label for="name" class="">Child Data Class</label>
                  <elements-as-tags elements="children"></elements-as-tags>
                  <div class="input-group">
                    <input type="text" class="form-control" id="name" placeholder="Name" ng-model="child.element" focus-me="step=='children'" catalogue-element-picker="dataClass" global="'allow'" typeahead-on-select="pushWithDataModelCheck('children', 'child')">
                    <span class="input-group-btn">
                      <button class="btn btn-success" ng-click="pushWithDataModelCheck('children', 'child')" ng-disabled="isEmpty(child.element)"><span class="glyphicon glyphicon-plus"></span></button>
                    </span>
                  </div>
                  <p class="help-block">Child data class is destination for the hierarchy relationship</p>
                </div>
                <div ng-click="importChildModelsFromCSV()">
                  <alert type="info">
                    <strong>Hint:</strong> If you have CSV file with sample data you can <a class="alert-link"><span class="fa fa-magic"></span> import child data classes from CSV file headers</a>.
                  </alert>
                </div>
                <metadata-editor object="child.ext" title="Relationship Metadata" owner="owners.children"></metadata-editor>
              </form>
          </div>
          <div ng-switch-when="elements" id="elements">
              <br/>
              <form role="form">
                <div class="form-group">
                  <label for="name" class="">Data Element</label>
                  <elements-as-tags elements="dataElements"></elements-as-tags>
                  <div class="input-group">
                    <input type="text" class="form-control" id="name" placeholder="Name" ng-model="dataElement.element" focus-me="step=='elements'" catalogue-element-picker="dataElement" global="'allow'" typeahead-on-select="pushWithDataModelCheck('dataElements', 'dataElement')">
                    <span class="input-group-btn">
                      <button class="btn btn-success" ng-click="pushWithDataModelCheck('dataElements', 'dataElement')" ng-disabled="isEmpty(dataElement.element)"><span class="glyphicon glyphicon-plus"></span></button>
                    </span>
                  </div>
                  <p class="help-block">Data element is destination for the containment relationship</p>
                </div>
                <div ng-click="importFromCSV()">
                  <alert type="info">
                    <strong>Hint:</strong> If you have CSV file with sample data you can <a class="alert-link"><span class="fa fa-magic"></span> import data elements from CSV file headers</a>.
                  </alert>
                </div>
                <metadata-editor object="dataElement.ext" title="Relationship Metadata" owner="owners.contains"></metadata-editor>
              </form>
            </tab>
          </div>
          <div ng-switch-when="dataModels" id="dataModels">
              <br/>
              <form role="form">
                <div class="form-group">
                  <label for="name" class="">Data Models</label>
                  <elements-as-tags elements="dataModels"></elements-as-tags>
                  <div class="input-group">
                    <input type="text" class="form-control" id="name" placeholder="Name" ng-model="dataModel.element" focus-me="step=='dataModels'" catalogue-element-picker="dataModel"  typeahead-on-select="push('dataModels', 'dataModel')" status="draft">
                    <span class="input-group-btn">
                      <button class="btn btn-success" ng-click="push('dataModels', 'dataModel')" ng-disabled="isEmpty(dataModel.element)"><span class="glyphicon glyphicon-plus"></span></button>
                    </span>
                  </div>
                </div>
              </form>
            </tab>
          </div>
          <div ng-switch-when="summary" id="summary">
              <h4 ng-show="dataClass.name &amp;&amp; !finished">Creating new data class <strong>{{dataClass.name}}</strong></h4>
              <h4 ng-show="dataClass.name &amp;&amp;  finished" class='wizard-summary'>Data Class <strong>{{dataClass.name}} created</strong></h4>
              <progressbar type="{{finished ? 'success' : 'primary'}}" value="pendingActionsCount == 0 ? 100 : Math.round(100 * (totalActions - pendingActionsCount) / totalActions)">{{totalActions - pendingActionsCount}} / {{totalActions}}</progressbar>
          </div>
        </div>
        <div class="modal-footer" ng-if="step == 'summary'">
          <button ng-disabled="!finished" class="btn btn-success" ng-click="reset()"><span class="glyphicon glyphicon-plus"></span> Create Another</button>
          <button ng-disabled="!finished" class="btn btn-default"  ng-click="$close(dataClass)" id="exit-wizard"><span class="glyphicon glyphicon-remove"></span> Close</button>
        </div>
        '''
        controller: ['$scope', '$state', '$window', 'messages', 'names', 'catalogueElementResource', '$modalInstance', '$timeout', 'args', 'delayedQueueExecutor', '$q', '$log', 'enhance', 'metadataEditors', 'catalogue', '$controller', ($scope, $state, $window, messages, names, catalogueElementResource, $modalInstance, $timeout, args, delayedQueueExecutor, $q, $log, enhance, metadataEditors, catalogue, $controller) ->

          angular.extend(this, $controller('watchAndAskForImportOrCloneCtrl', {$scope: $scope}))

          execAfter50 = delayedQueueExecutor(500)

          orderedMapEnhancer = enhance.getEnhancer('orderedMap')

          $scope.reset = ->
            $scope.args = args
            $scope.dataClass = {dataModels: []}
            $scope.metadata = orderedMapEnhancer.emptyOrderedMap()
            $scope.parent = {ext: orderedMapEnhancer.emptyOrderedMap()}
            $scope.parents = []
            $scope.child = {ext: orderedMapEnhancer.emptyOrderedMap()}
            $scope.children = []
            $scope.dataElement = args.dataElement ? {ext: orderedMapEnhancer.emptyOrderedMap()}
            $scope.dataElements = []
            $scope.dataModel = {}
            $scope.dataModels = []
            $scope.messages = messages.createNewMessages()
            $scope.steps = ['dataClass', 'metadata', 'parents', 'children', 'elements', 'dataModels']
            $scope.step = 'dataClass'
            $scope.pendingActions = []
            $scope.pendingActionsCount = 0
            $scope.totalActions = 0
            $scope.finishInProgress = false
            $scope.finished = false

            $scope.parentsVisited = false
            $scope.dataModelsVisited = false

            if args.parent
              $scope.parents.push {element: args.parent, name: args.parent.name, metadata: orderedMapEnhancer.emptyOrderedMap()}

            if args.currentDataModel
              $scope.dataModels.push  {element: args.currentDataModel, name: args.currentDataModel.name}

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

          $scope.pushWithDataModelCheck = (arrayName, propertyName) ->
            value = $scope[propertyName]
            unless value
              $log.warn "no scope value for #{propertyName}", $scope
              return


            $scope.cloneOrImport(value.element, args.currentDataModel).then (element) ->
              value.element = element

              if angular.isString element
                value.name = element
                value.create = true
              else
                value.name = element.name



              $scope[arrayName].push value
              $scope[propertyName] = {ext: orderedMapEnhancer.emptyOrderedMap()}

          $scope.openElementInNewWindow = (element) ->
            url = $state.href('mc.resource.show', {resource: names.getPropertyNameFromType(element.elementType), id: element.id})
            $window.open(url,'_blank')

          $scope.finish = () ->
            return if $scope.finishInProgress
            $scope.finishInProgress = true

            $scope.parents.push($scope.parent)                  if angular.isString($scope.parent?.element)
            $scope.children.push($scope.child)                  if angular.isString($scope.child?.element)
            $scope.dataElements.push($scope.dataElement)        if angular.isString($scope.dataElement?.element)
            $scope.dataModels.push($scope.dataModel)            if angular.isString($scope.dataModel?.element)


            angular.forEach $scope.dataModels, (dataModel) ->
              if angular.isString dataModel.element
                $scope.pendingActions.push (dataClass) ->
                  catalogueElementResource('dataModel').save({name: dataModel.element}).then (newDataModel) ->
                    dataClass.dataModels.push newDataModel
                    dataClass
              else
                $scope.pendingActions.push (dataClass) ->
                  dataClass.dataModels.push dataModel.element
                  dataClass

            $scope.pendingActions.push (dataClass) ->
              execAfter50.submit -> catalogueElementResource('dataClass').save(dataClass)

            unless $scope.isEmpty($scope.metadata)
              $scope.pendingActions.push (dataClass)->
                dataClass.ext = $scope.metadata
                execAfter50.submit -> catalogueElementResource('dataClass').update(dataClass)

            angular.forEach $scope.parents, (parent) ->
              if angular.isString parent.element
                $scope.pendingActions.push (dataClass) ->
                  catalogueElementResource("dataClass").save({name: parent.element, dataModels: dataClass.dataModels}).then (parentModel) ->
                    parent.element = parentModel
                    dataClass
              $scope.pendingActions.push (dataClass) ->
                parent.element.metadata = parent.ext
                execAfter50.submit -> dataClass.childOf.add parent.element
                dataClass

            angular.forEach $scope.children, (child) ->
              if angular.isString child.element
                $scope.pendingActions.push (dataClass) ->
                  catalogueElementResource("dataClass").save({name: child.element, dataModels: dataClass.dataModels}).then (childModel) ->
                    child.element = childModel
                    dataClass
              $scope.pendingActions.push (dataClass) ->
                child.element.metadata = child.ext
                execAfter50.submit -> dataClass.parentOf.add child.element
                dataClass

            angular.forEach $scope.dataElements, (element) ->
              if angular.isString element.element
                $scope.pendingActions.push (dataClass) ->
                  catalogueElementResource("dataElement").save({name: element.element, dataModels: dataClass.dataModels}).then (newElement) ->
                    element.element = newElement
                    dataClass
              $scope.pendingActions.push (dataClass) ->
                element.element.metadata = element.ext
                execAfter50.submit -> dataClass.contains.add element.element
                dataClass

            $scope.totalActions = $scope.pendingActionsCount = $scope.pendingActions.length
            $scope.step = 'summary'

            decreasePendingActionsCount = (dataClass) ->
              $scope.pendingActionsCount--
              # not very effective but otherwise lot of "entity updated by another transactions" occurs
              if angular.isFunction(dataClass.refresh)
                return dataClass.refresh().then (fresh) ->
                  $timeout((-> fresh), 200)
              return dataClass


            promise = $q.when($scope.dataClass)

            for action in $scope.pendingActions
             promise = promise.then(action).then decreasePendingActionsCount, (errorResponse) ->
               $scope.finishInProgress = false
               errorResponse = data: errorResponse unless errorResponse.data?
               if errorResponse.data?.errors
                for error in errorResponse.data.errors
                  messages.error(error.message)
               else if errorResponse.data?.error
                 messages.error(errorResponse.data.error)
               else
                 $log.error 'Unknown response', errorResponse
                 messages.error('Unknown exception happened while creating new dataClass. See application logs for details.')

               $q.reject(errorResponse.data ? errorResponse)

            promise.then (dataClass) ->
              messages.success "Data Class #{dataClass.name} created"
              $scope.finished = true
              $scope.dataClass = dataClass

          $scope.select = (step) ->
            $scope.parentsVisited |= step == 'parents'
            $scope.dataModelsVisited |= step == 'dataModels'
            return if step != 'dataClass' and not $scope.dataClass.name
            $scope.step = step

          $scope.next = ->
            return undefined if not $scope.dataClass.name
            for step, i in $scope.steps
              if step == $scope.step and i < $scope.steps.length - 1
                nextStep = $scope.steps[i + 1]
                if nextStep == 'summary'
                  $scope.finish()
                else
                  $scope.step = nextStep
                  break

          $scope.previous = ->
            return undefined if not $scope.dataClass.name
            for step, i in $scope.steps
              if step == $scope.step and i != 0
                $scope.step = $scope.steps[i - 1]
                break

          $scope.navigateOnKey = ($event, key, step) ->
            $scope.select(step) if $event.keyCode == key

          $scope.select('dataClass')

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
            messages.prompt("Import Child Data Classes", null, {type: 'child-model-suggestions-from-csv'}).then (result) ->
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
            if $scope.dataClass.name or $scope.dataClass.description or not $scope.isEmpty($scope.metadata) or $scope.parents.length > 0 or $scope.children.length > 0 or $scope.dataElements.length > 0 or $scope.dataModels.length > 0
              messages.confirm("Close Data Class Wizard", "Do you want to discard all changes?").then ->
                $modalInstance.dismiss(reason)
            else
              $modalInstance.dismiss(reason)

          $scope.prefillFrom = ->
            dataClassPromise = messages.prompt('Clone Data Class', 'Please, select from which Data Class should be the properties cloned', type: 'catalogue-element', resource: 'dataClass')
            dataClassPromise = dataClassPromise.then (dataClass) ->
              return dataClass.refresh() if dataClass.minimal
              return dataClass
            dataClassPromise.then (dataClass) ->
              promises = []
              $scope.dataClass.name         = dataClass.name
              $scope.dataClass.description  = dataClass.description

              $scope.metadata               = angular.copy dataClass.ext

              push = (container, property) ->
                (result) ->
                  angular.forEach result.list, (relation) ->
                    $scope[property] = element: relation.relation, ext: relation.ext
                    $scope.push container, property
                  $scope[property] = {ext: orderedMapEnhancer.emptyOrderedMap()}

              promises.push dataClass.parentOf(null, max: 100).then push('children', 'child')
              promises.push dataClass.contains(null, max: 100).then push('dataElements', 'dataElement')

              $q.all promises

          $scope.hasMetadata = ->
            return $scope.metadata.values.length > 0 and $scope.metadata.values[0].key


          $scope.isModelCatalogueIdValid = ->
            return true if not $scope.dataClass.modelCatalogueId
            return new RegExp(/(http|ftp|https):\/\/[\w-]+(\.[\w-]+)+([\w.,@?^=%&:/~+#-]*[\w@?^=%&/~+#-])?/).test($scope.dataClass.modelCatalogueId)

          $scope.owners =
            dataClass: metadataEditors.createFakeOwner('dataClass')
            parents: metadataEditors.createFakeOwner('=[hierarchy]=>dataClass')
            children: metadataEditors.createFakeOwner('dataClass=[hierarchy]=>')
            contains: metadataEditors.createFakeOwner('dataClass=[containment]=>')
        ]

      }

      $rootScope.createModelWizard.result.finally ->
        $rootScope.createModelWizard = undefined
  ]

  messagesProvider.setPromptFactory 'create-model', factory
  messagesProvider.setPromptFactory 'create-dataClass', factory
]
