angular.module('mc.core.ui.bs.dataClassWizard', ['mc.util.messages', 'mc.util.ui.focusMe', 'mc.core.ui.bs.watchAndAskForImportOrCloneCtrl']).config (messagesProvider)->
  factory = ($uibModal, $q, messages, $rootScope) ->
    'ngInject'
    (title, body, args) ->

      # TODO: add add dataModels step

      $rootScope.createModelWizard ?= $uibModal.open {
        windowClass: 'create-model-wizard'
        backdrop: 'static'
        keyboard: false
        size: 'lg'
        resolve:
          args: -> args

        templateUrl: '/mc/core/ui/modals/dataClassWizard.html'
        controller: ($scope, $state, $window, messages, names, catalogueElementResource, $uibModalInstance, $timeout,
          args, delayedQueueExecutor, $q, $log, enhance, metadataEditors, catalogue, $controller) ->
          'ngInject'
          angular.extend(this, $controller('watchAndAskForImportOrCloneCtrl', {$scope: $scope}))

          execAfter50 = delayedQueueExecutor(500)

          orderedMapEnhancer = enhance.getEnhancer('orderedMap')

          $scope.reset = ->
            $scope.args = args
            $scope.dataClass = {dataModels: []}
            $scope.dataClass.name = args.name
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
            $scope.errored = false

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

            $scope.messages.clearAllMessages()

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
                  catalogueElementResource("dataClass").save({name: parent.element, dataModels: dataClass.dataModels}, {skipPolicies: true}).then (parentModel) ->
                    parent.element = parentModel
                    dataClass
              $scope.pendingActions.push (dataClass) ->
                parent.element.metadata = parent.ext
                execAfter50.submit -> dataClass.childOf.add parent.element
                dataClass

            angular.forEach $scope.children, (child) ->
              if angular.isString child.element
                $scope.pendingActions.push (dataClass) ->
                  catalogueElementResource("dataClass").save({name: child.element, dataModels: dataClass.dataModels}, {skipPolicies: true}).then (childModel) ->
                    child.element = childModel
                    dataClass
              $scope.pendingActions.push (dataClass) ->
                child.element.metadata = child.ext
                execAfter50.submit -> dataClass.parentOf.add child.element
                dataClass

            angular.forEach $scope.dataElements, (element) ->
              if angular.isString element.element
                $scope.pendingActions.push (dataClass) ->
                  catalogueElementResource("dataElement").save({name: element.element, dataModels: dataClass.dataModels}, {skipPolicies: true}).then (newElement) ->
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
                  $scope.messages.error(error.message)
               else if errorResponse.data?.error
                 $scope.messages.error(errorResponse.data.error)
               else
                 $log.error 'Unknown response', errorResponse
                 $scope.messages.error('Unknown exception happened while creating new dataClass. See application logs for details.')

               $scope.errored = true
               $q.reject(errorResponse.data ? errorResponse)

            promise.then (dataClass) ->
              $scope.messages.success "Data Class #{dataClass.name} created"
              $scope.finished = true
              $scope.dataClass = dataClass

          $scope.select = (step) ->
            $scope.parentsVisited |= step == 'parents'
            $scope.dataModelsVisited |= step == 'dataModels'
            return if step != 'dataClass' and not $scope.dataClass.name
            $scope.step = step

          $scope.next = ->
            return undefined unless $scope.dataClass.name
            for step, i in $scope.steps
              if step == $scope.step and i < $scope.steps.length - 1
                nextStep = $scope.steps[i + 1]
                if nextStep == 'summary'
                  $scope.finish()
                else
                  $scope.step = nextStep
                  break

          $scope.previous = ->
            return undefined unless $scope.dataClass.name
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
            return $uibModalInstance.dismiss(reason) if $scope.finished
            if $scope.dataClass.name or $scope.dataClass.description or not $scope.isEmpty($scope.metadata) or $scope.parents.length > 0 or $scope.children.length > 0 or $scope.dataElements.length > 0 or $scope.dataModels.length > 0
              messages.confirm("Close Data Class Wizard", "Do you want to discard all changes?").then ->
                $uibModalInstance.dismiss(reason)
            else
              $uibModalInstance.dismiss(reason)

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


          $scope.owners =
            dataClass: metadataEditors.createFakeOwner('dataClass')
            parents: metadataEditors.createFakeOwner('=[hierarchy]=>dataClass')
            children: metadataEditors.createFakeOwner('dataClass=[hierarchy]=>')
            contains: metadataEditors.createFakeOwner('dataClass=[containment]=>')
      }

      $rootScope.createModelWizard.result.finally ->
        $rootScope.createModelWizard = undefined

  messagesProvider.setPromptFactory 'create-model', factory
  messagesProvider.setPromptFactory 'create-dataClass', factory
