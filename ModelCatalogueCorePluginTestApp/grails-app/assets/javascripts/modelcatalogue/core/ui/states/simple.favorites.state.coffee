angular.module('mc.core.ui.states.simple.favorites', ['mc.core.ui.states.controllers.FavoritesCtrl'])

.config(($stateProvider) ->
  'ngInject'
  $stateProvider.state 'simple.favorites', {
    views:
      '':
        templateUrl: '/mc/core/ui/states/favourites.html'
        controller: 'mc.core.ui.states.controllers.FavoritesCtrl'

      'navbar-left@':
        template: '<contextual-menu role="list"></contextual-menu>'
        controller: 'mc.core.ui.states.controllers.FavoritesCtrl'

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
