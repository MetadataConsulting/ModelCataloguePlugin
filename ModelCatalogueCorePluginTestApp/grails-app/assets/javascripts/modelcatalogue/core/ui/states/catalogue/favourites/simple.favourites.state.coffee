angular.module('modelcatalogue.core.ui.states.catalogue.favourites', ['modelcatalogue.core.ui.states.controllers.FavouritesCtrl',
  'modelcatalogue.core.ui.states.components.infiniteTable'])

.config(($stateProvider) ->
  'ngInject'
  $stateProvider.state 'simple.favourites', {
    views:
      '':
        templateUrl: '/modelcatalogue/core/ui/states/catalogue/favourites/favourites.html'
        controller: 'modelcatalogue.core.ui.states.controllers.FavouritesCtrl'

      'navbar-left@':
        template: '<contextual-menu role="{{::actionRoleAccess.ROLE_LIST_ACTION}}"></contextual-menu>'
        controller: 'modelcatalogue.core.ui.states.controllers.FavouritesCtrl'

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
