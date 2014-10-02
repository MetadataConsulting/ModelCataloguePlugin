angular.module('mc.core.ui.bs.actions', ['mc.util.ui.actions']).config ['actionsProvider', 'names', (actionsProvider, names)->


  ROLE_ACTION_ACTION = 'action'


  RESOURCES = [
    'classification'
    'model'
    'dataElement'
    'conceptualDomain'
    'valueDomain'
    'dataType'
    'measurementUnit'
    'asset'
    'relationshipType'
    'csvTransformation'
    'batch'
  ]

  actionsProvider.registerActionInRole 'navbar-catalogue-elements', actionsProvider.ROLE_NAVIGATION, -> {
    position:   100
    abstract:   true
    label:      'Catalogue'
  }


  angular.forEach RESOURCES, (resource, index) ->
    actionsProvider.registerChildAction 'navbar-catalogue-elements', 'navbar-' + resource , ['$scope', '$state', '$stateParams', 'names', 'security', 'messages', 'catalogue', ($scope, $state, $stateParams, names, security, messages, catalogue) ->
      return undefined if (resource == 'batch' or resource == 'relationshipType' or resource == 'csvTransformation') and not security.hasRole('CURATOR')

      label = names.getNaturalName(resource) + 's'

      if resource == 'batch'
        label = 'Actions'
      else if resource == 'csvTransformation'
        label = 'CSV Transformations'

      action = {
        icon:       catalogue.getIcon(resource)
        position:   index * 100
        label:      label
        action: ->
          $state.go 'mc.resource.list', {resource: resource}, {inherit: false}
      }

      $scope.$on '$stateChangeSuccess', (ignored, ignoredToState, toParams) ->
        action.active = toParams.resource == resource

      action


    ]


  actionsProvider.registerActionInRole 'navbar-data-architect', actionsProvider.ROLE_NAVIGATION, ['security', (security) ->
    return undefined if not security.hasRole('CURATOR')

    {
      navigation: true
      abstract:   true
      position:   1000
      label:      'Data Architect'
    }
  ]

  actionsProvider.registerChildAction 'navbar-data-architect', 'navbar-imports', ['$scope', '$state', ($scope, $state) ->
    action = {
      position:    100
      label:      'Imports'
      icon:       'fa fa-fw fa-cloud-upload'
      action: ->
        $state.go 'mc.dataArchitect.imports.list'
    }

    $scope.$on '$stateChangeSuccess', (ignored, state) ->
      action.active = state.name == 'mc.dataArchitect.imports.list'

    action
  ]

  actionsProvider.registerChildAction 'navbar-data-architect', 'navbar-uninstantiated-elements', ['$scope', '$state', ($scope, $state) ->
    action = {
      position:    200
      label:      'Uninstantiated Data Elements'
      icon:       'fa fa-fw fa-cube'
      action: ->
        $state.go 'mc.resource.list', {resource: 'dataElement', status: 'uninstantiated'}
    }

    $scope.$on '$stateChangeSuccess', (ignored, state) ->
      action.active = state.name == 'mc.dataArchitect.uninstantiatedDataElements'

    action
  ]

  actionsProvider.registerChildAction 'navbar-data-architect', 'navbar-relations-by-metadata-key', ['$scope', '$state', ($scope, $state) ->
    action = {
      position:    300
      label:      'Create COSD Synonym Data Element Relationships'
      icon:       'fa fa-fw fa-exchange'
      action: ->
        $state.go 'mc.dataArchitect.findRelationsByMetadataKeys'
    }

    $scope.$on '$stateChangeSuccess', (ignored, state) ->
      action.active = state.name == 'mc.dataArchitect.findRelationsByMetadataKeys'

    action
  ]

  actionsProvider.registerChildAction 'navbar-data-architect', 'navbar-element-without-key', ['$scope', '$state', ($scope, $state) ->
    action = {
      position:    400
      label:      'Data Elements without Metadata Key'
      icon:       'fa fa-fw fa-key'
      action: ->
        $state.go 'mc.dataArchitect.metadataKey'
    }

    $scope.$on '$stateChangeSuccess', (ignored, state) ->
      action.active = state.name == 'mc.dataArchitect.metadataKey'

    action
  ]

# TODO: fix or remove
#  actionsProvider.registerChildAction 'navbar-data-architect', 'navbar-export-uninstantiated', ['$window', 'modelCatalogueApiRoot', ($window, modelCatalogueApiRoot) ->
#    {
#      position:    500
#      label:      'Export Uninstantiated Elements'
#      icon:       'fa fa-fw fa-download'
#      action: ->
#        # will need special handling since it's exported to asset?
#        $window.open "#{modelCatalogueApiRoot}/dataArchitect/uninstantiatedDataElements?format=xlsx&report=NHIC", '_blank'; return true
#    }
#  ]


  showErrorsUsingMessages = (messages) ->
    (response) ->
      if response.data and response.data.errors
        if angular.isString response.data.errors
          messages.error response.data.errors
        else
          for err in response.data.errors
            messages.error err.message

  actionsProvider.registerActionInRole 'create-catalogue-element', actionsProvider.ROLE_LIST_ACTION, ['$scope', 'names', 'security', 'messages', ($scope, names, security, messages) ->
    return undefined if not security.hasRole('CURATOR')
    return undefined if not $scope.resource
    return undefined if $scope.resource == 'batch'
    return undefined if not messages.hasPromptFactory('create-' + $scope.resource) and not messages.hasPromptFactory('edit-' + $scope.resource)

    {
    position:   100
    label:      "New #{names.getNaturalName($scope.resource)}"
    icon:       'glyphicon glyphicon-plus-sign'
    type:       'success'
    action:     ->
      args      = {create: ($scope.resource)}
      args.type = if messages.hasPromptFactory('create-' + $scope.resource) then "create-#{$scope.resource}" else "edit-#{$scope.resource}"

      if $scope.resource == 'model' and $scope.contained?.element
        args.parent = $scope.contained.element

      messages.prompt('Create ' + names.getNaturalName($scope.resource), '', args).then (created)->
        unless args.parent
          created.show()

      # TODO: add element to the list instead of going to the detail screen or handle by event
    }
  ]

  actionsProvider.registerActionInRole 'resolveAll', actionsProvider.ROLE_LIST_ACTION, ['$scope', '$rootScope', 'modelCatalogueDataArchitect', 'security', ($scope, $rootScope, modelCatalogueDataArchitect, security)->
    return undefined unless $scope.element
    return undefined unless $scope.element.isInstanceOf 'dataImport'
    return undefined if not security.hasRole('CURATOR')
    action = {
    position:   1000
    label:      'Resolve All'
    icon:       'glyphicon glyphicon-thumbs-up'
    type:       'primary'
    action:     ->
      modelCatalogueDataArchitect.resolveAll($scope.element.id).then ->
        $rootScope.$broadcast 'actionsResolved', $scope.element
    }

    $scope.$watch 'element.pendingAction.total', (newTotal) ->
      action.disabled = newTotal == 0

    return action
  ]

  actionsProvider.registerActionInRole 'ingestQueue', actionsProvider.ROLE_ITEM_ACTION, ['$scope', '$rootScope', 'modelCatalogueDataArchitect', 'security', ($scope, $rootScope, modelCatalogueDataArchitect, security)->
    return undefined unless $scope.element
    return undefined unless $scope.element.isInstanceOf 'dataImport'
    return undefined if not security.hasRole('CURATOR')
    action = {
      position:   1000
      label:      'Ingest Queue'
      icon:       'glyphicon glyphicon-ok-circle'
      type:       'primary'
      action:     ->
        modelCatalogueDataArchitect.ingestQueue($scope.element.id).then ->
          $rootScope.$broadcast 'queueIngested', $scope.element
    }

    $scope.$watch 'element.importQueue.total', (newTotal) ->
      action.disabled = newTotal == 0

    return action
  ]


  actionsProvider.registerActionInRole 'edit-catalogue-element', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', 'messages', 'names', 'security', ($rootScope, $scope, messages, names, security) ->
    return undefined if not $scope.element
    return undefined if $scope.element.isInstanceOf 'dataImport'
    return undefined if not security.hasRole('CURATOR')

    action =
      position:   100
      label:      'Edit'
      icon:       'glyphicon glyphicon-edit'
      type:       'primary'
      disabled:   $scope.element.archived or $scope.element?.status == 'FINALIZED'
      action:     ->
        messages.prompt('Edit ' + $scope.element.getElementTypeName(), '', {type: 'edit-' + names.getPropertyNameFromType($scope.element.elementType), element: $scope.element}).then (updated)->
          $scope.element = updated

    updateAction = ->
      action.disabled = $scope.element.archived or $scope.element?.status == 'FINALIZED'

    $scope.$watch 'element.status', updateAction
    $scope.$watch 'element.archived', updateAction
    $rootScope.$on 'newVersionCreated', updateAction

    return action

  ]


  actionsProvider.registerActionInRole 'create-new-version', actionsProvider.ROLE_LIST_ITEM, ['$rootScope','$scope', 'messages', 'names', 'security', 'catalogueElementResource', ($rootScope, $scope, messages, names, security, catalogueElementResource) ->
    return undefined if not $scope.element
    return undefined if not $scope.element.status
    return undefined if not security.hasRole('CURATOR')

    action = {
    position:   150
    label:      'New Version'
    icon:       'glyphicon glyphicon-circle-arrow-up'
    type:       'primary'
    action:     ->
      messages.confirm('Do you want to create new version?', "New version will be created for #{$scope.element.getElementTypeName()} #{$scope.element.name}").then ->
        catalogueElementResource($scope.element.elementType).update($scope.element, {newVersion: true}).then (updated) ->
          $scope.element = updated
          messages.success("New version created for #{$scope.element.name}")
          $rootScope.$broadcast 'newVersionCreated', $scope.element
        , showErrorsUsingMessages(messages)
    }

    updateAction = ->
      action.disabled = $scope.element.archived

    $scope.$watch 'element.status', updateAction
    $scope.$watch 'element.archived', updateAction
    $rootScope.$on 'newVersionCreated', updateAction

    action
  ]

  actionsProvider.registerActionInRole 'finalize', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', 'messages', 'names', 'security', 'catalogueElementResource', ($rootScope, $scope, messages, names, security, catalogueElementResource) ->
    return undefined if not $scope.element
    return undefined if not $scope.element.status
    return undefined if not security.hasRole('CURATOR')

    action = {
      position:   150
      label:      'Finalize'
      icon:       'glyphicon glyphicon-check'
      type:       'primary'
      action:     ->
        messages.confirm("Do you want to finalize #{$scope.element.getElementTypeName()} #{$scope.element.name} ?", "The #{$scope.element.getElementTypeName()} #{$scope.element.name} will be finalized").then ->
          $scope.element.status = 'FINALIZED'
          catalogueElementResource($scope.element.elementType).update($scope.element).then (updated) ->
            $scope.element = updated
            messages.success("#{$scope.element.name} finalized")
            $rootScope.$broadcast 'newVersionCreated', $scope.element
          , showErrorsUsingMessages(messages)
    }

    updateAction = ->
      action.disabled = $scope.element.archived or $scope.element?.status == 'FINALIZED'

    $scope.$watch 'element.status', updateAction
    $scope.$watch 'element.archived', updateAction
    $rootScope.$on 'newVersionCreated', updateAction

    action
  ]

  actionsProvider.registerActionInRole 'archive', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', 'messages', 'names', 'security', 'enhance', 'rest', 'modelCatalogueApiRoot', ($rootScope, $scope, messages, names, security, enhance, rest, modelCatalogueApiRoot) ->
    return undefined if not $scope.element
    return undefined if not $scope.element.status
    return undefined if not security.hasRole('CURATOR')

    action = {
      position:   150
      label:      'Archive'
      icon:       'glyphicon glyphicon-compressed'
      type:       'danger'
      action:     ->
        messages.confirm("Do you want to archive #{$scope.element.getElementTypeName()} #{$scope.element.name} ?", "The #{$scope.element.getElementTypeName()} #{$scope.element.name} will be archived").then ->
          enhance(rest(url: "#{modelCatalogueApiRoot}#{$scope.element.link}/archive", method: 'POST')).then (archived) ->
            $scope.element = archived
          , showErrorsUsingMessages(messages)
    }

    updateAction = ->
      action.disabled = $scope.element.archived

    $scope.$watch 'element.status', updateAction
    $scope.$watch 'element.archived', updateAction
    $rootScope.$on 'newVersionCreated', updateAction

    action
  ]

  actionsProvider.registerActionInRole 'merge', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', 'messages', 'names', 'security', 'enhance', 'rest', 'modelCatalogueApiRoot', ($rootScope, $scope, messages, names, security, enhance, rest, modelCatalogueApiRoot) ->
    return undefined if not $scope.element
    return undefined if not $scope.element.status
    return undefined if not security.hasRole('CURATOR')

    action = {
      position:   125
      label:      'Merge'
      icon:       'fa fa-code-fork fa-rotate-180 fa-flip-vertical'
      type:       'danger'
      action:     ->
        messages.prompt("Merge #{$scope.element.getElementTypeName()} #{$scope.element.name} to another #{$scope.element.getElementTypeName()}", "All non-system relationships of the #{$scope.element.getElementTypeName()} #{$scope.element.name} will be moved to the following destination and than the #{$scope.element.getElementTypeName()} #{$scope.element.name} will be archived", {type: 'catalogue-element', resource: $scope.element.elementType}).then (destination)->
          enhance(rest(url: "#{modelCatalogueApiRoot}#{$scope.element.link}/merge/#{destination.id}", method: 'POST')).then (merged) ->
            merged.show()
          , showErrorsUsingMessages(messages)
    }

    updateAction = ->
      action.disabled = $scope.element.archived

    $scope.$watch 'element.status', updateAction
    $scope.$watch 'element.archived', updateAction
    $rootScope.$on 'newVersionCreated', updateAction

    action
  ]

  actionsProvider.registerChildActionInRole 'finalize', 'finalize-tree', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', 'messages', 'names', 'security', 'enhance', 'rest', 'modelCatalogueApiRoot', ($rootScope, $scope, messages, names, security, enhance, rest, modelCatalogueApiRoot) ->
    return undefined if not $scope.element
    return undefined if not $scope.element.isInstanceOf('model')
    return undefined if not $scope.element.status
    return undefined if not security.hasRole('CURATOR')

    action = {
      label:      'Finalize Tree'
      type:       'primary'
      action:     ->
        messages.confirm("Finalize Model Tree", "Do you really want to finalize Model #{$scope.element.name} and and all its child models and elements?" ).then ->
          enhance(rest(url: "#{modelCatalogueApiRoot}#{$scope.element.link}/finalizeTree", method: 'POST')).then (finalized) ->
            finalized.show()
          , showErrorsUsingMessages(messages)
    }

    updateAction = ->
      action.disabled = $scope.element.archived or $scope.element?.status == 'FINALIZED'

    $scope.$watch 'element.status', updateAction
    $scope.$watch 'element.archived', updateAction
    $rootScope.$on 'newVersionCreated', updateAction

    action
  ]


  actionsProvider.registerActionInRole 'archive-batch', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', 'messages', 'names', 'security', 'enhance', 'rest', 'modelCatalogueApiRoot', ($rootScope, $scope, messages, names, security, enhance, rest, modelCatalogueApiRoot) ->
    return undefined unless $scope.element and angular.isFunction($scope.element.isInstanceOf) and $scope.element.isInstanceOf('batch') or $scope.batch
    return undefined if not security.hasRole('CURATOR')

    action = {
      position:   150
      label:      'Archive'
      icon:       'glyphicon glyphicon-compressed'
      type:       'danger'
      action:     ->
        batch = $scope.batch ? $scope.element
        messages.confirm("Do you want to archive batch #{batch.name} ?", "The batch #{batch.name} will be archived").then ->
          enhance(rest(url: "#{modelCatalogueApiRoot}#{batch.link}/archive", method: 'POST')).then (archived) ->
            angular.extends batch, archived
          , showErrorsUsingMessages(messages)
    }

    updateAction = ->
      action.disabled = ($scope.batch ? $scope.element).archived

    $scope.$watch 'batch.archived', updateAction
    $scope.$watch 'element.archived', updateAction

    action
  ]

  actionsProvider.registerActionInRole 'delete', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', '$state', 'messages', 'names', 'security', ($rootScope, $scope, $state, messages, names, security) ->
    return undefined if not $scope.element
    return undefined if not security.hasRole('ADMIN')

    action = {
      position:   150
      label:      'Delete'
      icon:       'glyphicon glyphicon-remove'
      type:       'danger'
      action:     ->
        messages.confirm("Do you really want to delete #{$scope.element.getElementTypeName()} #{$scope.element.name} ?", "The #{$scope.element.getElementTypeName()} #{$scope.element.name} will be deleted permanently. This action cannot be undone.").then ->
          $scope.element.delete()
          .then ->
            messages.success "#{$scope.element.getElementTypeName()} #{$scope.element.name} deleted."
            $state.go('mc.resource.list', {resource: names.getPropertyNameFromType($scope.element.elementType)}, {reload: true})
          .catch showErrorsUsingMessages(messages)
    }

    updateAction = ->
      action.disabled = $scope.element.archived

    $scope.$watch 'element.status', updateAction
    $scope.$watch 'element.archived', updateAction
    $rootScope.$on 'newVersionCreated', updateAction

    action
  ]

  actionsProvider.registerActionInRole 'create-new-relationship', actionsProvider.ROLE_ITEM_ACTION, ['$scope', 'messages', 'names', 'security', ($scope, messages, names, security) ->
    return undefined if not $scope.element
    return undefined if not $scope.element.isInstanceOf('org.modelcatalogue.core.CatalogueElement')
    return undefined if $scope.element.isInstanceOf 'dataImport'
    return undefined if not security.hasRole('CURATOR')

    action = {
    position:   200
    label:      'Create Relationship'
    icon:       'glyphicon glyphicon-link'
    type:       'success'
    action:     ->
      messages.prompt('Create Relationship', '', {type: 'new-relationship', element: $scope.element}).then (updated)->
        $scope.element = updated
    }

    updateAction = ->
      action.disabled = $scope.element.archived

    $scope.$watch 'element.status', updateAction
    $scope.$watch 'element.archived', updateAction

    action
  ]

  actionsProvider.registerActionInRole 'create-new-mapping', actionsProvider.ROLE_ITEM_ACTION, ['$scope', 'messages', 'names', 'security', ($scope, messages, names, security) ->
    return undefined if not $scope.element
    return undefined if not $scope.element.hasOwnProperty('mappings')
    return undefined if not security.hasRole('CURATOR')

    {
      position:   300
      label:      'Create Mapping'
      icon:       'fa fa-superscript'
      type:       'success'
      action:     ->
        messages.prompt('Create new mapping for ' + $scope.element.name, '', {type: 'new-mapping', element: $scope.element}).catch showErrorsUsingMessages(messages)
    }
  ]



  actionsProvider.registerActionInRole 'download-asset', actionsProvider.ROLE_ITEM_ACTION, [ '$scope', '$window', ($scope, $window) ->
    return undefined if not $scope.element?.downloadUrl?

    {
      position:   0
      label:      'Download'
      icon:       'glyphicon glyphicon-download'
      type:       'primary'
      action:     ->
        $window.open $scope.element.downloadUrl, '_blank'; return true

    }
  ]

  actionsProvider.registerActionInRole 'transform-csv', actionsProvider.ROLE_ITEM_ACTION, [ '$scope', 'messages', 'security', ($scope, messages, security) ->
    return undefined if not $scope.element
    return undefined if not angular.isFunction $scope.element.isInstanceOf
    return undefined if not $scope.element.isInstanceOf('csvTransformation')
    return undefined if not security.isUserLoggedIn()

    {
      position:   0
      label:      'Transform'
      icon:       'fa fa-long-arrow-right'
      type:       'primary'
      action:     ->
        messages.prompt('Transform CSV File', '', {type: 'transform-csv-file', element: $scope.element})

    }
  ]

  actionsProvider.registerActionInRole 'convert', actionsProvider.ROLE_ITEM_ACTION, [ '$scope', 'messages', 'security', ($scope, messages) ->
    return undefined if not $scope.element
    return undefined if not angular.isFunction $scope.element.isInstanceOf
    return undefined if not $scope.element.isInstanceOf('valueDomain')

    action = {
      position:   -100
      label:      'Convert'
      icon:       'fa fa-long-arrow-right'
      type:       'primary'
      action:     ->
        messages.prompt('', '', {type: 'convert-with-value-domain', source: $scope.element})

    }

    $scope.$watch 'element.mappings.total', (total) ->
      action.disabled = not total

    action

  ]

  actionsProvider.registerActionInRole 'validate-value', actionsProvider.ROLE_ITEM_ACTION, [ '$scope', 'messages', 'security', ($scope, messages) ->
    return undefined if not $scope.element
    return undefined if not angular.isFunction $scope.element.isInstanceOf
    return undefined if not $scope.element.isInstanceOf('valueDomain')

    action = {
      position:   -200
      label:      'Validate Value'
      icon:       'fa fa-check-circle-o'
      type:       'primary'
      action:     ->
        messages.prompt('', '', {type: 'validate-value-by-domain', domain: $scope.element})
    }

    $scope.$watch 'element.rule', (rule) ->
      action.disabled = not rule

    action
  ]

  actionsProvider.registerActionInRole 'refresh-asset', actionsProvider.ROLE_ITEM_ACTION, [ '$scope', '$rootScope', 'catalogueElementResource', ($scope, $rootScope, catalogueElementResource) ->
    return undefined if $scope.element?.elementType != 'org.modelcatalogue.core.Asset'
    return undefined if $scope.element.status != 'PENDING'

    {
      position:   0
      label:      'Refresh'
      icon:       'glyphicon glyphicon-refresh'
      type:       'primary'
      action:     ->
        catalogueElementResource($scope.element.elementType).get($scope.element.id).then (refreshed) ->
          $scope.element = refreshed
          $rootScope.$broadcast 'redrawContextualActions'

    }
  ]

  actionsProvider.registerActionInRole 'generate-merge-models', actionsProvider.ROLE_LIST_ACTION, ['$scope', 'security', 'catalogue', 'modelCatalogueApiRoot', '$http', 'messages', '$state', ($scope, security, catalogue, modelCatalogueApiRoot, $http, messages, $state)->
    return undefined unless security.isUserLoggedIn()
    return undefined unless $scope.list
    return undefined unless catalogue.isInstanceOf($scope.list.itemType, 'batch')
    {
      position:   100
      label:      'Generate Suggestions'
      icon:       'fa fa-flash'
      type:       'primary'
      action: ->
        messages.confirm("Generate Suggestions", "Suggestions to optimalize catalogue will be generated. This may take a long time depending on complexity of the catalogue. You can rerun the action later to clean all resolved batches generated by this action.")
        $http.post("#{modelCatalogueApiRoot}/dataArchitect/generateSuggestions").then ->
          messages.success "Suggestions created"
          $state.go('.', {page: undefined}, {reload: true})
        , ->
          messages.error "Cannot create actions to merge models."
    }
  ]

  actionsProvider.registerActionInRoles 'export', [actionsProvider.ROLE_LIST_ACTION, actionsProvider.ROLE_ITEM_ACTION], ['$scope', 'security', ($scope, security)->
    return undefined unless security.isUserLoggedIn()
    return undefined unless $scope.list or $scope.element
    if $scope.list
      return undefined if $scope.resource == 'import'
    if $scope.element
      return undefined if $scope.element.isInstanceOf 'dataImport'
    {
      position:   1000
      label:      'Export'
      icon:       'glyphicon glyphicon-download-alt'
      type:       'primary'
      expandToLeft: true
    }
  ]

  generateReports = ($scope, $window, enhance, rest) ->
    (reports = []) ->
      for report in reports
        {
          label:  report.title
          url:    report.url
          action: ->
            if report.type == 'LINK'
              $window.open(@url, '_blank')
            else enhance(rest(method: 'GET', url: @url)).then (result) ->
              result.show()
            return true
        }

  actionsProvider.registerChildActionInRole 'export', 'catalogue-element-export-specific-reports', actionsProvider.ROLE_ITEM_ACTION, ['$scope', '$window', 'enhance', 'rest', ($scope, $window, enhance, rest) ->
    return undefined if not $scope.element

    {
    position:   1000
    label:      "#{$scope.element.name} Reports"
    disabled:   not $scope.element?.availableReports?.length
    generator:  (action) ->
      action.createActionsFrom 'element.availableReports', generateReports($scope, $window, enhance, rest)
    }
  ]

  actionsProvider.registerChildAction 'export', 'generic-reports', ['$scope', '$window', 'enhance', 'rest', ($scope, $window, enhance, rest) ->
    {
    position:   2000
    label:      "Other Reports"
    disabled:   not $scope.reports?.length
    generator: (action) ->
      action.createActionsFrom 'reports', generateReports($scope, $window, enhance, rest)
    }
  ]

  actionsProvider.registerChildAction 'export', 'list-exports-current', actionsProvider.ROLE_LIST_ACTION, ['$scope', '$window', 'enhance', 'rest', ($scope, $window, enhance, rest) ->
    return undefined if not $scope.list?

    {
    position:   5000
    label:      "Current Reports"
    disabled:   not $scope.list.availableReports?.length
    generator:  (action) ->
      action.createActionsFrom 'list.availableReports', generateReports($scope, $window, enhance, rest)
    }
  ]

  actionsProvider.registerActionInRole 'switch-status', actionsProvider.ROLE_LIST_ACTION, ['$state', '$scope', '$stateParams', ($state, $scope, $stateParams) ->
    return undefined unless $state.current.name == 'mc.resource.list' and $scope.list and not $scope.noStatusSwitch and $stateParams.resource in ['model', 'dataElement', 'asset']

    {
    abstract: true
    position: 500

    type:     (->
      return 'info'     if $stateParams.status == 'draft'
      return 'warning'  if $stateParams.status == 'pending'
      return 'primary'
    )()
    icon:     (->
      return 'glyphicon glyphicon-pencil'   if $stateParams.status == 'draft'
      return 'glyphicon glyphicon-time'     if $stateParams.status == 'pending'
      return 'glyphicon glyphicon-ok'
    )()
    label:    (->
      return 'Draft'    if $stateParams.status == 'draft'
      return 'Pending'  if $stateParams.status == 'pending'
      return 'Finalized'
    )()
    }
  ]

  actionsProvider.registerChildAction 'switch-status', 'switch-status-finalized', ['$state', '$stateParams', ($state, $stateParams) ->
    {
    position:   300
    label:      "Finalized"
    icon:       'glyphicon glyphicon-ok'
    type:       'primary'
    active:     !$stateParams.status or $stateParams.status == 'finalized'
    action:     ->
      newParams = angular.copy($stateParams)
      newParams.status = undefined
      $state.go 'mc.resource.list', newParams
    }
  ]

  actionsProvider.registerChildAction 'switch-status', 'switch-status-pending', ['$state', '$stateParams', ($state, $stateParams) ->
    {
      position:   200
      label:      "Pending"
      icon:       'glyphicon glyphicon-time'
      type:       'warning'
      active:     $stateParams.status == 'pending'
      action:     ->
        newParams = angular.copy($stateParams)
        newParams.status = 'pending'
        $state.go 'mc.resource.list', newParams
    }
  ]

  actionsProvider.registerChildAction 'switch-status', 'switch-status-draft', ['$state', '$stateParams', ($state, $stateParams) ->
    {
    position:   100
    label:      "Draft"
    icon:       'glyphicon glyphicon-pencil'
    type:       'info'
    active:     $stateParams.status == 'draft'
    action:     ->
      newParams = angular.copy($stateParams)
      newParams.status = 'draft'
      $state.go 'mc.resource.list', newParams
    }
  ]


  actionsProvider.registerActionInRole 'switch-archived-batches', actionsProvider.ROLE_LIST_ACTION, ['$state', '$scope', '$stateParams', ($state, $scope, $stateParams) ->
    return undefined unless $state.current.name == 'mc.resource.list' and $scope.list and $stateParams.resource == 'batch'

    {
    abstract: true
    position: 500

    type:     (->
      return 'info'     if $stateParams.status == 'archived'
      return 'primary'
    )()
    icon:     (->
      return 'glyphicon glyphicon-time'     if $stateParams.status == 'archived'
      return 'glyphicon glyphicon-ok'
    )()
    label:    (->
      return 'Archived'  if $stateParams.status == 'archived'
      return 'Active'
    )()
    }
  ]

  actionsProvider.registerChildAction 'switch-archived-batches', 'switch-archived-batches-active', ['$state', '$stateParams', ($state, $stateParams) ->
    {
    position:   300
    label:      "Active"
    icon:       'glyphicon glyphicon-ok'
    type:       'primary'
    active:     !$stateParams.status
    action:     ->
      newParams = angular.copy($stateParams)
      newParams.status = undefined
      $state.go 'mc.resource.list', newParams
    }
  ]

  actionsProvider.registerChildAction 'switch-archived-batches', 'switch-archived-batches-archived', ['$state', '$stateParams', ($state, $stateParams) ->
    {
    position:   200
    label:      "Archived"
    icon:       'glyphicon glyphicon-time'
    type:       'warning'
    active:     $stateParams.status == 'archived'
    action:     ->
      newParams = angular.copy($stateParams)
      newParams.status = 'archived'
      $state.go 'mc.resource.list', newParams
    }
  ]



  actionsProvider.registerActionInRole 'run-action', ROLE_ACTION_ACTION, ['$scope', ($scope) ->
    return undefined unless $scope.action and $scope.action.state == 'PENDING'

    {
      position: 200
      type:     'success'
      icon:     'glyphicon glyphicon-play'
      label:    'Run'
      action:   ->
        $scope.action.run().then ->
          $scope.reload() if angular.isFunction($scope.reload)
    }
  ]



  actionsProvider.registerActionInRole 'dismiss-action', ROLE_ACTION_ACTION, ['$scope', ($scope) ->
    return undefined unless $scope.action and $scope.action.state == 'PENDING'

    {
      position: 500
      type:     'danger'
      icon:     'glyphicon glyphicon-remove'
      label:    'Dismiss'
      action:   ->
        $scope.action.dismiss().then ->
          $scope.reload() if angular.isFunction($scope.reload)
    }
  ]



  actionsProvider.registerActionInRole 'reactivate-action', ROLE_ACTION_ACTION, ['$scope', ($scope) ->
    return undefined unless $scope.action and $scope.action.state == 'DISMISSED'

    {
      position: 200
      type:     'success'
      icon:     'glyphicon glyphicon-repeat'
      label:    'Reactivate'
      action:   ->
        $scope.action.reactivate().then ->
          $scope.reload() if angular.isFunction($scope.reload)
    }
  ]

  actionsProvider.registerActionInRole 'repeat-action', ROLE_ACTION_ACTION, ['$scope', ($scope) ->
    return undefined unless $scope.action and $scope.action.state == 'FAILED'

    {
      position: 900
      type:     'success'
      icon:     'glyphicon glyphicon-repeat'
      label:    'Retry'
      action:   ->
        $scope.action.reactivate().then ->
          $scope.reload() if angular.isFunction($scope.reload)
    }
  ]


  actionsProvider.registerActionInRoles 'reload-actions', [ROLE_ACTION_ACTION, actionsProvider.ROLE_ITEM_ACTION], ['$scope', ($scope) ->
    return undefined unless angular.isFunction($scope.reload) and ($scope.action and $scope.action.state == 'PERFORMING') or ($scope.batch and not $scope.action)

    {
      position: 900
      type:     'success'
      icon:     'glyphicon glyphicon-refresh'
      label:    'Reload'
      action:   ->
        $scope.reload()
    }
  ]

  actionsProvider.registerActionInRole 'link-actions', ROLE_ACTION_ACTION, ['$scope', '$rootScope', 'messages',($scope, $rootScope, messages) ->
    return undefined unless $scope.action and not ($scope.action.state == 'PERFORMING' or $scope.action.state == 'PERFORMED')

    action = {
      position: 950
      type:     'primary'
      icon:     'glyphicon glyphicon-open'
      label:    'Add or Remove Dependency'
      action:   ->
        if $rootScope.selectedAction == $scope.action
          $rootScope.selectedAction = undefined
        else
          if @mode == 'select'
            $rootScope.selectedAction = $scope.action
          else
            selected = $rootScope.selectedAction
            if @mode == 'add'
              messages.prompt('Add Dependency', 'Please, provide the name of the role for the new dependency').then (role) ->
                selected.addDependency($scope.action.id, role).then ->
                  $scope.reload() if angular.isFunction($scope.reload)
            else if @mode == 'remove'
              messages.confirm('Remove Dependency', 'Do you really want to remove dependency between these two actions? This may cause problems executing given action!').then ->
                selected.removeDependency(selected.dependsOn['' + $scope.action.id]).then ->
                  $scope.reload() if angular.isFunction($scope.reload)
            $rootScope.selectedAction = undefined


    }

    $rootScope.$watch 'selectedAction', (selectedAction) ->
      if selectedAction
        if selectedAction == $scope.action
          action.active = true
          action.icon = 'glyphicon glyphicon-open'
          action.label = 'Add or Remove Dependency'
          action.mode = 'select'
        else
          action.active = false
          if selectedAction.dependsOn.hasOwnProperty('' + $scope.action.id)
            action.icon = 'glyphicon glyphicon-remove-circle'
            action.label = 'Remove Dependency'
            action.mode = 'remove'
          else
            action.icon = 'glyphicon glyphicon-save'
            action.label = 'Select as Dependency'
            action.mode = 'add'

      else
        action.icon = 'glyphicon glyphicon-open'
        action.active = false
        action.label = 'Add or Remove Dependency'
        action.mode = 'select'

    action
  ]


  actionsProvider.registerActionInRole 'run-all-actions-in-batch', actionsProvider.ROLE_ITEM_ACTION, ['$scope', 'messages', 'modelCatalogueApiRoot', 'enhance', 'rest', '$timeout', 'security', ($scope, messages, modelCatalogueApiRoot, enhance, rest, $timeout, security) ->
    return undefined if not security.hasRole('CURATOR')
    return undefined unless $scope.element and angular.isFunction($scope.element.isInstanceOf) and $scope.element.isInstanceOf('batch') or $scope.batch

    action = {
      position: 200
      type:     'success'
      icon:     'glyphicon glyphicon-flash'
      label:    'Run All Pending'
      action:   ->
        batch = $scope.batch ? $scope.element
        messages.confirm('Run All Actions', "Do you really wan to run all actions from '#{batch.name}' batch").then ->
          enhance(rest(method: 'POST', url: "#{modelCatalogueApiRoot}#{batch.link}/run")).then (updated) ->
            angular.extend(batch, updated)
          $timeout($scope.reload, 1000) if angular.isFunction($scope.reload)
    }

    updateDisabled = (batch) ->
      return unless batch
      action.disabled = not batch.pending.total

    updateDisabled($scope.batch ? $scope.element)

    $scope.$watch 'batch', updateDisabled
    $scope.$watch 'element', updateDisabled

    action

  ]



  actionsProvider.registerActionInRole 'update-action-parameters', ROLE_ACTION_ACTION, ['$scope', 'messages', 'names', 'security', ($scope, messages, names, security) ->
    return undefined if not $scope.action
    return undefined if $scope.action.state in ['PERFORMING', 'PERFORMED']
    return undefined if not security.hasRole('CURATOR')

    action =
      position:   100
      label:      'Update Action Parameters'
      icon:       'glyphicon glyphicon-edit'
      type:       'primary'
      action:     ->
        messages.prompt('Update Action Parameters', '', {type: 'update-action-parameters', action: $scope.action}).then (updated)->
          $scope.action = updated

    updateAction = ->
      action.disabled = $scope.action.state in ['PERFORMING', 'PERFORMED']

    $scope.$watch 'action.state', updateAction

    updateAction()

    return action

  ]

  actionsProvider.registerActionInRole 'modal-cancel', actionsProvider.ROLE_MODAL_ACTION, ['$scope', ($scope) ->
    return undefined if not $scope.$dismiss

    {
      position:   10000
      label:      'Cancel'
      icon:       'glyphicon glyphicon-ban-circle'
      type:       'warning'
      action: -> $scope.$dismiss()
    }
  ]

  actionsProvider.registerActionInRole 'modal-save-element', actionsProvider.ROLE_MODAL_ACTION, ['$scope', ($scope) ->
    return undefined unless $scope.hasChanged and $scope.saveElement

    action = {
      position:   1000
      label:      'Save'
      icon:       'glyphicon glyphicon-ok'
      type:       'success'
      action: ->
       $scope.saveElement() if $scope.hasChanged()
    }

    $scope.$watch 'hasChanged()', (changed)->
      action.disabled = not changed

    action
  ]

  actionsProvider.registerActionInRole 'modal-save-and-add-another', actionsProvider.ROLE_MODAL_ACTION, ['$scope', ($scope) ->
    return undefined unless $scope.hasChanged and $scope.saveAndCreateAnother

    action = {
      position:   2000
      label:      'Save and Create Another'
      icon:       'glyphicon glyphicon-ok'
      type:       'success'
      action: ->
        $scope.saveAndCreateAnother() if $scope.hasChanged()
    }

    $scope.$watch 'hasChanged()', (changed)->
      action.disabled = not changed

    action
  ]


  actionsProvider.registerChildAction 'modal-save-element', 'modal-save-element-as-new-version', ['$scope', ($scope) ->
    return undefined unless $scope.hasChanged and $scope.saveElement and not $scope.create and $scope.original and $scope.original.isInstanceOf and $scope.original.isInstanceOf 'org.modelcatalogue.core.PublishedElement'

    action = {
      position:   1000
      label:      'Save as New Version'
      icon:       'glyphicon glyphicon-circle-arrow-up'
      type:       'success'
      action: ->
        $scope.saveElement(true) if $scope.hasChanged()
    }

    $scope.$watch 'hasChanged()', (changed)->
      action.disabled = not changed

    action
  ]

  actionsProvider.registerActionInRole 'filter-by-classification', actionsProvider.ROLE_LIST_ACTION, ['$scope', '$state', '$stateParams', 'messages', 'catalogueElementResource', 'catalogue', ($scope, $state, $stateParams, messages, catalogueElementResource, catalogue) ->
    return undefined unless $scope.list and not $scope.element and catalogue.isInstanceOf($scope.list.itemType, 'publishedElement')

    action = {
      position:   100
      label:      'Filter by Classification'
      icon:       'fa fa-tag'
      type:       'success'
      active:     $stateParams.classification?
      action: ->
        if action.active
          newParams = angular.copy($stateParams)
          newParams.classification = undefined
          $state.go 'mc.resource.list', newParams
        else
          messages.prompt('Filter by Classification', 'Please, select classification you want to filter results by.', {type: 'catalogue-element', resource: 'classification'}).then (classification)->
            return unless classification or angular.isString(classification)
            newParams = angular.copy($stateParams)
            newParams.classification = classification.id
            $state.go 'mc.resource.list', newParams
    }

    if $stateParams.classification
      catalogueElementResource('classification').get($stateParams.classification).then (c)->
        action.label = "Filtered by #{c.name}"

    action
  ]

]