angular.module('mc.core.ui.states.controllers.FavoritesCtrl', ['ui.router', 'mc.util.ui']).controller('mc.core.ui.states.controllers.FavoritesCtrl', [
  '$scope', 'modelCatalogueApiRoot', 'user', 'enhance', 'rest', 'columns',
  ($scope ,  modelCatalogueApiRoot ,  user ,  enhance ,  rest ,  columns) ->

    $scope.title = 'Favorites'
    $scope.user = user

    listEnhancer = enhance.getEnhancer('list')
    $scope.list = angular.extend(listEnhancer.createEmptyList(), base: "/user/#{user.id}/outgoing/favourite")

    $scope.columns = columns()

    enhance(rest(url: "#{modelCatalogueApiRoot}#{user.link}/outgoing/favourite")).then (list)->
      $scope.list = list

])