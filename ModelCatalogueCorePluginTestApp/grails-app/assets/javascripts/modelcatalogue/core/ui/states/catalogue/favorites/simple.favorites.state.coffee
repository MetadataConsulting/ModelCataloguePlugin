angular.module('mc.core.ui.states.simple.favorites', ['modelcatalogue.core.ui.states.controllers.FavoritesCtrl',
  'modelcatalogue.core.ui.states.components.infiniteTable'])

.config(($stateProvider) ->
  'ngInject'
  $stateProvider.state 'simple.favorites', {
    views:
      '':
        templateUrl: '/mc/core/ui/states/favourites.html'
        controller: 'modelcatalogue.core.ui.states.controllers.FavoritesCtrl'

      'navbar-left@':
        template: '<contextual-menu role="{{::actionRoleAccess.ROLE_LIST_ACTION}}"></contextual-menu>'
        controller: 'modelcatalogue.core.ui.states.controllers.FavoritesCtrl'

    url: '/favourites'

    onEnter: (applicationTitle) ->
      'ngInject'
      applicationTitle "Favourites"

    resolve:
      user: (security, catalogueElementResource, $q) ->
        'ngInject'
        userId = security.getCurrentUser()?.id
        return $q.reject('Please, log in!') if not userId

        catalogueElementResource('user').get(userId)
  })
