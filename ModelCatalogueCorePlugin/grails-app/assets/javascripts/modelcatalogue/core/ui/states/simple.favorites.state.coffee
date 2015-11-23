angular.module('mc.core.ui.states.simple.favorites', ['mc.core.ui.states.controllers.FavoritesCtrl'])

.config(['$stateProvider', ($stateProvider) ->

    $stateProvider.state 'simple.favorites', {
      views:
        "":
          templateUrl: 'modelcatalogue/core/ui/state/favorites.html'
          controller: 'mc.core.ui.states.controllers.FavoritesCtrl'

        'navbar-left@':
          template: '<contextual-menu role="list"></contextual-menu>'
          controller: 'mc.core.ui.states.controllers.FavoritesCtrl'

      url: '/favorites'

      onEnter: ['applicationTitle', (applicationTitle) ->
        applicationTitle "Favorites"
      ]

      resolve:
        user: [ 'security', 'catalogueElementResource', '$q', (security, catalogueElementResource, $q) ->
          userId = security.getCurrentUser()?.id
          return $q.reject('Please, log in!') if not userId

          catalogueElementResource('user').get(userId)
        ]


    }

])
