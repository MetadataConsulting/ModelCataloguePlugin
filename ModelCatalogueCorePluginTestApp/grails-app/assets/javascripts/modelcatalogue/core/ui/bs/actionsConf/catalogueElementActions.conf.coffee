angular.module('modelcatalogue.core.ui.bs.actionsConf.catalogueElementActions', ['mc.util.ui.actions']).config (actionsProvider, names, actionRoleRegister, actionClass) ->
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

  getIsSourceFinalized = (relationship) ->
    unless relationship
      return false
    if relationship.direction == "sourceToDestination"
      return relationship.element.status is 'FINALIZED'
    return relationship.relation.status is 'FINALIZED'

  ##############################
  # Common - Catalogue Element #
  ##############################

  # Deprecate / Restore
  actionsProvider.registerChildAction 'catalogue-element', 'archive',
    ($rootScope, $scope, messages, names, security, enhance, rest, modelCatalogueApiRoot) ->
      'ngInject'
      return undefined unless $scope.element?.status
      return undefined unless security.hasRole('CURATOR')

      action = Action.createStandardAction(
        position: -1800
        label: null
        icon: null
        type: null
        action: ->
          if $scope.element.archived
            if security.hasRole('ADMIN')
              messages.confirm("Do you want to restore #{$scope.element.getElementTypeName()} #{$scope.element.name} as finalized?",
                "The #{$scope.element.getElementTypeName()} #{$scope.element.name} will no longer be deprecated").then ->
                  enhance(rest(url: "#{modelCatalogueApiRoot}#{$scope.element.link}/restore", method: 'POST')).then (restored) ->
                    $scope.element.updateFrom restored
                    $rootScope.$broadcast 'catalogueElementUpdated', restored
                    $rootScope.$broadcast 'redrawContextualActions'
                    restored.focus()
                  , showErrorsUsingMessages(messages)
          else
            messages.confirm("Do you want to mark #{$scope.element.getElementTypeName()} #{$scope.element.name} as deprecated?",
              "The #{$scope.element.getElementTypeName()} #{$scope.element.name} will be marked as deprecated").then ->
                enhance(rest(url: "#{modelCatalogueApiRoot}#{$scope.element.link}/archive", method: 'POST')).then (archived) ->
                  $scope.element.updateFrom archived
                  $rootScope.$broadcast 'catalogueElementUpdated', archived
                  $rootScope.$broadcast 'redrawContextualActions'
                  archived.focus()
                , showErrorsUsingMessages(messages)
      )
        .watching ['element.status', 'element.archived']
        .disabledIf $scope.element.archived and not security.hasRole('ADMIN')



      if $scope.element.archived
          action.label = 'Restore'
          action.icon = 'fa fa-fw fa-repeat'
          action.type = 'primary'
      else
        action.label = 'Deprecate'
        action.icon = 'fa fa-fw fa-ban'
        action.type = 'danger'

      action

  # Delete
  actionsProvider.registerChildAction 'catalogue-element', 'delete',
    ($rootScope, $scope, $state, messages, names, security) ->
      'ngInject'
      return undefined unless $scope.element
      return undefined unless angular.isFunction($scope.element.delete)
      return undefined unless angular.isFunction($scope.element.isInstanceOf)
      return undefined unless security.hasRole('CURATOR')
      return undefined unless ($scope.element.isInstanceOf('asset') or $scope.element.isInstanceOf('dataClass') or
        $scope.element.isInstanceOf('dataElement') or $scope.element.isInstanceOf('dataModel') or
        $scope.element.isInstanceOf('dataType') or $scope.element.isInstanceOf('measurementUnit') or
        $scope.element.isInstanceOf('validationRule'))

      Action.createStandardAction(
        position: -1500
        label: 'Delete'
        icon: 'fa fa-fw fa-times-circle'
        type: 'danger'
        action: ->
          messages.confirm("Do you really want to delete #{$scope.element.getElementTypeName()} #{$scope.element.name}?",
            "The #{$scope.element.getElementTypeName()} #{$scope.element.name} will be deleted permanently. " +
              (if not security.hasRole('SUPERVISOR') then "This action cannot be undone. Be ware that only DRAFT can be deleted." else "This action cannot be undone."))
            .then ->
              $scope.element.delete()
              .then ->
                messages.success "#{$scope.element.getElementTypeName()} #{$scope.element.name} deleted."
                $rootScope.$broadcast 'catalogueElementDeleted', $scope.element
                resource = names.getPropertyNameFromType($scope.element.elementType)
                if resource == 'dataModel'
                  $state.go('dataModels')
                else if $state.current.name.indexOf('dataModel.resource.show') >= 0
                  $state.go('dataModel.resource.list', {resource: resource}, {reload: true})
              .catch showErrorsUsingMessages(messages)
      )
        .disabledIf $scope.element.status != 'DRAFT' and not security.hasRole('SUPERVISOR')


  # Merge
  actionsProvider.registerChildAction 'catalogue-element', 'merge',
    ($rootScope, $scope, messages, names, security, enhance, rest, modelCatalogueApiRoot) ->
      'ngInject'
      return undefined unless $scope.element?.status
      return undefined unless security.hasRole('CURATOR')
      Action.createStandardAction(
        position: 10000
        label: 'Merge'
        icon: 'fa fa-fw fa-code-fork fa-rotate-180 fa-flip-vertical'
        type: 'danger'
        action: ->
          messages.prompt("Merge #{$scope.element.getElementTypeName()} #{$scope.element.name} to another #{$scope.element.getElementTypeName()}",
            "All non-system relationships of the #{$scope.element.getElementTypeName()} #{$scope.element.name} will be moved to the following destination and " +
              "than the #{$scope.element.getElementTypeName()} #{$scope.element.name} will be archived",
            {type: 'catalogue-element', resource: $scope.element.elementType, status: 'draft'})
            .then (destination)->
              enhance(rest(url: "#{modelCatalogueApiRoot}#{$scope.element.link}/merge/#{destination.id}", method: 'POST')).then (merged) ->
                oldName = $scope.element.classifiedName
                messages.success "Element #{oldName} merged successfully into  #{$scope.element.classifiedName}"
                merged.show()
                $rootScope.$broadcast 'redrawContextualActions'
              , showErrorsUsingMessages(messages)
      )
        .watching ['element.status', 'element.archived']
        .disabledIf $scope.element.status != 'DRAFT'


  ##############
  # Data Model #
  ##############

  # New Version

  newVersionAction = ($rootScope, $scope, messages, security) ->
    'ngInject'
    return undefined unless $scope.element?.status
    return undefined unless $scope.element?.isInstanceOf?('dataModel')
    return undefined unless security.hasRole('CURATOR')
    Action.createStandardAction(
      position: -1900
      label: 'New Version'
      icon: 'fa fa-fw fa-arrow-circle-up'
      type: 'primary'
      action: ->
        messages.prompt(null, null, type: 'new-version', element: $scope.element)
    )
      .watching ['element.status', 'element.archived']
      .disabledIf $scope.element.archived || $scope.element.status == 'DRAFT' || $scope.element.status == 'PENDING'
  actionsProvider.registerChildAction 'catalogue-element', 'create-new-version', newVersionAction
  actionsProvider.registerActionInRoles 'create-new-version-tiny', [actionRoleRegister.ROLE_ITEM_DETAIL_ACTION, actionRoleRegister.ROLE_ITEM_INFINITE_LIST], newVersionAction




  actionsProvider.registerActionInRole 'catalogue-element',actionRoleRegister.ROLE_ITEM_ACTION, ['$scope', 'security', 'names', 'catalogue', ($scope, security, name, catalogue)->
    return undefined unless $scope.element
    return undefined unless angular.isFunction $scope.element.isInstanceOf

    Action.createAbstractAction(
      position: 0
      label: names.getNaturalName(names.getPropertyNameFromQualifier($scope.element.elementType))
      icon: catalogue.getIcon($scope.element.elementType)
      type: 'primary'
    )
      .watching ['element.elementType', 'element.status']
  ]

  actionsProvider.registerActionInRole 'edit-catalogue-element',actionRoleRegister.ROLE_ITEM_DETAIL_ACTION, ['$scope', 'messages', 'names', 'security', ($scope, messages, names, security) ->
    return undefined unless $scope.element
    return undefined unless angular.isFunction $scope.element.isInstanceOf
    return undefined unless angular.isFunction $scope.element.getResourceName
    return undefined unless messages.hasPromptFactory('create-' + $scope.element.getResourceName()) or messages.hasPromptFactory('edit-' + $scope.element.getResourceName())
    return undefined unless security.hasRole('CURATOR')
    return undefined unless not $scope.supportsInlineEdit?($scope.editableForm)
    return undefined unless (not $scope.element.isInstanceOf('dataModel') or $scope.element.status == 'DRAFT') # a bit weird. You can edit if it's not a draft but it's not a data model?

    Action.createStandardAction(
      position: -1000
      label: 'Edit'
      icon: 'fa fa-edit'
      type: 'primary'
      action: ->
        messages.prompt('Edit ' + $scope.element.getElementTypeName(), '', {type: 'edit-' + names.getPropertyNameFromType($scope.element.elementType), element: $scope.element}).then (updated)->
          $scope.element.updateFrom updated
    )
      .disabledIf $scope.element.archived or $scope.element?.status == 'FINALIZED'
      .watching ['element.status', 'element.archived']

  ]

  actionsProvider.registerActionInRole 'update-user',actionRoleRegister.ROLE_ITEM_DETAIL_ACTION, ($scope, messages, names, security, $state) ->
    'ngInject'

    return undefined unless security.hasRole('ADMIN')
    return undefined unless $scope.element?.isInstanceOf?('user')

    label = if $scope.element.enabled then "Disable User" else "Enable User"

    Action.createStandardAction(
      position: -2000
      label: label
      icon: if $scope.element.enabled then 'fa fa-ban' else 'fa fa-check'
      type: 'primary'
      action: ->
        title = label
        desc = if $scope.element.enabled then "Do you want to disable user?" else "Do you want to enable user?"
        messages.confirm(title, desc).then ->
          if $scope.element.enabled
            return $scope.element.execute('disable', 'POST').then (user) ->
              $scope.element.updateFrom(user)
              # for some reason the app does not refresh properly
              $state.go '.', {}, {reload: true}
            , showErrorsUsingMessages(messages)

          return $scope.element.execute('enable', 'POST').then (user) ->
            $scope.element.updateFrom(user)
            # for some reason the app does not refresh properly
            $state.go '.', {}, {reload: true}
          , showErrorsUsingMessages(messages)
    )
      .watching ['element.enabled']

  actionsProvider.registerActionInRole 'update-role',actionRoleRegister.ROLE_ITEM_DETAIL_ACTION, ($scope, messages, names, security, $rootScope) ->
    'ngInject'

    return undefined unless security.hasRole('ADMIN')
    return undefined unless $scope.element?.isInstanceOf?('user')
    Action.createStandardAction(
      position: -500
      label: "Change Role"
      icon: "fa fa-users"
      type: 'primary'
      action: ->
        options = [
          {classes: 'btn btn-primary', icon: 'fa fa-cog', label: 'Admin', value: 'admin'}
          {classes: 'btn btn-primary', icon: 'fa fa-object-group', label: 'Curator', value: 'curator'}
          {classes: 'btn btn-primary', icon: 'fa fa-user', label: 'Viewer', value: 'viewer'}
          {classes: 'btn btn-primary', icon: 'fa fa-user', label: 'Guest', value: 'guest'}
        ]

        messages.prompt("Select Role", "Select new role for user \"#{$scope.element.username}\"", type: 'options', options: options).then (role) ->
          $scope.element.execute("role/#{role}", "POST").then (user) ->
# for some reason the app does not refresh properly
            $state.go '.', {}, {reload: true}
    )
      .disabledIf $scope.element.username in ['admin', 'supervisor']

  actionsProvider.registerActionInRole 'show-link',actionRoleRegister.ROLE_ITEM_DETAIL_ACTION, ($scope, messages, security) ->
    'ngInject'

    return undefined unless $scope.element?.internalModelCatalogueId
    Action.createStandardAction(
      position: 10000
      label: "Show Link"
      icon: "fa fa-link"
      type: 'default'
      action: ->
        messages.prompt("Link for " + $scope.element.getLabel(), """
          <h4>Permanent Link</h4>
          <input type='text' class='form-control' value='#{$scope.element.internalModelCatalogueId}' readonly='readonly' select-on-click></input>
          <h4>cURL</h4>
          <input type='text' class='form-control' value='curl -L -u #{security.getCurrentUser()?.username ? 'username'}:&lt;API Key&gt; #{$scope.element.internalModelCatalogueId}/export' readonly='readonly' select-on-click></input>
          <p class='help-block small'>You can view your API key using the action in user top right menu <span class='fa fa-fw fa-user'></span></p>
        """, type: 'alert', size: 'lg')
    )

  actionsProvider.registerActionInRole 'inline-edit',actionRoleRegister.ROLE_ITEM_DETAIL_ACTION, ($scope, security) ->
    'ngInject'
    return undefined unless $scope.editableForm
    return undefined unless not $scope.editableForm.$visible #so the editableForm has to be there, but not visible.
    return undefined unless not angular.isFunction($scope.supportsInlineEdit) or $scope.supportsInlineEdit($scope.editableForm) # so it's fine if supportsInlineEdit is not a function...
    Action.createStandardAction(
      position: -1000
      label: 'Inline Edit'
      icon: 'fa fa-edit'
      type: 'primary'
      action: ->
        $scope.editableForm.$show()
        $scope.$broadcast 'redrawContextualActions'
    )
      .watching ['element.status', 'element.archived']
      .disabledIf not security.hasRole('SUPERVISOR') and ($scope.element.archived or $scope.element?.status == 'FINALIZED')

  actionsProvider.registerActionInRole 'inline-edit-submit', actionRoleRegister.ROLE_ITEM_DETAIL_ACTION, ['$scope', 'messages', 'names', 'security', ($scope) ->
    return undefined unless $scope.editableForm?.$visible
    return undefined unless not angular.isFunction($scope.supportsInlineEdit) or $scope.supportsInlineEdit($scope.editableForm)
    action = Action.createStandardAction(
      position: 2000
      label: 'Save'
      icon: 'fa fa-check'
      type: 'success'
      action: ->
        $scope.$broadcast 'redrawContextualActions'
    )
    action.submit = true
    return action
  ]

  actionsProvider.registerActionInRole 'inline-edit-cancel', actionRoleRegister.ROLE_ITEM_DETAIL_ACTION, ['$scope', 'messages', 'names', 'security', ($scope) ->
    return undefined unless $scope.editableForm?.$visible
    return undefined unless not angular.isFunction($scope.supportsInlineEdit) or $scope.supportsInlineEdit($scope.editableForm)
    Action.createStandardAction(
      position: 3000
      label: 'Cancel'
      icon: 'fa fa-ban'
      type: 'warning'
      action: ->
        $scope.editableForm.$cancel()
        $scope.$broadcast 'redrawContextualActions'
    )
  ]

  actionsProvider.registerChildAction 'catalogue-element', 'create-new-relationship', ['$scope', 'messages', 'names', 'security', ($scope, messages, names, security) ->
    return undefined unless $scope.element?.isInstanceOf?('catalogueElement')
    return undefined unless security.hasRole('CURATOR')
    Action.createStandardAction(
      position: 200
      label: 'Create Relationship'
      icon: 'fa fa-fw fa-chain'
      type: 'success'
      action: ->
        messages.prompt('Create Relationship', '', {type: 'create-new-relationship', element: $scope.element, currentDataModel: $scope.currentDataModel}).catch showErrorsUsingMessages(messages)
    )
      .watching ['element.status', 'element.archived']
      .disabledIf $scope.element.archived

  ]
  ###
  # remove compare action until properly implemented
  actionsProvider.registerChildAction 'catalogue-element', 'compare-catalogue-element', ['$scope', 'messages', '$state', ($scope, messages, $state) ->
    elementPresent = $scope.element and angular.isFunction($scope.element.getResourceName) and angular.isFunction($scope.element.getElementTypeName) and angular.isFunction($scope.element.isInstanceOf) and $scope.element.isInstanceOf('catalogueElement')
    diffView = $state.current.name == 'dataModel.resource.diff'

    return undefined unless elementPresent and not diffView

    element = if elementPresent then $scope.element else $scope.elements[0]
    ids = if elementPresent then [element.id] else (e.id for e in $scope.elements)

    Action.createStandardAction(
      position: -500
      label: if elementPresent then 'Compare' else 'Compare Another'
      icon: 'fa fa-fw fa-arrows-h'
      type: 'primary'
      action: ->
        messages.prompt('Compare ' + element.getElementTypeName(), "Select the #{element.getElementTypeName()} for the comparison",
          {type: 'catalogue-element', resource: element.getResourceName(), global: true}).then (toBeCompared)->
            $state.go 'dataModel.resource.diff', dataModelId: element.dataModel.id, resource: element.getResourceName(), ids: ids.concat([toBeCompared.id]).join('|')
    )
  ]
  ###


  actionsProvider.registerChildAction 'catalogue-element', 'create-new-mapping', ['$scope', 'messages', 'names', 'security', ($scope, messages, names, security) ->
    return undefined unless $scope.element?.hasOwnProperty('mappings')
    return undefined unless security.hasRole('CURATOR')
    Action.createStandardAction(
      position: 300
      label: 'Create Mapping'
      icon: 'fa fa-fw fa-superscript'
      type: 'success'
      action: ->
        messages.prompt('Create new mapping for ' + $scope.element.name, '', {type: 'new-mapping', element: $scope.element}).catch showErrorsUsingMessages(messages)
    )
  ]


  actionsProvider.registerChildAction 'catalogue-element', 'validate-value', [ '$scope', 'messages', 'security', ($scope, messages) ->
    return undefined unless $scope.element?.isInstanceOf?('dataType')
    Action.createStandardAction(
      position: 1200
      label: 'Validate Value'
      icon: 'fa fa-fw fa-check-circle-o'
      type: 'primary'
      action: ->
        messages.prompt('', '', {type: 'validate-value-by-domain', domain: $scope.element})
    )
      .watching ['element.rule', 'element.dataType']
      .disabledIf not $scope.element.rule and
        $scope.element.basedOn?.length == 0 and
        not (($scope.element.dataType and
          $scope.element.dataType.isInstanceOf('enumeratedType')) or
          $scope.element.isInstanceOf('enumeratedType'))
  ]


  actionsProvider.registerChildAction 'catalogue-element', 'convert', [ '$scope', 'messages', 'security', ($scope, messages) ->
    return undefined unless $scope.element?.isInstanceOf?('dataType') or
      $scope.element?.isInstanceOf?('mapping')
    Action.createStandardAction(
      position: 1100
      label: 'Convert Value'
      icon: 'fa fa-fw fa-long-arrow-right'
      type: 'primary'
      action: ->
        if $scope.element.isInstanceOf('dataType')
          messages.prompt('', '', {type: 'convert-with-value-domain', source: $scope.element})
        else if $scope.element.isInstanceOf('mapping')
          messages.prompt('', '', {type: 'convert-with-value-domain', source: $scope.element.source, destination: $scope.element.destination})
    )
  ]

  actionsProvider.registerChildAction 'catalogue-element', 'validate-xsd-schema', [ '$scope', 'messages', 'catalogue', ($scope, messages, catalogue) ->
    return undefined unless catalogue.isInstanceOf($scope.element?.elementType, 'asset')
    Action.createStandardAction(
      position: 1100
      label: 'Validate XML'
      icon: 'fa fa-fw fa-check-circle-o'
      type: 'default'
      action: ->
        messages.prompt('', '', {type: 'validate-xml-by-schema', asset: $scope.element})

    )
  ]


  actionsProvider.registerActionInRole 'download-asset',actionRoleRegister.ROLE_ITEM_DETAIL_ACTION, [ '$scope', '$window', ($scope, $window) ->
    return undefined unless $scope.element?.downloadUrl
    Action.createStandardAction(
      position: -50
      label: ''
      icon: 'fa fa-fw fa-download'
      type: 'primary'
      action: ->
        $window.open "#{$scope.element.downloadUrl}?force=true", '_blank'; return true
    )
  ]

  actionsProvider.registerActionInRole 'remove-relationship',actionRoleRegister.ROLE_ITEM_ACTION, ['$rootScope','$scope', '$state', 'messages', 'names', 'security', '$q', ($rootScope, $scope, $state, messages, names, security, $q) ->
    return undefined unless $scope.element?.isInstanceOf?('relationship')
    return undefined unless security.hasRole('CURATOR')
    Action.createStandardAction(
      position: 150
      label: ''
      icon: 'glyphicon glyphicon-remove'
      type: 'danger'
      action: ->
        rel   = $scope.element
        deferred = $q.defer()
        messages.confirm('Remove Relationship', "Do you really want to remove relation '#{rel.element.name} #{rel.type[rel.direction]} #{rel.relation.name}'?").then () ->
          rel.remove().then ->
            messages.success('Relationship removed!', "#{rel.relation.name} is no longer related to #{rel.element.name}")
            # reloads the table
            deferred.resolve(true)
            $rootScope.$broadcast 'catalogueElementDeleted', rel
          , (response) ->
            if response.data?.errors
              if angular.isString response.data.errors
                messages.error response.data.errors
              else
                for err in response.data.errors
                  messages.error err.message
            else if response.status == 404
              messages.error('Error removing relationship', 'Relationship cannot be removed, it probably does not exist anymore. The table was refreshed to get the most up to date results.')
              deferred.resolve(true)
            else
              messages.error('Error removing relationship', 'Relationship cannot be removed, see application logs for details')

        deferred.promise
    ).watching ['element.inherited' # For some reason there's a problem with applying a method on the next line to a multi-line list.
                'element.element.status'
                'element.relation.status'
                'element.type.versionSpecific']
      .disabledIf $scope.element.inherited or ($scope.element.type.versionSpecific and getIsSourceFinalized($scope.element))
  ]
  actionsProvider.registerChildAction 'catalogue-element',  'restore-relationship', ['$rootScope','$scope', '$state', 'messages', 'names', 'security', '$q', 'rest', 'enhance', 'modelCatalogueApiRoot', ($rootScope, $scope, $state, messages, names, security, $q, rest, enhance, modelCatalogueApiRoot) ->
    return undefined unless $scope.element?.isInstanceOf?('relationship')
    return undefined unless security.hasRole('CURATOR')
    Action.createStandardAction(
      position: 1000
      label: 'Restore Archived'
      icon: 'glyphicon glyphicon-refresh'
      type: 'primary'
      action: ->
        rel   = $scope.element
        messages.confirm('Restore Relationship', "Do you really want to restore relation '#{rel.element.name} #{rel.type[rel.direction]} #{rel.relation.name}'?").then () ->
          enhance(rest(method: 'POST', url: "#{modelCatalogueApiRoot}/relationship/#{rel.id}/restore")).then ->
            messages.success('Relationship restored!', "Relation '#{rel.element.name} #{rel.type[rel.direction]} #{rel.relation.name}' is no longer archived")
            rel.archived = false
          , (response) ->
            if response.data?.errors
              if angular.isString response.data.errors
                messages.error response.data.errors
              else
                for err in response.data.errors
                  messages.error err.message
            else if response.status == 404
              messages.error('Error restoring relationship', 'Relationship cannot be restored, it probably does not exist anymore. The table was refreshed to get the most up to date results.')
            else
              messages.error('Error restoring relationship', 'Relationship cannot be restored, see application logs for details')
    )
    .watching 'element.archived'
    .disabledIf !$scope.element.archived
  ]

  actionsProvider.registerActionInRole 'edit-relationship', actionRoleRegister.ROLE_ITEM_ACTION, ['$rootScope','$scope', '$state', 'messages', 'names', 'security', ($rootScope, $scope, $state, messages, names, security) ->
    getRelationship = ->
      $scope.element ? $scope.tab?.value
    return undefined unless getRelationship()?.isInstanceOf?('relationship')
    return undefined unless security.hasRole('CURATOR')
    Action.createStandardAction(
      position: 100
      label: ''
      icon: 'glyphicon glyphicon-edit'
      type: 'primary'
      action: ->
        rel   = getRelationship()
        rel.element.refresh().then (element) ->
          args = {relationshipType: rel.type, direction: rel.direction, type: 'update-relationship', currentDataModel: $scope.currentDataModel, update: true, element: element, relation: rel.relation, classification: rel.classification, metadata: angular.copy(rel.ext)}
          messages.prompt('Update Relationship', '', args).then (updated)->
            $rootScope.$broadcast 'catalogueElementUpdated', updated
            rel.ext = updated.ext
            rel.classification = updated.classification
    ).watching [
      'element.inherited'
      'element.element.status'
      'element.relation.status'
      'element.type.versionSpecific'
    ]
      .disabledIf !security.hasRole('SUPERVISOR') and ($scope.element.inherited or ($scope.element.type.versionSpecific and getIsSourceFinalized($scope.element)))
  ]

  actionsProvider.registerActionInRole 'edit-mapping', actionRoleRegister.ROLE_ITEM_ACTION, [ '$scope', 'messages', 'enhance', ($scope, messages, enhance) ->
    return undefined unless $scope.element?.isInstanceOf?('mapping')

    catalogueElementEnhancer = enhance.getEnhancer('catalogueElement')
    Action.createStandardAction (
      position: 100
      label: ''
      icon: 'glyphicon glyphicon-edit'
      type: 'primary'
      action: ->
        $scope.element.source.refresh().then (element)->
          args = {type: 'new-mapping', update: true, element: element, mapping: $scope.element}
          messages.prompt('Update Mapping', '', args).then (updated)->
            catalogueElementEnhancer.updateFrom $scope.element, updated
    )
      .watching 'element.mappings.total'
      .disabledIf $scope.element.isInstanceOf('dataType') and not $scope.element.mappings.total

  ]

  actionsProvider.registerActionInRole 'remove-mapping', actionRoleRegister.ROLE_ITEM_ACTION, ['$rootScope','$scope', '$state', 'messages', 'names', 'security', '$q', ($rootScope, $scope, $state, messages, names, security, $q) ->
    return undefined unless $scope.element?.isInstanceOf?('mapping')
    return undefined unless security.hasRole('CURATOR')
    Action.createStandardAction(
      position: 150
      label: ''
      icon: 'glyphicon glyphicon-remove'
      type: 'danger'
      action: ->
        mapping   = $scope.element
        deferred = $q.defer()
        messages.confirm('Remove Mapping', "Do you really want to remove mapping from #{mapping.source.name} to #{mapping.destination.name}?").then ->
          mapping.remove().then ->
            messages.success('Mapping removed!', "#{mapping.destination.name} is no longer related to #{mapping.source.name}")
            # reloads the table
            deferred.resolve(true)
          , (response) ->
            if response.data?.errors
              if angular.isString response.data.errors
                messages.error response.data.errors
              else
                for err in response.data.errors
                  messages.error err.message
            else if response.status == 404
              messages.error('Error removing mapping', 'Mapping cannot be removed, it probably does not exist anymore. The table was refreshed to get the most up to date results.')
              deferred.resolve(true)
            else
              messages.error('Error removing mapping', 'Mapping cannot be removed, see application logs for details')

        deferred.promise
    )
  ]

  actionsProvider.registerActionInRole 'change-type',actionRoleRegister.ROLE_ITEM_DETAIL_ACTION, ['$rootScope','$scope', 'messages', 'names', 'security', 'catalogueElementResource', ($rootScope, $scope, messages, names, security, catalogueElementResource) ->
    return undefined unless $scope.element?.isInstanceOf?('dataType')
    return undefined unless security.hasRole('CURATOR')
    Action.createStandardAction(
      position: 10000
      label: 'Change Type'
      icon: 'fa fa-fw fa-random'
      type: 'primary'
      action: ->
        options =
          dataType: 'Data Type'
          enumeratedType: 'Enumerated Type'
          primitiveType: 'Primitive Type'
          referenceType: 'Reference Type'

        messages.prompt('Change Type', "To which type should be #{$scope.element.name} converted? WARNING: any extra information such as enumerated values, data classes and measurement units will be lost!", {type: 'with-options', selected: names.getPropertyNameFromType($scope.element.elementType), options: options}).then (type)->
          catalogueElementResource($scope.element.elementType).update($scope.element, {newVersion: true, newType: type}).then (updated) ->
            $scope.element.updateFrom  updated
            messages.success("#{$scope.element.name} is now #{options[type]}")
            $rootScope.$broadcast 'newVersionCreated', updated
            $rootScope.$broadcast 'catalogueElementUpdated', updated
          , showErrorsUsingMessages(messages)
    )
      .watching ['element.status', 'element.archived']
      .disabledIf $scope.element.archived
  ]


  actionsProvider.registerChildAction 'catalogue-element', 'clone', ['$rootScope','$scope', 'messages', 'names', 'security', 'catalogueElementResource', 'enhance', 'rest', 'modelCatalogueApiRoot', ($rootScope, $scope, messages, names, security, catalogueElementResource, enhance, rest, modelCatalogueApiRoot) ->
    return undefined unless security.hasRole('CURATOR')
    return undefined unless $scope.element?.isInstanceOf?('catalogueElement')
    Action.createStandardAction(
      position: 20100
      label: 'Clone Current Element into Another Data Model'
      icon: 'fa fa-fw fa-clone'
      type: 'primary'
      action: ->
        messages.prompt("Clone #{$scope.element.name}", "Please, select the destination data model for the cloned element.", type: 'catalogue-element', status: 'draft', resource: 'dataModel').then (destinationDataModel) ->
          enhance(rest(url: "#{modelCatalogueApiRoot}#{$scope.element.link}/clone/#{destinationDataModel.id}", method: 'POST')).then (finalized) ->
            finalized.show()
          , showErrorsUsingMessages(messages)
    )
  ]

  actionsProvider.registerChildAction 'catalogue-element', 'clone-from', ['$rootScope','$scope', 'messages', 'names', 'security', 'catalogueElementResource', 'enhance', 'rest', 'modelCatalogueApiRoot', ($rootScope, $scope, messages, names, security, catalogueElementResource, enhance, rest, modelCatalogueApiRoot) ->
    return undefined unless security.hasRole('CURATOR')
    return undefined unless $scope.element?.isInstanceOf?('dataModel')
    return undefined unless $scope.element?.status == 'DRAFT'
    Action.createStandardAction(
      position: 20000
      label: 'Clone Another Element into Current Data Model'
      icon: 'fa fa-fw fa-clone fa-flip-horizontal'
      type: 'primary'
      action: ->
        messages.prompt("Clone into #{$scope.element.name}", null, type: 'clone-into', currentDataModel: $scope.element).then (elementToBeCloned) ->
          enhance(rest(url: "#{modelCatalogueApiRoot}#{elementToBeCloned.link}/clone/#{$scope.element.id}", method: 'POST')).then (finalized) ->
            finalized.show()
          , showErrorsUsingMessages(messages)
    )
  ]

  actionsProvider.registerChildAction 'catalogue-element', 'reindex-data-model', ['$rootScope','$scope', 'messages', 'names', 'security', 'catalogueElementResource', 'enhance', 'rest', 'modelCatalogueApiRoot', ($rootScope, $scope, messages, names, security, catalogueElementResource, enhance, rest, modelCatalogueApiRoot) ->
    return undefined unless security.hasRole('ADMIN')
    return undefined unless $scope.element?.isInstanceOf?('dataModel')
    Action.createStandardAction(
      position: 50500
      label: 'Reindex Data Model'
      icon: 'fa fa-search fa-fw'
      type: 'primary'
      action: ->
        messages.confirm("Reindex #{$scope.element.name}", "Do you want to reindex #{$scope.element.name}? This may have negative impact on application performance for large data models.").then ->
          $scope.element.execute('reindex', 'POST').then ->
            messages.success("Reindexing #{$scope.element.name} started")
          , showErrorsUsingMessages(messages)
    )
  ]

  actionsProvider.registerChildAction 'catalogue-element', 'finalize', ['$rootScope','$scope', 'messages', 'security', ($rootScope, $scope, messages, security) ->
    return undefined unless security.hasRole('CURATOR')
    return undefined unless $scope.element?.status == 'DRAFT'
    return undefined unless $scope.element?.isInstanceOf?('dataModel')

    Action.createStandardAction(
      position: -2000
      label: 'Finalize'
      icon: 'fa fa-fw fa-check-circle'
      type: 'primary'
      action: ->
        messages.prompt(null, null, type: 'finalize', element: $scope.element)
    )
      .watching ['element.status', 'element.archived']
      .disabledIf $scope.element?.status != 'DRAFT'
  ]

  newAssetVersion = ['$rootScope','$scope', 'messages', 'security', ($rootScope, $scope, messages, security) ->
    return undefined unless security.hasRole('CURATOR')
    return undefined unless $scope.element?.isInstanceOf?('asset')
    Action.createStandardAction(
      position: -1900
      label: 'Upload New Version'
      icon: 'fa fa-fw fa-arrow-circle-up'
      type: 'primary'
      action: ->
        messages.prompt('Upload New Version', null, type: 'edit-asset', element: $scope.element, currentDataModel: $scope.element.dataModel).then (newOne) ->
          newOne.show()
    )
      .watching ['element.status', 'element.archived']
      .disabledIf $scope.element.archived
  ]


  actionsProvider.registerChildAction 'catalogue-element', 'upload-new-asset-version', newAssetVersion
  actionsProvider.registerActionInRoles 'upload-new-asset-version-tiny', [actionRoleRegister.ROLE_ITEM_DETAIL_ACTION, actionRoleRegister.ROLE_ITEM_INFINITE_LIST], newAssetVersion
