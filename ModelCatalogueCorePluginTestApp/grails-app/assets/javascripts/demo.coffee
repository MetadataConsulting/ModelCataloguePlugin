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
  'mc.core.modelCatalogueSearch'

]).controller('demo.DemoCtrl', ['catalogueElementResource', 'modelCatalogueSearch', '$scope', (catalogueElementResource, modelCatalogueSearch, $scope)->
  $scope.listResource     = """resource("dataElement").list()"""
  $scope.listRelTypes     = """resource("relationshipType").list()"""
  $scope.searchSomething  = """search("patient")"""
  $scope.searchModel      = """resource("model").search("patient")"""

  $scope.valueDomainColumns = [
    {header: 'Code', value: 'code', classes: 'col-md-2'}
    {header: 'Name', value: 'name', classes: 'col-md-4'}
    {header: 'Description', value: 'description', classes: 'col-md-6'}
  ]

  $scope.idAndNameColumns = [

    {header: 'Type', value: 'elementTypeName', classes: 'col-md-5'}
    {header: 'ID', value: 'id', classes: 'col-md-2'}
    {header: 'Name', value: 'name', classes: 'col-md-5'}
  ]

  $scope.relationshipTypeColumns = [
    {header: 'Name', value: 'name', classes: 'col-md-2'}
    {header: 'Source to Destination', value: 'sourceToDestination', classes: 'col-md-2'}
    {header: 'Destination to Source', value: 'destinationToSource', classes: 'col-md-2'}
    {header: 'Source Class', value: 'sourceClass', classes: 'col-md-3'}
    {header: 'Destination Class', value: 'destinationClass', classes: 'col-md-3'}
  ]

  $scope.resource         = catalogueElementResource
  $scope.search           = modelCatalogueSearch
  $scope.expression       = $scope.listResource

  $scope.show = () ->
    listFun = $scope.$eval($scope.expression)
    if listFun? and listFun.then?
      listFun.then (result) ->
        $scope.list = result


  $scope.columns = $scope.valueDomainColumns

  $scope.removeColumn = (index) ->
    return if $scope.columns.length <= 1
    $scope.columns.splice(index, 1)

  $scope.addColumn = (index, column = {header: 'ID', value: 'id', classes: 'col-md-2'}) ->
    $scope.columns.splice(index + 1, 0, angular.copy(column))


  $scope.show()
])