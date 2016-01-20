angular.module('mc.core.ui.bs.catalogueElementActions', ['mc.util.ui.actions']).config ['actionsProvider', 'names', (actionsProvider, names)->

  showErrorsUsingMessages = (messages) ->
    (response) ->
      if response?.data and response.data.errors
        if angular.isString response.data.errors
          messages.error response.data.errors
        else
          for err in response.data.errors
            messages.error err.message


  actionsProvider.registerActionInRoles 'catalogue-element',[actionsProvider.ROLE_ITEM_ACTION], ['$scope', 'security', 'names', 'catalogue', ($scope, security, name, catalogue)->
    return undefined if not security.hasRole('CURATOR')
    return undefined unless $scope.element
    if $scope.element
      return undefined if not angular.isFunction $scope.element.isInstanceOf

    {
      show:       true
      position:   0
      label:      names.getNaturalName(names.getPropertyNameFromQualifier($scope.element.elementType))
      icon:       catalogue.getIcon($scope.element.elementType)
      type:       'primary'
      watches:    'element.elementType'
      expandToLeft: true
    }
  ]

  actionsProvider.registerActionInRoles 'edit-catalogue-element',[actionsProvider.ROLE_ITEM_DETAIL_ACTION], ['$scope', 'messages', 'names', 'security', ($scope, messages, names, security) ->
    return undefined if not $scope.element
    return undefined if not angular.isFunction $scope.element.isInstanceOf
    return undefined if not angular.isFunction $scope.element.getResourceName
    return undefined if not messages.hasPromptFactory('create-' + $scope.element.getResourceName()) and not messages.hasPromptFactory('edit-' + $scope.element.getResourceName())
    return undefined if not security.hasRole('CURATOR')
    return undefined if angular.isFunction($scope.supportsInlineEdit) and $scope.supportsInlineEdit($scope.editableForm)

    {
      position:   -1000
      label:      'Edit'
      icon:       'fa fa-edit'
      type:       'primary'
      disabled:   $scope.element.archived or $scope.element?.status == 'FINALIZED'
      watches:    ['element.status', 'element.archived']
      action:     ->
        messages.prompt('Edit ' + $scope.element.getElementTypeName(), '', {type: 'edit-' + names.getPropertyNameFromType($scope.element.elementType), element: $scope.element}).then (updated)->
          $scope.element.updateFrom updated

    }

  ]

  actionsProvider.registerChildActionInRoles 'catalogue-element', 'create-new-relationship',[actionsProvider.ROLE_ITEM_ACTION], ['$scope', 'messages', 'names', 'security', ($scope, messages, names, security) ->
    return undefined if not $scope.element
    return undefined if not angular.isFunction($scope.element.isInstanceOf)
    return undefined if not $scope.element.isInstanceOf('catalogueElement')
    return undefined if not security.hasRole('CURATOR')

    {
      position:   200
      label:      'Create Relationship'
      icon:       'fa fa-fw fa-chain'
      icon:       'fa fa-fw fa-chain'
      type:       'success'
      watches:    ['element.status', 'element.archived']
      disabled:   $scope.element.archived
      action:     ->
        messages.prompt('Create Relationship', '', {type: 'create-new-relationship', element: $scope.element, currentDataModel: $scope.currentDataModel}).catch showErrorsUsingMessages(messages)
    }

  ]

  actionsProvider.registerChildActionInRoles 'catalogue-element', 'compare-catalogue-element',[actionsProvider.ROLE_ITEM_ACTION], ['$scope', 'messages', '$state', ($scope, messages, $state) ->
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


  actionsProvider.registerChildActionInRoles 'catalogue-element', 'create-new-mapping',[actionsProvider.ROLE_ITEM_ACTION], ['$scope', 'messages', 'names', 'security', ($scope, messages, names, security) ->
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


  actionsProvider.registerChildActionInRoles 'catalogue-element', 'validate-value',[actionsProvider.ROLE_ITEM_ACTION], [ '$scope', 'messages', 'security', ($scope, messages) ->
    return undefined if not $scope.element
    return undefined if not angular.isFunction $scope.element.isInstanceOf
    return undefined if not $scope.element.isInstanceOf('dataType')

    {
      position:   1200
      label:      'Validate Value'
      icon:       'fa fa-fw fa-check-circle-o'
      type:       'primary'
      watches:    ['element.rule', 'element.dataType']
      disabled:   not $scope.element.rule \
        and $scope.element.basedOn?.length == 0 \
        and not (($scope.element.dataType and $scope.element.dataType.isInstanceOf('enumeratedType')) or $scope.element.isInstanceOf('enumeratedType'))
    action:     ->
        messages.prompt('', '', {type: 'validate-value-by-domain', domain: $scope.element})
    }
  ]


  actionsProvider.registerChildActionInRoles 'catalogue-element', 'convert',[actionsProvider.ROLE_ITEM_ACTION], [ '$scope', 'messages', 'security', ($scope, messages) ->
    return undefined if not $scope.element
    return undefined if not angular.isFunction $scope.element.isInstanceOf
    return undefined if not $scope.element.isInstanceOf('dataType') and not $scope.element.isInstanceOf('mapping')

    {
      position:   1100
      label:      'Convert Value'
      icon:       'fa fa-fw fa-long-arrow-right'
      type:       'primary'
      action:     ->
        if $scope.element.isInstanceOf('dataType')
          messages.prompt('', '', {type: 'convert-with-value-domain', source: $scope.element})
        else if $scope.element.isInstanceOf('mapping')
          messages.prompt('', '', {type: 'convert-with-value-domain', source: $scope.element.source, destination: $scope.element.destination})

    }
  ]

  actionsProvider.registerChildActionInRoles 'catalogue-element', 'validate-xsd-schema',[actionsProvider.ROLE_ITEM_ACTION], [ '$scope', 'messages', 'catalogue', ($scope, messages, catalogue) ->
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


  actionsProvider.registerActionInRoles 'download-asset',[actionsProvider.ROLE_ITEM_ACTION], [ '$scope', '$window', ($scope, $window) ->
    return undefined if not $scope.element?.downloadUrl?

    {
      position:   -50
      label:      ''
      icon:       'fa fa-fw fa-download'
      type:       'primary'
      action:     ->
        $window.open "#{$scope.element.downloadUrl}?force=true", '_blank'; return true

    }
  ]


  actionsProvider.registerActionInRoles 'remove-relationship',[actionsProvider.ROLE_ITEM_ACTION], ['$rootScope','$scope', '$state', 'messages', 'names', 'security', '$q', ($rootScope, $scope, $state, messages, names, security, $q) ->
    return undefined if not $scope.element
    return undefined if not angular.isFunction($scope.element.isInstanceOf)
    return undefined if not $scope.element.isInstanceOf('relationship')
    return undefined if not security.hasRole('CURATOR')


    {
      position:   150
      label:      ''
      icon:       'glyphicon glyphicon-remove'
      type:       'danger'
      watches:    'element.inherited'
      disabled:   $scope.element.inherited
      action:     ->
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
    }
  ]

  actionsProvider.registerChildActionInRoles 'catalogue-element',  'restore-relationship',[actionsProvider.ROLE_ITEM_ACTION], ['$rootScope','$scope', '$state', 'messages', 'names', 'security', '$q', 'rest', 'enhance', 'modelCatalogueApiRoot', ($rootScope, $scope, $state, messages, names, security, $q, rest, enhance, modelCatalogueApiRoot) ->
    return undefined if not $scope.element
    return undefined if not angular.isFunction($scope.element.isInstanceOf)
    return undefined if not $scope.element.isInstanceOf('relationship')
    return undefined if not security.hasRole('CURATOR')

    action =
      position:   1000
      label:      'Restore Archived'
      icon:       'glyphicon glyphicon-refresh'
      type:       'primary'
      watches:    'element.archived'
      disabled:   !$scope.element.archived
      action:     ->
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

    return action
  ]

  actionsProvider.registerActionInRoles 'edit-relationship', [actionsProvider.ROLE_ITEM_ACTION], ['$rootScope','$scope', '$state', 'messages', 'names', 'security', ($rootScope, $scope, $state, messages, names, security) ->
    getRelationship = ->
      $scope.element ? $scope.tab?.value
    return undefined if not getRelationship()
    return undefined if not angular.isFunction(getRelationship().isInstanceOf)
    return undefined if not getRelationship().isInstanceOf('relationship')
    return undefined if not security.hasRole('CURATOR')

    {
    position:   100
    label:      ''
    icon:       'glyphicon glyphicon-edit'
    type:       'primary'
    watches:    'element.inherited'
    disabled:   $scope.element.inherited
    action:     ->
      rel   = getRelationship()
      rel.element.refresh().then (element) ->
        args = {relationshipType: rel.type, direction: rel.direction, type: 'update-relationship', update: true, element: element, relation: rel.relation, classification: rel.classification, metadata: angular.copy(rel.ext)}
        messages.prompt('Update Relationship', '', args).then (updated)->
          $rootScope.$broadcast 'catalogueElementUpdated', updated
          rel.ext = updated.ext
          rel.classification = updated.classification
    }
  ]

  actionsProvider.registerActionInRoles 'edit-mapping', [actionsProvider.ROLE_ITEM_ACTION], [ '$scope', 'messages', 'enhance', ($scope, messages, enhance) ->
    return undefined if not $scope.element
    return undefined if not angular.isFunction $scope.element.isInstanceOf
    return undefined if not $scope.element.isInstanceOf('mapping')

    catalogueElementEnhancer = enhance.getEnhancer('catalogueElement')

    {
      position:   100
      label:      ''
      icon:       'glyphicon glyphicon-edit'
      type:       'primary'
      watches:    'element.mappings.total'
      disabled:    $scope.element.isInstanceOf('dataType') and not $scope.element.mappings.total
      action:     ->
        $scope.element.source.refresh().then (element)->
          args = {type: 'new-mapping', update: true, element: element, mapping: $scope.element}
          messages.prompt('Update Mapping', '', args).then (updated)->
            catalogueElementEnhancer.updateFrom $scope.element, updated

    }

  ]

  actionsProvider.registerActionInRoles 'remove-mapping', [actionsProvider.ROLE_ITEM_ACTION], ['$rootScope','$scope', '$state', 'messages', 'names', 'security', '$q', ($rootScope, $scope, $state, messages, names, security, $q) ->
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

  actionsProvider.registerChildActionInRole 'catalogue-element', 'clone', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', 'messages', 'names', 'security', 'catalogueElementResource', 'enhance', 'rest', 'modelCatalogueApiRoot', ($rootScope, $scope, messages, names, security, catalogueElementResource, enhance, rest, modelCatalogueApiRoot) ->
    return undefined if not security.hasRole('CURATOR')
    return undefined if not $scope.element
    return undefined if not angular.isFunction($scope.element.isInstanceOf)
    return undefined if not $scope.element.isInstanceOf('catalogueElement')

    {
      position:   20100
      label:      'Clone Current Element into Another Data Model'
      icon:       'fa fa-fw fa-clone'
      type:       'primary'
      action:     ->
        messages.prompt("Clone #{$scope.element.name}", "Please, select the destination data model for the cloned element.", type: 'catalogue-element', status: 'draft', resource: 'dataModel').then (destinationDataModel) ->
          enhance(rest(url: "#{modelCatalogueApiRoot}#{$scope.element.link}/clone/#{destinationDataModel.id}", method: 'POST')).then (finalized) ->
            finalized.show()
          , showErrorsUsingMessages(messages)
    }
  ]

  actionsProvider.registerChildActionInRole 'catalogue-element', 'clone-from', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', 'messages', 'names', 'security', 'catalogueElementResource', 'enhance', 'rest', 'modelCatalogueApiRoot', ($rootScope, $scope, messages, names, security, catalogueElementResource, enhance, rest, modelCatalogueApiRoot) ->
    return undefined if not security.hasRole('CURATOR')
    return undefined if not $scope.element
    return undefined if not angular.isFunction($scope.element.isInstanceOf)
    return undefined if not $scope.element.isInstanceOf('dataModel')
    return undefined if not $scope.element.status == 'DRAFT'

    {
      position:   20000
      label:      'Clone Another Element into Current Data Model'
      icon:       'fa fa-fw fa-clone fa-flip-horizontal'
      type:       'primary'
      action:     ->
        messages.prompt("Clone into #{$scope.element.name}", "Please, select the element to be cloned", type: 'catalogue-element', status: 'finalized', resource: 'catalogueElement', global: true).then (elementToBeCloned) ->
          enhance(rest(url: "#{modelCatalogueApiRoot}#{elementToBeCloned.link}/clone/#{$scope.element.id}", method: 'POST')).then (finalized) ->
            finalized.show()
          , showErrorsUsingMessages(messages)
    }
  ]


  actionsProvider.registerChildActionInRole 'catalogue-element', 'delete', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', '$state', 'messages', 'names', 'security', ($rootScope, $scope, $state, messages, names, security) ->
    return undefined if not $scope.element
    return undefined if not angular.isFunction($scope.element.delete)
    return undefined unless security.hasRole('CURATOR')
    # currently constrained for assets only
    return undefined unless $scope.element.isInstanceOf('asset')

    {
      position:   50000
      label:      'Delete'
      icon:       'fa fa-fw fa-times-circle'
      type:       'danger'
      action:     ->
        messages.confirm("Do you really want to delete #{$scope.element.getElementTypeName()} #{$scope.element.name} ?", "The #{$scope.element.getElementTypeName()} #{$scope.element.name} will be deleted permanently. This action cannot be undone.").then ->
          $scope.element.delete()
          .then ->
            messages.success "#{$scope.element.getElementTypeName()} #{$scope.element.name} deleted."
            if $state.current.name.indexOf('mc.resource.show') >= 0
              $state.go('mc.resource.list', {resource: names.getPropertyNameFromType($scope.element.elementType)}, {reload: true})
          .catch showErrorsUsingMessages(messages)
    }
  ]

]