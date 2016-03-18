angular.module('mc.core.ui.bs.actions', ['mc.util.ui.actions']).config ['actionsProvider', 'names', (actionsProvider, names)->
  ROLE_ACTION_ACTION = 'action'


  showErrorsUsingMessages = (messages) ->
    (response) ->
      if response?.data and response.data.errors
        if angular.isString response.data.errors
          messages.error response.data.errors
        else
          for err in response.data.errors
            messages.error err.message

  anyParentDataModel = ($scope) ->
    return $scope.currentDataModel if $scope.currentDataModel
    return anyParentDataModel($scope.$parent) if $scope.$parent
    return undefined

  actionsProvider.registerActionInRoles 'create-catalogue-element', [actionsProvider.ROLE_LIST_ACTION, actionsProvider.ROLE_LIST_FOOTER_ACTION], ['$scope', 'names', 'security', 'messages', '$state', '$log', ($scope, names, security, messages, $state, $log) ->
    resource = $scope.resource
    if not resource and $state.current.name == 'mc.resource.list'
      resource = $state.params.resource

    return undefined if not security.hasRole('CURATOR')
    return undefined if not resource
    return undefined if resource == 'batch'
    return undefined if not messages.hasPromptFactory('create-' + resource) and not messages.hasPromptFactory('edit-' + resource)

    {
    position:   100
    label:      "New #{names.getNaturalName(resource)}"
    icon:       'fa fa-plus-circle'
    type:       'success'
    action:     ->
      args      = {create: (resource), currentDataModel: anyParentDataModel($scope)}
      args.type = if messages.hasPromptFactory('create-' + resource) then "create-#{resource}" else "edit-#{resource}"

      if (resource == 'model' || resource == 'dataClass') and $scope.element and $scope.elementSelectedInTree
        args.parent = $scope.element

      security.requireRole('CURATOR')
      .then ->
        messages.prompt('Create ' + names.getNaturalName(resource), '', args).then ->
          if (resource == 'model' || resource == 'dataClass')and $state.current.name == 'mc.resource.list'
            # reload in draft mode
            $state.go '.', {status: 'draft'}, {reload: true}
      , (errors)->
        $log.error errors
        messages.error('You don\'t have rights to create new elements')
    }
  ]

  actionsProvider.registerActionInRoles 'favorite-element',[actionsProvider.ROLE_ITEM_DETAIL_ACTION, actionsProvider.ROLE_ITEM_INIFINITE_LIST], ['$scope', 'messages', '$state', 'security', 'catalogueElementResource', 'modelCatalogueApiRoot', 'enhance', 'rest', ($scope, messages, $state, security, catalogueElementResource, modelCatalogueApiRoot, enhance, rest) ->
    elementPresent = $scope.element and angular.isFunction($scope.element.getResourceName) and angular.isFunction($scope.element.getElementTypeName) and angular.isFunction($scope.element.isInstanceOf) and $scope.element.isInstanceOf('catalogueElement')

    return undefined if not elementPresent
    return undefined if not security.getCurrentUser()?.id

    action =
      position: -20000
      label: 'Favorite'
      iconOnly: true
      icon: 'fa fa-star'
      type: 'primary'
      watches: ['element.favourite', 'element.id']
      action: ->
        catalogueElementResource('user').get(security.getCurrentUser()?.id).then (user) ->
          favourite = $scope.element.favourite
          url = "#{modelCatalogueApiRoot}#{user.link}/outgoing/favourite"
          enhance(rest(url: url, method: (if favourite then 'DELETE' else 'POST'), data: $scope.element)).then (relation) ->
            messages.success(if favourite then "#{$scope.element.getLabel()} has been removed from favorites" else "#{$scope.element.getLabel()} has been added to favorites")
            $scope.element.favourite = not favourite
            if favourite
              $scope.$broadcast 'catalogueElementDeleted', $scope.element, relation, url
            else
              $scope.$broadcast 'catalogueElementCreated', relation, url, $scope.element

            relation

    if $scope.element.favourite
      action.active = true
      action.icon   = 'fa fa-star-o'
    else
      action.active = false
      action.icon   = 'fa fa-star'

    action
  ]


  actionsProvider.registerActionInRoles 'favorite-element-in-header', [actionsProvider.ROLE_LIST_HEADER_ACTION, actionsProvider.ROLE_LIST_FOOTER_ACTION], ['$scope', 'messages', '$state', 'security', 'catalogueElementResource', 'modelCatalogueApiRoot', 'enhance', 'rest', ($scope, messages, $state, security, catalogueElementResource, modelCatalogueApiRoot, enhance, rest) ->
    return undefined if not $scope.list
    return undefined if not $scope.list.base
    return undefined if $scope.list.base.indexOf('/outgoing/favourite') == -1
    return undefined if not security.getCurrentUser()?.id

    {
      position:   200
      label:      'Add to Favourites'
      icon:       'fa fa-plus-circle'
      type:       'success'
      action: ->
        messages.prompt("Add to Favourites", "Please, select which element should be added to the favourite ones", {type: 'catalogue-element', resource: 'catalogueElement'}).then (element)->
          catalogueElementResource('user').get(security.getCurrentUser()?.id).then (user) ->
            url = "#{modelCatalogueApiRoot}#{user.link}/outgoing/favourite"
            enhance(rest(url: url, method: 'POST', data: element)).then (relation) ->
              $scope.$broadcast 'catalogueElementCreated', relation, url, element
              messages.success "#{element.getLabel()} has been added to favorites"
              relation

    }
  ]

  actionsProvider.registerActionInRoles 'archive-batch',[actionsProvider.ROLE_ITEM_ACTION], ['$rootScope','$scope', 'messages', 'names', 'security', 'enhance', 'rest', 'modelCatalogueApiRoot', ($rootScope, $scope, messages, names, security, enhance, rest, modelCatalogueApiRoot) ->
    return undefined unless $scope.element and angular.isFunction($scope.element.isInstanceOf) and $scope.element.isInstanceOf('batch') or $scope.batch
    return undefined if not security.hasRole('CURATOR')

    {
      position:   150
      label:      'Archive'
      icon:       'glyphicon glyphicon-compressed'
      type:       'danger'
      watches:    ['batch.archived', 'element.archived']
      disabled:   ($scope.batch ? $scope.element).archived
      action:     ->
        batch = $scope.batch ? $scope.element
        messages.confirm("Do you want to archive batch #{batch.name} ?", "The batch #{batch.name} will be archived").then ->
          enhance(rest(url: "#{modelCatalogueApiRoot}#{batch.link}/archive", method: 'POST')).then (archived) ->
            batch.updateFrom archived
          , showErrorsUsingMessages(messages)
    }
  ]


  actionsProvider.registerActionInRoles 'create-new-relationship-in-header', [actionsProvider.ROLE_LIST_HEADER_ACTION, actionsProvider.ROLE_LIST_FOOTER_ACTION], ['$scope', 'messages', 'names', 'security', 'catalogue', ($scope, messages, names, security, catalogue) ->
    return undefined if not $scope.list
    return undefined if not $scope.list.base
    return undefined if not catalogue.isInstanceOf($scope.list.itemType, 'relationship')
    return undefined if not $scope.$parent
    return undefined if not $scope.$parent.element
    return undefined if not security.hasRole('CURATOR')

    direction = if $scope.list.base?.indexOf('/incoming/') > -1 then 'destinationToSource' else 'sourceToDestination'
    relationshipType = $scope.list.base.substring($scope.list.base.lastIndexOf('/') + 1)

    {
      position:   200
      label:      'Add'
      icon:       'fa fa-plus-circle'
      type:       'success'
      disabled:   $scope.$parent.element.archived
      watches:    [
        (scope) -> scope.$parent.element.status
        (scope) -> scope.$parent.element.archived
      ]
      action:     ->
        messages.prompt('Create Relationship', '', {type: 'create-new-relationship', currentDataModel: $scope.currentDataModel, element: $scope.$parent.element, direction: direction, relationshipTypeName: relationshipType}).catch showErrorsUsingMessages(messages)
    }
  ]

  actionsProvider.registerActionInRoles 'create-new-mapping-in-header',  [actionsProvider.ROLE_LIST_HEADER_ACTION, actionsProvider.ROLE_LIST_FOOTER_ACTION], ['$scope', 'messages', 'names', 'security', 'catalogue', ($scope, messages, names, security, catalogue) ->
    return undefined if not $scope.$parent.element
    return undefined if not $scope.$parent.element.hasOwnProperty('mappings')
    return undefined if not security.hasRole('CURATOR')
    return undefined if not $scope.list
    return undefined if not catalogue.isInstanceOf($scope.list.itemType, 'mapping')

    {
      position:   300
      label:      'Add'
      icon:       'fa fa-plus-circle'
      type:       'success'
      action:     ->
        messages.prompt('Create new mapping for ' + $scope.$parent.element.name, '', {type: 'new-mapping', element: $scope.$parent.element}).catch showErrorsUsingMessages(messages)
    }
  ]

  actionsProvider.registerActionInRoles 'transform-csv',[actionsProvider.ROLE_ITEM_ACTION], [ '$scope', 'messages', 'security', ($scope, messages, security) ->
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

  actionsProvider.registerActionInRoles 'refresh-asset',[actionsProvider.ROLE_ITEM_DETAIL_ACTION], [ '$scope', '$rootScope', 'catalogueElementResource', ($scope, $rootScope, catalogueElementResource) ->
    return undefined if $scope.element?.elementType != 'org.modelcatalogue.core.Asset'
    return undefined if $scope.element.status != 'PENDING'

    {
      position:   -100
      label:      ''
      icon:       'glyphicon glyphicon-refresh'
      type:       'primary'
      action:     ->
        catalogueElementResource($scope.element.elementType).get($scope.element.id).then (refreshed) ->
          $scope.element.updateFrom refreshed
          $rootScope.$broadcast 'redrawContextualActions'
          $rootScope.$broadcast 'catalogueElementUpdated', refreshed

    }
  ]

  actionsProvider.registerActionInRole 'generate-merge-models', actionsProvider.ROLE_LIST_ACTION, ['$scope', 'security', 'catalogue', 'modelCatalogueApiRoot', 'enhance', 'rest', 'messages', '$state', ($scope, security, catalogue, modelCatalogueApiRoot, enhance, rest, messages, $state)->
    return undefined unless security.isUserLoggedIn()
    return undefined unless $scope.list
    return undefined unless catalogue.isInstanceOf($scope.list.itemType, 'batch')
    {
      position:   100
      label:      'Generate Suggestions'
      icon:       'fa fa-flash'
      type:       'primary'
      action: ->
        messages.prompt('Generate Suggestions', "Suggestions to optimalize catalogue will be generated. This may take a long time depending on complexity of the catalogue. You can rerun the action later to clean all resolved batches generated by this action.", {type: 'generate-suggestions'}).then ->
          $state.go('.', {page: undefined}, {reload: true})

    }
  ]

  actionsProvider.registerActionInRole 'refresh-batches', actionsProvider.ROLE_LIST_ACTION, ['$state', '$scope', 'security', 'catalogue', ($state, $scope, security, catalogue)->
    return undefined unless security.isUserLoggedIn()
    return undefined unless $scope.list
    return undefined unless catalogue.isInstanceOf($scope.list.itemType, 'batch')
    {
      position:   0
      label:      'Refresh'
      icon:       'fa fa-refresh'
      type:       'primary'
      action: ->
        $state.go('.', {page: undefined}, {reload: true})
    }
  ]

  actionsProvider.registerActionInRoles 'export', [actionsProvider.ROLE_LIST_ACTION, actionsProvider.ROLE_ITEM_ACTION, actionsProvider.ROLE_NAVIGATION, actionsProvider.ROLE_LIST_HEADER_ACTION], ['$scope', 'security', ($scope, security)->
    return undefined unless security.hasRole('VIEWER')
    return undefined unless $scope.list or $scope.element
    if $scope.list
      return undefined if $scope.resource == 'import'
    if $scope.element
      return undefined if not angular.isFunction $scope.element.isInstanceOf
      return undefined if $scope.element.isInstanceOf('asset')
    {
      position:   100000
      label:      'Export'
      icon:       'glyphicon glyphicon-download-alt'
      type:       'primary'
      expandToLeft: true
    }
  ]

  actionsProvider.registerChildAction 'export', 'export-cart', ['security', '$state', '$window', 'modelCatalogueApiRoot', (security, $state, $window, modelCatalogueApiRoot) ->
    return undefined if not security.isUserLoggedIn()
    return undefined if not $state.current.name == 'mc.favorites'

    {
    position:   100000
    label:      'Export Favorites'
    action: ->
      $window.open "#{modelCatalogueApiRoot}/user/#{security.getCurrentUser().id}/outgoing/favourite?format=xml"

    }
  ]

  generateReports = ($scope, $window, enhance, rest, $log, messages) ->
    (reports = []) ->
      for report in reports
        {
          label:  report.title
          defaultName: report.defaultName
          depth: report.depth
          url:    report.url
          type:   report.type
          watches: 'element'
          action: ->
            url = @url
            defaultValue = if @defaultName then @defaultName else ''
            depth = if @depth then @depth else false
            if @type == 'LINK'
              $window.open(url, '_blank')
            else if @type == 'ASSET'
              messages.prompt('Asset Name', 'Asset Name', {type: 'export', value: defaultValue, depth: depth})
              .then (result) ->
                $log.debug result.depth
                if (result.assetName)
                  url = URI(url).setQuery({name: result.assetName})
                if (result.depth)
                  url = URI(url).setQuery({depth: result.depth})
                $log.debug "export new asset with asset name '#{result.assetName}' using url #{url}"
                $window.open(url, '_blank')
            else
              $log.error "unknown type of report '#{@type}'"
            return true
        }

  actionsProvider.registerChildActionInRoles('export', 'catalogue-element-export-specific-reports', [actionsProvider.ROLE_ITEM_ACTION],
    ['$scope', '$window', 'enhance', 'rest', '$log', 'messages', ($scope, $window, enhance, rest, $log, messages) ->
      return undefined if not $scope.element

      {
      position:   1000
      label:      "#{$scope.element.name} Reports"
      disabled:   not $scope.element?.availableReports?.length
      watches:    'element.availableReports'
      generator:  (action) ->
        action.createActionsFrom 'element.availableReports', generateReports($scope, $window, enhance, rest, $log, messages)
      }
    ])

  actionsProvider.registerChildAction('export', 'generic-reports',
    ['$scope', '$window', 'enhance', 'rest', '$log', 'messages', ($scope, $window, enhance, rest, $log, messages) ->
      {
      position:   2000
      label:      "Other Reports"
      disabled:   not $scope.reports?.length
      watches:   'reports'
      generator: (action) ->
        action.createActionsFrom 'reports', generateReports($scope, $window, enhance, rest, $log, messages)
      }
    ])

  actionsProvider.registerChildAction('export', 'list-exports-current', actionsProvider.ROLE_LIST_ACTION,
    ['$scope', '$window', 'enhance', 'rest', '$log', 'messages', ($scope, $window, enhance, rest, $log, messages) ->
      return undefined if not $scope.list?

      {
      position:   5000
      label:      "Current Reports"
      disabled:   not $scope.list.availableReports?.length
      watches:    'list.availableReports'
      generator:  (action) ->
        action.createActionsFrom 'list.availableReports', generateReports($scope, $window, enhance, rest, $log, messages)
      }
    ])

  actionsProvider.registerActionInRole 'switch-archived-batches', actionsProvider.ROLE_LIST_ACTION, ['$state', '$scope', '$stateParams', ($state, $scope, $stateParams) ->
    return undefined unless $state.current.name == 'mc.resource.list' and $scope.list and $stateParams.resource == 'batch'

    {
    abstract: true
    position: 500

    type:     (->
      return 'info'        if $stateParams.status == 'archived'
      return 'glyphicon glyphicon-ok'
    )()
    label:    (->
      return 'Archived'    if $stateParams.status == 'archived'
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
          $scope.batch.$$reload()  if angular.isFunction($scope.batch?.$$reload)

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
          $scope.batch.$$reload()  if angular.isFunction($scope.batch?.$$reload)
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
          $scope.batch.$$reload()  if angular.isFunction($scope.batch?.$$reload)
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
          $scope.batch.$$reload()  if angular.isFunction($scope.batch?.$$reload)
    }
  ]


  actionsProvider.registerActionInRoles 'reload-actions', [ROLE_ACTION_ACTION, actionsProvider.ROLE_ITEM_ACTION, actionsProvider.ROLE_NAVIGATION], ['$scope', ($scope) ->
    return undefined unless angular.isFunction($scope.batch?.$$reload) and ($scope.action and $scope.action.state == 'PERFORMING') or ($scope.batch and not $scope.action)

    {
      position: 900
      type:     'success'
      icon:     'glyphicon glyphicon-refresh'
      label:    'Reload'
      action:   ->
        if $scope.batch?.$$reload
          $scope.batch?.$$reload()
          return
          $scope.reload() if angular.isFunction($scope.reload)
          $scope.batch.$$reload()  if angular.isFunction($scope.batch?.$$reload)
    }
  ]

  actionsProvider.registerActionInRole 'link-actions', ROLE_ACTION_ACTION, ['$scope', '$rootScope', 'messages',($scope, $rootScope, messages) ->
    return undefined unless $scope.action and not ($scope.action.state == 'PERFORMING' or $scope.action.state == 'PERFORMED')

    action = {
      position: 950
      type:     'primary'
      icon:     'glyphicon glyphicon-open'
      label:    'Add or Remove Dependency'
      watches:   -> $rootScope.selectedAction
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
                  $scope.batch.$$reload()  if angular.isFunction($scope.batch?.$$reload)
            else if @mode == 'remove'
              messages.confirm('Remove Dependency', 'Do you really want to remove dependency between these two actions? This may cause problems executing given action!').then ->
                selected.removeDependency(selected.dependsOn['' + $scope.action.id]).then ->
                  $scope.reload() if angular.isFunction($scope.reload)
                  $scope.batch.$$reload()  if angular.isFunction($scope.batch?.$$reload)
            $rootScope.selectedAction = undefined


    }

    if $rootScope.selectedAction
      if $rootScope.selectedAction == $scope.action
        action.active = true
        action.icon = 'glyphicon glyphicon-open'
        action.label = 'Add or Remove Dependency'
        action.mode = 'select'
      else
        action.active = false
        if $rootScope.selectedAction.dependsOn.hasOwnProperty('' + $scope.action.id)
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


  actionsProvider.registerActionInRoles 'run-all-actions-in-batch',[actionsProvider.ROLE_ITEM_ACTION], ['$scope', 'messages', 'modelCatalogueApiRoot', 'enhance', 'rest', '$timeout', 'security', ($scope, messages, modelCatalogueApiRoot, enhance, rest, $timeout, security) ->
    return undefined if not security.hasRole('CURATOR')
    return undefined unless $scope.element and angular.isFunction($scope.element.isInstanceOf) and $scope.element.isInstanceOf('batch') or $scope.batch

    action = {
      position: 200
      type:     'success'
      icon:     'glyphicon glyphicon-flash'
      label:    'Run All Pending'
      watches:  ['batch', 'element']
      action:   ->
        batch = $scope.batch ? $scope.element
        messages.confirm('Run All Actions', "Do you really wan to run all actions from '#{batch.name}' batch").then ->
          enhance(rest(method: 'POST', url: "#{modelCatalogueApiRoot}#{batch.link}/run")).then (updated) ->
            batch.updateFrom(updated)
          $timeout($scope.reload, 1000) if angular.isFunction($scope.reload)
    }

    updateDisabled = (batch) ->
      return unless batch
      action.disabled = not batch.pending.total

    updateDisabled($scope.batch ? $scope.element)

    action

  ]



  actionsProvider.registerActionInRole 'update-action-parameters', ROLE_ACTION_ACTION, ['$scope', 'messages', 'names', 'security', ($scope, messages, names, security) ->
    return undefined if not $scope.action
    return undefined if $scope.action.state in ['PERFORMING', 'PERFORMED']
    return undefined if not security.hasRole('CURATOR')

    {
      position: 100
      label: 'Update Action Parameters'
      icon: 'glyphicon glyphicon-edit'
      type: 'primary'
      watches: 'action.state'
      disabled: $scope.action.state in ['PERFORMING', 'PERFORMED']
      action: ->
        messages.prompt('Update Action Parameters', '',
          {type: 'update-action-parameters', action: $scope.action}).then (updated)->
            $scope.action = updated
    }

  ]

  actionsProvider.registerActionInRole 'modal-cancel', actionsProvider.ROLE_MODAL_ACTION, ['$scope', ($scope) ->
    return undefined if not angular.isFunction($scope.$dismiss)

    {
      position:   10000
      label:      'Cancel'
      icon:       'glyphicon glyphicon-ban-circle'
      type:       'warning'
      action: -> $scope.$dismiss()
    }
  ]


  actionsProvider.registerActionInRole 'modal-finalize-data-modal', actionsProvider.ROLE_MODAL_ACTION, ['$scope', ($scope) ->
    return undefined unless angular.isFunction($scope.finalizeElement)

    {
      position:   1000
      label:      'Finalize'
      icon:       'glyphicon glyphicon-ok'
      type:       'success'
      watches:    'pending'
      disabled:   $scope.pending
      action: ->
       $scope.finalizeElement()
    }
  ]


  actionsProvider.registerActionInRole 'modal-create-new-version', actionsProvider.ROLE_MODAL_ACTION, ['$scope', ($scope) ->
    return undefined unless angular.isFunction($scope.createDraftVersion)

    {
      position:   1000
      label:      'Create New Version'
      icon:       'glyphicon glyphicon-ok'
      type:       'success'
      watches:    'pending'
      disabled:   $scope.pending
      action: ->
       $scope.createDraftVersion()
    }
  ]


  actionsProvider.registerActionInRole 'modal-save-element', actionsProvider.ROLE_MODAL_ACTION, ['$scope', ($scope) ->
    return undefined unless $scope.hasChanged and $scope.saveElement

    {
      position:   1000
      label:      'Save'
      icon:       'glyphicon glyphicon-ok'
      type:       'success'
      watches:    'hasChanged()'
      disabled:   not $scope.hasChanged()
      action: ->
       $scope.saveElement() if $scope.hasChanged()
    }
  ]

  actionsProvider.registerActionInRole 'modal-save-and-add-another', actionsProvider.ROLE_MODAL_ACTION, ['$scope', ($scope) ->
    return undefined unless $scope.hasChanged and $scope.saveAndCreateAnother

    {
      position:   2000
      label:      'Save and Create Another'
      icon:       'glyphicon glyphicon-ok'
      type:       'success'
      watches:    'hasChanged()'
      disabled:   not $scope.hasChanged()
      action: ->
        $scope.saveAndCreateAnother() if $scope.hasChanged()
    }
  ]


  actionsProvider.registerChildAction 'modal-save-element', 'modal-save-element-as-new-version', ['$scope', ($scope) ->
    return undefined unless $scope.hasChanged and $scope.saveElement and not $scope.create and $scope.original and $scope.original.isInstanceOf and $scope.original.isInstanceOf 'catalogueElement'

    {
      position:   1000
      label:      'Save as New Version'
      icon:       'glyphicon glyphicon-circle-arrow-up'
      type:       'success'
      watches:    'hasChanged()'
      disabled:   not $scope.hasChanged()
      action: ->
        $scope.saveElement(true) if $scope.hasChanged()
    }
  ]

  actionsProvider.registerActionInRole 'expand-all-rows', actionsProvider.ROLE_LIST_HEADER_ACTION, ['$scope', ($scope) ->
    return undefined unless $scope.rows

    {
      position:   -10000
      label:      'Expand All'
      icon:       'fa fa-plus-square-o'
      type:       'primary'
      active:     false
      action: ->
        $scope.$$expandAll = not @active

        @active = not @active
        if @active
          @label = "Collapse All"
          @icon  = 'fa fa-minus-square-o'
        else
          @label = 'Expand All'
          @icon  = 'fa fa-plus-square-o'

    }
  ]

  actionsProvider.registerActionInRole 'import-data-models-screen', 'data-models', [ ->

    {
      position:   10000
      label:      'Import'
      icon:       'fa fa-fw fa-upload'
      type:       'primary'
      abstract:     true
      expandToLeft: true
    }
  ]

]
