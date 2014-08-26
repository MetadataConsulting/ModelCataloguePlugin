#= require jquery/dist/jquery
#= require bootstrap/dist/js/bootstrap
#= require ng-file-upload-shim/angular-file-upload-shim
#= require angular/angular
#= require ng-file-upload/angular-file-upload
#= require modelcatalogue/util/index
#= require modelcatalogue/core/index
#= require modelcatalogue/core/ui/index
#= require modelcatalogue/core/ui/bs/index
#= require modelcatalogue/core/ui/states/index
#= require modelcatalogue/core/ui/bs/elementViews/index

angular.module('demo', [
  'demo.config'
  'mc.core.ui.bs'
  'mc.core.ui.states'
  'ui.bootstrap'

]).controller('demo.DemoCtrl', ['catalogueElementResource', 'modelCatalogueSearch', '$scope', '$log', '$q', 'columns', '$rootScope', 'messages', 'enhance', (catalogueElementResource, modelCatalogueSearch, $scope, $log, $q, columns, $rootScope, messages, enhance)->
  listEnhancer = enhance.getEnhancer('list')



  $scope.listResource     = """resource("dataElement").list()"""
  $scope.listRelTypes     = """resource("relationshipType").list()"""
  $scope.searchSomething  = """search("patient")"""
  $scope.searchModel      = """resource("model").search("patient")"""
  $scope.outgoing         = """resource("dataElement").list() >>> $r.list[0].outgoingRelationships()"""
  $scope.indicator        = """resource("conceptualDomain").search("NHIC") >>> $r.list[0]"""

  $scope.resource         = catalogueElementResource
  $scope.search           = modelCatalogueSearch
  $scope.expression       = $scope.indicator

  $scope.show = () ->
    $log.info "Evaluating: #{$scope.expression}"
    if $scope.expression.indexOf('>>>') == -1
      $q.when($scope.$eval($scope.expression)).then (result) ->
        if result?.size?
          $scope.columns = columns(result.itemType)
          $scope.list = result
          $scope.element = null
        if result?.elementType?
          $scope.list = listEnhancer.createSingletonList(result)
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
          $scope.columns = columns(result.itemType)
          $scope.element = null
        if result?.elementType?
          $scope.list = listEnhancer.createSingletonList(result)
          $scope.element = result
        else
          $log.info "Instead of list got: ", result


  $scope.selection = []

  $scope.columns = columns()
  $scope.list    = listEnhancer.createEmptyList()

  $scope.actions = [
    {type: 'primary', title: 'Test', icon: 'info-sign', action: (element) -> alert(element.name)}
    #{title: 'Test 2', action: (element) -> alert(element.name)}
  ]

  $scope.show()

  # TODO: rewrite to states when finished
  $scope.$on 'showCatalogueElement', (event, element) ->
    $scope.element = element

  $scope.$on 'treeviewElementSelected', (event, element) ->
    $scope.selectedInTreeview = element

  onDescendPathChange = (path) ->
    if path.indexOf(',') > -1
      $scope.descend = path.split(/\s*,\s*/)
    else
      $scope.descend = path

  $scope.descendPath = 'valueDomains, instantiates'
  $scope.selectedInTreeview = null

  $scope.$watch 'descendPath', onDescendPathChange
  $scope.$watch 'selectedInTreeview', (selectedInTreeview) ->
    $rootScope.$broadcast 'treeviewElementSelected', selectedInTreeview

  $scope.messages       = messages
  $scope.messageText    = 'Try me!'
  $scope.messageType    = 'success'
  $scope.messagesTypes  = ['info', 'success', 'warning', 'error']
  $scope.addMessage     = (text, type) ->
    messages[type](text)

  $scope.showConfirm    = () ->
    messages.confirm('Confirm Dialog', $scope.messageText).then (result) ->
      if result
        messages.success('Confirmed')
      else
        messages.error('Rejected')

  $scope.showPrompt    = () ->
    messages.prompt('Prompt Dialog', $scope.messageText).then (result) ->
      messages.success('Returned: ' + result)
    , () ->
      messages.error('Rejected')



  onDescendPathChange $scope.descendPath
])