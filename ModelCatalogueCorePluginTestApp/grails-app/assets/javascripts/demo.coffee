#= require jquery/dist/jquery
#= require bootstrap/dist/js/bootstrap
#= require angular/angular
#= require modelcatalogue/util/index
#= require modelcatalogue/core/index
#= require modelcatalogue/core/ui/index

angular.module('demo', [
  'demo.config'
  'mc.core.catalogueElementResource'
  'mc.core.ui.decoratedListTable'
  'mc.core.listEnhancer'

]).controller('demo.DemoCtrl', ['catalogueElementResource', '$scope', (catalogueElementResource, $scope)->
  $scope.resource = catalogueElementResource
  $scope.expression = '''resource('dataElement').list()'''

  $scope.show = () ->
    listFun = $scope.$eval($scope.expression)
    if listFun? and listFun.then?
      listFun.then (result) ->
        $scope.list = result


  $scope.columns = [
    {header: 'Code', value: 'code', classes: 'col-md-2'}
    {header: 'Name', value: 'name', classes: 'col-md-4'}
    {header: 'Description', value: 'description', classes: 'col-md-6'}
  ]

  $scope.removeColumn = (index) ->
    return if $scope.columns.length <= 1
    $scope.columns.splice(index, 1)

  $scope.addColumn = (index, column = {header: 'ID', value: 'id', classes: 'col-md-2'}) ->
    $scope.columns.splice(index + 1, 0, angular.copy(column))

  $scope.show()
])