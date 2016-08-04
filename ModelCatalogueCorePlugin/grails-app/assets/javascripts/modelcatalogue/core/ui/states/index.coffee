angular.module('mc.core.ui.states', [
  # depends on
  'mc.util'
  'mc.core.ui'
  'mc.core.ui.states.controllers'
  # list of modules
  'mc.core.ui.states.landing'
  'mc.core.ui.states.dataModels'
  'mc.core.ui.states.simple.actions.show'
  'mc.core.ui.states.simple.actions'
  'mc.core.ui.states.mc.csvTransformations.show'
  'mc.core.ui.states.mc.csvTransformations'
  'mc.core.ui.states.mc.search'
  'mc.core.ui.states.mc.resource.diff'
  'mc.core.ui.states.mc.resource.show.property'
  'mc.core.ui.states.mc.resource.show'
  'mc.core.ui.states.mc.resource.list'
  'mc.core.ui.states.mc.resource.list-imported'
  'mc.core.ui.states.mc.resource.xml-editor'
  'mc.core.ui.states.mc.resource'
  'mc.core.ui.states.mc'
  'mc.core.ui.states.simple.favorites'
  'mc.core.ui.states.simple.feedback'
  'mc.core.ui.states.simple.resource.show.property'
  'mc.core.ui.states.simple.resource.show'
  'mc.core.ui.states.simple.resource.list'
  'mc.core.ui.states.simple.resource'
  'mc.core.ui.states.simple'
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
