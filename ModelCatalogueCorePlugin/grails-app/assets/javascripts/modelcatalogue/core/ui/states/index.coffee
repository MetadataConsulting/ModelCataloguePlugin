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
  'mc.core.ui.states.mc.resource'
  'mc.core.ui.states.mc'
  'mc.core.ui.states.simple.favorites'
  'mc.core.ui.states.simple.resource.show.property'
  'mc.core.ui.states.simple.resource.show'
  'mc.core.ui.states.simple.resource.list'
  'mc.core.ui.states.simple.resource'
  'mc.core.ui.states.simple'
])

.run(['$rootScope', '$state', '$stateParams', 'messages', ($rootScope, $state, $stateParams, messages) ->
# It's very handy to add references to $state and $stateParams to the $rootScope
# so that you can access them from any scope within your applications.For example,
# <li ui-sref-active="active }"> will set the <li> // to active whenever
# 'contacts.list' or one of its decendents is active.
  $rootScope.$state = $state
  $rootScope.$stateParams = $stateParams

  $rootScope.$on 'applicationOffline', ->
    messages.error 'Application is not available at the moment, please, retry later'

  $rootScope.$on 'resourceNotFound', ->
    messages.error 'Selected resource cannot be found in the catalogue.'
    if $stateParams.resource
      $state.go 'simple.resource.list', resource: $stateParams.resource
    else
      $state.go 'landing'
])

.config([ '$modalProvider', ($modalProvider) ->
  $modalProvider.options.backdrop = 'static'
])