angular.module('mc.core.ui.bs.actions', ['mc.util.ui.actions']).config ['actionsProvider', (actionsProvider)->

  actionsProvider.registerAction {
    id:         'edit-catalogue-element'
    position:   100
    label:      'Edit'
    icon:       'edit'
    type:       'primary'
    condition:  ['security', 'actionContext', (security, actionContext)->
      # returning false will hide the element completely
      return false if not actionContext.element
      # returning 'disabled' will show the action but disable it (action.disabled == true)
      return false if not security.hasRole('CURATOR')
      # disable for archived and finalized elements
      return 'disabled' if actionContext.element.archived or actionContext.element?.status == 'FINALIZED'
      # always return true explicitly
      return true
    ]
    action: [ 'actionContext', 'messages', 'names', (actionContext, messages, names) ->
      messages.prompt('Edit ' + actionContext.element.elementTypeName, '', {type: 'edit-' + names.getPropertyNameFromType(actionContext.element.elementType), element: actionContext.element}).then (updated)->
        actionContext.element = updated
    ]
  }

  actionsProvider.registerAction {
    id:         'create-new-relationship'
    position:   200
    label:      'Create Relationship'
    icon:       'link'
    type:       'success'
    condition:  ['security', 'actionContext', (security, actionContext)->
      # returning false will hide the element completely
      return false if not actionContext.element
      # returning 'disabled' will show the action but disable it (action.disabled == true)
      return false if not security.hasRole('CURATOR')
      # always return true explicitly
      return true
    ]
    action: [ 'actionContext', 'messages', 'names', (actionContext, messages) ->
      messages.prompt('Create Relationship', '', {type: 'new-relationship', element: actionContext.element})
    ]
  }

  actionsProvider.registerAction {
      id:         'download-asset'
      position:   0
      label:      'Download'
      icon:       'download'
      type:       'primary'
      condition:  ['actionContext', (actionContext)->
        # returning false will hide the element completely
        return false if not actionContext.element?.downloadUrl?
        # always return true explicitly
        return true
      ]
      action: [ 'actionContext', '$window', (actionContext, $window) ->
        $window.open actionContext.element.downloadUrl, '_blank'
      ]
    }

  # actionsProvider.registerAction {}

]