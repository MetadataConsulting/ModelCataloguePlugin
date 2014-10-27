angular.module('mc.core.ui.bs.actions', ['mc.util.ui.actions']).config ['actionsProvider', 'names', (actionsProvider, names)->
  ROLE_ACTION_ACTION = 'action'


  updateFrom = (original, update) ->
    for originalKey of original
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
      security.requireRole('CURATOR')
      .then ->
        messages.prompt('Create ' + names.getNaturalName($scope.resource), '', args)
      , ->
        messages.error('You don\'t have rights to create new elements')
    }
  ]

  actionsProvider.registerActionInRole 'new-import', actionsProvider.ROLE_LIST_ACTION, [
    '$scope', 'names','security', '$state',
    ($scope ,  names , security ,  $state ) ->
      return undefined if not security.hasRole('CURATOR')
      return undefined if not $state.current.name == 'mc.dataArchitect.imports'

      {
        position: 100
        label: "Import"
        icon: 'fa fa-cloud-upload'
        type: 'success'
      }
  ]


  actionsProvider.registerChildAction 'new-import', 'import-excel', ['$scope', 'messages', ($scope, messages) -> {
    label:  "Import Excel"
    action: ->
      messages.prompt('Import Excel File', '', type: 'new-excel-import')
  }]

  actionsProvider.registerChildAction 'new-import', 'import-obo', ['$scope', 'messages', ($scope, messages) -> {
    label:  "Import OBO"
    action: ->
      messages.prompt('Import OBO File', '', type: 'new-obo-import')
  }]

  actionsProvider.registerChildAction 'new-import', 'import-xsd', ['$scope', 'messages', ($scope, messages) -> {
    label:  "Import XSD"
    action: ->
      messages.prompt('Import XSD File', '', type: 'new-xsd-import')
  }]



  actionsProvider.registerActionInRole 'edit-catalogue-element', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', 'messages', 'names', 'security', ($rootScope, $scope, messages, names, security) ->
    return undefined if not $scope.element
    return undefined if not angular.isFunction $scope.element.isInstanceOf
    return undefined if not angular.isFunction $scope.element.getResourceName
    return undefined if $scope.element.isInstanceOf 'dataImport'
    return undefined if not messages.hasPromptFactory('create-' + $scope.element.getResourceName()) and not messages.hasPromptFactory('edit-' + $scope.element.getResourceName())
    return undefined if not security.hasRole('CURATOR')

    action =
      position:   100
      label:      'Edit'
      icon:       'glyphicon glyphicon-edit'
      type:       'primary'
      disabled:   $scope.element.archived or $scope.element?.status == 'FINALIZED'
      action:     ->
        messages.prompt('Edit ' + $scope.element.getElementTypeName(), '', {type: 'edit-' + names.getPropertyNameFromType($scope.element.elementType), element: $scope.element}).then (updated)->
          updateFrom $scope.element, updated

    updateAction = ->
      action.disabled = $scope.element.archived or $scope.element?.status == 'FINALIZED'

    $scope.$watch 'element.status', updateAction
    $scope.$watch 'element.archived', updateAction
    $rootScope.$on 'newVersionCreated', updateAction

    return action

  ]

  actionsProvider.registerActionInRole 'compare-catalogue-element', actionsProvider.ROLE_ITEM_ACTION, ['$scope', 'messages', '$state', ($scope, messages, $state) ->
    elementPresent = $scope.element and angular.isFunction($scope.element.getResourceName) and angular.isFunction($scope.element.getElementTypeName) and angular.isFunction($scope.element.isInstanceOf) and $scope.element.isInstanceOf('catalogueElement')
    diffView = $state.current.name == 'mc.resource.diff'

    return undefined if not elementPresent and not diffView

    element = if elementPresent then $scope.element else $scope.elements[0]
    ids = if elementPresent then [element.id] else (e.id for e in $scope.elements)

    {
      position: 500
      label: if elementPresent then 'Compare' else 'Compare Another'
      icon: 'fa fa-arrows-h'
      type: 'primary'
      action: ->
        messages.prompt('Compare ' + element.getElementTypeName(), "Select the #{element.getElementTypeName()} for the comparison",
          {type: 'catalogue-element', resource: element.getResourceName()}).then (toBeCompared)->
            $state.go 'mc.resource.diff', ids: ids.concat([toBeCompared.id]).join('~')
    }
  ]

  actionsProvider.registerActionInRole 'favorite-element', actionsProvider.ROLE_ITEM_ACTION, ['$scope', 'messages', '$state', 'security', 'catalogueElementResource', 'modelCatalogueApiRoot', 'enhance', 'rest', ($scope, messages, $state, security, catalogueElementResource, modelCatalogueApiRoot, enhance, rest) ->
    elementPresent = $scope.element and angular.isFunction($scope.element.getResourceName) and angular.isFunction($scope.element.getElementTypeName) and angular.isFunction($scope.element.isInstanceOf) and $scope.element.isInstanceOf('catalogueElement')

    return undefined if not elementPresent
    return undefined if not security.getCurrentUser()?.id

    action =
      position: -200
      label: ''
      icon: 'fa fa-star'
      type: 'primary'
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

    $scope.$watch 'element.favourite', (favourite) ->
      if favourite
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

  actionsProvider.registerActionInRole 'create-new-version', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', 'messages', 'names', 'security', 'catalogueElementResource', ($rootScope, $scope, messages, names, security, catalogueElementResource) ->
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
          updateFrom $scope.element, updated
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
            updateFrom $scope.element, updated
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
      label:      'Mark as Deprecated'
      icon:       'glyphicon glyphicon-ban-circle'
      type:       'danger'
      action:     ->
        messages.confirm("Do you want to mark #{$scope.element.getElementTypeName()} #{$scope.element.name} as deprecated?", "The #{$scope.element.getElementTypeName()} #{$scope.element.name} will be marked as deprecated").then ->
          enhance(rest(url: "#{modelCatalogueApiRoot}#{$scope.element.link}/archive", method: 'POST')).then (archived) ->
            updateFrom $scope.element, archived
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
            updateFrom $scope.element, merged
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
    return undefined if not angular.isFunction $scope.element.isInstanceOf
    return undefined if not $scope.element.isInstanceOf('model')
    return undefined if not $scope.element.status
    return undefined if not security.hasRole('CURATOR')

    action = {
      label:      'Finalize Tree'
      type:       'primary'
      action:     ->
        messages.confirm("Finalize Model Tree", "Do you really want to finalize Model #{$scope.element.name} and and all its child models and elements?" ).then ->
          enhance(rest(url: "#{modelCatalogueApiRoot}#{$scope.element.link}/finalizeTree", method: 'POST')).then (finalized) ->
            updateFrom $scope.element, finalized
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
            updateFroms batch, archived
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
    return undefined if not angular.isFunction($scope.element.delete)
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
            #$state.go('mc.resource.list', {resource: names.getPropertyNameFromType($scope.element.elementType)}, {reload: true})
          .catch showErrorsUsingMessages(messages)
    }

    updateAction = ->
      action.disabled = $scope.element.archived

    $scope.$watch 'element.status', updateAction
    $scope.$watch 'element.archived', updateAction
    $rootScope.$on 'newVersionCreated', updateAction

    action
  ]


  actionsProvider.registerActionInRole 'remove-mapping', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', '$state', 'messages', 'names', 'security', '$q', ($rootScope, $scope, $state, messages, names, security, $q) ->
    return undefined if not $scope.element
    return undefined if not angular.isFunction($scope.element.isInstanceOf)
    return undefined if not $scope.element.isInstanceOf('mapping')
    return undefined if not security.hasRole('CURATOR')

    {
      position:   150
      label:      'Remove'
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

  actionsProvider.registerActionInRole 'remove-relationship', actionsProvider.ROLE_ITEM_ACTION, ['$rootScope','$scope', '$state', 'messages', 'names', 'security', '$q', ($rootScope, $scope, $state, messages, names, security, $q) ->
    return undefined if not $scope.element
    return undefined if not angular.isFunction($scope.element.isInstanceOf)
    return undefined if not $scope.element.isInstanceOf('relationship')
    return undefined if not security.hasRole('CURATOR')


    {
      position:   150
      label:      'Remove'
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
      label:      'Edit'
      icon:       'glyphicon glyphicon-edit'
      type:       'primary'
      action:     ->
        rel   = $scope.element
        rel.element.refresh().then (element) ->
          args = {relationshipType: rel.type, direction: rel.direction, type: 'create-new-relationship', update: true, element: element, relation: rel.relation, classification: rel.classification, metadata: angular.copy(rel.ext)}
          messages.prompt('Update Relationship', '', args).then (updated)->
            rel.ext = updated.ext
    }
  ]

  actionsProvider.registerActionInRoles 'create-new-relationship', [actionsProvider.ROLE_ITEM_ACTION], ['$scope', 'messages', 'names', 'security', ($scope, messages, names, security) ->
    return undefined if not $scope.element
    return undefined if not angular.isFunction($scope.element.isInstanceOf)
    return undefined if not $scope.element.isInstanceOf('org.modelcatalogue.core.CatalogueElement')
    return undefined if $scope.element.isInstanceOf 'dataImport'
    return undefined if not security.hasRole('CURATOR')

    action = {
      position:   200
      label:      'Create Relationship'
      icon:       'glyphicon glyphicon-link'
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


  actionsProvider.registerActionInRoles 'create-new-relationship-in-header', [actionsProvider.ROLE_LIST_HEADER_ACTION, actionsProvider.ROLE_LIST_FOOTER_ACTION], ['$scope', 'messages', 'names', 'security', 'catalogue', ($scope, messages, names, security, catalogue) ->
    return undefined if not $scope.list
    return undefined if not $scope.list.base
    return undefined if not catalogue.isInstanceOf($scope.list.itemType, 'relationship')
    return undefined if not $scope.$parent
    return undefined if not $scope.$parent.element
    return undefined if not security.hasRole('CURATOR')

    direction = if $scope.list.base?.indexOf('/incoming/') > -1 then 'destinationToSource' else 'sourceToDestination'
    relationshipType = $scope.list.base.substring($scope.list.base.lastIndexOf('/') + 1)

    action = {
      position:   200
      label:      'Add'
      icon:       'fa fa-plus-circle'
      type:       'success'
      action:     ->
        messages.prompt('Create Relationship', '', {type: 'create-new-relationship', element: $scope.$parent.element, direction: direction, relationshipTypeName: relationshipType}).catch showErrorsUsingMessages(messages)
    }

    updateAction = ->
      action.disabled = $scope.$parent.element.archived

    $scope.$parent.$watch 'element.status', updateAction
    $scope.$parent.$watch 'element.archived', updateAction

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

  actionsProvider.registerActionInRole 'validate-xsd-schema', actionsProvider.ROLE_ITEM_ACTION, [ '$scope', 'messages', 'catalogue', ($scope, messages, catalogue) ->
    return undefined if not catalogue.isInstanceOf($scope.element?.elementType, 'asset')

    {
      position:   0
      label:      'Validate XML'
      icon:       'fa fa-check-circle-o'
      type:       'default'
      action:     ->
        messages.prompt('', '', {type: 'validate-xml-by-schema', asset: $scope.element})

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
    return undefined if not $scope.element.isInstanceOf('valueDomain') and not $scope.element.isInstanceOf('mapping')

    {
      position:   -100
      label:      'Convert'
      icon:       'fa fa-long-arrow-right'
      type:       'primary'
      action:     ->
        if $scope.element.isInstanceOf('valueDomain')
          messages.prompt('', '', {type: 'convert-with-value-domain', source: $scope.element})
        else if $scope.element.isInstanceOf('mapping')
          messages.prompt('', '', {type: 'convert-with-value-domain', source: $scope.element.source, destination: $scope.element.destination})

    }
  ]

  actionsProvider.registerActionInRole 'edit-mapping', actionsProvider.ROLE_ITEM_ACTION, [ '$scope', 'messages', 'security', ($scope, messages) ->
    return undefined if not $scope.element
    return undefined if not angular.isFunction $scope.element.isInstanceOf
    return undefined if not $scope.element.isInstanceOf('mapping')

    action =  {
      position:   100
      label:      'Edit'
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

    updateDisabled =  ->
      action.disabled = not $scope.element.rule and not ($scope.element.dataType and $scope.element.dataType.isInstanceOf('enumeratedType'))


    $scope.$watch 'element.rule',     updateDisabled
    $scope.$watch 'element.dataType', updateDisabled

    updateDisabled()

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
          updateFrom $scope.element, refreshed
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

  actionsProvider.registerActionInRoles 'export', [actionsProvider.ROLE_LIST_ACTION, actionsProvider.ROLE_ITEM_ACTION, actionsProvider.ROLE_LIST_HEADER_ACTION], ['$scope', 'security', ($scope, security)->
    return undefined unless security.isUserLoggedIn()
    return undefined unless $scope.list or $scope.element
    if $scope.list
      return undefined if $scope.resource == 'import'
    if $scope.element
      return undefined if not angular.isFunction $scope.element.isInstanceOf
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
            updateFrom(batch, updated)
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
    return undefined unless $scope.list and catalogue.isInstanceOf($scope.list.itemType, 'publishedElement')

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

]