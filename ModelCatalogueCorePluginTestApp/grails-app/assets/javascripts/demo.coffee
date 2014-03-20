#= require jquery/dist/jquery
#= require bootstrap/dist/js/bootstrap
#= require angular/angular
#= require modelcatalogue/util/index
#= require modelcatalogue/core/index
#= require modelcatalogue/core/ui/index
#= require modelcatalogue/core/ui/bs/index
#= require modelcatalogue/core/ui/bs/elementViews/index

angular.module('demo', [
  'demo.config'
  'mc.core.ui.bs'
  'ui.bootstrap'

]).controller('demo.DemoCtrl', ['catalogueElementResource', 'modelCatalogueSearch', '$scope', '$log', '$q', (catalogueElementResource, modelCatalogueSearch, $scope, $log, $q)->
  emptyList =
    list: []
    next: {size: 0}
    previous: {size: 0}
    total: 0
    empty: true
    source: 'demo'


  $scope.listResource     = """resource("dataElement").list()"""
  $scope.listRelTypes     = """resource("relationshipType").list()"""
  $scope.searchSomething  = """search("patient")"""
  $scope.searchModel      = """resource("model").search("patient")"""
  $scope.outgoing         = """resource("dataElement").list() >>> $r.list[0].outgoingRelationships()"""
  $scope.indicator        = """resource("dataElement").search("NHS_NUMBER_STATUS_INDICATOR_CODE") >>> $r.list[0]"""

  $scope.valueDomainColumns = () -> [
    {header: 'Code', value: 'code', classes: 'col-md-2', show: true}
    {header: 'Name', value: 'name', classes: 'col-md-4', show: true}
    {header: 'Description', value: 'description', classes: 'col-md-6'}
  ]

  $scope.idAndNameColumns = () -> [

    {header: 'Type', value: 'elementTypeName', classes: 'col-md-5'}
    {header: 'ID', value: 'id', classes: 'col-md-2', show: true}
    {header: 'Name', value: 'name', classes: 'col-md-5', show: true}
  ]

  $scope.relationshipTypeColumns = () -> [
    {header: 'Name', value: 'name', classes: 'col-md-2', show: true}
    {header: 'Source to Destination', value: 'sourceToDestination', classes: 'col-md-2'}
    {header: 'Destination to Source', value: 'destinationToSource', classes: 'col-md-2'}
    {header: 'Source Class', value: 'sourceClass', classes: 'col-md-3'}
    {header: 'Destination Class', value: 'destinationClass', classes: 'col-md-3'}
  ]

  $scope.relationshipsColumns = () -> [

    {header: 'Relation',    value: 'type[direction]', classes: 'col-md-6'}
    {header: 'Destinaiton', value: 'relation.name', classes: 'col-md-6', show: 'relation.show()'}
  ]

  $scope.resource         = catalogueElementResource
  $scope.search           = modelCatalogueSearch
  $scope.expression       = $scope.indicator

  $scope.show = () ->
    $log.info "Evaluating: #{$scope.expression}"
    if $scope.expression.indexOf('>>>') == -1
      $q.when($scope.$eval($scope.expression)).then (result) ->
        if result?.size?
          $scope.list = result
          $scope.element = null
        if result?.elementType?
          $scope.list = emptyList
          $scope.element = result
        else
          $log.info "Instead of list or element got: ", result
    else
      parts     = $scope.expression?.split /\s*>>>\s*/g
      lastPart  = parts[0]
      promise   = $q.when $scope.$eval(lastPart)
      for part, i in parts when i != 0
        promise = promise.then (result) ->
          $log.info 'expression in chain {{', lastPart, '}} resolved to ', result
          lastPart = part
          $q.when $scope.$eval lastPart, $r: result
      promise.then (result) ->
        $log.info 'expression in chain {{', lastPart, '}} resolved to ', result
        if result?.size?
          $scope.list = result
          $scope.element = null
        if result?.elementType?
          $scope.list = emptyList
          $scope.element = result
        else
          $log.info "Instead of list got: ", result


  $scope.selection = []

  $scope.columns = $scope.relationshipsColumns()

  $scope.removeColumn = (index) ->
    return if $scope.columns.length <= 1
    $scope.columns.splice(index, 1)

  $scope.addColumn = (index, column = {header: 'ID', value: 'id', classes: 'col-md-2'}) ->
    $scope.columns.splice(index + 1, 0, angular.copy(column))


  $scope.show()

  $scope.$on 'showCatalogueElement', (event, element) ->
    $scope.element = element
])