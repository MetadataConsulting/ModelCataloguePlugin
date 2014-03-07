#= require jquery/dist/jquery
#= require angular/angular
#= require_tree modelcatalogue

angular.module('demo', [
  'demo.config'
  'mc.core.catalogueElementResource'
  'mc.core.ui.decoratedListTable'
  'mc.core.listEnhancer'

]).controller('demo.DemoCtrl', ['catalogueElementResource', '$scope', (catalogueElementResource, $scope)->
  resource = catalogueElementResource('valueDomain')
  resource.list().then (unitsList) ->
    $scope.list = unitsList

  $scope.list =
      list: []
      next: {size: 0}
      previous: {size: 0}

  $scope.columns = [
    {header: 'ID', value: 'id'}
    {header: 'Name', value: (element) -> element.name }
    {header: 'Description', value: (element) -> element.description }
  ]
])