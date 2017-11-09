angular.module('mc.core.ui.bs.dataModelWizard', ['mc.util.messages', 'mc.util.ui.focusMe']).config (messagesProvider)->
  factory = ($uibModal, $q, messages, $rootScope) ->
    'ngInject'
    (title, body, args) ->

      $rootScope.createDataModelWizard ?= $uibModal.open {
        windowClass: 'create-classification-wizard'
        backdrop: 'static'
        keyboard: false
        size: 'lg'
        resolve:
          args: -> args

        #language=HTML
        templateUrl: '/mc/core/ui/dataModelWizard.html'
        controller: ($scope, $state, $window, messages, names, catalogueElementResource, $q, $uibModalInstance,
          catalogue, $rootScope, delayedQueueExecutor) ->
          'ngInject'
          execAfter50 = delayedQueueExecutor(500)


          $scope.reset = ->
            $scope.classification = { classifies:{}, semanticVersion: '0.0.1', policies: [] }
            $scope.import = {}
            $scope.imports = []
            $scope.messages = messages.createNewMessages()
            $scope.steps = ['classification', 'imports']
            $scope.step = 'classification'
            $scope.finishInProgress = false
            $scope.finished = false
            $scope.nameUnique = false
            $scope.copy = {}

            catalogueElementResource('dataModelPolicy').search('unique of kind').then (policies) ->
              $scope.classification.policies = policies.list


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
            return undefined unless $scope.classification.name
            for step, i in $scope.steps
              if step == $scope.step and i < $scope.steps.length - 1
                nextStep = $scope.steps[i + 1]
                if nextStep == 'summary'
                  $scope.finish()
                else
                  $scope.step = nextStep
                  break

          $scope.previous = ->
            return undefined unless $scope.classification.name
            for step, i in $scope.steps
              if step == $scope.step and i != 0
                $scope.step = $scope.steps[i - 1]
                break

          $scope.navigateOnKey = ($event, key, step) ->
            $scope.select(step) if $event.keyCode == key

          $scope.select('classification')

          $scope.dismiss = (reason) ->
            return $uibModalInstance.dismiss(reason) if $scope.finished
            if $scope.classification.name or $scope.imports.length > 0
              messages.confirm("Close Data Model Wizard", "Do you want to discard all changes?").then ->
                $uibModalInstance.dismiss(reason)
            else
              $uibModalInstance.dismiss(reason)
      }

      $rootScope.createDataModelWizard.result.finally ->
        $rootScope.createDataModelWizard = undefined

  messagesProvider.setPromptFactory 'create-classification', factory
  messagesProvider.setPromptFactory 'create-dataModel', factory
