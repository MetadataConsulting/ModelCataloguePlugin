angular.module('modelcatalogue.core.ui.bs.actionsConf.navigationActions', ['mc.util.ui.actions', 'mc.util.security'])
.config (actionsProvider, names, actionRoleRegister, actionClass)->
  'ngInject'
  Action = actionClass
  ##############
  # Data Model #
  ##############

  # Import
  actionsProvider.registerChildAction 'catalogue-element', 'add-import', ($scope, messages, names, security, catalogue) ->
    'ngInject'
    return undefined unless $scope.element?.isInstanceOf?('dataModel')
    return undefined unless security.hasRole('CURATOR')
    Action.createStandardAction(
      position:   -1000
      label:      'Add Data Model Import'
      icon:       'fa fa-fw fa-puzzle-piece'
      type:       'success'
      action:     ->
        messages.prompt('Add Data Model Import', 'If you want to reuse data classes, data types or measurement units ' +
            'form different data models you need to import the containing data model first.',
          {type: 'catalogue-elements', resource: 'dataModel' }).then (elements) ->
            angular.forEach elements, (element) ->
              unless angular.isString(element)
                $scope.element.imports.add element
    )

  # Create
  angular.forEach [
    'dataClass'
    'dataElement'
    'dataType'
    'measurementUnit'
    'asset'
    'validationRule'
  ], (resource, index) ->
    actionsProvider.registerChildAction 'catalogue-element', 'catalogue-element-create-' + resource,
      ($scope, names, security, messages, $state, $log, catalogue, dataModelService) ->
        'ngInject'
        dataModel = dataModelService.anyParentDataModel($scope)
        return undefined unless security.hasRole('CURATOR')
        return undefined unless messages.hasPromptFactory('create-' + resource) or
          messages.hasPromptFactory('edit-' + resource)
        return undefined unless $scope.element?.isInstanceOf?('dataModel') or resource is 'dataClass' and $scope.element?.isInstanceOf?('dataClass')

        Action.createStandardAction(
          position: 5000 + index
          label: "New #{names.getNaturalName(resource)}"
          icon: catalogue.getIcon(resource)
          type: 'success'
          action:     ->
            args =
              create: resource
              currentDataModel: dataModel

            args.parent = $scope.element if resource is 'dataClass' and $scope.element.isInstanceOf('dataClass')
            args.type = if messages.hasPromptFactory('create-' + resource) then "create-#{resource}" else "edit-#{resource}"

            security.requireRole('CURATOR')
            .then ->
              messages.prompt('Create ' + names.getNaturalName(resource), '', args).then ->
                $state.go 'dataModel.resource.list', {status: 'draft'}, {reload: true}
            , (errors)->
              $log.error errors
              messages.error('You don\'t have rights to create new elements')
        )
          .disabledIf $scope.element.status != 'DRAFT'
