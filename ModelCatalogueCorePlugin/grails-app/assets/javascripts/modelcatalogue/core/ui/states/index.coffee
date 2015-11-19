angular.module('mc.core.ui.states', [
  # depends on
  'mc.util'
  'mc.core.ui'
  'mc.core.ui.states.controllers'
  # list of modules
  'mc.core.ui.states.landing'
  'mc.core.ui.states.mc.actions.show'
  'mc.core.ui.states.mc.actions'
  'mc.core.ui.states.mc.csvTransformations.show'
  'mc.core.ui.states.mc.csvTransformations'
  'mc.core.ui.states.mc.dataArchitect.findRelationsByMetadataKeys'
  'mc.core.ui.states.mc.dataArchitect.metadataKey'
  'mc.core.ui.states.mc.dataArchitect.metadataKeyCheck'
  'mc.core.ui.states.mc.dataArchitect.showMetadataRelations'
  'mc.core.ui.states.mc.dataArchitect'
  'mc.core.ui.states.mc.favorites'
  'mc.core.ui.states.mc.resource.diff'
  'mc.core.ui.states.mc.resource.list'
  'mc.core.ui.states.mc.resource.show.property'
  'mc.core.ui.states.mc.resource.show'
  'mc.core.ui.states.mc.resource'
  'mc.core.ui.states.mc.resource.uuid'
  'mc.core.ui.states.mc.search'
  'mc.core.ui.states.mc'
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
      $state.go 'mc.resource.list', resource: $stateParams.resource
    else
      $state.go 'landing'
])

.config(['$provide', ($provide) ->
  fixStateParams = (state, params) ->
    return params if not state

    stateName = if angular.isString(state) then state else state.name

    return params if not state

    if stateName?.startsWith 'mc'
      return {dataModelId: 'catalogue' } if not params

      if not params.dataModelId
        params.dataModelId = 'catalogue'

    return params


  $provide.decorator('$state', ['$delegate', ($delegate) ->
    originalHref = $delegate.href

    $delegate.href = (stateOrName, params, options) ->
      return originalHref(stateOrName, fixStateParams(stateOrName, params), options)


    originalTransitionTo = $delegate.transitionTo

    $delegate.transitionTo = (to, toParams, options) ->
      return originalTransitionTo(to, fixStateParams(to, toParams), options)

    $delegate

  ])

])

# keep track of the data model used
.run(['$rootScope', 'catalogueElementResource', ($rootScope, catalogueElementResource) ->
  $rootScope.$on '$stateChangeStart', (event, toState, toParams, fromState, fromParams) ->
    if toParams.dataModelId isnt fromParams.dataModelId or (toParams.dataModelId and not $rootScope.currentDataModel)
      if not toParams.dataModelId or toParams.dataModelId is 'catalogue'
        $rootScope.currentDataModel = undefined
        $rootScope.$broadcast 'redrawContextualActions'
        return
      if toParams.dataModelId isnt $rootScope.currentDataModel?.id?.toString
        catalogueElementResource('dataModel').get(toParams.dataModelId).then (dataModel) ->
          $rootScope.currentDataModel = dataModel
          $rootScope.$broadcast 'redrawContextualActions'
])

.config([ '$modalProvider', ($modalProvider) ->
  $modalProvider.options.backdrop = 'static'
])