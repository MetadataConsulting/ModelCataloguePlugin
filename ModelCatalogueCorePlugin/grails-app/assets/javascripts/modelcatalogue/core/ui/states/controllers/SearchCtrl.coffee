angular.module('mc.core.ui.states.controllers.SearchCtrl', ['ui.router', 'mc.util.ui']).controller('mc.core.ui.states.controllers.SearchCtrl', [
  'catalogueElementResource', 'modelCatalogueSearch', '$scope', '$rootScope', '$q', '$state', 'names', 'messages', 'actions', 'modelCatalogueApiRoot', '$http', 'enhance'
  (catalogueElementResource ,  modelCatalogueSearch ,  $scope, $rootScope, $q, $state, names, messages, actions, modelCatalogueApiRoot, $http, enhance)->
    actions = []

    $scope.search = (item, model, label) ->
      if angular.isString(item)
        $state.go('mc.search', {q: model })
      else
        item?.action item, model, label

    $scope.clearSelection = ->
      $state.searchSelect = undefined
      $rootScope.$stateParams.q = undefined
      $state.go('.', {q: undefined })

    initActions = ->
      actions = []
      actions.push {
        condition: (term) -> term
        label: (term) ->
          "Search <strong>Catalogue Element</strong> for <strong>#{term}</strong>"

        action: (term) -> ->
          $state.go('mc.search', {q: term})

        icon: 'fa fa-fw fa-search'
      }

      actions.push {
        condition: (term) -> term and $state.$current.params.hasOwnProperty('q') and $state.params.resource
        label: (term) ->
          naturalName = names.getNaturalName($state.params.resource)
          "Search any <strong>#{naturalName}</strong> for <strong>#{term}</strong>"
        action: (term) ->
          ->
            $state.go('mc.resource.list', {q: term})
        icon: 'fa fa-fw fa-search'
      }

      actions.push {
        condition: (term) ->
          term and $state.current.name == 'mc.resource.show.property' and  $state.$current.params.hasOwnProperty('q') and $rootScope.$$searchContext
        label: (term) ->
          "Search current <strong>#{$rootScope.$$searchContext}</strong> for <strong>#{term}</strong>"
        action: (term) ->
          ->
            $state.go('mc.resource.show.property', {q: term})
        icon: 'fa fa-fw fa-search'
      }

      actions.push {
        condition: -> true
        label: (term) ->
          if $rootScope.elementToShow?.isInstanceOf('dataType') and $rootScope.elementToShow?.rule
            "Validate <strong>#{term}</strong> by <strong>#{$rootScope.elementToShow.name}</strong>"
          else
            "Validate <strong>#{term}</strong>"

        action: (term) ->
          ->
            messages.prompt('', '', {type: 'validate-value-by-domain', value: term, domainHint: if $rootScope.elementToShow?.rule then $rootScope.elementToShow else undefined})
        icon: 'fa fa-fw fa-check-circle-o'
      }

      actions.push {
        condition: -> true
        label: (term) ->
          if $rootScope.elementToShow?.isInstanceOf('dataType') and $rootScope.elementToShow?.mappings?.total > 0
            "Convert <strong>#{term}</strong> from <strong>#{$rootScope.elementToShow.name}</strong>"
          else
            "Convert <strong>#{term}</strong>"
        action: (term) ->
          ->
            messages.prompt('', '', {type: 'convert-with-value-domain', value: term, sourceHint: if $rootScope.elementToShow?.mappings?.total > 0 then $rootScope.elementToShow else undefined})
        icon: 'fa fa-fw fa-long-arrow-right'
      }

    $scope.getResults = (term) ->
      deferred = $q.defer()

      results = []

      return if not term

      for action in actions when action.condition(term)
        results.push {
          label:  action.label(term)
          action: action.action(term)
          icon:   action.icon
          term:   term
        }

      deferred.notify results

      if term
        params = {}
        params.dataModel = $state.params.dataModelId if $state.params.dataModelId and $state.params.dataModelId != 'catalogue'
        p1 = modelCatalogueSearch(term, params).then (searchResults)->
          for searchResult in searchResults.list
            results.push {
              label:      if searchResult.getLabel then searchResult.getLabel() else searchResult.name
              action:     searchResult.show
              icon:       if searchResult.getIcon  then searchResult.getIcon()  else 'glyphicon glyphicon-file'
              term:       term
              highlight:  true
              element:    searchResult
            }

        p2 = $q.when true

        if term.match(/^\d+$/)
          p2 = $http.get("#{modelCatalogueApiRoot}/catalogueElement/#{term}").then (result) ->
            return unless result.data?.elementType
            searchResult = enhance result.data
            results.push {
              label:      if searchResult.getLabel then searchResult.getLabel() else searchResult.name
              action:     searchResult.show
              icon:       if searchResult.getIcon  then searchResult.getIcon()  else 'glyphicon glyphicon-file'
              term:       term
              highlight:  true
              element:    searchResult
            }
          , -> true

        $q.all([p1, p2]).then ->
          deferred.resolve results
      else
        deferred.resolve results

      deferred.promise

    initActions()

    $scope.$on '$stateChangeSuccess', (event, toState, toParams) ->
      $scope.searchSelect = toParams.q

])