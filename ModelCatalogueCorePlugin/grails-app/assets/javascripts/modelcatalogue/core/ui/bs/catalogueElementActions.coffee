angular.module('mc.core.ui.bs.catalogueElementActions', ['mc.util.ui.actions']).config ['actionsProvider', 'names', (actionsProvider, names)->

  updateFrom = (original, update) ->
    for originalKey of original
      if originalKey.indexOf('$') != 0 # keep the private fields such as number of children in tree view
        delete original[originalKey]

    for newKey of update
      original[newKey] = update[newKey]
    original


  showErrorsUsingMessages = (messages) ->
    (response) ->
      if response?.data and response.data.errors
        if angular.isString response.data.errors
          messages.error response.data.errors
        else
          for err in response.data.errors
            messages.error err.message


  actionsProvider.registerActionInRoles 'catalogue-element', [actionsProvider.ROLE_ITEM_ACTION], ['$scope', 'security', 'names', 'catalogue', ($scope, security, name, catalogue)->
    return undefined if not security.hasRole('CURATOR')
    return undefined unless $scope.element
    if $scope.element
      return undefined if not angular.isFunction $scope.element.isInstanceOf
      return undefined if $scope.element.isInstanceOf 'dataImport'

    action = {
      position:   3000
      label:      names.getNaturalName(names.getPropertyNameFromQualifier($scope.element.elementType))
      icon:       catalogue.getIcon($scope.element.elementType)
      type:       'primary'
      expandToLeft: true
    }

    $scope.$watch 'element.elementType', (elementType) ->
      action.icon   = catalogue.getIcon(elementType)
      action.label  = names.getNaturalName(names.getPropertyNameFromQualifier(elementType))
    action
  ]

  actionsProvider.registerActionInRole 'edit-catalogue-element', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', 'messages', 'names', 'security', ($rootScope, $scope, messages, names, security) ->
    return undefined if not $scope.element
    return undefined if not angular.isFunction $scope.element.isInstanceOf
    return undefined if not angular.isFunction $scope.element.getResourceName
    return undefined if $scope.element.isInstanceOf 'dataImport'
    return undefined if not messages.hasPromptFactory('create-' + $scope.element.getResourceName()) and not messages.hasPromptFactory('edit-' + $scope.element.getResourceName())
    return undefined if not security.hasRole('CURATOR')

    action =
      position:   4000
      label:      ''
      icon:       'fa fa-fw fa-edit'
      type:       'primary'
      disabled:   $scope.element.archived or $scope.element?.status == 'FINALIZED'
      action:     ->
        messages.prompt('Edit ' + $scope.element.getElementTypeName(), '', {type: 'edit-' + names.getPropertyNameFromType($scope.element.elementType), element: $scope.element}).then (updated)->
          updateFrom $scope.element, updated

    updateAction = ->
      action.disabled = $scope.element.archived or $scope.element?.status == 'FINALIZED'

    $scope.$watch 'element.status', updateAction
    $scope.$watch 'element.archived', updateAction
    $scope.$on 'newVersionCreated', updateAction

    return action

  ]

  actionsProvider.registerChildActionInRoles 'catalogue-element', 'create-new-relationship', [actionsProvider.ROLE_ITEM_ACTION], ['$scope', 'messages', 'names', 'security', ($scope, messages, names, security) ->
    return undefined if not $scope.element
    return undefined if not angular.isFunction($scope.element.isInstanceOf)
    return undefined if not $scope.element.isInstanceOf('catalogueElement')
    return undefined if $scope.element.isInstanceOf 'dataImport'
    return undefined if not security.hasRole('CURATOR')

    action = {
      position:   200
      label:      'Create Relationship'
      icon:       'fa fa-fw fa-chain'
      icon:       'fa fa-fw fa-chain'
      type:       'success'
      action:     ->
        messages.prompt('Create Relationship', '', {type: 'create-new-relationship', element: $scope.element}).catch showErrorsUsingMessages(messages)
    }

    updateAction = ->
      action.disabled = $scope.element.archived

    $scope.$watch 'element.status', updateAction
    $scope.$watch 'element.archived', updateAction

    action
  ]

  actionsProvider.registerChildActionInRole 'catalogue-element', 'compare-catalogue-element', actionsProvider.ROLE_ITEM_ACTION, ['$scope', 'messages', '$state', ($scope, messages, $state) ->
    elementPresent = $scope.element and angular.isFunction($scope.element.getResourceName) and angular.isFunction($scope.element.getElementTypeName) and angular.isFunction($scope.element.isInstanceOf) and $scope.element.isInstanceOf('catalogueElement')
    diffView = $state.current.name == 'mc.resource.diff'

    return undefined if not elementPresent and not diffView

    element = if elementPresent then $scope.element else $scope.elements[0]
    ids = if elementPresent then [element.id] else (e.id for e in $scope.elements)

    {
    position: 500
    label: if elementPresent then 'Compare' else 'Compare Another'
    icon: 'fa fa-fw fa-arrows-h'
    type: 'primary'
    action: ->
      messages.prompt('Compare ' + element.getElementTypeName(), "Select the #{element.getElementTypeName()} for the comparison",
        {type: 'catalogue-element', resource: element.getResourceName()}).then (toBeCompared)->
          $state.go 'mc.resource.diff', ids: ids.concat([toBeCompared.id]).join('~')
    }
  ]


  actionsProvider.registerChildActionInRole 'catalogue-element', 'create-new-mapping', actionsProvider.ROLE_ITEM_ACTION, ['$scope', 'messages', 'names', 'security', ($scope, messages, names, security) ->
    return undefined if not $scope.element
    return undefined if not $scope.element.hasOwnProperty('mappings')
    return undefined if not security.hasRole('CURATOR')

    {
    position:   300
    label:      'Create Mapping'
    icon:       'fa fa-fw fa-superscript'
    type:       'success'
    action:     ->
      messages.prompt('Create new mapping for ' + $scope.element.name, '', {type: 'new-mapping', element: $scope.element}).catch showErrorsUsingMessages(messages)
    }
  ]


  actionsProvider.registerChildActionInRole 'catalogue-element', 'validate-value', actionsProvider.ROLE_ITEM_ACTION, [ '$scope', 'messages', 'security', ($scope, messages) ->
    return undefined if not $scope.element
    return undefined if not angular.isFunction $scope.element.isInstanceOf
    return undefined if not $scope.element.isInstanceOf('valueDomain')

    action = {
      position:   1200
      label:      'Validate Value'
      icon:       'fa fa-fw fa-check-circle-o'
      type:       'primary'
      action:     ->
        messages.prompt('', '', {type: 'validate-value-by-domain', domain: $scope.element})
    }

    updateDisabled =  ->
      action.disabled = not $scope.element.rule and not ($scope.element.dataType and $scope.element.dataType.isInstanceOf('enumeratedType')) and $scope.element.basedOn?.length == 0


    $scope.$watch 'element.rule',     updateDisabled
    $scope.$watch 'element.dataType', updateDisabled

    updateDisabled()

    action
  ]


  actionsProvider.registerChildActionInRole 'catalogue-element', 'convert', actionsProvider.ROLE_ITEM_ACTION, [ '$scope', 'messages', 'security', ($scope, messages) ->
    return undefined if not $scope.element
    return undefined if not angular.isFunction $scope.element.isInstanceOf
    return undefined if not $scope.element.isInstanceOf('valueDomain') and not $scope.element.isInstanceOf('mapping')

    {
    position:   1100
    label:      'Convert Value'
    icon:       'fa fa-fw fa-long-arrow-right'
    type:       'primary'
    action:     ->
      if $scope.element.isInstanceOf('valueDomain')
        messages.prompt('', '', {type: 'convert-with-value-domain', source: $scope.element})
      else if $scope.element.isInstanceOf('mapping')
        messages.prompt('', '', {type: 'convert-with-value-domain', source: $scope.element.source, destination: $scope.element.destination})

    }
  ]

  actionsProvider.registerChildActionInRole 'catalogue-element', 'validate-xsd-schema', actionsProvider.ROLE_ITEM_ACTION, [ '$scope', 'messages', 'catalogue', ($scope, messages, catalogue) ->
    return undefined if not catalogue.isInstanceOf($scope.element?.elementType, 'asset')

    {
      position:   1100
      label:      'Validate XML'
      icon:       'fa fa-fw fa-check-circle-o'
      type:       'default'
      action:     ->
        messages.prompt('', '', {type: 'validate-xml-by-schema', asset: $scope.element})

    }
  ]


  actionsProvider.registerActionInRole 'download-asset', actionsProvider.ROLE_ITEM_ACTION, [ '$scope', '$window', ($scope, $window) ->
    return undefined if not $scope.element?.downloadUrl?

    {
    position:   - 50
    label:      ''
    icon:       'fa fa-fw fa-download'
    type:       'primary'
    action:     ->
      $window.open $scope.element.downloadUrl, '_blank'; return true

    }
  ]


  actionsProvider.registerActionInRole 'remove-relationship', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', '$state', 'messages', 'names', 'security', '$q', ($rootScope, $scope, $state, messages, names, security, $q) ->
    return undefined if not $scope.element
    return undefined if not angular.isFunction($scope.element.isInstanceOf)
    return undefined if not $scope.element.isInstanceOf('relationship')
    return undefined if not security.hasRole('CURATOR')


    {
      position:   150
      label:      ''
      icon:       'glyphicon glyphicon-remove'
      type:       'danger'
      action:     ->
        rel   = $scope.element
        deferred = $q.defer()
        messages.confirm('Remove Relationship', "Do you really want to remove relation '#{rel.element.name} #{rel.type[rel.direction]} #{rel.relation.name}'?").then () ->
          rel.remove().then ->
            messages.success('Relationship removed!', "#{rel.relation.name} is no longer related to #{rel.element.name}")
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
              messages.error('Error removing relationship', 'Relationship cannot be removed, it probably does not exist anymore. The table was refreshed to get the most up to date results.')
              deferred.resolve(true)
            else
              messages.error('Error removing relationship', 'Relationship cannot be removed, see application logs for details')

        deferred.promise
    }
  ]

  actionsProvider.registerActionInRole 'edit-relationship', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', '$state', 'messages', 'names', 'security', ($rootScope, $scope, $state, messages, names, security) ->
    return undefined if not $scope.element
    return undefined if not angular.isFunction($scope.element.isInstanceOf)
    return undefined if not $scope.element.isInstanceOf('relationship')
    return undefined if not security.hasRole('CURATOR')

    {
    position:   100
    label:      ''
    icon:       'glyphicon glyphicon-edit'
    type:       'primary'
    action:     ->
      rel   = $scope.element
      rel.element.refresh().then (element) ->
        args = {relationshipType: rel.type, direction: rel.direction, type: 'update-relationship', update: true, element: element, relation: rel.relation, classification: rel.classification, metadata: angular.copy(rel.ext)}
        messages.prompt('Update Relationship', '', args).then (updated)->
          rel.ext = updated.ext
          rel.classification = updated.classification
    }
  ]

  actionsProvider.registerActionInRole 'edit-mapping', actionsProvider.ROLE_ITEM_ACTION, [ '$scope', 'messages', 'security', ($scope, messages) ->
    return undefined if not $scope.element
    return undefined if not angular.isFunction $scope.element.isInstanceOf
    return undefined if not $scope.element.isInstanceOf('mapping')

    action =  {
      position:   100
      label:      ''
      icon:       'glyphicon glyphicon-edit'
      type:       'primary'
      action:     ->
        $scope.element.source.refresh().then (element)->
          args = {type: 'new-mapping', update: true, element: element, mapping: $scope.element}
          messages.prompt('Update Mapping', '', args).then (updated)->
            updateFrom $scope.element, updated

    }

    $scope.$watch 'element.mappings.total', (total) ->
      action.disabled = $scope.element.isInstanceOf('valueDomain') and not total

    action

  ]

  actionsProvider.registerActionInRole 'remove-mapping', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', '$state', 'messages', 'names', 'security', '$q', ($rootScope, $scope, $state, messages, names, security, $q) ->
    return undefined if not $scope.element
    return undefined if not angular.isFunction($scope.element.isInstanceOf)
    return undefined if not $scope.element.isInstanceOf('mapping')
    return undefined if not security.hasRole('CURATOR')

    {
    position:   150
    label:      ''
    icon:       'glyphicon glyphicon-remove'
    type:       'danger'
    action:     ->
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
    }
  ]


]