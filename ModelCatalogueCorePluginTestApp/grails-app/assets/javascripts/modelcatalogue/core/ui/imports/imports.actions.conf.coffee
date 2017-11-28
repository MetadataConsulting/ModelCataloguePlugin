angular.module('modelcatalogue.core.ui.imports.actionsConf', ['mc.util.ui.actions', 'mc.util.security']).config ['actionsProvider', 'names', 'actionRoleRegister', 'actionClass', (actionsProvider, names, actionRoleRegister, actionClass)->
  Action = actionClass
  actionsProvider.registerActionInRole 'new-import', actionRoleRegister.ROLE_LIST_ACTION, [
    '$scope', 'names','security', '$state',
    ($scope ,  names , security ,  $state ) ->
      return undefined unless security.hasRole('CURATOR')
      return undefined unless $state.current.name == 'dataModel.resource.list'
      return undefined unless $scope.resource == 'asset'

      Action.createAbstractAction(
        position: 10000
        label: "Import"
        icon: 'fa fa-upload'
        type: 'success'
      )
  ]


  loincImport = ($scope, messages, security) ->
    'ngInject'
    return undefined unless security.hasRole('CURATOR')

    Action.createDefaultTypeAction(
      position: 13001
      label: "Import Loinc"
      icon:  'fa fa-upload fa-fw'
      action: ->
        messages.prompt('Import Loinc File', '', type: 'new-loinc-import')
    )
  if false # No longer provide LOINC import because it is only half-implemented and we don't use it anyway
    actionsProvider.registerChildAction 'new-import', 'import-loinc', loincImport
    actionsProvider.registerChildAction 'import-data-models-screen', 'import-loinc', loincImport
    actionsProvider.registerChildAction 'curator-menu', 'import-loinc', loincImport
    actionsProvider.registerActionInRole 'global-import-loinc', actionRoleRegister.ROLE_GLOBAL_ACTION, loincImport

  excelImport = ($scope, messages, security) ->
    'ngInject'
    return undefined unless security.hasRole('CURATOR')

    Action.createDefaultTypeAction(
      position: 13002
      label:  "Import Excel"
      icon:  'fa fa-upload fa-fw'
      action: ->
        messages.prompt('Import Excel File', '', type: 'new-excel-import')
    )

  actionsProvider.registerChildAction 'new-import', 'import-excel', excelImport
  actionsProvider.registerChildAction 'import-data-models-screen', 'import-excel', excelImport
  actionsProvider.registerChildAction 'curator-menu', 'import-excel', excelImport
  actionsProvider.registerActionInRole 'global-import-excel', actionRoleRegister.ROLE_GLOBAL_ACTION, excelImport

  oboImport = ($scope, messages, security) ->
    'ngInject'
    return undefined unless security.hasRole('CURATOR')
    Action.createDefaultTypeAction(
      position: 13003
      label: "Import OBO"
      icon:  'fa fa-upload fa-fw'
      action: ->
        messages.prompt('Import OBO File', '', type: 'new-obo-import')
    )
  actionsProvider.registerChildAction 'new-import', 'import-obo', oboImport
  actionsProvider.registerChildAction 'import-data-models-screen', 'import-obo', oboImport
  actionsProvider.registerChildAction 'curator-menu', 'import-obo', oboImport
  actionsProvider.registerActionInRole 'global-import-obo', actionRoleRegister.ROLE_GLOBAL_ACTION, oboImport

  umlImport = ($scope, messages, security) ->
    'ngInject'
    return undefined unless security.hasRole('CURATOR')
    Action.createDefaultTypeAction(
      position: 13004
      label: "Import Star Uml"
      icon:  'fa fa-upload fa-fw'
      action: ->
        messages.prompt('Import Star Uml File', '', type: 'new-umlj-import')
    )
  if false # No longer provide Star UML import as it is an old thing from days of collaboration with Oxford
    actionsProvider.registerChildAction 'new-import', 'import-umlj', umlImport
    actionsProvider.registerChildAction 'import-data-models-screen', 'import-umlj', umlImport
    actionsProvider.registerChildAction 'curator-menu', 'import-umlj', umlImport
    actionsProvider.registerActionInRole 'global-import-uml', actionRoleRegister.ROLE_GLOBAL_ACTION, umlImport

  mcImport = ($scope, messages, security) ->
    'ngInject'
    return undefined unless security.hasRole('CURATOR')
    Action.createDefaultTypeAction(
      position: 13005
      label: "Import Model Catalogue DSL File"
      icon:  'fa fa-upload fa-fw'
      action: ->
        messages.prompt('Import Model Catalogue DSL File', '', type: 'new-mc-import')
    )
  actionsProvider.registerChildAction 'new-import', 'import-mc', mcImport
  actionsProvider.registerChildAction 'import-data-models-screen', 'import-mc', mcImport
  actionsProvider.registerChildAction 'curator-menu', 'import-mc', mcImport
  actionsProvider.registerActionInRole 'global-import-mc', actionRoleRegister.ROLE_GLOBAL_ACTION, mcImport

  xmlImport = ($scope, messages, security) ->
    'ngInject'
    return undefined unless security.hasRole('CURATOR')
    Action.createDefaultTypeAction(
      position: 13006
      label: "Import Catalogue XML"
      icon:  'fa fa-upload fa-fw'
      action: ->
        messages.prompt('Import Model Catalogue XML File', '', type: 'new-catalogue-xml-import')
    )
  actionsProvider.registerChildAction 'new-import', 'import-catalogue-xml', xmlImport
  actionsProvider.registerChildAction 'import-data-models-screen', 'import-catalogue-xml', xmlImport
  actionsProvider.registerChildAction 'curator-menu', 'import-catalogue-xml', xmlImport
  actionsProvider.registerActionInRole 'global-import-xml', actionRoleRegister.ROLE_GLOBAL_ACTION, xmlImport

  rareDiseaseCsvImport = ($scope, messages, security) ->
    'ngInject'
    return undefined unless security.hasRole('CURATOR')
    Action.createDefaultTypeAction(
      position: 13007
      label: "Import Rare Disease Csv"
      icon:  'fa fa-upload fa-fw'
      action: ->
        messages.prompt('Import Rare Disease Csv File', '', type: 'new-rare-disease-csv-import')
    )

  if false # No longer provide Rare Disease CSV Import function
    actionsProvider.registerChildAction 'new-import', 'rare-disease-csv', rareDiseaseCsvImport
    actionsProvider.registerChildAction 'import-data-models-screen', 'rare-disease-csv', rareDiseaseCsvImport
    actionsProvider.registerChildAction 'curator-menu', 'rare-disease-csv', rareDiseaseCsvImport
    actionsProvider.registerActionInRole 'global-import-csv', actionRoleRegister.ROLE_GLOBAL_ACTION, rareDiseaseCsvImport

  actionsProvider.registerActionInRole 'connected', actionRoleRegister.ROLE_NAVIGATION_RIGHT_ACTION, ($rootScope, messages, $window) ->
    'ngInject'
    if $rootScope.$$disconnected
      return Action.createDefaultTypeAction(
        position:   -10000000
        label:      'Application is no longer receiving real-time updates from the server. Please, reload the page to reconnect.'
        icon:       'fa fa-exclamation-triangle text-danger fa-fw fa-2x-if-wide'
        action:     ->
          messages.confirm(
            'Application Disconnected',
            'Application is disconnected and no longer accepts real-time updates from the server. Do you want to reload current page? All unsaved progress will be lost.'
          ).then -> $window.location.reload()
      ).withIconOnly()
]
