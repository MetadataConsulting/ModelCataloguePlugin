angular.module('modelcatalogue.core.ui.bs.actionsConf.navigationActions', ['mc.util.ui.actions', 'mc.util.security'])
.config (actionsProvider, names, actionRoleRegister, actionClass)->
  'ngInject'
  Action = actionClass
  ##############
  # Data Model #
  ##############
