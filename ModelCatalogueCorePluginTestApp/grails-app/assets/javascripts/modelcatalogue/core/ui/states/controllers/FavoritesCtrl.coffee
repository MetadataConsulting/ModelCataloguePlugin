angular.module('mc.core.ui.states.controllers.FavoritesCtrl', ['ui.router', 'mc.util.ui']).controller('mc.core.ui.states.controllers.FavoritesCtrl', [
  '$scope', 'modelCatalogueApiRoot', 'user', 'enhance', 'rest', 'columns', 'actionRoleAccess',
  ($scope ,  modelCatalogueApiRoot ,  user ,  enhance ,  rest ,  columns, actionRoleAccess) ->

    $scope.title = 'Favourites'
    $scope.user = user
    $scope.actionRoleAccess = actionRoleAccess

    listEnhancer = enhance.getEnhancer('list')
    $scope.list = listEnhancer.createEmptyList(base: "/user/#{user.id}/outgoing/favourite")

    $scope.columns = columns()

    enhance(rest(url: "#{modelCatalogueApiRoot}#{user.link}/outgoing/favourite")).then (list)->
      $scope.list = list

])
