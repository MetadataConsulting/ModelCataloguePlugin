#= require jquery/dist/jquery
#= require bootstrap/dist/js/bootstrap
#= require angular/angular
#= require_tree modelcatalogue

angular.module('demo', [
  'demo.config'
  'mc.core.catalogueElementResource'
  'mc.core.ui.decoratedListTable'
  'mc.core.listEnhancer'

]).controller('demo.DemoCtrl', ['catalogueElementResource', '$scope', (catalogueElementResource, $scope)->
  resource = catalogueElementResource('measurementUnit')
  resource.list().then (unitsList) ->
    $scope.list = unitsList

  $scope.columns = [
    {header: 'ID', value: 'id', classes: 'col-md-3'}
    {header: 'Name', classes: 'col-md-9', value: (element) -> element.name }
  ]
])