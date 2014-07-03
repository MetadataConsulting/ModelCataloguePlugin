angular.module('mc.core.ui.bs.actions', ['mc.util.ui.actions']).config ['actionsProvider', (actionsProvider)->

  actionsProvider.registerAction {
    id:         'edit-catalogue-element'
    position:   100
    label:      'Edit'
    icon:       'edit'
    type:       'primary'
    condition:  ['security', '$scope', (security, $scope)->
      # returning false will hide the element completely
      return false if not $scope.element
      # returning 'disabled' will show the action but disable it (action.disabled == true)
      return false if not security.hasRole('CURATOR')
      # disable for archived and finalized elements
      return 'disabled' if $scope.element.archived or $scope.element?.status == 'FINALIZED'
      # always return true explicitly
      return true
    ]
    action: [ '$scope', 'messages', 'names', ($scope, messages, names) ->
      messages.prompt('Edit ' + $scope.element.elementTypeName, '', {type: 'edit-' + names.getPropertyNameFromType($scope.element.elementType), element: $scope.element}).then (updated)->
        $scope.element = updated
    ]
  }

  actionsProvider.registerAction {
    id:         'create-new-relationship'
    position:   200
    label:      'Create Relationship'
    icon:       'link'
    type:       'success'
    condition:  ['security', '$scope', (security, $scope)->
      # returning false will hide the element completely
      return false if not $scope.element
      # returning 'disabled' will show the action but disable it (action.disabled == true)
      return false if not security.hasRole('CURATOR')
      # always return true explicitly
      return true
    ]
    action: [ '$scope', 'messages', 'names', ($scope, messages) ->
      messages.prompt('Create Relationship', '', {type: 'new-relationship', element: $scope.element})
    ]
  }

  actionsProvider.registerAction {
      id:         'download-asset'
      position:   0
      label:      'Download'
      icon:       'download'
      type:       'primary'
      condition:  ['$scope', ($scope)->
        # returning false will hide the element completely
        return false if not $scope.element?.downloadUrl?
        # always return true explicitly
        return true
      ]
      action: [ '$scope', '$window', ($scope, $window) ->
        $window.open $scope.element.downloadUrl, '_blank'; return true
      ]
  }

  actionsProvider.registerAction {
    id:         'catalogue-element-export'
    position:   1000
    label:      'Export'
    icon:       'download-alt'
    type:       'primary'
    condition:  ['$scope', ($scope)->
      # returning false will hide the element completely
      return false if not $scope.element
      # always return true explicitly
      return true
    ]
  }

  actionsProvider.registerAction {
    id: 'catalogue-element-export-specific-reports'
    parent: 'catalogue-element-export'
    position: 1000

    label: ['$scope', ($scope)->
      "#{$scope.element.name} Reports"
    ]

    condition: ['$scope', ($scope)->
      return false      if not $scope.element
      return 'disabled' if not $scope.element?.availableReports?.length
      return true
    ]

    generator: ['$scope', '$window', 'action', '$filter', 'headingAction', ($scope, $window, action, $filter, headingAction)->
      updateReports = (reports)->

        ret = $filter('filter')(action.children, (cha) -> cha.generatedBy != 'catalogue-element-export-specific-reports')

        added = 0

        for report, i in reports
          added++
          ret.push {
            generatedBy: 'catalogue-element-export-specific-reports'
            label:  report.title
            position: 1001 + i
            run:    -> $window.open(report.url, '_blank') ; return true
          }
        ret
        action.children = ret
        action.sortChildren()
        headingAction.disabled = added == 0

      $scope.$watch 'element.availableReports', updateReports


      updateReports($scope.element.availableReports)
    ]

  }

  actionsProvider.registerAction {
    id:         'catalogue-element-export-generic-reports'
    parent:     'catalogue-element-export'
    position:   2000

    label:      "Other Reports"

    condition:  ['$scope', ($scope)->
      # returning false will hide the element completely
      return 'disabled' if not $scope.reports?.length
      # always return true explicitly
      return true
    ]

    generator: ['$scope', '$window', 'action', '$filter', 'headingAction', ($scope, $window, action, $filter, headingAction)->
      updateReports = (reports)->
        ret = $filter('filter')(action.children, (cha) -> cha.generatedBy != 'catalogue-element-export-generic-reports')
        added = 0
        for report, i in reports
          added++
          ret.push {
            generatedBy: 'catalogue-element-export-generic-reports'
            label:  report.title
            position: 2001 + i
            run:    -> $window.open(report.url, '_blank') ; return true
          }
        ret
        action.children = ret
        action.sortChildren()
        headingAction.disabled = added == 0

      $scope.$watch 'reports', updateReports


      updateReports($scope.reports)
    ]

  }

  # actionsProvider.registerAction {}

]