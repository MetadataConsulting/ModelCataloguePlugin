angular.module('mc.core.ui.states', [
  # depends on
  'mc.util'
  'mc.core.ui'
  'modelcatalogue.core.ui.states.controllers'
  # list of modules
  'modelcatalogue.core.ui.states.landing'
  'modelcatalogue.core.ui.states.dataModels'
  'modelcatalogue.core.ui.states.dataModel'
  'modelcatalogue.core.ui.states.catalogue'
])

.run(($rootScope, $log, $state, $stateParams, messages) ->
  'ngInject'
  # It's very handy to add references to $state and $stateParams to the $rootScope
  # so that you can access them from any scope within your applications.For example,
  # <li ui-sref-active="active }"> will set the <li> // to active whenever
  # 'contacts.list' or one of its decendents is active.
  $rootScope.$state = $state
  $rootScope.$stateParams = $stateParams

  $rootScope.$on 'applicationOffline', ->
    messages.error 'Application is not available at the moment, please, retry later'

  $rootScope.$on 'resourceNotFound', (ignored, config) ->
    # let's keep it simple just showing the user the resource does not exist
    messages.error 'Selected resource cannot be found in the catalogue', config.url

  $rootScope.$on '$stateChangeStart', (event, toState, toParams, fromState) ->
    $log.debug "ui.router state change start from state [#{fromState.name}] to [#{toState.name}]"

  $rootScope.$on '$stateNotFound', (event, unfoundState, fromState) ->
    $log.warn "ui.router state not found [#{unfoundState}] going from state [#{fromState}]"

  $rootScope.$on '$stateChangeSuccess', (event, toState, toParams, fromState) ->
    $log.debug "ui.router state change success from state [#{fromState.name}] to [#{toState.name}]"

  $rootScope.$on '$stateChangeError', (event, toState, toParams, fromState, fromParams, error) ->
    $log.error "ui.router state change error when changing from state [#{fromState.name}] to [#{toState.name}]", error
)

.config(($urlRouterProvider, $uibModalProvider) ->
  'ngInject'
  # default url
  $urlRouterProvider.otherwise('/');

  $uibModalProvider.options.backdrop = 'static'
)
