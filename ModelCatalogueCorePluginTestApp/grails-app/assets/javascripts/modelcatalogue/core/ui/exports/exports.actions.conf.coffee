angular.module('modelcatalogue.core.ui.exports.actionsConf', ['mc.util.ui.actions', 'modelcatalogue.core.ui.exports.modalExport']).config (actionsProvider, names, actionRoleRegister, actionClass) ->
  'ngInject'
  Action = actionClass

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
