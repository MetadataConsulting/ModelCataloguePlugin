angular.module('mc.core.ui.bs.modalPromptAssetEdit', ['mc.util.messages', 'ngFileUpload', 'mc.core.ui.bs.withClassificationCtrlMixin']).config ['messagesProvider', (messagesProvider)->
  factory = [ '$uibModal', '$q', 'messages', ($uibModal, $q, messages) ->
    (title, body, args) ->
      if not args?.element? and not args?.create?
        messages.error('Cannot create edit dialog.', 'The element to be edited is missing.')
        return $q.reject('Missing element argument!')

      dialog = $uibModal.open {
        windowClass: 'basic-edit-modal-prompt'
        backdrop: 'static'
        keyboard: false
        size: 'lg'
        resolve:
          args: -> args
        template: '''
         <div class="modal-header">
            <h4>''' + title + '''</h4>
        </div>
        <div class="modal-body">
            <messages-panel messages="messages"></messages-panel>
            <form role="form" ng-submit="saveElement()">
              <div class="form-group" ng-if="!hideDataModels()">
                <label for="dataModel"> Data Models</label>
                <elements-as-tags elements="copy.dataModels"></elements-as-tags>
                <input id="dataModel-{{$index}}" placeholder="Data Model" ng-model="pending.dataModel" catalogue-element-picker="dataModel" label="el.name" typeahead-on-select="addToDataModels()">
              </div>
              <div class="form-group">
                <label for="name" class="">Name</label>
                <input type="text" class="form-control" id="name" placeholder="Name (leave blank to use filename)" ng-model="copy.name">
              </div>

              <div class="form-group">
                <label for="asset" class="">File</label>
                <input ng-hide="uploading &amp;&amp; progress" type="file" class="form-control" id="asset" placeholder="File" ngf-model="copy.asset" ngf-select="onFileSelect($files)">
                <progressbar value="progress" ng-show="uploading &amp;&amp; progress">{{progress}} %</progressbar>
              </div>
              <div class="form-group">
                <label for="description" class="">Description</label>
                <textarea rows="10" ng-model="copy.description" placeholder="Description" class="form-control" id="description"></textarea>
              </div>
              <fake-submit-button/>
            </form>
        </div>
        <div class="modal-footer">
          <contextual-actions role="modal"></contextual-actions>
        </div>
        '''
        controller: ['$scope', 'messages', 'names', 'catalogueElementResource', '$uibModalInstance', 'Upload', 'modelCatalogueApiRoot', 'enhance', '$rootScope', '$controller', ($scope, messages, names, catalogueElementResource, $uibModalInstance, Upload, modelCatalogueApiRoot, enhance, $rootScope, $controller) ->
          $scope.pending        = {dataModel: null}
          $scope.newEntity      = -> {dataModels: $scope.copy?.dataModels ? []}
          $scope.copy     = angular.copy(args.element ? $scope.newEntity())
          $scope.original = args.element ? {}
          $scope.messages = messages.createNewMessages()
          $scope.create   = args.create
          $scope.currentDataModel = args.currentDataModel

          angular.extend(this, $controller('withClassificationCtrlMixin', {$scope: $scope}))

          $scope.hasChanged   = ->
            $scope.copy.file or $scope.copy.name != $scope.original.name or $scope.copy.description != $scope.original.description or $scope.copy.dataModels != $scope.original.dataModels

          $scope.cancel = ->
            $scope.progress = undefined
            if $scope.upload
              $scope.upload.abort()

            $uibModalInstance.dismiss('Upload Canceled')

          $scope.onFileSelect = ($files) ->
            if not args?.create
              $scope.messages.clearAllMessages()
              $scope.messages.info 'By changing the file you\'re creating new version automatically'
            $scope.copy.file = $files[0]
            if $scope.copy.name == $scope.copy.originalFileName or $scope.nameFromFile
              $scope.nameFromFile = true
              $scope.copy.name = $scope.copy.file.name

          $scope.saveElement = (newVersion) ->

            if angular.isString($scope.pending.dataModel)
              promise.then -> catalogueElementResource('dataModel').save({name: $scope.pending.dataModel}).then (newDataModel) ->
                $scope.copy.dataModels.push newDataModel
                $scope.pending.dataModel = null

            $scope.messages.clearAllMessages()
            if not $scope.copy.name and not $scope.copy.file
              $scope.messages.error 'Empty Name', 'Please fill the name'
              return


            if $scope.copy.file
              $scope.uploading = true
              $scope.upload = Upload.upload({
                url: "#{modelCatalogueApiRoot}/asset/upload"
                params: {
                  id: $scope.copy.id,
                  name: $scope.copy.name,
                  description: $scope.copy.description,
                  dataModel: $scope.currentDataModel?.id
                } 
                data: {asset: $scope.copy.file}
              }).progress((evt) ->
                $scope.progress = parseInt(100.0 * evt.loaded / evt.total)
              ).success((result) ->
                result = enhance result

                $rootScope.$broadcast 'catalogueElementCreated', result, "#{modelCatalogueApiRoot}/asset/upload"

                $scope.uploading = false
                if result.errors
                  for err in result.errors
                    $scope.messages.error err.message
                else
                  promise = $q.when result
                  if not angular.equals(result.dataModels, $scope.copy.dataModels)
                    result.dataModels = $scope.copy.dataModels
                    promise = catalogueElementResource(result.elementType).update(result)
                  promise.then ->
                    if args?.create
                      messages.success('Created ' + result.getElementTypeName(), "You have created #{result.getElementTypeName()} #{result.name}.")
                    else
                      messages.success('Updated ' + result.getElementTypeName(), "You have updated #{result.getElementTypeName()} #{result.name}.")
                    $uibModalInstance.close(result)
              ).error((data) ->
                for err in data.errors
                  $scope.messages.error err.message
                $scope.uploading = false
                $scope.progress  = 0
              )
            else
              promise = null

              if args?.create
                promise = catalogueElementResource(args.create).save($scope.copy)
              else
                promise = catalogueElementResource($scope.copy.elementType).update($scope.copy, {newVersion: newVersion})

              promise.then (result) ->
                if args?.create
                  messages.success('Created ' + result.getElementTypeName(), "You have created #{result.getElementTypeName()} #{result.name}.")
                else
                  messages.success('Updated ' + result.getElementTypeName(), "You have updated #{result.getElementTypeName()} #{result.name}.")
                $uibModalInstance.close(enhance result)
              , (response) ->
                for err in response.data.errors
                  $scope.messages.error err.message

        ]

      }

      dialog.result
  ]

  messagesProvider.setPromptFactory 'edit-asset', factory
]
