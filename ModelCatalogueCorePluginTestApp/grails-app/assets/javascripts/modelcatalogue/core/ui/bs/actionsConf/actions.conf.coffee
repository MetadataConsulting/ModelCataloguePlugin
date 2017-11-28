angular.module('modelcatalogue.core.ui.bs.actionsConf.actions', ['mc.util.ui.actions']).config (actionsProvider, names, actionRoleRegister, actionClass) ->
  'ngInject'
  Action = actionClass
  showErrorsUsingMessages = (messages) ->
    (response) ->
      if response?.data and response.data.errors
        if angular.isString response.data.errors
          messages.error response.data.errors
        else
          for err in response.data.errors
            messages.error err.message

  actionsProvider.registerActionInRoles 'create-catalogue-element',
    [actionRoleRegister.ROLE_LIST_ACTION, actionRoleRegister.ROLE_LIST_FOOTER_ACTION],
    ($scope, names, security, messages, $state, $log, dataModelService) ->
      'ngInject'
      resource = $scope.resource
      if not resource and $state.current.name == 'dataModel.resource.list'
        resource = $state.params.resource

      return undefined unless security.hasRole('CURATOR')
      return undefined unless resource
      return undefined unless resource != 'batch'
      return undefined unless messages.hasPromptFactory('create-' + resource) or messages.hasPromptFactory('edit-' + resource)

      dataModel = dataModelService.anyParentDataModel($scope)

      return undefined unless not dataModel or dataModel.status == 'DRAFT'

      Action.createStandardAction(
        position: 100
        label: "New #{names.getNaturalName(resource)}"
        icon: 'fa fa-plus-circle'
        type: 'success'
        action: ->
          args = {create: (resource), currentDataModel: dataModelService.anyParentDataModel($scope)}
          args.type = if messages.hasPromptFactory('create-' + resource) then "create-#{resource}" else "edit-#{resource}"

          if (resource == 'model' || resource == 'dataClass') and $scope.element and $scope.elementSelectedInTree
            args.parent = $scope.element

          security.requireRole('CURATOR')
          .then ->
            messages.prompt('Create ' + names.getNaturalName(resource), '', args).then ->
              if (resource == 'model' || resource == 'dataClass') and $state.current.name == 'dataModel.resource.list'
# reload in draft mode
                $state.go '.', {status: 'draft'}, {reload: true}
          , (errors)->
            $log.error errors
            messages.error('You don\'t have rights to create new elements')
      )

  actionsProvider.registerActionInRoles 'favourite-element',
    [actionRoleRegister.ROLE_ITEM_DETAIL_ACTION, actionRoleRegister.ROLE_ITEM_INFINITE_LIST],
    ($scope, messages, $state, security, catalogueElementResource, modelCatalogueApiRoot, enhance, rest, $rootScope) ->
      'ngInject'
      elementPresent = $scope.element and
        angular.isFunction($scope.element.getResourceName) and
        angular.isFunction($scope.element.getElementTypeName) and
        angular.isFunction($scope.element.isInstanceOf) and
        $scope.element.isInstanceOf('catalogueElement')

      return undefined unless elementPresent
      return undefined unless security.getCurrentUser()?.id

      action = Action.createStandardAction(
        position: -20000
        label: 'Favourite'
        icon: 'fa fa-star'
        type: 'primary'
        action: ->
          catalogueElementResource('user').get(security.getCurrentUser()?.id).then (user) ->
            favourite = $scope.element.favourite
            url = "#{modelCatalogueApiRoot}#{user.link}/favourite"
            enhance(
              rest(url: url, method: (if favourite then 'DELETE' else 'POST'), data: $scope.element)
            ).then (relation) ->
              messages.success(if favourite then "#{$scope.element.getLabel()} has been removed from favourites" else
                "#{$scope.element.getLabel()} has been added to favourites")
              $scope.element.favourite = not favourite
              if favourite
                $rootScope.$broadcast 'catalogueElementDeleted', $scope.element, relation, url
              else
                $rootScope.$broadcast 'catalogueElementCreated', relation, url, $scope.element

              relation
      ).withIconOnly()
      .watching ['element.favourite', 'element.id']


      if $scope.element.favourite
        action.active = true
        action.icon = 'fa fa-star-o'
      else
        action.active = false
        action.icon = 'fa fa-star'

      action

  actionsProvider.registerActionInRoles 'favourite-element-in-header', [actionRoleRegister.ROLE_LIST_HEADER_ACTION,
    actionRoleRegister.ROLE_LIST_FOOTER_ACTION], ['$scope', 'messages', '$state', 'security', 'catalogueElementResource',
    'modelCatalogueApiRoot', 'enhance', 'rest',
    ($scope, messages, $state, security, catalogueElementResource, modelCatalogueApiRoot, enhance, rest) ->
      return undefined unless $scope.list?.base?.indexOf('/outgoing/favourite') >= 0
      return undefined unless security.getCurrentUser()?.id

      Action.createStandardAction(
        position: 200
        label: 'Add to Favourites'
        icon: 'fa fa-plus-circle'
        type: 'success'
        action: ->
          messages.prompt("Add to Favourites", "Please, select which element should be added to the favourite ones", {
            type: 'catalogue-element',
            resource: 'catalogueElement'
          }).then (element)->
            catalogueElementResource('user').get(security.getCurrentUser()?.id).then (user) ->
              url = "#{modelCatalogueApiRoot}#{user.link}/favourite"
              enhance(rest(url: url, method: 'POST', data: element)).then (relation) ->
                $scope.$broadcast 'catalogueElementCreated', relation, $scope.list.base, element
                messages.success "#{element.getLabel()} has been added to favourites"
                relation

      )
  ]



  actionsProvider.registerActionInRoles 'create-new-relationship-in-header', [actionRoleRegister.ROLE_LIST_HEADER_ACTION,
    actionRoleRegister.ROLE_LIST_FOOTER_ACTION], ['$scope', 'messages', 'names', 'security', 'catalogue',
    ($scope, messages, names, security, catalogue) ->
      return undefined unless $scope.list?.base
      return undefined unless catalogue.isInstanceOf($scope.list.itemType, 'relationship')
      return undefined unless $scope.$parent?.element
      return undefined unless $scope.$parent.element.status not in ["FINALIZED", "DEPRECATED"]
      return undefined unless security.hasRole('CURATOR')

      direction = if $scope.list.base?.indexOf('/incoming/') > -1 then 'destinationToSource' else 'sourceToDestination'
      relationshipType = $scope.list.base.substring($scope.list.base.lastIndexOf('/') + 1)

      Action.createStandardAction(
        position: 200
        label: 'Add'
        icon: 'fa fa-plus-circle'
        type: 'success'

        action: ->
          messages.prompt('Create Relationship', '', {
            type: 'create-new-relationship',
            currentDataModel: $scope.currentDataModel,
            element: $scope.$parent.element,
            direction: direction,
            relationshipTypeName: relationshipType
          }).catch showErrorsUsingMessages(messages)
      ).watching [ # keep method call on same line as paren or could possibly have syntax problems with multiline list
        (scope) -> scope.$parent.element.status
        (scope) -> scope.$parent.element.archived
      ]
  ]

  actionsProvider.registerActionInRoles 'create-new-mapping-in-header', [actionRoleRegister.ROLE_LIST_HEADER_ACTION,
    actionRoleRegister.ROLE_LIST_FOOTER_ACTION], ['$scope', 'messages', 'names', 'security', 'catalogue',
    ($scope, messages, names, security, catalogue) ->
      return undefined unless $scope.$parent.element?.hasOwnProperty('mappings')
      return undefined unless security.hasRole('CURATOR')
      return undefined unless catalogue.isInstanceOf($scope.list?.itemType, 'mapping')

      Action.createStandardAction(
        position: 300
        label: 'Add'
        icon: 'fa fa-plus-circle'
        type: 'success'
        action: ->
          messages.prompt('Create new mapping for ' + $scope.$parent.element.name, '', {
            type: 'new-mapping',
            element: $scope.$parent.element
          }).catch showErrorsUsingMessages(messages)
      )
  ]

  actionsProvider.registerActionInRole 'transform-csv', actionRoleRegister.ROLE_ITEM_ACTION, ['$scope', 'messages',
    'security', ($scope, messages, security) ->
      return undefined unless $scope.element?.isInstanceOf?('csvTransformation')
      return undefined unless security.isUserLoggedIn()

      Action.createStandardAction(
        position: 0
        label: 'Transform'
        icon: 'fa fa-long-arrow-right'
        type: 'primary'
        action: ->
          messages.prompt('Transform CSV File', '', {type: 'transform-csv-file', element: $scope.element})

      )
  ]

  actionsProvider.registerActionInRole 'refresh-asset', actionRoleRegister.ROLE_ITEM_DETAIL_ACTION, ['$scope',
    '$rootScope', 'catalogueElementResource', ($scope, $rootScope, catalogueElementResource) ->
      return undefined unless $scope.element?.elementType == 'org.modelcatalogue.core.Asset'
      return undefined unless $scope.element.status == 'PENDING'

      Action.createStandardAction(
        position: -100
        label: ''
        icon: 'glyphicon glyphicon-refresh'
        type: 'primary'
        action: ->
          catalogueElementResource($scope.element.elementType).get($scope.element.id).then (refreshed) ->
            $scope.element.updateFrom refreshed
            $rootScope.$broadcast 'redrawContextualActions'
            $rootScope.$broadcast 'catalogueElementUpdated', refreshed
      )
  ]


  actionsProvider.registerActionInRoles 'export', [actionRoleRegister.ROLE_LIST_ACTION, actionRoleRegister.ROLE_ITEM_ACTION,
    actionRoleRegister.ROLE_NAVIGATION_ACTION, actionRoleRegister.ROLE_LIST_HEADER_ACTION], ['$scope', 'security',
    ($scope, security)->
      return undefined unless security.hasRole('CURATOR')
      return undefined unless ($scope.list and
                              not $scope.resource == 'import') or
        ($scope.element and
          angular.isFunction($scope.element.isInstanceOf) and
          not $scope.element.isInstanceOf?('asset'))

      action = Action.createAbstractAction(
        position: 100000
        label: 'Export'
        icon: 'glyphicon glyphicon-download-alt'
        type: 'primary'
      )
      action.expandToLeft = true
      action
  ]

  actionsProvider.registerChildAction 'export', 'export-cart', ['security', '$state', '$window',
    'modelCatalogueApiRoot', (security, $state, $window, modelCatalogueApiRoot) ->
      return undefined unless security.isUserLoggedIn()
      return undefined unless $state.current.name == 'mc.favourites'

      console.log $state.current.name

      Action.createStandardAction(
        position: 100000
        label: 'Export Favourites'
        icon: null
        type: null
        action: ->
          $window.open "#{modelCatalogueApiRoot}/user/#{security.getCurrentUser().id}/outgoing/favourite?format=xml"

      )
  ]

  actionsProvider.registerChildAction 'export', 'edit-XML', ['security', '$state', '$window', 'modelCatalogueApiRoot',
    '$scope', (security, $state, $window, modelCatalogueApiRoot, $scope) ->
      return undefined unless security.hasRole('CURATOR')
      return undefined unless $scope.element?.isInstanceOf?('dataClass')
      Action.createStandardAction(
        position: 100010
        label: 'Edit Xml Schema'
        icon: null
        type: null
        action: ->
          $state.go('dataModel.resource.xml-editor', {resource: 'dataClass', id: $scope.element.id})
      )
  ]

  generateReports = ($scope, $window, enhance, rest, $log, messages, $timeout) ->
    (reports = []) ->
      for report in reports
        Action.createActionFromReport(
          label: report.title
          defaultName: report.defaultName
          depth: report.depth
          includeMetadata: report.includeMetadata
          url: report.url
          type: report.type
          action: ->
            url = @url
            defaultValue = if @defaultName then @defaultName else ''
            depth = @depth
            includeMetadata = @includeMetadata
            if @type == 'LINK'
              $timeout -> $window.open(url, '_blank')
            else if @type == 'ASSET'
              messages.prompt('Export Settings', '', {
                type: 'export',
                assetName: defaultValue,
                depth: depth,
                includeMetadata: includeMetadata
              })
              .then (result) ->
                if (result.assetName?)
                  $log.debug "exporting with name: #{result.assetName}"
                  url = URI(url).setQuery({name: result.assetName})
                if (result.depth?)
                  $log.debug "exporting with depth: #{result.depth}"
                  url = URI(url).setQuery({depth: result.depth})
                if (result.includeMetadata?)
                  $log.debug "exporting with includeMetadata: #{result.includeMetadata}"
                  url = URI(url).setQuery({includeMetadata: result.includeMetadata})
                $log.debug "export new asset using url #{url}"
                $timeout -> $window.open(url, '_blank')
            else
              $log.error "unknown type of report '#{@type}'"
            return true
        ).watching 'element'

  actionsProvider.registerChildAction('export', 'catalogue-element-export-specific-reports',
    ['$scope', '$window', 'enhance', 'rest', '$log', 'messages', '$timeout',
      ($scope, $window, enhance, rest, $log, messages, $timeout) ->
        return undefined unless $scope.element

        Action.createChildWithGenerator(
          position: 1000
          label: "#{$scope.element.name} Reports"
          disabled: not $scope.element?.availableReports?.length
          watches: 'element.availableReports'
          generator: (action) ->
            action.createActionsFrom 'element.availableReports', generateReports($scope, $window, enhance, rest, $log, messages, $timeout)
        )
    ])

  actionsProvider.registerChildAction('export', 'generic-reports',
    ['$scope', '$window', 'enhance', 'rest', '$log', 'messages', '$timeout',
      ($scope, $window, enhance, rest, $log, messages, $timeout) ->
        Action.createChildWithGenerator(
          position: 2000
          label: "Other Reports"
          disabled: not $scope.reports?.length
          watches: 'reports'
          generator: (action) ->
            action.createActionsFrom 'reports', generateReports($scope, $window, enhance, rest, $log, messages, $timeout)
        )
    ])

  actionsProvider.registerChildAction('export', 'list-exports-current',
    ['$scope', '$window', 'enhance', 'rest', '$log', 'messages', '$timeout',
      ($scope, $window, enhance, rest, $log, messages, $timeout) ->
        return undefined unless $scope.list?

        Action.createChildWithGenerator(
          position: 5000
          label: "Current Reports"
          disabled: not $scope.list.availableReports?.length
          watches: 'list.availableReports'
          generator: (action) ->
            action.createActionsFrom 'list.availableReports', generateReports($scope, $window, enhance, rest, $log, messages, $timeout)
        )
    ])


  actionsProvider.registerActionInRole 'modal-cancel', actionRoleRegister.ROLE_MODAL_ACTION, ['$scope', ($scope) ->
    return undefined unless angular.isFunction($scope.$dismiss)

    Action.createStandardAction(
      position: 10000
      label: 'Cancel'
      icon: 'glyphicon glyphicon-ban-circle'
      type: 'warning'
      action: -> $scope.$dismiss()
    )
  ]


  actionsProvider.registerActionInRole 'modal-finalize-data-modal', actionRoleRegister.ROLE_MODAL_ACTION, ['$scope',
    ($scope) ->
      return undefined unless angular.isFunction($scope.finalizeElement)

      Action.createStandardAction(
        position: 1000
        label: 'Finalize'
        icon: 'glyphicon glyphicon-ok'
        type: 'success'
        action: ->
          $scope.finalizeElement()
      )
        .watching 'pending'
        .disabledIf $scope.pending
  ]


  actionsProvider.registerActionInRole 'modal-create-new-version', actionRoleRegister.ROLE_MODAL_ACTION, ['$scope',
    ($scope) ->
      return undefined unless angular.isFunction($scope.createDraftVersion)

      Action.createStandardAction(
        position: 1000
        label: 'Create New Version'
        icon: 'glyphicon glyphicon-ok'
        type: 'success'
        action: ->
          $scope.createDraftVersion()
      )
        .watching 'pending'
        .disabledIf $scope.pending
  ]


  actionsProvider.registerActionInRole 'modal-save-element', actionRoleRegister.ROLE_MODAL_ACTION, ($scope) ->
    'ngInject'

    return undefined unless $scope.hasChanged and $scope.saveElement

    Action.createStandardAction(
      position: 1000
      label: 'Save'
      icon: 'glyphicon glyphicon-ok'
      type: 'success'
      action: ->
        if $scope.hasChanged() and not $scope.saveInProgress
          $scope.saveInProgress = true
          $scope.saveElement()
          .then (result) ->
            return result
          .finally ->
            $scope.saveInProgress = false
    )
      .watching ['hasChanged()', 'saveInProgress']
      .disabledIf not $scope.hasChanged() or $scope.saveInProgress

  actionsProvider.registerActionInRole 'modal-save-and-add-another', actionRoleRegister.ROLE_MODAL_ACTION, ($scope, $q) ->
    'ngInject'

    return undefined unless $scope.hasChanged and $scope.saveAndCreateAnother

    Action.createStandardAction(
      position: 2000
      label: 'Save and Create Another'
      icon: 'glyphicon glyphicon-ok'
      type: 'success'
      action: ->
        if $scope.hasChanged() and not $scope.saveInProgress
          $scope.saveInProgress = true
          $q.when($scope.saveAndCreateAnother())
          .then (result) ->
            $scope.saveInProgress = false
            return result
          .finally ->
            $scope.saveInProgress = false
    )
      .watching ['hasChanged()', 'saveInProgress']
      .disabledIf not $scope.hasChanged() or $scope.saveInProgress

  actionsProvider.registerActionInRole 'expand-all-rows', actionRoleRegister.ROLE_LIST_HEADER_ACTION, ['$scope',
    ($scope) ->
      return undefined unless $scope.rows

      Action.createStandardAction(
        position: -10000
        label: 'Expand All'
        icon: 'fa fa-plus-square-o'
        type: 'primary'
        action: ->
          $scope.$$expandAll = not @active

          @active = not @active
          if @active
            @label = "Collapse All"
            @icon = 'fa fa-minus-square-o'
          else
            @label = 'Expand All'
            @icon = 'fa fa-plus-square-o'
      )
        .activeIf false # what the. This is not even used?
  ]

  actionsProvider.registerActionInRole 'import-data-models-screen', actionRoleRegister.ROLE_DATA_MODELS_ACTION, [
    'security',
    (security) ->
      return undefined unless security.hasRole('CURATOR')
      action = Action.createAbstractAction(
        position: 10000
        label: 'Import'
        icon: 'fa fa-fw fa-upload'
        type: 'primary'
      )
      action.expandToLeft= true
      return action
  ]
