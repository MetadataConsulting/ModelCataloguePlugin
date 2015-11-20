angular.module('mc.core.ui.states.simple.favorites', ['mc.core.ui.states.controllers.FavoritesCtrl'])

.config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'simple.favorites', {
      url: '/favorites'
      templateUrl: 'modelcatalogue/core/ui/state/favorites.html'
      onEnter: ['applicationTitle', (applicationTitle) ->
        applicationTitle "Favorites"
      ]
      resolve:
        user: [ 'security', 'catalogueElementResource', '$q', (security, catalogueElementResource, $q) ->
          userId = security.getCurrentUser()?.id
          return $q.reject('Please, log in!') if not userId

          catalogueElementResource('user').get(userId)
        ]
      controller: 'mc.core.ui.states.controllers.FavoritesCtrl'

    }

])
